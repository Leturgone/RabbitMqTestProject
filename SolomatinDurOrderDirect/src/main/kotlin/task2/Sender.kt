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

    //Установка соединения и канала, по которому и будет происходить общение с RabbitMQ

    rabbitConnectionFactory.newConnection().use { connection ->
        connection.createChannel().use {channel ->
            while (true) {
                print("message: ")
                val input = readLine() ?: break
                if (input.equals("exit", ignoreCase = true)) {
                    break
                }
                //Разделение на сообщение и ключ
                val message = input.split("/");

                // Отправка сообщения
                channel.basicPublish(
                    "direct_solomatin",
                    message[0],
                    null,
                    message[1].toByteArray()
                )

                println("message sent: ${message[1]} key ${message[0]}")
            }
        }
    }

}
