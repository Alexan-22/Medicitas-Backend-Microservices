package pe.cibertec.doctor_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import pe.cibertec.doctor_service.entity.Medico;

public interface MedicoRepository extends JpaRepository<Medico, Long> {
	
	long countByEstado(String estado);
}
