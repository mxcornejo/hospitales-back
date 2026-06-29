package cl.duoc.hospital.signosvitales.config;

import org.springframework.amqp.core.FanoutExchange;
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

    @Bean
    public FanoutExchange alertasFanoutExchange() {
        return new FanoutExchange(alertasExchange, true, false);
    }

    @Bean
    public FanoutExchange resumenesFanoutExchange() {
        return new FanoutExchange(resumenesExchange, true, false);
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
