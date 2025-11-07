package task2

import com.rabbitmq.client.AMQP
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

    //Установка соединения и канала, по которому и будет происходить общение с RabbitMQ

    rabbitConnectionFactory.newConnection().use { connection ->
        connection.createChannel().use {channel ->
            while (true) {
                print("Введите сообщение: ")
                val input = readLine() ?: break
                if (input.equals("exit", ignoreCase = true)) {
                    break
                }


                // Отправка сообщения
                channel.basicPublish(
                    "fanout_exchange",
                    "",
                    MessageProperties.PERSISTENT_BASIC,
                    input.toByteArray()
                )

                println("Сообщение отправлено: $input")
            }
        }
    }

}
