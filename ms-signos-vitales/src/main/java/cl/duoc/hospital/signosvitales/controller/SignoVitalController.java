package cl.duoc.hospital.signosvitales.controller;

import cl.duoc.hospital.signosvitales.entity.SignoVital;
import cl.duoc.hospital.signosvitales.service.SignoVitalService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/signos-vitales")
public class SignoVitalController {

    private final SignoVitalService signoVitalService;

    public SignoVitalController(SignoVitalService signoVitalService) {
        this.signoVitalService = signoVitalService;
    }

    @GetMapping
    public ResponseEntity<List<SignoVital>> getAll() {
        return ResponseEntity.ok(signoVitalService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SignoVital> getById(@PathVariable Long id) {
        return ResponseEntity.ok(signoVitalService.findById(id));
    }

    @GetMapping("/paciente/{pacienteId}")
    public ResponseEntity<List<SignoVital>> getByPacienteId(@PathVariable Long pacienteId) {
        return ResponseEntity.ok(signoVitalService.findByPacienteId(pacienteId));
    }

    @GetMapping("/paciente/{pacienteId}/ultimos")
    public ResponseEntity<List<SignoVital>> getUltimos10(@PathVariable Long pacienteId) {
        return ResponseEntity.ok(signoVitalService.findUltimos10ByPacienteId(pacienteId));
    }

    @PostMapping
    public ResponseEntity<SignoVital> create(@RequestBody SignoVital signoVital) {
        return ResponseEntity.status(HttpStatus.CREATED).body(signoVitalService.save(signoVital));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SignoVital> update(@PathVariable Long id, @RequestBody SignoVital signoVital) {
        return ResponseEntity.ok(signoVitalService.update(id, signoVital));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        signoVitalService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
