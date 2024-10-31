package com.plcoding.bluetoothchat.data.chat

data class BolgarkaInfo(
    var revolutionsPerMinute: Int = 0,
    var current: Int = 0,
    var voltage: Int = 0,
    var status: String = "unknown",
    var operatingTime: Int = 0
) {
    companion object {
        // Метод для разбора строки и создания экземпляра BolgarkaInfo
        fun fromProtocolMessage(message: String): BolgarkaInfo {
            val cleanMessage = message.trim()

            if (cleanMessage.startsWith("!R")) {
                // Удаляем префикс и делим по пробелам
                val data = cleanMessage.removePrefix("!R").trim().split("\\s+".toRegex())

                if (data.size >= 5) {
                    return BolgarkaInfo(
                        revolutionsPerMinute = data[0].toIntOrNull() ?: 0,
                        current = data[1].toIntOrNull() ?: 0,
                        voltage = data[2].toIntOrNull() ?: 0,
                        status = data[3],
                        operatingTime = data[4].toIntOrNull() ?: 0
                    )
                }
            }
            throw IllegalArgumentException("Invalid message format")
        }
    }
}
