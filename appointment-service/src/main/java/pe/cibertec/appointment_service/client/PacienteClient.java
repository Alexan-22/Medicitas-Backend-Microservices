package pe.cibertec.appointment_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import pe.cibertec.appointment_service.dto.PacienteDTO;

@FeignClient(name = "PATIENT-SERVICE")
public interface PacienteClient {

    @GetMapping("/patients/{id}")
    PacienteDTO obtenerPaciente(@PathVariable("id") Long id);

    @GetMapping("/patients/me")
    PacienteDTO obtenerMiPerfil(
            @RequestHeader("Authorization") String authorization);
}