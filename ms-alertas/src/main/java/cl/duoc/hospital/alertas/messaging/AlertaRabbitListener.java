package cl.duoc.hospital.alertas.messaging;

import cl.duoc.hospital.alertas.dto.AlertaMessage;
import cl.duoc.hospital.alertas.entity.Alerta;
import cl.duoc.hospital.alertas.service.AlertaService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class AlertaRabbitListener {

    private final AlertaService alertaService;

    public AlertaRabbitListener(AlertaService alertaService) {
        this.alertaService = alertaService;
    }

    @RabbitListener(queues = "${hospital.rabbit.alertas-db-queue}")
    public void recibirAlerta(AlertaMessage alertaMessage) {
        Alerta alerta = new Alerta();
        alerta.setPacienteId(alertaMessage.pacienteId());
        alerta.setTipo(alertaMessage.tipo());
        alerta.setSeveridad(alertaMessage.severidad());
        alerta.setDescripcion(alertaMessage.descripcion());
        alerta.setEstado(alertaMessage.estado() != null ? alertaMessage.estado() : "ACTIVA");
        alerta.setFechaHora(alertaMessage.fechaHora() != null ? alertaMessage.fechaHora() : LocalDateTime.now());
        alertaService.save(alerta);
    }
}
