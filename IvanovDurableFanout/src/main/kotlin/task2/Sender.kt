package task2

import com.rabbitmq.client.ConnectionFactory
import com.rabbitmq.client.MessageProperties


fun main() {

    //Создание фабрики соединений
    val rabbitConnectionFactory = ConnectionFactory().apply {
        host = "localhost"
        port = 5672
        username = "guest"
        password = "guest"
    }

    //Установка соединения и канала, для общения с RabbitMQ
    rabbitConnectionFactory.newConnection().use { connection ->
        connection.createChannel().use {channel ->
            while (true) {
                print("Сообщение: ")
                val input = readLine() ?: break
                if (input.equals("exit", ignoreCase = true)) {
                    break
                }
                // Отправка сообщения c сохранением MessageProperties.PERSISTENT_BASIC
                channel.basicPublish("fanout_ivanov", "",
                    MessageProperties.PERSISTENT_BASIC,
                    input.toByteArray()
                )
                println("Отправили $input")
            }
        }
    }

}
