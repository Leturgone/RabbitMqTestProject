package task2

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

    // Установка соединения и канала, по которому и будет происходить общение с RabbitMQ
    rabbitConnectionFactory.newConnection().use { connection ->
        connection.createChannel().use {channel ->

            // Создание очереди
            val queueName = "ikbo-07_ivanov_durable_topic"

            // durable чтобы сохранялось
            channel.queueDeclare(queueName, true, false, false, null)

            // Объявление обменника topic, нужны ключи маршрутизации, управление отправкой сообщений по очередям
            val exchangeName = "topic_ivanov"
            channel.exchangeDeclare(exchangeName, "topic", true);

            // Связываем очередь с обменником, добавляем ключ для получения конкретных сообщений
            channel.queueBind(queueName, exchangeName, "hard");

            // Получение сообщения
            val deliverCallback = DeliverCallback { _, delivery ->
                val message = String(delivery.body)
                println("принято: $message")
                runBlocking { process(message) }
                println("обработано: $message")

                //deliveryTag - идентификатор сообщения, false - подтверждение для одного сообщения
                channel.basicAck(delivery.envelope.deliveryTag,false)
            }

            val consumerTag  = channel.basicConsume(queueName, false, deliverCallback, { _ -> })

            println("выйти - enter")
            readLine()

            // Отменяем потребителя
            channel.basicCancel(consumerTag)
        }
    }

}