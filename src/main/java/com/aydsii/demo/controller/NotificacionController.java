package com.aydsii.demo.controller;

import com.aydsii.demo.dto.ApiResponse;
import com.aydsii.demo.dto.NotificacionDTO;
import com.aydsii.demo.util.RegistroNotificaciones;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/notificaciones")
@Tag(name = "Patrón Singleton - Notificaciones",
        description = "Notificaciones emitidas por el sistema, guardadas en memoria en una única instancia de RegistroNotificaciones")
public class NotificacionController {

    @GetMapping
    @Operation(summary = "Listar notificaciones emitidas",
            description = "Devuelve las notificaciones que emitió el sistema desde que arrancó la aplicación "
                    + "(a Ventas, Producción y al cliente), leídas del Singleton RegistroNotificaciones. "
                    + "Para ver resultados, primero hacé una compra o cancelá un pedido.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lista de notificaciones")
    })
    public ResponseEntity<ApiResponse<List<NotificacionDTO>>> listarNotificaciones() {
        List<NotificacionDTO> notificaciones = RegistroNotificaciones.getInstance().getNotificaciones();
        String mensaje = notificaciones.isEmpty()
                ? "Todavía no se emitieron notificaciones"
                : "Consulta realizada correctamente";
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), mensaje, notificaciones));
    }
}
