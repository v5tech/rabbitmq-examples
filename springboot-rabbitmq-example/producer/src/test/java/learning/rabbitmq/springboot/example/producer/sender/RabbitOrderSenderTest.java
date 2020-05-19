package learning.rabbitmq.springboot.example.producer.sender;

import learning.rabbitmq.springboot.example.entity.Order;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.UUID;

@SpringBootTest
@RunWith(SpringRunner.class)
public class RabbitOrderSenderTest {

    @Autowired
    private RabbitOrderSender rabbitOrderSender;

    @Test
    public void sendOrder() {
        Order order = new Order();
        order.setId("202004010000001")
                .setName("abc")
                .setMessageId(System.currentTimeMillis() + "$" + UUID.randomUUID().toString());
        rabbitOrderSender.sendOrder(order);
    }
}