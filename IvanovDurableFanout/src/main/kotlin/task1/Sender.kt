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
            val queueName = "ikbo-07_ivanov_durable"

            // durable - очередь сохраняется, устойчивая
            channel.queueDeclare(queueName,
                true,
                false,
                false,
                null)

            while (true) {
                print("Сообщение: ")
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

                println("отправили: $input")
            }
        }
    }

}
