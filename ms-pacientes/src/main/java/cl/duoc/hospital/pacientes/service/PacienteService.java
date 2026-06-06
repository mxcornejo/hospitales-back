package cl.duoc.hospital.pacientes.service;

import cl.duoc.hospital.pacientes.entity.Paciente;
import cl.duoc.hospital.pacientes.repository.PacienteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PacienteService {

    private final PacienteRepository pacienteRepository;

    public PacienteService(PacienteRepository pacienteRepository) {
        this.pacienteRepository = pacienteRepository;
    }

    public List<Paciente> findAll() {
        return pacienteRepository.findAll();
    }

    public Paciente findById(Long id) {
        return pacienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado con id: " + id));
    }

    public Paciente save(Paciente paciente) {
        return pacienteRepository.save(paciente);
    }

    public Paciente update(Long id, Paciente pacienteDetails) {
        Paciente paciente = findById(id);
        paciente.setNombre(pacienteDetails.getNombre());
        paciente.setApellido(pacienteDetails.getApellido());
        paciente.setEdad(pacienteDetails.getEdad());
        paciente.setHabitacion(pacienteDetails.getHabitacion());
        paciente.setDiagnostico(pacienteDetails.getDiagnostico());
        paciente.setEstado(pacienteDetails.getEstado());
        return pacienteRepository.save(paciente);
    }

    public void delete(Long id) {
        pacienteRepository.deleteById(id);
    }

    public List<Paciente> findByEstado(String estado) {
        return pacienteRepository.findByEstado(estado);
    }
}
