package com.aydsii.demo.dto;

import com.aydsii.demo.model.Cliente;
import com.aydsii.demo.model.EstadoPedido;
import com.aydsii.demo.model.Pedido;
import com.aydsii.demo.model.TipoEntrega;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** CU-03: una fila del historial de ventas. */
public class VentaDTO {

    private Long pedidoId;
    private LocalDateTime fecha;
    private String cliente;
    private EstadoPedido estado;
    private TipoEntrega tipoEntrega;
    private BigDecimal total;

    public VentaDTO(Pedido pedido) {
        this.pedidoId = pedido.getId();
        this.fecha = pedido.getFechaCreacion();     // RN-02: la fecha de la venta es la de creación del pedido
        this.estado = pedido.getEstado();
        this.tipoEntrega = pedido.getTipoEntrega();
        this.total = pedido.getTotal();             // RN-03: productos + envío + personalización
        Cliente c = pedido.getCliente();
        this.cliente = c.getNombre() + " " + c.getApellido();
    }

    public Long getPedidoId() {
        return pedidoId;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public String getCliente() {
        return cliente;
    }

    public EstadoPedido getEstado() {
        return estado;
    }

    public TipoEntrega getTipoEntrega() {
        return tipoEntrega;
    }

    public BigDecimal getTotal() {
        return total;
    }
}
