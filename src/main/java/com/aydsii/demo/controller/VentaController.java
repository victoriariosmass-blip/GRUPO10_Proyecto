package com.aydsii.demo.controller;

import com.aydsii.demo.dto.ApiResponse;
import com.aydsii.demo.dto.FiltroVentasDTO;
import com.aydsii.demo.dto.HistorialVentasDTO;
import com.aydsii.demo.service.VentaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ventas")
@Tag(name = "CU-03 Consultar historial de ventas",
        description = "Ventas de un período con sus métricas (para el personal de Ventas)")
public class VentaController {

    private final VentaService ventaService;

    public VentaController(VentaService ventaService) {
        this.ventaService = ventaService;
    }

    @GetMapping
    @Operation(summary = "Consultar historial de ventas",
            description = "Devuelve las ventas del período (pedidos con el pago acreditado: CONFIRMADO o posterior), "
                    + "de la más reciente a la más antigua, y las métricas: cantidad, monto total, monto promedio y "
                    + "cantidad por estado. Si no se indica período se toma el mes en curso.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Historial de ventas"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400",
                    description = "Período inválido: la fecha desde es posterior a la fecha hasta (FE-01)")
    })
    public ResponseEntity<ApiResponse<HistorialVentasDTO>> consultarHistorial(@ParameterObject FiltroVentasDTO filtro) {
        HistorialVentasDTO historial = ventaService.obtenerHistorial(filtro);
        // FA-02: si no hubo ventas se informa en el mensaje (las métricas vienen en cero)
        String mensaje = historial.getVentas().isEmpty()
                ? "No se registraron ventas en el período"
                : "Consulta realizada correctamente";
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), mensaje, historial));
    }
}
