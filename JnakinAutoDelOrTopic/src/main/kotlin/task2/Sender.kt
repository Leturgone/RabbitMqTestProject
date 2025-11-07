package task2

import com.rabbitmq.client.ConnectionFactory
import com.rabbitmq.client.MessageProperties

fun processKey(key:String) = key.split('.')

fun main() {

    //Создание фабрики соединений
    val rabbitConnectionFactory = ConnectionFactory().apply {
        host = "localhost"
        port = 5672
        username = "guest"
        password = "guest"
    }

    //Установка соединения и канала
    rabbitConnectionFactory.newConnection().use { connection ->
        connection.createChannel().use {channel ->
            while (true) {
                print("введите сообщение: ")
                val input = readLine() ?: break
                if (input.equals("exit", ignoreCase = true)) {
                    break
                }
                //Разделение на сообщение и топик
                val message = input.split("/");
                val key = processKey(message[0])

                // Отправка сообщения, сохранение - MessageProperties.PERSISTENT_BASIC
                channel.basicPublish(
                    "topic_jnakin",
                    key[0],
                    MessageProperties.PERSISTENT_BASIC,
                    message[1].toByteArray()
                )

                println("сообщение отправлено: ${message[1]} топик ${key[0]}")
            }
        }
    }

}
