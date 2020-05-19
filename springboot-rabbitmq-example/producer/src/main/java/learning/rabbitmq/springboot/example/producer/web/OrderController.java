package learning.rabbitmq.springboot.example.producer.web;

import learning.rabbitmq.springboot.example.entity.Order;
import learning.rabbitmq.springboot.example.producer.service.OrderService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.UUID;

/**
 * OrderController
 */
@RestController
@RequestMapping("order")
public class OrderController {

    @Resource
    private OrderService orderService;

    @PostMapping
    public void createOrder(Order order) {
        order.setMessageId(System.currentTimeMillis() + "$" + UUID.randomUUID().toString());
        orderService.createOrder(order);
    }

    @GetMapping("list")
    public Object list() {
        return orderService.list();
    }
}