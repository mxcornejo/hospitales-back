package cl.duoc.hospital.bff.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Convierte un Jwt en un AbstractAuthenticationToken manejando tanto tokens
 * locales
 * (donde los roles ya tienen el prefijo ROLE_) como tokens de Azure AD
 * (donde los roles vienen sin prefijo, ej. "Admin").
 */
public class HospitalJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final String aadIssuerPrefix;

    public HospitalJwtAuthenticationConverter(String aadTenantId) {
        this.aadIssuerPrefix = "https://login.microsoftonline.com/" + aadTenantId;
    }

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        Collection<GrantedAuthority> authorities = extractAuthorities(jwt);
        return new JwtAuthenticationToken(jwt, authorities, resolvePrincipalName(jwt));
    }

    private String resolvePrincipalName(Jwt jwt) {
        // Azure AD usa preferred_username o upn; tokens locales usan sub
        String preferredUsername = jwt.getClaimAsString("preferred_username");
        if (preferredUsername != null && !preferredUsername.isBlank()) {
            return preferredUsername;
        }
        String upn = jwt.getClaimAsString("upn");
        if (upn != null && !upn.isBlank()) {
            return upn;
        }
        return jwt.getSubject();
    }

    private Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
        boolean isAadToken = jwt.getIssuer() != null
                && jwt.getIssuer().toString().startsWith(aadIssuerPrefix);

        List<String> roles;
        Object rolesClaim = jwt.getClaim("roles");
        if (rolesClaim instanceof List<?> list) {
            roles = list.stream().map(Object::toString).collect(Collectors.toList());
        } else {
            roles = Collections.emptyList();
        }

        if (roles.isEmpty()) {
            return Collections.emptyList();
        }

        return roles.stream()
                .map(role -> {
                    // Tokens locales ya incluyen "ROLE_"; tokens AAD no
                    if (isAadToken && !role.startsWith("ROLE_")) {
                        return new SimpleGrantedAuthority("ROLE_" + role.toUpperCase());
                    }
                    return new SimpleGrantedAuthority(role);
                })
                .collect(Collectors.toList());
    }
}
