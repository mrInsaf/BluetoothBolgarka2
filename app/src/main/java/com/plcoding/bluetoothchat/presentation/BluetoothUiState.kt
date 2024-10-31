package com.plcoding.bluetoothchat.presentation

import com.plcoding.bluetoothchat.domain.chat.BluetoothDevice
import com.plcoding.bluetoothchat.domain.chat.BluetoothMessage

data class BluetoothUiState(
    val scannedDevices: List<BluetoothDevice> = emptyList(),
    val pairedDevices: List<BluetoothDevice> = emptyList(),
    val isConnected: Boolean = false,
    val isConnecting: Boolean = false,
    val errorMessage: String? = null,
    val messages: List<BluetoothMessage> = emptyList(),

    val isBolgarkaScreenOpened: Boolean = false,

//    Instrument State
    val revolutionsPerMinute: Int = 0,
    val currentTemperature: Int = 0,
    val inputPower: Int = 0,
    val outputPower: Int = 0,

    val operatingHours: Int = 0,
    val rebootCount: Int = 0,
    val jamShutdownCount: Int = 0,
    val overheatShutdownCount: Int = 0,
    val dropDetectionShutdownCount: Int = 0,

)
