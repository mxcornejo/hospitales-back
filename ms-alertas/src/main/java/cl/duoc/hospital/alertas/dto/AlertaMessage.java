package cl.duoc.hospital.alertas.dto;

import java.time.LocalDateTime;

public record AlertaMessage(
        Long pacienteId,
        String tipo,
        String severidad,
        String descripcion,
        String estado,
        LocalDateTime fechaHora,
        Integer frecuenciaCardiaca,
        Integer presionSistolica,
        Integer presionDiastolica,
        Double saturacionOxigeno,
        Double temperatura
) {
}
