package cl.duoc.hospital.auditoria.service;

import cl.duoc.hospital.auditoria.dto.AlertaMessage;
import cl.duoc.hospital.auditoria.dto.ResumenSignosVitalesMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
public class AuditoriaJsonService {

    private static final DateTimeFormatter FILE_TIMESTAMP = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

    private final ObjectMapper objectMapper;
    private final Path alertasOutputDir;
    private final Path resumenesOutputDir;

    public AuditoriaJsonService(
            ObjectMapper objectMapper,
            @Value("${hospital.alertas.output-dir}") String alertasOutputDir,
            @Value("${hospital.resumenes.output-dir}") String resumenesOutputDir) {
        this.objectMapper = objectMapper;
        this.alertasOutputDir = Path.of(alertasOutputDir);
        this.resumenesOutputDir = Path.of(resumenesOutputDir);
    }

    @RabbitListener(queues = "${hospital.rabbit.alertas-json-queue}")
    public void guardarAlerta(AlertaMessage alertaMessage) throws IOException {
        Files.createDirectories(alertasOutputDir);
        String paciente = alertaMessage.pacienteId() != null ? alertaMessage.pacienteId().toString() : "sin-paciente";
        String tipo = limpiar(alertaMessage.tipo() != null ? alertaMessage.tipo() : "alerta");
        Path archivo = alertasOutputDir.resolve(timestamp() + "-paciente-" + paciente + "-" + tipo + ".json");
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(archivo.toFile(), alertaMessage);
    }

    @RabbitListener(queues = "${hospital.rabbit.resumenes-json-queue}")
    public void guardarResumen(ResumenSignosVitalesMessage resumenMessage) throws IOException {
        Files.createDirectories(resumenesOutputDir);
        Path archivo = resumenesOutputDir.resolve(timestamp() + "-resumen-signos-vitales.json");
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(archivo.toFile(), resumenMessage);
    }

    private String timestamp() {
        return LocalDateTime.now().format(FILE_TIMESTAMP);
    }

    private String limpiar(String value) {
        String normalizado = Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT);
        return normalizado.replaceAll("[^a-z0-9-]", "-").replaceAll("-+", "-");
    }
}
