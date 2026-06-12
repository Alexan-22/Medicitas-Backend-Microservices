package pe.cibertec.doctor_service.entity;

import java.time.LocalTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "medicos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Medico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String cmp;

    private String nombres;

    private String apellidos;

    private String especialidad;

    private String telefono;

    private String email;

    private String consultorio;

    private LocalTime horarioInicio;

    private LocalTime horarioFin;

    private String estado;
}