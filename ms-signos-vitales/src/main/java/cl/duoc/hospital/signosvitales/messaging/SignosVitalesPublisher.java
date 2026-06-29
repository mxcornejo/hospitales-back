package cl.duoc.hospital.signosvitales.messaging;

import cl.duoc.hospital.signosvitales.dto.AlertaMessage;
import cl.duoc.hospital.signosvitales.dto.ResumenSignosVitalesMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SignosVitalesPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final String alertasExchange;
    private final String resumenesExchange;

    public SignosVitalesPublisher(
            RabbitTemplate rabbitTemplate,
            @Value("${hospital.rabbit.alertas-exchange}") String alertasExchange,
            @Value("${hospital.rabbit.resumenes-exchange}") String resumenesExchange) {
        this.rabbitTemplate = rabbitTemplate;
        this.alertasExchange = alertasExchange;
        this.resumenesExchange = resumenesExchange;
    }

    public void publicarAlerta(AlertaMessage alertaMessage) {
        rabbitTemplate.convertAndSend(alertasExchange, "", alertaMessage);
    }

    public void publicarResumen(ResumenSignosVitalesMessage resumenMessage) {
        rabbitTemplate.convertAndSend(resumenesExchange, "", resumenMessage);
    }
}
