package com.aydsii.demo.dto;
import com.aydsii.demo.model.EstadoPedido;

/** CU-03: cuántas ventas hay en un estado. */
public class VentasPorEstadoDTO {

    private EstadoPedido estado;
    private int cantidad;

    public VentasPorEstadoDTO(EstadoPedido estado, int cantidad) {
        this.estado = estado;
        this.cantidad = cantidad;
    }

    public EstadoPedido getEstado() { return estado; }

    public int getCantidad() { return cantidad; }
}

