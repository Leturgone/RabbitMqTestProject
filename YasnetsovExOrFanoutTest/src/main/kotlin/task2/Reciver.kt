package task2

import com.rabbitmq.client.ConnectionFactory
import com.rabbitmq.client.DeliverCallback
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking


suspend fun processMessage(message: String){
    val count = message.count{it == '#'}.toLong()
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
            val queueName = "ikbo-07_yasnetsov_exclusive"

            // exclusive - эксклюзивная, после отключение потребителя очередь будет удалена, может быть только один потребитель и поставщик
            channel.queueDeclare(queueName, false, true, false, null)

            // Объявление обменника fanout, шлет во все очереди
            val exchangeName = "fanout_exchange"
            channel.exchangeDeclare(exchangeName, "fanout", true);

            // Связываем очередь с обменником
            channel.queueBind(queueName, exchangeName, "");

            // Получение сообщения
            val deliverCallback = DeliverCallback { _, delivery ->
                val message = String(delivery.body)
                println("Получено: $message")
                runBlocking { processMessage(message) }
                println("Обработано: $message")
                //deliveryTag - идентификатор сообщения, false - подтвреждение для одного сообщения
                channel.basicAck(delivery.envelope.deliveryTag,false)
            }

            val consumerTag  = channel.basicConsume(queueName, false, deliverCallback, { _ -> })

            println("Нажмите Enter для выхода")
            readLine()

            // После нажатия Enter, отменяем потребителя
            channel.basicCancel(consumerTag)
        }
    }

}