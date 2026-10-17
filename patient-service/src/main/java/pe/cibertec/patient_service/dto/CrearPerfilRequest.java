package pe.cibertec.patient_service.dto;

import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CrearPerfilRequest {
    private String dni;
    private String nombres;
    private String apellidos;
    private LocalDate fechaNacimiento;
    private String sexo;
    private String telefono;
    private String email;
    private String direccion;
    private String tipoSangre;
    private String alergias;
}