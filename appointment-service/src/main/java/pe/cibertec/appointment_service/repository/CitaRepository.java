package pe.cibertec.appointment_service.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.cibertec.appointment_service.entity.Cita;

public interface CitaRepository extends JpaRepository<Cita, Long> {

	boolean existsByMedicoIdAndFechaAndHora(
			Long medicoId, LocalDate fecha, LocalTime hora);

	List<Cita> findByMedicoIdAndFecha(Long medicoId, LocalDate fecha);

	List<Cita> findByPacienteIdOrderByFechaDescHoraDesc(Long pacienteId);

	List<Cita> findByMedicoIdOrderByFechaDescHoraDesc(Long medicoId);

	long countByEstado(String estado);
}