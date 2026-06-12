package pe.cibertec.doctor_service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import pe.cibertec.doctor_service.entity.Medico;
import pe.cibertec.doctor_service.repository.MedicoRepository;

@SpringBootTest
class DoctorServiceApplicationTests {

	 @Autowired
	    private MedicoRepository repository;

	    @Test
	    void insertarMedico() {

	        Medico medico = new Medico();

	        medico.setCmp("CMP99999");
	        medico.setNombres("Carlos");
	        medico.setApellidos("Prueba");
	        medico.setEspecialidad("Cardiologia");
	        medico.setTelefono("999888777");
	        medico.setEmail("doctor@test.com");
	        medico.setConsultorio("Consultorio Test");
	        medico.setHorarioInicio(
	                LocalTime.of(8, 0));
	        medico.setHorarioFin(
	                LocalTime.of(17, 0));
	        medico.setEstado("ACTIVO");

	        Medico guardado =
	                repository.save(medico);

	        assertNotNull(
	                guardado.getId());
	    }

	    @Test
	    void listarMedicos() {

	        assertNotNull(
	                repository.findAll());
	    }

	    @Test
	    void buscarMedicoPorId() {

	        Medico medico =
	                repository.findAll()
	                        .get(0);

	        Medico encontrado =
	                repository.findById(
	                        medico.getId())
	                        .orElse(null);

	        assertNotNull(
	                encontrado);
	    }

	    @Test
	    void eliminarMedico() {

	        Medico medico =
	                new Medico();

	        medico.setCmp(
	        		"CMP" + System.currentTimeMillis());

	        medico.setNombres(
	                "Eliminar");

	        medico =
	                repository.save(
	                        medico);

	        Long id =
	                medico.getId();

	        repository.deleteById(id);

	        assertFalse(
	                repository.findById(id)
	                        .isPresent());
	    }

	    
}
