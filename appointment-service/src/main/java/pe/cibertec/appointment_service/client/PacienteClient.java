package pe.cibertec.appointment_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import pe.cibertec.appointment_service.dto.PacienteDTO;

@FeignClient(name = "PATIENT-SERVICE")
public interface PacienteClient {

    @GetMapping("/patients/{id}")
    PacienteDTO obtenerPaciente(
            @PathVariable Long id);
}
