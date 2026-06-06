package cl.duoc.hospital.alertas.service;

import cl.duoc.hospital.alertas.entity.Alerta;
import cl.duoc.hospital.alertas.repository.AlertaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlertaService {

    private final AlertaRepository alertaRepository;

    public AlertaService(AlertaRepository alertaRepository) {
        this.alertaRepository = alertaRepository;
    }

    public List<Alerta> findAll() {
        return alertaRepository.findAll();
    }

    public Alerta findById(Long id) {
        return alertaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Alerta no encontrada con id: " + id));
    }

    public Alerta save(Alerta alerta) {
        return alertaRepository.save(alerta);
    }

    public Alerta update(Long id, Alerta alertaDetails) {
        Alerta alerta = findById(id);
        alerta.setPacienteId(alertaDetails.getPacienteId());
        alerta.setTipo(alertaDetails.getTipo());
        alerta.setSeveridad(alertaDetails.getSeveridad());
        alerta.setDescripcion(alertaDetails.getDescripcion());
        alerta.setEstado(alertaDetails.getEstado());
        return alertaRepository.save(alerta);
    }

    public void delete(Long id) {
        alertaRepository.deleteById(id);
    }

    public List<Alerta> findByPacienteId(Long pacienteId) {
        return alertaRepository.findByPacienteIdOrderByFechaHoraDesc(pacienteId);
    }

    public List<Alerta> findByEstado(String estado) {
        return alertaRepository.findByEstado(estado);
    }

    public List<Alerta> findBySeveridad(String severidad) {
        return alertaRepository.findBySeveridad(severidad);
    }
}
