package cl.duoc.hospital.procesador.dto;
import java.time.LocalDateTime;
import java.util.List;
public record AlertaKafkaMessage(String eventoId, Long pacienteId, Integer frecuenciaCardiaca,
        Integer presionSistolica, Integer presionDiastolica, Double temperatura, List<String> anomalias,
        String tipo, String severidad, String descripcion, LocalDateTime fechaHora) {}
