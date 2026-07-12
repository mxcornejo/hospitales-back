package cl.duoc.hospital.alertas.messaging;

import cl.duoc.hospital.alertas.dto.AlertaKafkaMessage;
import cl.duoc.hospital.alertas.entity.Alerta;
import cl.duoc.hospital.alertas.repository.AlertaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AlertaKafkaListener {

    private static final Logger log = LoggerFactory.getLogger(AlertaKafkaListener.class);
    private final AlertaRepository alertaRepository;

    public AlertaKafkaListener(AlertaRepository alertaRepository) {
        this.alertaRepository = alertaRepository;
    }

    @KafkaListener(topics = "${hospital.kafka.topic-alertas}")
    @Transactional
    public void consumir(AlertaKafkaMessage mensaje) {
        if (mensaje.eventoId() == null || mensaje.pacienteId() == null) {
            throw new IllegalArgumentException("Alerta Kafka sin eventoId o pacienteId");
        }
        if (alertaRepository.findByEventoId(mensaje.eventoId()).isPresent()) {
            log.info("Alerta Kafka duplicada ignorada: {}", mensaje.eventoId());
            return;
        }

        Alerta alerta = new Alerta();
        alerta.setEventoId(mensaje.eventoId());
        alerta.setPacienteId(mensaje.pacienteId());
        alerta.setTipo(mensaje.tipo());
        alerta.setSeveridad(mensaje.severidad());
        alerta.setDescripcion(mensaje.descripcion());
        alerta.setEstado("ACTIVA");
        alerta.setFechaHora(mensaje.fechaHora());
        alertaRepository.save(alerta);
        log.info("Alerta Kafka {} guardada para paciente {}", mensaje.eventoId(), mensaje.pacienteId());
    }
}
