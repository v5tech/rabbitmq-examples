package learning.rabbitmq.springboot.example.producer.service;

import learning.rabbitmq.springboot.example.entity.Order;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.UUID;

@SpringBootTest
@RunWith(SpringRunner.class)
public class OrderServiceTest {

    @Resource
    private OrderService orderService;

    @Test
    public void createOrder() {
        Order order = new Order();
        order.setId("202004010000001")
                .setName("abc")
                .setMessageId(System.currentTimeMillis() + "$" + UUID.randomUUID().toString());
        orderService.createOrder(order);
    }
}