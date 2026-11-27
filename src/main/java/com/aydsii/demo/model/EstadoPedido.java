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

    /**
     * CU-03, RN-01: un pedido cuenta como venta cuando el pago ya fue acreditado,
     * es decir, desde CONFIRMADO en adelante. ACEPTADO (sin acreditar), CANCELADO
     * y RECHAZADO no son venta
     */
    public boolean esVenta() {
        return this == CONFIRMADO || this == EN_PRODUCCION || this == LISTO
                || this == EN_CAMINO || this == ENTREGADO;
    }
}
