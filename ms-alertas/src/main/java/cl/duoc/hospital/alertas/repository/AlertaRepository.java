package cl.duoc.hospital.alertas.repository;

import cl.duoc.hospital.alertas.entity.Alerta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertaRepository extends JpaRepository<Alerta, Long> {
    List<Alerta> findByPacienteIdOrderByFechaHoraDesc(Long pacienteId);

    List<Alerta> findByEstado(String estado);

    List<Alerta> findBySeveridad(String severidad);
}
