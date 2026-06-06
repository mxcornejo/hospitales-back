package cl.duoc.hospital.bff.controller;

import cl.duoc.hospital.bff.dto.ApiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/alertas")
@CrossOrigin(origins = "*")
public class AlertaController {

    private final RestTemplate restTemplate;

    @Value("${ms.alertas.url}")
    private String msAlertasUrl;

    public AlertaController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping
    public ResponseEntity<?> getAll() {
        return proxied(restTemplate.getForEntity(msAlertasUrl + "/alertas", Object.class));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return proxied(restTemplate.getForEntity(msAlertasUrl + "/alertas/" + id, Object.class));
    }

    @GetMapping("/paciente/{pacienteId}")
    public ResponseEntity<?> getByPaciente(@PathVariable Long pacienteId) {
        return proxied(restTemplate.getForEntity(
                msAlertasUrl + "/alertas/paciente/" + pacienteId, Object.class));
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<?> getByEstado(@PathVariable String estado) {
        return proxied(restTemplate.getForEntity(
                msAlertasUrl + "/alertas/estado/" + estado, Object.class));
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Object alerta) {
        return proxied(restTemplate.postForEntity(msAlertasUrl + "/alertas", alerta, Object.class));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Object alerta) {
        HttpEntity<Object> entity = new HttpEntity<>(alerta, jsonHeaders());
        return proxied(restTemplate.exchange(msAlertasUrl + "/alertas/" + id,
                HttpMethod.PUT, entity, Object.class));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        restTemplate.delete(msAlertasUrl + "/alertas/" + id);
        return ResponseEntity.ok(ApiResponse.ok("Alerta eliminada correctamente", null));
    }

    private HttpHeaders jsonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    private ResponseEntity<?> proxied(ResponseEntity<?> response) {
        return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
    }
}
