package pe.cibertec.appointment_service.dto;

import lombok.Data;

@Data
public class PacienteDTO {

    private Long id;
    private String nombres;
    private String apellidos;
    private String dni;
    private String telefono;
    private String email;
}