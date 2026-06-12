package pe.cibertec.patient_service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import pe.cibertec.patient_service.entity.Paciente;
import pe.cibertec.patient_service.repository.PacienteRepository;

@SpringBootTest
class PatientServiceApplicationTests {

	@Autowired
    private PacienteRepository repository;

    @Test
    void insertarPaciente() {

        Paciente paciente = new Paciente();

        paciente.setDni("75083164");
        paciente.setNombres("Paciente Test");
        paciente.setApellidos("JUnit");
        paciente.setFechaNacimiento(
                LocalDate.of(2000, 1, 1));
        paciente.setSexo("M");
        paciente.setTelefono("999999999");
        paciente.setEmail("test@test.com");
        paciente.setDireccion("Lima");
        paciente.setTipoSangre("O+");
        paciente.setAlergias("Ninguna");
        paciente.setEstado("ACTIVO");
        paciente.setFechaRegistro(
                LocalDateTime.now());

        Paciente guardado =
                repository.save(paciente);

        assertNotNull(guardado.getId());
    }
    
    @Test
    void listarPacientes() {

        assertFalse(
                repository.findAll().isEmpty());
    }
    
    @Test
    void buscarPacientePorId() {

        Paciente paciente =
                repository.findAll().get(0);

        Paciente encontrado =
                repository.findById(
                        paciente.getId())
                        .orElse(null);

        assertNotNull(encontrado);
    }
    
    @Test
    void eliminarPaciente() {

        Paciente paciente =
                new Paciente();

        paciente.setDni(
                "88888888");

        paciente.setNombres(
                "Eliminar");

        paciente =
                repository.save(
                        paciente);

        Long id =
                paciente.getId();

        repository.deleteById(id);

        assertFalse(
                repository.findById(id)
                        .isPresent());
    }

}
