package task2

import com.rabbitmq.client.ConnectionFactory
import com.rabbitmq.client.DeliverCallback
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking


suspend fun processMessage(message: String){
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

    // Установка соединения и канала, по которому и будет происходить общение с RabbitMQ
    rabbitConnectionFactory.newConnection().use { connection ->
        connection.createChannel().use {channel ->

            // Создание очереди
            val queueName = "ikbo-07_solomatin_durable_dir"

            // требуется сохранение сообщений, значит обозначаем durable
            channel.queueDeclare(queueName, true, false, false, null)

            // Объявление обменника direct, нужны ключи маршрутизации, управление отправкой сообщений по очередям
            val exchangeName = "direct_solomatin"
            channel.exchangeDeclare(exchangeName, "direct", true);

            // Связываем очередь с обменником, добавляем ключ для получения конкретных сообщений
            channel.queueBind(queueName, exchangeName, "hard");

            // Получение сообщения
            val deliverCallback = DeliverCallback { _, delivery ->
                val message = String(delivery.body)
                println("RECEIVED: $message")
                runBlocking { processMessage(message) }
                println("PROCESSED: $message")

                //deliveryTag - идентификатор сообщения, false - подтверждение для одного сообщения
                channel.basicAck(delivery.envelope.deliveryTag,false)
            }

            val consumerTag  = channel.basicConsume(queueName, false, deliverCallback, { _ -> })

            println("Enter for exit")
            readLine()

            // После нажатия Enter, отменяем потребителя
            channel.basicCancel(consumerTag)
        }
    }

}