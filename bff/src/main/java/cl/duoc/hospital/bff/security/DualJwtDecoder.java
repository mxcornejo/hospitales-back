package cl.duoc.hospital.bff.security;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;

/**
 * Decodificador JWT dual que soporta tokens locales (HMAC) y tokens de Azure AD
 * (RSA/JWKS).
 * Intenta primero validar como token local; si falla, valida contra los JWKS de
 * Azure AD.
 */
public class DualJwtDecoder implements JwtDecoder {

    private final JwtDecoder localDecoder;
    private final JwtDecoder aadDecoder;

    public DualJwtDecoder(JwtDecoder localDecoder, JwtDecoder aadDecoder) {
        this.localDecoder = localDecoder;
        this.aadDecoder = aadDecoder;
    }

    @Override
    public Jwt decode(String token) throws JwtException {
        try {
            return localDecoder.decode(token);
        } catch (JwtException localEx) {
            try {
                return aadDecoder.decode(token);
            } catch (JwtException aadEx) {
                throw new JwtException(
                        "Token inválido: no se pudo validar como token local ni como token de Azure AD. "
                                + aadEx.getMessage());
            }
        }
    }
}
