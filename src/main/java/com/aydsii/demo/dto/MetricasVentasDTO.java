package com.aydsii.demo.dto;

import com.aydsii.demo.model.EstadoPedido;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.ArrayList;

/** CU-03: métricas del período. Si no hay ventas quedan en cero (FA-02). */
public class MetricasVentasDTO {

    private int cantidadVentas;
    private BigDecimal montoTotal;
    private BigDecimal montoPromedio;
    private List<VentasPorEstadoDTO> ventasPorEstado;

    public MetricasVentasDTO(List<VentaDTO> ventas) {
        this.cantidadVentas = ventas.size();
        this.montoTotal = BigDecimal.ZERO;
        for (VentaDTO venta : ventas) {
            montoTotal = montoTotal.add(venta.getTotal());
        }
        this.montoPromedio = cantidadVentas == 0
                ? BigDecimal.ZERO
                : montoTotal.divide(BigDecimal.valueOf(cantidadVentas), 2, RoundingMode.HALF_UP);

        // una entrada por cada estado de venta (RN-01), aunque tenga 0
        this.ventasPorEstado = new ArrayList<>();
        for (EstadoPedido estado : EstadoPedido.values()) {
            if (estado.esVenta()) {
                int cantidad = 0;
                for (VentaDTO venta : ventas) {
                    if (venta.getEstado() == estado) {
                        cantidad++;
                    }
                }
                ventasPorEstado.add(new VentasPorEstadoDTO(estado, cantidad));
            }
        }
    }

    public int getCantidadVentas() {
        return cantidadVentas;
    }

    public BigDecimal getMontoTotal() {
        return montoTotal;
    }

    public BigDecimal getMontoPromedio() {
        return montoPromedio;
    }

    public List<VentasPorEstadoDTO> getVentasPorEstado() {
        return ventasPorEstado;
    }
}
