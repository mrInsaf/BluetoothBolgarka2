package com.plcoding.bluetoothchat.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.plcoding.bluetoothchat.R
import com.plcoding.bluetoothchat.domain.chat.BluetoothController
import com.plcoding.bluetoothchat.presentation.BluetoothViewModel
import com.plcoding.bluetoothchat.presentation.components.BolgarkaBlock

@Composable
fun BolgarkaStatisticsScreen(viewModel: BluetoothViewModel?) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceAround,
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
    ) {
        val uiState = viewModel?.state?.collectAsState()?.value

        Text(stringResource(R.string.logo), fontSize = 24.sp)

//        Инструмент
        BolgarkaBlock(
            title = stringResource(R.string.instrumentBlockTitle),
            contentMap = mapOf(
                "Модель" to "УШМВ-125/1700ЭВМ",
                "Серийный номер" to "912.000175",
                "Дата производства" to "10.10.2024",
                "Сообщение" to (uiState?.messages?.joinToString("\n") { it.message } ?: "Нет сообщений")
            )
        )

        // Текущие показатели инструмента
        BolgarkaBlock(
            title = "Текущие показатели инструмента",
            contentMap = mapOf(
                "Обороты" to "${uiState?.revolutionsPerMinute ?: 0} об./мин",
                "Текущая температура" to "${uiState?.currentTemperature ?: 0}° C",
                "Мощность двигателя (потребляемая)" to "${uiState?.inputPower ?: 0} Вт",
                "Мощность двигателя (полезная)" to "${uiState?.outputPower ?: 0} Вт"
            )
        )

        // Статистика по использованию
        BolgarkaBlock(
            title = "Статистика по использованию",
            contentMap = mapOf(
                "Счетчик моточасов" to "${uiState?.operatingHours ?: 0}:00", // Предположим, что это часы
                "Количество отключений по перегрузке" to "${uiState?.overheatShutdownCount ?: 0}",
                "Количество отключений по заклинивании" to "${uiState?.jamShutdownCount ?: 0}",
                "Количество отключений по перегреву" to "${uiState?.overheatShutdownCount ?: 0}",
                "Количество отключений при падении" to "${uiState?.dropDetectionShutdownCount ?: 0}"
            )
        )
    }
}

@Preview
@Composable
fun BolgarkaStatisticsScreenPreview() {
    BolgarkaStatisticsScreen(
        viewModel = null
    )
}
