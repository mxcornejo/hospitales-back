package cl.duoc.hospital.bff.security;

import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;

import java.util.List;

/**
 * Decodificador JWT dual que soporta tokens locales (HMAC) y tokens de Azure AD
 * (RSA/JWKS).
 * Intenta primero validar como token local; si falla, valida contra los JWKS de
 * Azure AD.
 */
public class DualJwtDecoder implements JwtDecoder {

    private final JwtDecoder localDecoder;
    private final List<JwtDecoder> aadDecoders;

    public DualJwtDecoder(JwtDecoder localDecoder, List<JwtDecoder> aadDecoders) {
        this.localDecoder = localDecoder;
        this.aadDecoders = aadDecoders;
    }

    @Override
    public Jwt decode(String token) throws JwtException {
        try {
            return localDecoder.decode(token);
        } catch (JwtException localEx) {
            JwtException lastAadException = localEx;
            for (JwtDecoder aadDecoder : aadDecoders) {
                try {
                    return aadDecoder.decode(token);
                } catch (JwtException aadEx) {
                    lastAadException = aadEx;
                }
            }

            throw new BadJwtException(
                    "Token inválido: no se pudo validar como token local ni como token de Azure AD. "
                            + lastAadException.getMessage());
        }
    }
}
