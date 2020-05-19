package learning.rabbitmq.springboot.example.consumer.receiver;

import com.rabbitmq.client.Channel;
import learning.rabbitmq.springboot.example.entity.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class OrderReceiver {

    /**
     * @param order
     * @param channel
     * @param headers
     * @throws Exception
     * @RabbitListener作用在方法上时，@RabbitHandler不能缺少
     */
    @RabbitHandler
    @RabbitListener(
            bindings = @QueueBinding(
                    // 交换机
                    exchange = @Exchange(
                            name = "${spring.rabbitmq.listener.order.exchange.name}",
                            durable = "${spring.rabbitmq.listener.order.exchange.durable}",
                            type = "${spring.rabbitmq.listener.order.exchange.type}",
                            ignoreDeclarationExceptions = "${spring.rabbitmq.listener.order.exchange.ignore-declaration-exceptions}"
                    ),
                    // 队列
                    value = @Queue(
                            value = "${spring.rabbitmq.listener.order.queue.name}",
                            declare = "${spring.rabbitmq.listener.order.queue.durable}"
                    ),
                    // routingKey
                    key = "${spring.rabbitmq.listener.order.key}"
            ))
    public void onOrderMessage(@Payload Order order, Channel channel, @Headers Map<String, Object> headers)
            throws Exception {
        log.info("-----------------------收到消息, 开始消费-----------------------");
        log.info("订单id: {}", order.getId());

        Long deliveryTag = (Long) headers.get(AmqpHeaders.DELIVERY_TAG);
        // 手工ACK
        /**
         *  取值为 false 时，表示通知 RabbitMQ 当前消息被确认(仅提交确认当前消息)
         *  如果为 true，则额外将比第一个参数指定的 delivery tag 小的消息一并确认
         */
        channel.basicAck(deliveryTag, false);
    }
}