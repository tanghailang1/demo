package com.efs.cloud.trackingservice.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author jabez.huang
 */
@Configuration
public class CloudRabbitConfig {

    @Value("${output.rabbitmq.tracking.exchange.name}")
    private String exchangeCloud;
    @Value("${output.rabbitmq.tracking.page.queue.name}")
    private String queueCloudTrackingPage;
    @Value("${output.rabbitmq.tracking.action.queue.name}")
    private String queueCloudTrackingAction;
    @Value("${output.rabbitmq.tracking.cart.queue.name}")
    private String queueCloudTrackingCart;
    @Value("${output.rabbitmq.tracking.order.queue.name}")
    private String queueCloudTrackingOrder;
    @Value("${sync.tracking.page_route_key}")
    private String routingKeyPage;
    @Value("${sync.tracking.action_route_key}")
    private String routingKeyAction;
    @Value("${sync.tracking.cart_route_key}")
    private String routingKeyCart;
    @Value("${sync.tracking.order_route_key}")
    private String routingKeyOrder;

    @Bean
    public Queue queueMessageCloudTrackingPage(){
        return new Queue(queueCloudTrackingPage);
    }

    @Bean
    public Queue queueMessageCloudTrackingAction(){
        return new Queue(queueCloudTrackingAction);
    }

    @Bean
    public Queue queueMessageCloudTrackingCart(){
        return new Queue(queueCloudTrackingCart);
    }

    @Bean
    public Queue queueMessageCloudTrackingOrder(){
        return new Queue(queueCloudTrackingOrder);
    }

    @Bean
    public TopicExchange exchangeCloud(){
        return new TopicExchange(exchangeCloud);
    }

    @Bean
    Binding bindingExchangeCloudTrackingPage(Queue queueMessageCloudTrackingPage, Exchange exchangeCloud) {
        return BindingBuilder.bind(queueMessageCloudTrackingPage).to(exchangeCloud).with(routingKeyPage).noargs();
    }

    @Bean
    Binding bindingExchangeCloudTrackingAction(Queue queueMessageCloudTrackingAction, Exchange exchangeCloud) {
        return BindingBuilder.bind(queueMessageCloudTrackingAction).to(exchangeCloud).with(routingKeyAction).noargs();
    }

    @Bean
    Binding bindingExchangeCloudTrackingCart(Queue queueMessageCloudTrackingCart, Exchange exchangeCloud) {
        return BindingBuilder.bind(queueMessageCloudTrackingCart).to(exchangeCloud).with(routingKeyCart).noargs();
    }

    @Bean
    Binding bindingExchangeCloudTrackingOrder(Queue queueMessageCloudTrackingOrder, Exchange exchangeCloud) {
        return BindingBuilder.bind(queueMessageCloudTrackingOrder).to(exchangeCloud).with(routingKeyOrder).noargs();
    }
}
