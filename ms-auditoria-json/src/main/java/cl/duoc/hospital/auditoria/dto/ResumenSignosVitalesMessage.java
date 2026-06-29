package cl.duoc.hospital.auditoria.dto;

import java.time.LocalDateTime;

public record ResumenSignosVitalesMessage(
        LocalDateTime fechaHora,
        long totalRegistros,
        long totalPacientes,
        Double promedioFrecuenciaCardiaca,
        Integer minimaFrecuenciaCardiaca,
        Integer maximaFrecuenciaCardiaca,
        Double promedioPresionSistolica,
        Integer minimaPresionSistolica,
        Integer maximaPresionSistolica,
        Double promedioPresionDiastolica,
        Integer minimaPresionDiastolica,
        Integer maximaPresionDiastolica,
        Double promedioSaturacionOxigeno,
        Double minimaSaturacionOxigeno,
        Double maximaSaturacionOxigeno,
        Double promedioTemperatura,
        Double minimaTemperatura,
        Double maximaTemperatura
) {
}
