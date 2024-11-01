package com.plcoding.bluetoothchat.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plcoding.bluetoothchat.data.chat.BolgarkaInfo
import com.plcoding.bluetoothchat.domain.chat.BluetoothController
import com.plcoding.bluetoothchat.domain.chat.BluetoothDeviceDomain
import com.plcoding.bluetoothchat.domain.chat.ConnectionResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class BluetoothViewModel @Inject constructor(
    private val bluetoothController: BluetoothController
): ViewModel() {

    private val _state = MutableStateFlow(BluetoothUiState())
    val state = combine(
        bluetoothController.scannedDevices,
        bluetoothController.pairedDevices,
        _state
    ) { scannedDevices, pairedDevices, state ->
        state.copy(
            scannedDevices = scannedDevices,
            pairedDevices = pairedDevices,
            messages = if(state.isConnected) state.messages else emptyList()
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), _state.value)

    private var deviceConnectionJob: Job? = null

    init {
        bluetoothController.isConnected.onEach { isConnected ->
            _state.update { it.copy(isConnected = isConnected) }
        }.launchIn(viewModelScope)

        bluetoothController.errors.onEach { error ->
            _state.update { it.copy(
                errorMessage = error
            ) }
        }.launchIn(viewModelScope)
    }

    fun connectToDevice(device: BluetoothDeviceDomain) {
        _state.update { it.copy(isConnecting = true) }
        deviceConnectionJob = bluetoothController
            .connectToDevice(device)
            .listen()
    }

    fun disconnectFromDevice() {
        deviceConnectionJob?.cancel()
        bluetoothController.closeConnection()
        _state.update { it.copy(
            isConnecting = false,
            isConnected = false
        ) }
    }

    fun waitForIncomingConnections() {
        _state.update { it.copy(isConnecting = true) }
        deviceConnectionJob = bluetoothController
            .startBluetoothServer()
            .listen()
    }

    fun sendMessage(message: String) {
        viewModelScope.launch {
            val bluetoothMessage = bluetoothController.trySendMessage(message)
            if(bluetoothMessage != null) {
                _state.update { it.copy(
                    messages = it.messages + bluetoothMessage
                ) }
            }
        }
    }

    fun startScan() {
        bluetoothController.startDiscovery()
    }

    fun stopScan() {
        bluetoothController.stopDiscovery()
    }

    fun onBolgarkaClick() {
        _state.update { it.copy(
            isBolgarkaScreenOpened = true
        ) }
    }

    private fun Flow<ConnectionResult>.listen(): Job {
        return onEach { result ->
            when(result) {
                ConnectionResult.ConnectionEstablished -> {
                    _state.update { it.copy(
                        isConnected = true,
                        isConnecting = false,
                        errorMessage = null
                    ) }
                }
                is ConnectionResult.TransferSucceeded -> {
                    _state.update { it.copy(
                        messages = it.messages + result.message
                    ) }
                    parseAndApplyDeviceData(result)
                }
                is ConnectionResult.Error -> {
                    _state.update { it.copy(
                        isConnected = false,
                        isConnecting = false,
                        errorMessage = result.message
                    ) }
                }
            }
        }
            .catch { throwable ->
                bluetoothController.closeConnection()
                _state.update { it.copy(
                    isConnected = false,
                    isConnecting = false,
                ) }
            }
            .launchIn(viewModelScope)
    }

    private fun parseAndApplyDeviceData(result: ConnectionResult.TransferSucceeded) {
        try {
            val bolgarkaInfo = BolgarkaInfo.fromProtocolMessage(result.message.message)
            println(bolgarkaInfo)
            when (bolgarkaInfo.status) {
                1 -> _state.update { currentState ->  // Ошибка перегрева
                    currentState.copy(overheatShutdownCount = currentState.overheatShutdownCount + 1)
                }
                2 -> _state.update { currentState ->  // Ошибка при перезагрузке
                    currentState.copy(rebootCount = currentState.rebootCount + 1)
                }
                3 -> _state.update { currentState ->  // Ошибка по заклиниванию
                    currentState.copy(jamShutdownCount = currentState.jamShutdownCount + 1)
                }
                4 -> _state.update { currentState ->  // Ошибка по падению
                    currentState.copy(dropDetectionShutdownCount = currentState.dropDetectionShutdownCount + 1)
                }
                else -> {  }
            }
            _state.update { it.copy(
                revolutionsPerMinute = bolgarkaInfo.revolutionsPerMinute,
                inputPower = bolgarkaInfo.voltage * bolgarkaInfo.current,
                operatingTime = formatOperatingTime(bolgarkaInfo.operatingTime)
            ) }
        }
        catch (e: IllegalArgumentException) {
            println("это неправильное сообщение")
        }
    }

    override fun onCleared() {
        super.onCleared()
        bluetoothController.release()
    }

    private fun formatOperatingTime(totalSeconds: Int): String {
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60

        return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds)
    }
}