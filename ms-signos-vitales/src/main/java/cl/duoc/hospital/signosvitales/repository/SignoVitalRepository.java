package cl.duoc.hospital.signosvitales.repository;

import cl.duoc.hospital.signosvitales.entity.SignoVital;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SignoVitalRepository extends JpaRepository<SignoVital, Long> {
    List<SignoVital> findByPacienteIdOrderByFechaHoraDesc(Long pacienteId);

    List<SignoVital> findTop10ByPacienteIdOrderByFechaHoraDesc(Long pacienteId);
}
