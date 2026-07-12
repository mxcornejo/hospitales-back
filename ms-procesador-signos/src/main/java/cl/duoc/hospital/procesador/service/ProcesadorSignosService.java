package cl.duoc.hospital.procesador.service;

import cl.duoc.hospital.procesador.dto.AlertaKafkaMessage;
import cl.duoc.hospital.procesador.dto.SignoVitalEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProcesadorSignosService {
    private static final Logger log = LoggerFactory.getLogger(ProcesadorSignosService.class);
    private final KafkaTemplate<String, AlertaKafkaMessage> kafkaTemplate;
    private final String topicAlertas;
    private final int fcMin, fcMax, psMin, psMax, pdMin, pdMax;
    private final double tempMin, tempMax;

    public ProcesadorSignosService(KafkaTemplate<String, AlertaKafkaMessage> kafkaTemplate,
            @Value("${hospital.kafka.topic-alertas}") String topicAlertas,
            @Value("${hospital.threshold.fc-min}") int fcMin, @Value("${hospital.threshold.fc-max}") int fcMax,
            @Value("${hospital.threshold.ps-min}") int psMin, @Value("${hospital.threshold.ps-max}") int psMax,
            @Value("${hospital.threshold.pd-min}") int pdMin, @Value("${hospital.threshold.pd-max}") int pdMax,
            @Value("${hospital.threshold.temp-min}") double tempMin, @Value("${hospital.threshold.temp-max}") double tempMax) {
        this.kafkaTemplate = kafkaTemplate; this.topicAlertas = topicAlertas;
        this.fcMin = fcMin; this.fcMax = fcMax; this.psMin = psMin; this.psMax = psMax;
        this.pdMin = pdMin; this.pdMax = pdMax; this.tempMin = tempMin; this.tempMax = tempMax;
    }

    @KafkaListener(topics = "${hospital.kafka.topic-signos}")
    public void procesar(SignoVitalEvent evento) {
        List<String> anomalias = detectar(evento);
        if (anomalias.isEmpty()) {
            log.info("Señal {} normal", evento.eventoId());
            return;
        }
        String severidad = anomalias.size() >= 2 ? "CRITICA" : "ALTA";
        AlertaKafkaMessage alerta = new AlertaKafkaMessage(evento.eventoId(), evento.pacienteId(),
                evento.frecuenciaCardiaca(), evento.presionSistolica(), evento.presionDiastolica(),
                evento.temperatura(), anomalias, anomalias.get(0), severidad,
                String.join("; ", anomalias), evento.fechaHora());
        kafkaTemplate.send(topicAlertas, evento.pacienteId().toString(), alerta);
        log.warn("Alerta {} publicada: {}", evento.eventoId(), alerta.descripcion());
    }

    List<String> detectar(SignoVitalEvent e) {
        List<String> resultado = new ArrayList<>();
        if (e.frecuenciaCardiaca() < fcMin) resultado.add("BRADICARDIA");
        if (e.frecuenciaCardiaca() > fcMax) resultado.add("TAQUICARDIA");
        if (e.presionSistolica() < psMin || e.presionDiastolica() < pdMin) resultado.add("HIPOTENSION");
        if (e.presionSistolica() > psMax || e.presionDiastolica() > pdMax) resultado.add("HIPERTENSION");
        if (e.temperatura() < tempMin) resultado.add("HIPOTERMIA");
        if (e.temperatura() > tempMax) resultado.add("FIEBRE");
        return resultado;
    }
}
