package com.example.demo.config.rabbitmq;


import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * MQ配置
 *
 * @author
 */
@Configuration
public class RabbitMqConfig {

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(new Jackson2JsonMessageConverter());
        return template;
    }

    @Bean("rabbitListenerContainerFactory")
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setPrefetchCount(200);
        factory.setConcurrentConsumers(6);
        ExecutorService service = Executors.newFixedThreadPool(100);
        factory.setTaskExecutor(service);
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(new Jackson2JsonMessageConverter());
        return factory;
    }



    /**
     * 超时队列
     * 超时队列中，消息超时成为死信后，路由到对应交换机和key绑定的队列
     * @return
     */
    @Bean("varProcessDelayQueue")
    public Queue varProcessDelayQueue() {
        Map<String, Object> params = new HashMap<>();
        // x-dead-letter-exchange 声明了队列里的死信转发到的DLX名称，
        params.put("x-dead-letter-exchange", MqQueueConstant.VAR_PROCESS_DELAY_EXCHANGE);
        // x-dead-letter-routing-key 声明了这些死信在转发时携带的 routing-key 名称。
        params.put("x-dead-letter-routing-key", MqQueueConstant.VAR_PROCESS_DELAY_HANDLE_KEY);
        return new Queue(MqQueueConstant.VAR_PROCESS_DELAY,true, false, false, params);
    }


    /**
     * 超时处理队列，成为死信后，消息路由到此队列
     * @return
     */
    @Bean("varProcessDelayHandleQueue")
    public Queue varProcessDelayHandleQueue() {
        return new Queue(MqQueueConstant.VAR_PROCESS_DELAY_HANDLE);
    }

    /**
     * 变量处理队列
     * @return
     */
    @Bean("varProcessHandleQueue")
    public Queue varProcessHandleQueue() {
        return new Queue(MqQueueConstant.VAR_PROCESS_HANDLE);
    }

    // 延时后转发交换机
    @Bean
    DirectExchange varProcessDelayHandleExchange(){
        return new DirectExchange(MqQueueConstant.VAR_PROCESS_DELAY_EXCHANGE,true,false);
    }

    // 绑定处理队列与延迟后转发交换机
    @Bean
    public Binding varProcessDelayHandelBind() {
        return BindingBuilder.bind(varProcessDelayHandleQueue()).to(varProcessDelayHandleExchange()).with(MqQueueConstant.VAR_PROCESS_DELAY_HANDLE_KEY);
    }


}
