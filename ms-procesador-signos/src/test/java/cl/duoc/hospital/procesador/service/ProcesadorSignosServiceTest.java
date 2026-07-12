package cl.duoc.hospital.procesador.service;

import cl.duoc.hospital.procesador.dto.AlertaKafkaMessage;
import cl.duoc.hospital.procesador.dto.SignoVitalEvent;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class ProcesadorSignosServiceTest {
    private final ProcesadorSignosService service = new ProcesadorSignosService(
            mock(KafkaTemplate.class), "alertas", 60, 100, 90, 140, 60, 90, 35.5, 38.0);

    @Test
    void noDetectaAnomaliasEnLecturaNormal() {
        var evento = new SignoVitalEvent("e1", 1L, 80, 120, 80, 36.8, LocalDateTime.now());
        assertThat(service.detectar(evento)).isEmpty();
    }

    @Test
    void detectaMultiplesAnomalias() {
        var evento = new SignoVitalEvent("e2", 1L, 130, 165, 105, 39.0, LocalDateTime.now());
        assertThat(service.detectar(evento)).containsExactly("TAQUICARDIA", "HIPERTENSION", "FIEBRE");
    }
}
