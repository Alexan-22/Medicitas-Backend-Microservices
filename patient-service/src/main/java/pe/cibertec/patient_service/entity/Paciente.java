package pe.cibertec.patient_service.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "pacientes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Paciente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", unique = true)
    private Long usuarioId;

    @Column(unique = true)
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

    private String estado;

    private LocalDateTime fechaRegistro;


}