package cl.duoc.hospital.pacientes.repository;

import cl.duoc.hospital.pacientes.entity.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PacienteRepository extends JpaRepository<Paciente, Long> {
    Optional<Paciente> findByRut(String rut);

    List<Paciente> findByEstado(String estado);
}
