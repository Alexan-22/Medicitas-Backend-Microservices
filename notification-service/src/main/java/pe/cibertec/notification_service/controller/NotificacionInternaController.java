package pe.cibertec.notification_service.controller;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;
import pe.cibertec.notification_service.entity.Notificacion;
import pe.cibertec.notification_service.service.NotificacionService;

@RestController
@RequestMapping("/internal/notifications")
@RequiredArgsConstructor
public class NotificacionInternaController {

    private final NotificacionService service;

    @Value("${NOTIFICATION_WRITE_KEY}")
    private String writeKey;

    @Value("${NOTIFICATION_READ_KEY}")
    private String readKey;

    @PostMapping
    public Notificacion registrar(
            @RequestHeader(value = "X-Service-Key", required = false) String clave,
            @RequestBody Notificacion datos) {

        validarClave(clave, writeKey);
        datos.setId(null);
        return service.guardar(datos);
    }

    @GetMapping("/count")
    public Long contar(
            @RequestHeader(value = "X-Service-Key", required = false) String clave) {

        validarClave(clave, readKey);
        return (long) service.listar().size();
    }

    private void validarClave(String recibida, String esperada) {
        if (recibida == null || esperada == null || esperada.length() < 32
                || !MessageDigest.isEqual(
                recibida.getBytes(StandardCharsets.UTF_8),
                esperada.getBytes(StandardCharsets.UTF_8))) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Credencial interna no válida");
        }
    }
}