package pe.cibertec.appointment_service.controller;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;
import pe.cibertec.appointment_service.service.CitaService;

@RestController
@RequestMapping("/internal/appointments")
@RequiredArgsConstructor
public class CitaInternaController {

    private final CitaService service;

    @Value("${APPOINTMENT_READ_KEY}")
    private String readKey;

    @GetMapping("/count")
    public Long contar(
            @RequestHeader(value = "X-Service-Key", required = false) String clave) {
        validarClave(clave);
        return service.contarTodas();
    }

    @GetMapping("/count/{estado}")
    public Long contarEstado(
            @PathVariable("estado") String estado,
            @RequestHeader(value = "X-Service-Key", required = false) String clave) {
        validarClave(clave);
        return service.contarPorEstado(estado);
    }

    private void validarClave(String recibida) {
        if (recibida == null || readKey == null || readKey.length() < 32
                || !MessageDigest.isEqual(
                recibida.getBytes(StandardCharsets.UTF_8),
                readKey.getBytes(StandardCharsets.UTF_8))) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Credencial interna no válida");
        }
    }
}