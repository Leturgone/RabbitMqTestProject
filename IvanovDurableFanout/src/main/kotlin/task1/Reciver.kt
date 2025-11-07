package task1

import com.rabbitmq.client.ConnectionFactory
import com.rabbitmq.client.DeliverCallback
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking


suspend fun process(message: String){
    val count = message.count{it == '*'}.toLong()
    delay(count * 1000L)
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
            val queueName = "ikbo-07_ivanov_durable"

            // durable - очередь сохраняется, устойчивая
            channel.queueDeclare(queueName,
                true,
                false,
                false,
                null)

            // Получение сообщения
            val deliverCallback = DeliverCallback { _, delivery ->
                val message = String(delivery.body)
                println("Получили: $message")
                runBlocking { process(message) }
                println("Обработали: $message")
            }

            val consumerTag  = channel.basicConsume(
                queueName,
                true,
                deliverCallback,
                { _ -> })

            println("Чтобы выйти enter")
            readLine()
            // Отмена потребителя
            channel.basicCancel(consumerTag)
        }
    }

}