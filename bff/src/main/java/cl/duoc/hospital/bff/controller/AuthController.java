package cl.duoc.hospital.bff.controller;

import cl.duoc.hospital.bff.dto.LoginRequest;
import cl.duoc.hospital.bff.dto.LoginResponse;
import cl.duoc.hospital.bff.entity.Usuario;
import cl.duoc.hospital.bff.repository.UsuarioRepository;
import cl.duoc.hospital.bff.security.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UsuarioRepository usuarioRepository;

    public AuthController(AuthenticationManager authenticationManager,
            JwtUtil jwtUtil,
            UsuarioRepository usuarioRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.usuarioRepository = usuarioRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()));
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtUtil.generateToken(userDetails);
        String rol = usuarioRepository.findByUsername(userDetails.getUsername())
                .map(Usuario::getRol)
                .orElse("USER");
        return ResponseEntity.ok(new LoginResponse(token, userDetails.getUsername(), rol));
    }
}
