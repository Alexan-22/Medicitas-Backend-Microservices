package pe.cibertec.appointment_service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import pe.cibertec.appointment_service.entity.Cita;
import pe.cibertec.appointment_service.repository.CitaRepository;

@SpringBootTest
class AppointmentServiceApplicationTests {
	
	 @Autowired
	    private CitaRepository repository;

	@Test
	void insertarCita() {

	    Cita cita = new Cita();

	    cita.setPacienteId(1L);

	    cita.setMedicoId(1L);

	    cita.setFecha(
	            LocalDate.now().plusDays(1));

	    cita.setHora(
	            LocalTime.of(9,0));

	    cita.setMotivoConsulta(
	            "Control General");

	    cita.setObservaciones(
	            "Prueba JUnit");

	    cita.setEstado(
	            "PROGRAMADA");

	    cita.setFechaCreacion(
	            LocalDateTime.now());

	    cita.setUsuarioCreacion(
	            "admin");

	    Cita guardada =
	            repository.save(cita);

	    assertNotNull(
	            guardada.getId());
	}
	
	@Test
	void listarCitas() {

	    assertNotNull(
	            repository.findAll());
	}
	
	@Test
	void buscarCitaPorId() {

	    Cita cita =
	            repository.findAll()
	                    .get(0);

	    Cita encontrada =
	            repository.findById(
	                    cita.getId())
	                    .orElse(null);

	    assertNotNull(
	            encontrada);
	}
	
	@Test
	void eliminarCita() {

	    Cita cita = new Cita();

	    cita.setPacienteId(1L);

	    cita.setMedicoId(1L);

	    cita =
	            repository.save(cita);

	    Long id =
	            cita.getId();

	    repository.deleteById(id);

	    assertFalse(
	            repository.findById(id)
	                    .isPresent());
	}

}
