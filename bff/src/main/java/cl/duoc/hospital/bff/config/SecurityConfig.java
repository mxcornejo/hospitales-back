package cl.duoc.hospital.bff.config;

import cl.duoc.hospital.bff.security.DualJwtDecoder;
import cl.duoc.hospital.bff.security.HospitalJwtAuthenticationConverter;
import cl.duoc.hospital.bff.security.JwtAuthEntryPoint;
import cl.duoc.hospital.bff.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${azure.activedirectory.tenant-id}")
    private String aadTenantId;

    @Value("${azure.activedirectory.b2c-jwk-set-uri}")
    private String b2cJwkSetUri;

    @Value("${app.cors.allowed-origins:http://localhost:4200,http://127.0.0.1:4200}")
    private String allowedOrigins;

    private final JwtAuthEntryPoint authEntryPoint;
    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(JwtAuthEntryPoint authEntryPoint,
            CustomUserDetailsService userDetailsService) {
        this.authEntryPoint = authEntryPoint;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Decodificador para tokens locales firmados con HMAC-SHA256.
     */
    @Bean
    public JwtDecoder localJwtDecoder() {
        byte[] keyBytes = Base64.getDecoder().decode(jwtSecret);
        SecretKey secretKey = new SecretKeySpec(keyBytes, "HmacSHA256");
        return NimbusJwtDecoder.withSecretKey(secretKey)
                .macAlgorithm(MacAlgorithm.HS384)
                .build();
    }

    /**
     * Decodificador para tokens de Azure AD usando los JWKS públicos del tenant.
     * La URI se construye con el tenant-id configurado. La descarga de claves
     * es lazy (se realiza al validar el primer token AAD).
     */
    @Bean
    public List<JwtDecoder> aadJwtDecoders() {
        Set<String> tenants = new LinkedHashSet<>();
        tenants.add(aadTenantId);
        tenants.add("common");
        tenants.add("organizations");
        tenants.add("consumers");

        List<JwtDecoder> decoders = new ArrayList<>();
        decoders.add(NimbusJwtDecoder.withJwkSetUri(b2cJwkSetUri).build());
        for (String tenant : tenants) {
            String jwkSetUri = "https://login.microsoftonline.com/" + tenant + "/discovery/v2.0/keys";
            decoders.add(NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build());
        }
        return decoders;
    }

    /**
     * Decodificador combinado: intenta validar el token como local y, si falla,
     * lo valida contra los JWKS de Azure AD.
     */
    @Bean
    public JwtDecoder jwtDecoder() {
        return new DualJwtDecoder(localJwtDecoder(), aadJwtDecoders());
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(splitCsv(allowedOrigins));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept", "Origin"));
        configuration.setExposedHeaders(List.of("Authorization"));
        configuration.setAllowCredentials(false);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> ex.authenticationEntryPoint(authEntryPoint))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/api/auth/**", "/api/test/**", "/error").permitAll()
                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .decoder(jwtDecoder())
                                .jwtAuthenticationConverter(
                                        new HospitalJwtAuthenticationConverter(aadTenantId)))
                        .authenticationEntryPoint(authEntryPoint));

        http.authenticationProvider(authenticationProvider());

        return http.build();
    }

    private List<String> splitCsv(String value) {
        return List.of(value.split(",")).stream()
                .map(String::trim)
                .filter(origin -> !origin.isBlank())
                .toList();
    }
}
