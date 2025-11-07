package task1

import com.rabbitmq.client.ConnectionFactory

fun main() {

    //Создание фабрики соединений
    val rabbitConnectionFactory = ConnectionFactory().apply {
        host = "localhost"
        port = 5672
        username = "guest"
        password = "guest"
    }

    // Установка соединения и канала для общения с RabbitMQ
    rabbitConnectionFactory.newConnection().use { connection ->
        connection.createChannel().use {channel ->
            // Создание очереди
            val queueName = "ikbo-07_jnakin_auto"

            // autoDelete - очередь автоудаляемая
            channel.queueDeclare(queueName,
                false,
                false,
                true,
                null)

            while (true) {
                print("введите сообщение: ")
                val input = readLine() ?: break
                if (input.equals("exit", ignoreCase = true)) {
                    break
                }
                // Отправка сообщения
                channel.basicPublish(
                    "",
                    queueName,
                    null,
                    input.toByteArray()
                )

                println("отправлено: $input")
            }
        }
    }

}
