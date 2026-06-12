package pe.cibertec.notification_service.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import pe.cibertec.notification_service.entity.Notificacion;
import pe.cibertec.notification_service.service.NotificacionService;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificacionController {

    private final NotificacionService service;

    @GetMapping
    public List<Notificacion> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public Notificacion buscarPorId(
            @PathVariable Long id) {

        return service.buscarPorId(id);
    }

    @PostMapping
    public Notificacion guardar(
            @RequestBody Notificacion n) {

        return service.guardar(n);
    }

    @PutMapping("/{id}")
    public Notificacion actualizar(
            @PathVariable Long id,
            @RequestBody Notificacion n) {

        return service.actualizar(id, n);
    }

    @DeleteMapping("/{id}")
    public void eliminar(
            @PathVariable Long id) {

        service.eliminar(id);
    }
}
