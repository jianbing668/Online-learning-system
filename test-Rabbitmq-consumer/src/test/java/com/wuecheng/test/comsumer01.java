package com.wuecheng.test;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @ClassName : comsumer01
 * @Description :
 * @Author : JB
 * @Date: 2020-01-11 15:31
 */


public class comsumer01 {
    //队列
    private  static final String QUEUE="duilie";
    public static void main(String[] args) throws IOException, TimeoutException {
        //通过连接工厂创建新的连接和mq建立连接
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("127.0.0.1");
        connectionFactory.setPort(5672);//5672是生产通信的端口，15672是rabbitmq的WEB管理的端口
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        //设置虚拟机  一个mq服务可以设置多个虚拟机，每一个虚拟机相当于一个独立的raabbitmq，他们之间互不影响
        connectionFactory.setVirtualHost("/");
        Connection connection = null;
            //建立连接
            connection = connectionFactory.newConnection();
            //创建会话通道,生产者和mq服务所有的通信都在channel通道中完成
            Channel channel = connection.createChannel();
            //声明队列
            channel.queueDeclare(QUEUE,true,false,false,null);

            //实现消费方法
        DefaultConsumer defaultConsumer = new DefaultConsumer(channel){
            /**当接受到消息后此方法被调用
             * @param consumerTag  消费者标签，用来标识消费者的，可在监听队列时设置
             * @param envelope  信封，可拿到很多信息
             * @param properties  消息的一些额外属性
             * @param body   消息的内容
             * @throws IOException
             */
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                //super.handleDelivery(consumerTag, envelope, properties, body);

                //交换机
                String exchange = envelope.getExchange();
                //路由Key
                String routingKey = envelope.getRoutingKey();
                //消息ID mq在channel中用来标识消息的id,可用来确认消息已接收
                long envelopeDeliveryTag = envelope.getDeliveryTag();
                //消息内容
                String message = new String(body,"utf-8");
                System.out.println("receive message"+message);

            }
        };

        //监听队列
        //String queue, boolean autoAck, String consumerTag, boolean noLocal, boolean exclusive, Map<String, Object> arguments, Consumer callback
        /*参数列表
        * 1.queue  要监听的队列名称
        * 2.autoAck 自动回复，当消费者接收到消息后告诉mq消息已接受，如果将此参数设置为true表示会自动恢复mq,如果设置false, 通过编程实现回复。
        * 。。
        * 5.callback 消费方法
        * */
        channel.basicConsume(QUEUE,true,defaultConsumer);
    }
}
