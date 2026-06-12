package pe.cibertec.report_service.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "PATIENT-SERVICE")
	public interface PacienteClient {

	    @GetMapping("/patients")
	    List<Object> listarPacientes();
	    
	    @GetMapping("/patients/count/activos")
	    Long contarActivos();

	}
