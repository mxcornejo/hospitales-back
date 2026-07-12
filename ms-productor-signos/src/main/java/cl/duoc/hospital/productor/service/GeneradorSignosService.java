package cl.duoc.hospital.productor.service;

import cl.duoc.hospital.productor.dto.SignoVitalEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class GeneradorSignosService {
    private static final Logger log = LoggerFactory.getLogger(GeneradorSignosService.class);
    private final KafkaTemplate<String, SignoVitalEvent> kafkaTemplate;
    private final String topic;
    private final List<Long> pacientes;
    private final int anomaliaCada;
    private final Random random = new Random();
    private final AtomicLong secuencia = new AtomicLong();

    public GeneradorSignosService(KafkaTemplate<String, SignoVitalEvent> kafkaTemplate,
            @Value("${hospital.kafka.topic-signos}") String topic,
            @Value("${hospital.signos.pacientes}") List<Long> pacientes,
            @Value("${hospital.signos.anomalia-cada}") int anomaliaCada) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
        this.pacientes = pacientes;
        this.anomaliaCada = Math.max(1, anomaliaCada);
    }

    @Scheduled(fixedRateString = "${hospital.signos.fixed-rate-ms}")
    public void generar() {
        long numero = secuencia.incrementAndGet();
        boolean anomala = numero % anomaliaCada == 0;
        Long pacienteId = pacientes.get((int) ((numero - 1) % pacientes.size()));
        SignoVitalEvent evento = new SignoVitalEvent(UUID.randomUUID().toString(), pacienteId,
                anomala ? 125 + random.nextInt(20) : 65 + random.nextInt(31),
                anomala ? 155 + random.nextInt(25) : 105 + random.nextInt(31),
                anomala ? 100 + random.nextInt(15) : 65 + random.nextInt(21),
                anomala ? 38.6 + random.nextDouble() : 36.2 + random.nextDouble() * 1.2,
                LocalDateTime.now());
        kafkaTemplate.send(topic, pacienteId.toString(), evento);
        log.info("Señal {} publicada para paciente {} (anómala={})", evento.eventoId(), pacienteId, anomala);
    }
}
