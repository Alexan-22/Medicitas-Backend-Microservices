package pe.cibertec.report_service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import pe.cibertec.report_service.dto.ReporteActivosDTO;
import pe.cibertec.report_service.dto.ReporteCitasEstadoDTO;
import pe.cibertec.report_service.dto.ReporteGeneralDTO;
import pe.cibertec.report_service.service.ReportService;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService service;

    @GetMapping("/general")
    public ReporteGeneralDTO reporte() {

        return service.obtenerReporte();
    }
    
    @GetMapping("/citas-estado")
    public ReporteCitasEstadoDTO
            reporteEstado() {

        return service.obtenerReporteEstado();
    }
    
    @GetMapping("/activos")
    public ReporteActivosDTO activos() {

        return service.obtenerActivos();
    }
}
