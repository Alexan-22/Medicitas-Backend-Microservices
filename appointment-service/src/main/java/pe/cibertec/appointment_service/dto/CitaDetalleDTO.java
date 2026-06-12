package pe.cibertec.appointment_service.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.Data;

@Data
public class CitaDetalleDTO {

    private Long id;

    private String paciente;

    private String medico;

    private String especialidad;

    private LocalDate fecha;

    private LocalTime hora;

    private String estado;
}