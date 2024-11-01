package com.plcoding.bluetoothchat.data.chat

data class BolgarkaInfo(
    var revolutionsPerMinute: Int = 0,
    var current: Float = 0f,
    var voltage: Int = 0,
    var status: Int = 0,
    var operatingTime: Int = 0
) {
    companion object {
        // Метод для разбора строки и создания экземпляра BolgarkaInfo
        fun fromProtocolMessage(message: String): BolgarkaInfo {
            val cleanMessage = message.trim()

            if (cleanMessage.startsWith("!R")) {
                // Удаляем префикс и делим по пробелам
                val data = cleanMessage.removePrefix("!R").trim().split("\\s+".toRegex())

                val revolutionsPerMinute = data[0].toIntOrNull() ?: 0
                val current = data[1].drop(1).toFloatOrNull()?.div(10) ?: 0f //
                val voltage = data[2].drop(1).toIntOrNull() ?: 0 // Извлекаем число после 'U'
                val status = data[3].drop(1).toIntOrNull() ?: 0 // Статус без префикса 'E'
                val operatingTime = data[4].removePrefix("t").removeSuffix("<CR>").toIntOrNull() ?: 0

                return BolgarkaInfo(
                    revolutionsPerMinute = revolutionsPerMinute,
                    current = current,
                    voltage = voltage,
                    status = status,
                    operatingTime = operatingTime
                )
            }
            throw IllegalArgumentException("Invalid message format")
        }
    }
}

