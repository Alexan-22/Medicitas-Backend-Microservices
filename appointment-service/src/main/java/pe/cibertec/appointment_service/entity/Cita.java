package pe.cibertec.appointment_service.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "citas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Cita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long pacienteId;

    private Long medicoId;

    private LocalDate fecha;

    private LocalTime hora;

    private String motivoConsulta;

    private String observaciones;

    private String estado;

    private LocalDateTime fechaCreacion;

    private String usuarioCreacion;
}
