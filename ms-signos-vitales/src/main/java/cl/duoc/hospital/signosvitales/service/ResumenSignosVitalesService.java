package cl.duoc.hospital.signosvitales.service;

import cl.duoc.hospital.signosvitales.dto.ResumenSignosVitalesMessage;
import cl.duoc.hospital.signosvitales.entity.SignoVital;
import cl.duoc.hospital.signosvitales.messaging.SignosVitalesPublisher;
import cl.duoc.hospital.signosvitales.repository.SignoVitalRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.DoubleSummaryStatistics;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Objects;

@Service
public class ResumenSignosVitalesService {

    private final SignoVitalRepository signoVitalRepository;
    private final SignosVitalesPublisher signosVitalesPublisher;

    public ResumenSignosVitalesService(SignoVitalRepository signoVitalRepository,
                                       SignosVitalesPublisher signosVitalesPublisher) {
        this.signoVitalRepository = signoVitalRepository;
        this.signosVitalesPublisher = signosVitalesPublisher;
    }

    @Scheduled(fixedRateString = "${hospital.resumenes.fixed-rate-ms}")
    public void publicarResumenPeriodico() {
        List<SignoVital> signosVitales = signoVitalRepository.findAll();
        ResumenSignosVitalesMessage resumen = construirResumen(signosVitales);
        signosVitalesPublisher.publicarResumen(resumen);
    }

    private ResumenSignosVitalesMessage construirResumen(List<SignoVital> signosVitales) {
        IntSummaryStatistics frecuenciaCardiaca = signosVitales.stream()
                .map(SignoVital::getFrecuenciaCardiaca)
                .filter(Objects::nonNull)
                .mapToInt(Integer::intValue)
                .summaryStatistics();

        IntSummaryStatistics presionSistolica = signosVitales.stream()
                .map(SignoVital::getPresionSistolica)
                .filter(Objects::nonNull)
                .mapToInt(Integer::intValue)
                .summaryStatistics();

        IntSummaryStatistics presionDiastolica = signosVitales.stream()
                .map(SignoVital::getPresionDiastolica)
                .filter(Objects::nonNull)
                .mapToInt(Integer::intValue)
                .summaryStatistics();

        DoubleSummaryStatistics saturacionOxigeno = signosVitales.stream()
                .map(SignoVital::getSaturacionOxigeno)
                .filter(Objects::nonNull)
                .mapToDouble(Double::doubleValue)
                .summaryStatistics();

        DoubleSummaryStatistics temperatura = signosVitales.stream()
                .map(SignoVital::getTemperatura)
                .filter(Objects::nonNull)
                .mapToDouble(Double::doubleValue)
                .summaryStatistics();

        long totalPacientes = signosVitales.stream()
                .map(SignoVital::getPacienteId)
                .filter(Objects::nonNull)
                .distinct()
                .count();

        return new ResumenSignosVitalesMessage(
                LocalDateTime.now(),
                signosVitales.size(),
                totalPacientes,
                promedioInt(frecuenciaCardiaca),
                minimoInt(frecuenciaCardiaca),
                maximoInt(frecuenciaCardiaca),
                promedioInt(presionSistolica),
                minimoInt(presionSistolica),
                maximoInt(presionSistolica),
                promedioInt(presionDiastolica),
                minimoInt(presionDiastolica),
                maximoInt(presionDiastolica),
                promedioDouble(saturacionOxigeno),
                minimoDouble(saturacionOxigeno),
                maximoDouble(saturacionOxigeno),
                promedioDouble(temperatura),
                minimoDouble(temperatura),
                maximoDouble(temperatura)
        );
    }

    private Double promedioInt(IntSummaryStatistics statistics) {
        return statistics.getCount() == 0 ? null : statistics.getAverage();
    }

    private Integer minimoInt(IntSummaryStatistics statistics) {
        return statistics.getCount() == 0 ? null : statistics.getMin();
    }

    private Integer maximoInt(IntSummaryStatistics statistics) {
        return statistics.getCount() == 0 ? null : statistics.getMax();
    }

    private Double promedioDouble(DoubleSummaryStatistics statistics) {
        return statistics.getCount() == 0 ? null : statistics.getAverage();
    }

    private Double minimoDouble(DoubleSummaryStatistics statistics) {
        return statistics.getCount() == 0 ? null : statistics.getMin();
    }

    private Double maximoDouble(DoubleSummaryStatistics statistics) {
        return statistics.getCount() == 0 ? null : statistics.getMax();
    }
}
