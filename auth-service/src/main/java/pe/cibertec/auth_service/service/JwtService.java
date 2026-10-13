package pe.cibertec.auth_service.service;

import java.time.Instant;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import com.nimbusds.jose.jwk.source.ImmutableSecret;

import pe.cibertec.auth_service.entity.Usuario;

@Service
public class JwtService {

    private final JwtEncoder encoder;

    public JwtService(@Value("${JWT_SECRET}") String secret) {
        byte[] key = Base64.getDecoder().decode(secret);

        if (key.length < 32) {
            throw new IllegalArgumentException(
                    "JWT_SECRET debe contener al menos 32 bytes en Base64"
            );
        }

        this.encoder = new NimbusJwtEncoder(new ImmutableSecret<>(key));
    }

    public String generarToken(Usuario usuario) {
        Instant ahora = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("medicitas-auth")
                .subject(usuario.getUsername())
                .issuedAt(ahora)
                .expiresAt(ahora.plusSeconds(3600))
                .claim("userId", usuario.getId())
                .claim("role", usuario.getRol())
                .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256)
                .type("JWT")
                .build();

        return encoder.encode(
                JwtEncoderParameters.from(header, claims)
        ).getTokenValue();
    }
}