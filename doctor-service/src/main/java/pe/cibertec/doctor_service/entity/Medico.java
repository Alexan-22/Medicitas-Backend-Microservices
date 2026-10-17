package pe.cibertec.doctor_service.entity;

import java.time.LocalTime;
import com.fasterxml.jackson.annotation.JsonProperty;
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

    // La vinculación se modifica únicamente desde su endpoint protegido.
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Column(name = "usuario_id", unique = true)
    private Long usuarioId;

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