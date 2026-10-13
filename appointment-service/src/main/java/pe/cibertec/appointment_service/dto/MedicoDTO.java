package pe.cibertec.appointment_service.dto;

import java.time.LocalTime;
import lombok.Data;

@Data
public class MedicoDTO {

    private Long id;
    private String nombres;
    private String apellidos;
    private String especialidad;
    private String telefono;
    private String email;
    private LocalTime horarioInicio;
    private LocalTime horarioFin;
    private String estado;
}