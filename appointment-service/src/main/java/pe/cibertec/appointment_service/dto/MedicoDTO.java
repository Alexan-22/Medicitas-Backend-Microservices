package pe.cibertec.appointment_service.dto;

import lombok.Data;

@Data
public class MedicoDTO {

    private Long id;
    private String nombres;
    private String especialidad;
    private String telefono;
    private String email;
}
