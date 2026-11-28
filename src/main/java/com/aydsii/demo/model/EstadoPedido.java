package com.aydsii.demo.model;

public enum EstadoPedido {
    ACEPTADO,
    CONFIRMADO,
    EN_PRODUCCION,
    LISTO,
    EN_CAMINO,
    ENTREGADO,
    CANCELADO,
    RECHAZADO;

    // regla de negocio: estados que corresponden a venta
    public boolean esVenta() {
        return this == CONFIRMADO || this == EN_PRODUCCION || this == LISTO
                || this == EN_CAMINO || this == ENTREGADO;
    }
}
