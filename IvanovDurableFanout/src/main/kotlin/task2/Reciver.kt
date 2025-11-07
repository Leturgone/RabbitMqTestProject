package task2

import com.rabbitmq.client.ConnectionFactory
import com.rabbitmq.client.DeliverCallback
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking


suspend fun processMessage(message: String){
    delay(message.count{it == '#'}.toLong() * 1000L)
}
fun main() {

    // Создание фабрики соединений
    val rabbitConnectionFactory = ConnectionFactory().apply {
        host = "localhost"
        port = 5672
        username = "guest"
        password = "guest"
    }

    // Установка соединения и канала, по которому и будет происходить общение с RabbitMQ
    rabbitConnectionFactory.newConnection().use { connection ->
        connection.createChannel().use {channel ->

            // Создание очереди
            val queueName = "ikbo-07_ivanov_durable_fanout"

            // требуется сохранение сообщений, значит обозначаем durable
            channel.queueDeclare(queueName, true, false, false, null)

            // отправляется всем с fanout
            val exchangeName = "fanout_ivanov"
            channel.exchangeDeclare(
                exchangeName,
                "fanout",
                true);

            // Связываем очередь с обменником
            channel.queueBind(queueName, exchangeName, "");

            // Получение сообщения
            val deliverCallback = DeliverCallback { _, delivery ->
                val message = String(delivery.body)
                println("Приняли: $message")

                runBlocking { processMessage(message) }

                println("Обработали: $message")

                //deliveryTag - идентификатор сообщения
                channel.basicAck(delivery.envelope.deliveryTag,false)
            }

            val consumerTag  = channel.basicConsume(queueName, false, deliverCallback, { _ -> })

            println("чтобы выйти - enter")
            readLine()
            channel.basicCancel(consumerTag)
        }
    }

}