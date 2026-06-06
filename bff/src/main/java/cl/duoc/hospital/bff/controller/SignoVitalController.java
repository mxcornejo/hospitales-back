package cl.duoc.hospital.bff.controller;

import cl.duoc.hospital.bff.dto.ApiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/signos-vitales")
@CrossOrigin(origins = "*")
public class SignoVitalController {

    private final RestTemplate restTemplate;

    @Value("${ms.signos-vitales.url}")
    private String msSignosVitalesUrl;

    public SignoVitalController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping
    public ResponseEntity<?> getAll() {
        return proxied(restTemplate.getForEntity(msSignosVitalesUrl + "/signos-vitales", Object.class));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return proxied(restTemplate.getForEntity(msSignosVitalesUrl + "/signos-vitales/" + id, Object.class));
    }

    @GetMapping("/paciente/{pacienteId}")
    public ResponseEntity<?> getByPaciente(@PathVariable Long pacienteId) {
        return proxied(restTemplate.getForEntity(
                msSignosVitalesUrl + "/signos-vitales/paciente/" + pacienteId, Object.class));
    }

    @GetMapping("/paciente/{pacienteId}/ultimos")
    public ResponseEntity<?> getUltimos(@PathVariable Long pacienteId) {
        return proxied(restTemplate.getForEntity(
                msSignosVitalesUrl + "/signos-vitales/paciente/" + pacienteId + "/ultimos", Object.class));
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Object signoVital) {
        return proxied(restTemplate.postForEntity(msSignosVitalesUrl + "/signos-vitales", signoVital, Object.class));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Object signoVital) {
        HttpEntity<Object> entity = new HttpEntity<>(signoVital, jsonHeaders());
        return proxied(restTemplate.exchange(msSignosVitalesUrl + "/signos-vitales/" + id,
                HttpMethod.PUT, entity, Object.class));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        restTemplate.delete(msSignosVitalesUrl + "/signos-vitales/" + id);
        return ResponseEntity.ok(ApiResponse.ok("Signo vital eliminado correctamente", null));
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
