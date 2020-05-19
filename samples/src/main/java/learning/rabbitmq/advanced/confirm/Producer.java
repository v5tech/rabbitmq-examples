package learning.rabbitmq.advanced.confirm;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmListener;
import com.rabbitmq.client.Connection;
import learning.rabbitmq.utils.ConnectionUtils;

/**
 * 如何实现Confirm确认消息？
 * <p>
 * 1. 在channel上开启确认模式:channel.confirmSelect()
 * 2. 在channel上添加监听：addConfirmListener，监听成功和失败的返回结果，根据具体返回结果对消息进行重发或日志记录等
 */
public class Producer {

    private static final String exchangeName = "test_confirm_exchange";
    private static final String routingKey = "confirm.save";

    public static void main(String[] args) throws Exception {
        Connection connection = ConnectionUtils.getConnection();
        //1. 通过connection创建一个Channel
        Channel channel = connection.createChannel();
        //2.指定消息确认模式
        channel.confirmSelect();
        //3. 通过Channel发送数据
        String message = "Hello RabbitMQ Send confirm message!";
        channel.basicPublish(exchangeName, routingKey, null, message.getBytes());
        //4. 添加一个确认监听
        channel.addConfirmListener(new ConfirmListener() {
            @Override
            public void handleAck(long deliveryTag, boolean multiple) {
                //成功的情况 deliveryTag:消息的唯一标签；
                System.out.println("——get ack——");
            }

            @Override
            public void handleNack(long deliveryTag, boolean multiple) {
                //失败的情况
                System.out.println("——have no  ack——");
            }
        });
    }
}