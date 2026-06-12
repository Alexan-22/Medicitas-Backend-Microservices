package pe.cibertec.report_service.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "APPOINTMENT-SERVICE")
	public interface CitaClient {

	    @GetMapping("/appointments")
	    List<Object> listarCitas();
	    
	    @GetMapping("/appointments/count/{estado}")
	    Long contarPorEstado(
	            @PathVariable String estado);

	}
