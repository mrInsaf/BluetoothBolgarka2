package com.plcoding.bluetoothchat.presentation.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BolgarkaBlock(
    title: String,
    contentMap: Map<String, Any>
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .padding(8.dp)
            .border(width = 1.dp, color = Color.Black)
    ) {
        Text(
            text = title,
            fontSize = 20.sp,
            color = Color.Black
        )
        contentMap.forEach { (key, value) ->
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text(
                    text = key,
                    color = Color.Black,
                    modifier = Modifier.fillMaxWidth(0.5f)
                )
                if (value is String) {
                    Text(
                        text = value,
                        color = Color.Black
                    )
                }
                else {
                    Text(
                        text = value.toString(),
                        color = Color.Black
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun BolgarkaBlockPreview() {
    BolgarkaBlock(
        title = "Инструмент",
        contentMap = mapOf(
            "Модель" to "pzc",
            "Серийный номер" to "wtf"
        )
    )
}