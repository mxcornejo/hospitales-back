package cl.duoc.hospital.bff.config;

import cl.duoc.hospital.bff.entity.Usuario;
import cl.duoc.hospital.bff.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder) {
        return args -> {
            if (!usuarioRepository.existsByUsername("admin")) {
                Usuario admin = new Usuario();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setRol("ADMIN");
                admin.setActivo(true);
                usuarioRepository.save(admin);
            }
            if (!usuarioRepository.existsByUsername("medico")) {
                Usuario medico = new Usuario();
                medico.setUsername("medico");
                medico.setPassword(passwordEncoder.encode("medico123"));
                medico.setRol("MEDICO");
                medico.setActivo(true);
                usuarioRepository.save(medico);
            }
            if (!usuarioRepository.existsByUsername("enfermera")) {
                Usuario enfermera = new Usuario();
                enfermera.setUsername("enfermera");
                enfermera.setPassword(passwordEncoder.encode("enfermera123"));
                enfermera.setRol("ENFERMERA");
                enfermera.setActivo(true);
                usuarioRepository.save(enfermera);
            }
        };
    }
}
