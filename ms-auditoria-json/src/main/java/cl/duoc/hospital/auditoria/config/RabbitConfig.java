package cl.duoc.hospital.auditoria.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Value("${hospital.rabbit.alertas-exchange}")
    private String alertasExchange;

    @Value("${hospital.rabbit.resumenes-exchange}")
    private String resumenesExchange;

    @Value("${hospital.rabbit.alertas-json-queue}")
    private String alertasJsonQueue;

    @Value("${hospital.rabbit.resumenes-json-queue}")
    private String resumenesJsonQueue;

    @Bean
    public FanoutExchange alertasFanoutExchange() {
        return new FanoutExchange(alertasExchange, true, false);
    }

    @Bean
    public FanoutExchange resumenesFanoutExchange() {
        return new FanoutExchange(resumenesExchange, true, false);
    }

    @Bean
    public Queue alertasJsonQueue() {
        return new Queue(alertasJsonQueue, true);
    }

    @Bean
    public Queue resumenesJsonQueue() {
        return new Queue(resumenesJsonQueue, true);
    }

    @Bean
    public Binding alertasJsonBinding(FanoutExchange alertasFanoutExchange, Queue alertasJsonQueue) {
        return BindingBuilder.bind(alertasJsonQueue).to(alertasFanoutExchange);
    }

    @Bean
    public Binding resumenesJsonBinding(FanoutExchange resumenesFanoutExchange, Queue resumenesJsonQueue) {
        return BindingBuilder.bind(resumenesJsonQueue).to(resumenesFanoutExchange);
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        return rabbitTemplate;
    }
}
