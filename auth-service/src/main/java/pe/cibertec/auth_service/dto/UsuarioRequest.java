package pe.cibertec.auth_service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsuarioRequest {
    private String username;
    private String password;
    private String rol;
}