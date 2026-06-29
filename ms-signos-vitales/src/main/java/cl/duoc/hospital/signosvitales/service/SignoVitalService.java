package cl.duoc.hospital.signosvitales.service;

import cl.duoc.hospital.signosvitales.dto.AlertaMessage;
import cl.duoc.hospital.signosvitales.entity.SignoVital;
import cl.duoc.hospital.signosvitales.messaging.SignosVitalesPublisher;
import cl.duoc.hospital.signosvitales.repository.SignoVitalRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SignoVitalService {

    private final SignoVitalRepository signoVitalRepository;
    private final SignosVitalesPublisher signosVitalesPublisher;
    private final int frecuenciaCardiacaMin;
    private final int frecuenciaCardiacaMax;
    private final int presionSistolicaMin;
    private final int presionSistolicaMax;
    private final double saturacionOxigenoMin;
    private final double temperaturaMax;

    public SignoVitalService(
            SignoVitalRepository signoVitalRepository,
            SignosVitalesPublisher signosVitalesPublisher,
            @Value("${hospital.thresholds.frecuencia-cardiaca-min:${hospital.thresholds.frecuencia-cardicaca-min:60}}") int frecuenciaCardiacaMin,
            @Value("${hospital.thresholds.frecuencia-cardiaca-max:${hospital.thresholds.frecuencia-cardicaca-max:100}}") int frecuenciaCardiacaMax,
            @Value("${hospital.thresholds.presion-sistolica-min}") int presionSistolicaMin,
            @Value("${hospital.thresholds.presion-sistolica-max}") int presionSistolicaMax,
            @Value("${hospital.thresholds.saturacion-oxigeno-min}") double saturacionOxigenoMin,
            @Value("${hospital.thresholds.temperatura-max}") double temperaturaMax) {
        this.signoVitalRepository = signoVitalRepository;
        this.signosVitalesPublisher = signosVitalesPublisher;
        this.frecuenciaCardiacaMin = frecuenciaCardiacaMin;
        this.frecuenciaCardiacaMax = frecuenciaCardiacaMax;
        this.presionSistolicaMin = presionSistolicaMin;
        this.presionSistolicaMax = presionSistolicaMax;
        this.saturacionOxigenoMin = saturacionOxigenoMin;
        this.temperaturaMax = temperaturaMax;
    }

    public List<SignoVital> findAll() {
        return signoVitalRepository.findAll();
    }

    public SignoVital findById(Long id) {
        return signoVitalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Signo vital no encontrado con id: " + id));
    }

    public SignoVital save(SignoVital signoVital) {
        SignoVital signoVitalGuardado = signoVitalRepository.save(signoVital);
        publicarAlertasSiCorresponde(signoVitalGuardado);
        return signoVitalGuardado;
    }

    public SignoVital update(Long id, SignoVital signoVitalDetails) {
        SignoVital signoVital = findById(id);
        signoVital.setPacienteId(signoVitalDetails.getPacienteId());
        signoVital.setFrecuenciaCardiaca(signoVitalDetails.getFrecuenciaCardiaca());
        signoVital.setPresionSistolica(signoVitalDetails.getPresionSistolica());
        signoVital.setPresionDiastolica(signoVitalDetails.getPresionDiastolica());
        signoVital.setSaturacionOxigeno(signoVitalDetails.getSaturacionOxigeno());
        signoVital.setTemperatura(signoVitalDetails.getTemperatura());
        SignoVital signoVitalGuardado = signoVitalRepository.save(signoVital);
        publicarAlertasSiCorresponde(signoVitalGuardado);
        return signoVitalGuardado;
    }

    public void delete(Long id) {
        signoVitalRepository.deleteById(id);
    }

    public List<SignoVital> findByPacienteId(Long pacienteId) {
        return signoVitalRepository.findByPacienteIdOrderByFechaHoraDesc(pacienteId);
    }

    public List<SignoVital> findUltimos10ByPacienteId(Long pacienteId) {
        return signoVitalRepository.findTop10ByPacienteIdOrderByFechaHoraDesc(pacienteId);
    }

    private void publicarAlertasSiCorresponde(SignoVital signoVital) {
        if (signoVital.getFrecuenciaCardiaca() != null
                && signoVital.getFrecuenciaCardiaca() > frecuenciaCardiacaMax) {
            publicarAlerta(signoVital, "TAQUICARDIA", "ALTA",
                    "Frecuencia cardiaca sobre el rango normal: " + signoVital.getFrecuenciaCardiaca() + " lpm");
        }

        if (signoVital.getFrecuenciaCardiaca() != null
                && signoVital.getFrecuenciaCardiaca() < frecuenciaCardiacaMin) {
            publicarAlerta(signoVital, "BRADICARDIA", "ALTA",
                    "Frecuencia cardiaca bajo el rango normal: " + signoVital.getFrecuenciaCardiaca() + " lpm");
        }

        if (signoVital.getPresionSistolica() != null
                && signoVital.getPresionSistolica() > presionSistolicaMax) {
            publicarAlerta(signoVital, "HIPERTENSION", "MEDIA",
                    "Presion sistolica sobre el rango normal: " + signoVital.getPresionSistolica() + " mmHg");
        }

        if (signoVital.getPresionSistolica() != null
                && signoVital.getPresionSistolica() < presionSistolicaMin) {
            publicarAlerta(signoVital, "HIPOTENSION", "ALTA",
                    "Presion sistolica bajo el rango normal: " + signoVital.getPresionSistolica() + " mmHg");
        }

        if (signoVital.getSaturacionOxigeno() != null
                && signoVital.getSaturacionOxigeno() < saturacionOxigenoMin) {
            publicarAlerta(signoVital, "HIPOXIA", "ALTA",
                    "Saturacion de oxigeno bajo el rango normal: " + signoVital.getSaturacionOxigeno() + "%");
        }

        if (signoVital.getTemperatura() != null
                && signoVital.getTemperatura() >= temperaturaMax) {
            publicarAlerta(signoVital, "FIEBRE", "MEDIA",
                    "Temperatura sobre el rango normal: " + signoVital.getTemperatura() + " C");
        }
    }

    private void publicarAlerta(SignoVital signoVital, String tipo, String severidad, String descripcion) {
        LocalDateTime fechaHora = signoVital.getFechaHora() != null ? signoVital.getFechaHora() : LocalDateTime.now();
        AlertaMessage alertaMessage = new AlertaMessage(
                signoVital.getPacienteId(),
                tipo,
                severidad,
                descripcion,
                "ACTIVA",
                fechaHora,
                signoVital.getFrecuenciaCardiaca(),
                signoVital.getPresionSistolica(),
                signoVital.getPresionDiastolica(),
                signoVital.getSaturacionOxigeno(),
                signoVital.getTemperatura()
        );
        signosVitalesPublisher.publicarAlerta(alertaMessage);
    }
}
