package cl.duoc.hospital.procesador.dto;
import java.time.LocalDateTime;
public record SignoVitalEvent(String eventoId, Long pacienteId, Integer frecuenciaCardiaca,
        Integer presionSistolica, Integer presionDiastolica, Double temperatura, LocalDateTime fechaHora) {}
