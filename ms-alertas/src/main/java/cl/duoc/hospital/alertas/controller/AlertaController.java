package cl.duoc.hospital.alertas.controller;

import cl.duoc.hospital.alertas.entity.Alerta;
import cl.duoc.hospital.alertas.service.AlertaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/alertas")
public class AlertaController {

    private final AlertaService alertaService;

    public AlertaController(AlertaService alertaService) {
        this.alertaService = alertaService;
    }

    @GetMapping
    public ResponseEntity<List<Alerta>> getAll() {
        return ResponseEntity.ok(alertaService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Alerta> getById(@PathVariable Long id) {
        return ResponseEntity.ok(alertaService.findById(id));
    }

    @GetMapping("/paciente/{pacienteId}")
    public ResponseEntity<List<Alerta>> getByPacienteId(@PathVariable Long pacienteId) {
        return ResponseEntity.ok(alertaService.findByPacienteId(pacienteId));
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Alerta>> getByEstado(@PathVariable String estado) {
        return ResponseEntity.ok(alertaService.findByEstado(estado));
    }

    @GetMapping("/severidad/{severidad}")
    public ResponseEntity<List<Alerta>> getBySeveridad(@PathVariable String severidad) {
        return ResponseEntity.ok(alertaService.findBySeveridad(severidad));
    }

    @PostMapping
    public ResponseEntity<Alerta> create(@RequestBody Alerta alerta) {
        return ResponseEntity.status(HttpStatus.CREATED).body(alertaService.save(alerta));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Alerta> update(@PathVariable Long id, @RequestBody Alerta alerta) {
        return ResponseEntity.ok(alertaService.update(id, alerta));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        alertaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
