package learning.rabbitmq.springboot.example.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 订单表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_order")
@Accessors(chain = true)
public class Order implements Serializable {

    /**
     * 订单 id
     */
    @TableId
    private String id;

    /**
     * 订单名称
     */
    private String name;

    /**
     * 消息唯一 id
     */
    private String messageId;

}
