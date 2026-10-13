package pe.cibertec.auth_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AuthResponse {

    private String mensaje;
    private String token;
    private String username;
    private String role;

    public AuthResponse(String mensaje) {
        this(mensaje, null, null, null);
    }
}