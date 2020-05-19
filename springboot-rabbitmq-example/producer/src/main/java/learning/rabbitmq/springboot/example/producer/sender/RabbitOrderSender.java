package learning.rabbitmq.springboot.example.producer.sender;

import cn.hutool.json.JSONUtil;
import learning.rabbitmq.springboot.example.entity.Order;
import learning.rabbitmq.springboot.example.producer.service.BrokerMessageLogService;
import learning.rabbitmq.springboot.example.utils.Constant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 消息可靠投递生产消息方
 */
@Slf4j
@Component
public class RabbitOrderSender implements RabbitTemplate.ConfirmCallback, RabbitTemplate.ReturnCallback {

    /**
     * 注入rabbitTemplate模板类
     */
    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private BrokerMessageLogService messageLogService;

    /**
     * 消息发送方调用，构建自定义调用
     *
     * @param order
     */
    public void sendOrder(Order order) {
        // 设置消息确认回调
        rabbitTemplate.setConfirmCallback(this);
        // 设置消息失败回调
        rabbitTemplate.setReturnCallback(this);
        // 设置消息唯一 id
        CorrelationData correlationData = new CorrelationData(order.getMessageId());
        // 发送消息
        rabbitTemplate.convertAndSend(Constant.ORDER_EXCHANGE, Constant.ORDER_ROUTING, order, correlationData);
        log.info("消息已发送, messageId={}", order.getMessageId());
    }

    /**
     * 回调函数：confirm确认
     *
     * @param correlationData 唯一标识，根据该值唯一确定哪条消息
     * @param ack             是否投递成功
     * @param cause           原因
     */
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        System.out.println("correlationData:" + correlationData);
        String messageId = correlationData.getId();
        if (ack) {
            // 如果confirm返回成功，则进行更新
            messageLogService.changeBrokerMessageLogStatus(messageId, Constant.ORDER_SEND_SUCCESS);
            log.info("消息发送成功, messageId={}", messageId);
        } else {
            // 失败则进行后续操作：重试、补偿等
            log.error("消息发送失败, messageId={}", messageId);
        }
    }

    /**
     * 回调函数：失败后回调
     *
     * @param message    message
     * @param replyCode  replyCode
     * @param replyText  replyText
     * @param exchange   交换机
     * @param routingKey routingKey
     */
    @Override
    public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
        byte[] body = message.getBody();
        MessageProperties messageProperties = message.getMessageProperties();
        log.error("消息发送失败, body={},messageProperties={}", new String(body), JSONUtil.toJsonStr(messageProperties));
    }
}