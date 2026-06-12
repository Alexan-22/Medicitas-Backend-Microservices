package pe.cibertec.report_service.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "DOCTOR-SERVICE")
	public interface MedicoClient {

	    @GetMapping("/doctors")
	    List<Object> listarMedicos();
	    
	    @GetMapping("/doctors/count/activos")
	    Long contarActivos();

}