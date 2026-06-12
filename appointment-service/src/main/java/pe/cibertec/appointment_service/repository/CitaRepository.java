package pe.cibertec.appointment_service.repository;

import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.data.jpa.repository.JpaRepository;

import pe.cibertec.appointment_service.entity.Cita;
public interface CitaRepository extends JpaRepository<Cita, Long> {

	boolean existsByMedicoIdAndFechaAndHora(
	        Long medicoId,
	        LocalDate fecha,
	        LocalTime hora);
	long countByEstado(String estado);
}
