package learning.rabbitmq.springboot.example.producer.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import learning.rabbitmq.springboot.example.entity.Order;

/**
 * 订单对象数据库操作接口
 */
public interface OrderMapper extends BaseMapper<Order> {
}
