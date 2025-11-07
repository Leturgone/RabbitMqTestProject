package task1

import com.rabbitmq.client.ConnectionFactory
import com.rabbitmq.client.DeliverCallback
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking


suspend fun process(message: String){
    val count = message.count{it == '-'}
    delay(count.toLong()* 1000L )
}

fun main() {

    // Создание фабрики соединений
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

            // Получение сообщения
            val deliverCallback = DeliverCallback { _, delivery ->
                val message = String(delivery.body)
                println("получено: $message")
                runBlocking { process(message) }
                println("обработано: $message")
            }

            val consumerTag  = channel.basicConsume(
                queueName,
                true,
                deliverCallback,
                { _ -> })

            println("выйти - enter")
            readLine()
            // Отмена потребителя
            channel.basicCancel(consumerTag)
        }
    }

}