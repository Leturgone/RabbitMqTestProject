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

    //Установка соединения и канала,по которому и будет происходить общение с RabbitMQ

    rabbitConnectionFactory.newConnection().use { connection ->
        connection.createChannel().use {channel ->
            //Создание очереди
            val queueName = "ikbo-07_yasnetsov_exclusive"
            // exclusive - эксклюзивная, после отключение потребителя очередь будет удалена, может быть только один потребитель
            while (true) {
                print("Введите сообщение: ")
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

                println("Сообщение отправлено: $input")
            }
        }
    }

}
