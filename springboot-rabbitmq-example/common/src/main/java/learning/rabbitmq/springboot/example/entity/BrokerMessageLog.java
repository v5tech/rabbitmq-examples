package learning.rabbitmq.springboot.example.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 消息记录表
 */
@Data
@Accessors(chain = true)
@TableName("broker_message_log")
@AllArgsConstructor
@NoArgsConstructor
public class BrokerMessageLog implements Serializable {

    /**
     * 消息唯一 id
     */
    @TableId
    private String messageId;

    /**
     * 消息体内容
     */
    private String message;

    /**
     * 重试次数
     */
    private Integer tryCount;

    /**
     * 消息投递状态
     */
    private String status;

    /**
     * 下一次重试时间
     */
    private LocalDateTime nextRetry;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

}
