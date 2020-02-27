package com.xuecheng.test.rabittmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @ClassName : Producer01
 * @Description :rabittmq入门程序
 * @Author : JB
 * @Date: 2020-01-11 11:18
 */


public class Producer01 {

    //队列
    private  static final String QUEUE="duilie";

    public static void main(String[] args) {
        //通过连接工厂创建新的连接和mq建立连接
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("127.0.0.1");
        connectionFactory.setPort(5672);//5672是生产通信的端口，15672是rabbitmq的WEB管理的端口
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        //设置虚拟机  一个mq服务可以设置多个虚拟机，每一个虚拟机相当于一个独立的raabbitmq，他们之间互不影响
        connectionFactory.setVirtualHost("/");

        Connection connection = null;
        Channel channel = null;
        try {
            //建立连接
             connection = connectionFactory.newConnection();
             //创建会话通道,生产者和mq服务所有的通信都在channel通道中完成
             channel = connection.createChannel();
            //声明队列，如果队列在mq中没有则要创建
            /**参数：String queue, boolean durable, boolean exclusive, boolean autoDelete, Map<String, Object> arguments
             *参数明细：
             * 1. queue 队列名称
             * 2.durable 是否持久化，持久化后，mq重启后队列还在
             * 3.exclusive 是否独占连接，队列只允许在该连接中访问，如果connection连接关闭队列自动删除。如果设置为true,可用于创建临时队列
             * 4.autoDelete 队列不在使用时是否自动删除
             * 5.arguments 队列参数，设置扩充参数，如队列存活时间
             */
            channel.queueDeclare(QUEUE,true,false,false,null);
            //发送消息
            /*参数明细：String exchange, String routingKey, BasicProperties props, byte[] body
            * 1.exchange  交换机
            * 2.routingKey  路由Key,交换机根据路由Key来将消息转发到指定的队列，如果使用默认的交换机，routingKey设置为队列的名称
            *3.props   消息的属性
            *4。body  消息的内容
            * */
            String message = "first use rabbitmq";
            channel.basicPublish("",QUEUE,null,message.getBytes());
            System.out.println("send to mq "+message);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }finally {
           //关闭连接前，先关闭通道
            try {
                channel.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
            try {
                connection.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
