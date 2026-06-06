package cl.duoc.hospital.signosvitales.service;

import cl.duoc.hospital.signosvitales.entity.SignoVital;
import cl.duoc.hospital.signosvitales.repository.SignoVitalRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SignoVitalService {

    private final SignoVitalRepository signoVitalRepository;

    public SignoVitalService(SignoVitalRepository signoVitalRepository) {
        this.signoVitalRepository = signoVitalRepository;
    }

    public List<SignoVital> findAll() {
        return signoVitalRepository.findAll();
    }

    public SignoVital findById(Long id) {
        return signoVitalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Signo vital no encontrado con id: " + id));
    }

    public SignoVital save(SignoVital signoVital) {
        return signoVitalRepository.save(signoVital);
    }

    public SignoVital update(Long id, SignoVital signoVitalDetails) {
        SignoVital signoVital = findById(id);
        signoVital.setPacienteId(signoVitalDetails.getPacienteId());
        signoVital.setFrecuenciaCardiaca(signoVitalDetails.getFrecuenciaCardiaca());
        signoVital.setPresionSistolica(signoVitalDetails.getPresionSistolica());
        signoVital.setPresionDiastolica(signoVitalDetails.getPresionDiastolica());
        signoVital.setSaturacionOxigeno(signoVitalDetails.getSaturacionOxigeno());
        signoVital.setTemperatura(signoVitalDetails.getTemperatura());
        return signoVitalRepository.save(signoVital);
    }

    public void delete(Long id) {
        signoVitalRepository.deleteById(id);
    }

    public List<SignoVital> findByPacienteId(Long pacienteId) {
        return signoVitalRepository.findByPacienteIdOrderByFechaHoraDesc(pacienteId);
    }

    public List<SignoVital> findUltimos10ByPacienteId(Long pacienteId) {
        return signoVitalRepository.findTop10ByPacienteIdOrderByFechaHoraDesc(pacienteId);
    }
}
