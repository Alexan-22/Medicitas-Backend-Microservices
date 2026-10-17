package pe.cibertec.appointment_service.config;

import java.util.Base64;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.*;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public JwtDecoder jwtDecoder(@Value("${JWT_SECRET}") String secret) {
        byte[] key = Base64.getDecoder().decode(secret);

        if (key.length < 32) {
            throw new IllegalArgumentException(
                    "JWT_SECRET debe contener al menos 32 bytes en Base64");
        }

        NimbusJwtDecoder decoder = NimbusJwtDecoder
                .withSecretKey(new SecretKeySpec(key, "HmacSHA256"))
                .macAlgorithm(MacAlgorithm.HS256)
                .build();

        decoder.setJwtValidator(
                JwtValidators.createDefaultWithIssuer("medicitas-auth"));

        return decoder;
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter roles = new JwtGrantedAuthoritiesConverter();
        roles.setAuthoritiesClaimName("role");
        roles.setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(roles);
        return converter;
    }

    @Bean
    @Order(1)
    public SecurityFilterChain citasSecurity(
            HttpSecurity http,
            JwtAuthenticationConverter converter) throws Exception {

        http
                .securityMatcher("/appointments", "/appointments/**")
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET, "/appointments/me")
                        .hasRole("PACIENTE")

                        .requestMatchers(HttpMethod.GET, "/appointments/medico/me")
                        .hasRole("MEDICO")

                        .requestMatchers(HttpMethod.GET,
                                "/appointments",
                                "/appointments/listado",
                                "/appointments/count/*",
                                "/appointments/disponibles/*/*")
                        .hasAnyRole("ADMIN", "RECEPCIONISTA", "ENFERMERA")

                        .requestMatchers(HttpMethod.GET,
                                "/appointments/detail/*", "/appointments/*")
                        .hasAnyRole("ADMIN", "RECEPCIONISTA", "ENFERMERA", "MEDICO")

                        .requestMatchers(HttpMethod.POST, "/appointments")
                        .hasAnyRole("ADMIN", "RECEPCIONISTA")

                        .requestMatchers(HttpMethod.PUT, "/appointments/*/atender")
                        .hasAnyRole("ADMIN", "MEDICO")

                        .requestMatchers(HttpMethod.PUT, "/appointments/*/no-asistio")
                        .hasAnyRole("ADMIN", "RECEPCIONISTA", "MEDICO")

                        .requestMatchers(HttpMethod.PUT,
                                "/appointments/*/cancelar", "/appointments/*")
                        .hasAnyRole("ADMIN", "RECEPCIONISTA")

                        .requestMatchers(HttpMethod.DELETE, "/appointments/*")
                        .hasRole("ADMIN")

                        .anyRequest().denyAll())
                .oauth2ResourceServer(resource -> resource
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(converter)));

        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain internasSecurity(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // La clave interna se valida en el controlador.
                        .requestMatchers(HttpMethod.GET,
                                "/internal/appointments/count",
                                "/internal/appointments/count/*").permitAll()
                        .requestMatchers("/error").permitAll()
                        .anyRequest().denyAll());

        return http.build();
    }
}