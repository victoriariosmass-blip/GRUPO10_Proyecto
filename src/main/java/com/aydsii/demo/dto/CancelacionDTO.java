package com.aydsii.demo.dto;

import com.aydsii.demo.model.CambioEstado;
import com.aydsii.demo.model.EstadoPedido;
import com.aydsii.demo.model.Pedido;

import java.time.LocalDateTime;
import java.util.Comparator;

// respuesta de la cancelación de un pedido
public class CancelacionDTO {

    private Long pedidoId;
    private EstadoPedido estado;
    private LocalDateTime fechaCancelacion;
    private String mensaje;
    private String enlaceWhatsApp;   // null si no había pago acreditado

    public CancelacionDTO(Pedido pedido, boolean pagoAcreditado, String enlace) {
        this.pedidoId = pedido.getId();
        this.estado = pedido.getEstado();
        // fecha y hora del último cambio de estado, que es el de CANCELADO
        this.fechaCancelacion = pedido.getHistorial().stream()
                .map(CambioEstado::getFechaHora)
                .max(Comparator.naturalOrder())
                .orElse(null);
        this.mensaje = pagoAcreditado
                ? "El pedido fue cancelado. Ventas se va a comunicar por WhatsApp para coordinar la devolución del dinero."
                : "El pedido fue cancelado.";
        this.enlaceWhatsApp = enlace;
    }

    public Long getPedidoId() {
        return pedidoId;
    }

    public EstadoPedido getEstado() {
        return estado;
    }

    public LocalDateTime getFechaCancelacion() {
        return fechaCancelacion;
    }

    public String getMensaje() {
        return mensaje;
    }

    public String getEnlaceWhatsApp() {
        return enlaceWhatsApp;
    }
}
