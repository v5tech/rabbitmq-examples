package learning.rabbitmq.springboot.example.utils;

public class Constant {

    public static final String ORDER_EXCHANGE = "order-exchange";
    public static final String ORDER_ROUTING = "order.save";

    /**
     * 消息投递状态 0 投递中
     */
    public static final String ORDER_SENDING = "0";
    /**
     * 消息投递状态 1 投递成功
     */
    public static final String ORDER_SEND_SUCCESS = "1";
    /**
     * 消息投递状态 2 投递失败
     */
    public static final String ORDER_SEND_FAILURE = "2";

    /**
     * 分钟超时单位 默认 1 分钟
     */
    public static final int ORDER_TIMEOUT_MINUTES = 1;

}
