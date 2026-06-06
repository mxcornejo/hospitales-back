package cl.duoc.hospital.bff.controller;

import cl.duoc.hospital.bff.dto.ApiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/pacientes")
@CrossOrigin(origins = "*")
public class PacienteController {

    private final RestTemplate restTemplate;

    @Value("${ms.pacientes.url}")
    private String msPacientesUrl;

    public PacienteController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping
    public ResponseEntity<?> getAll() {
        return proxied(restTemplate.getForEntity(msPacientesUrl + "/pacientes", Object.class));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return proxied(restTemplate.getForEntity(msPacientesUrl + "/pacientes/" + id, Object.class));
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<?> getByEstado(@PathVariable String estado) {
        return proxied(restTemplate.getForEntity(msPacientesUrl + "/pacientes/estado/" + estado, Object.class));
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Object paciente) {
        return proxied(restTemplate.postForEntity(msPacientesUrl + "/pacientes", paciente, Object.class));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Object paciente) {
        HttpEntity<Object> entity = new HttpEntity<>(paciente, jsonHeaders());
        return proxied(restTemplate.exchange(msPacientesUrl + "/pacientes/" + id,
                HttpMethod.PUT, entity, Object.class));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        restTemplate.delete(msPacientesUrl + "/pacientes/" + id);
        return ResponseEntity.ok(ApiResponse.ok("Paciente eliminado correctamente", null));
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
