package com.aydsii.demo.dto;

import com.aydsii.demo.model.EstadoPedido;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/** CU-03, paso 6: métricas del período. Si no hay ventas quedan en cero (FA-02). */
public class MetricasVentasDTO {

    private int cantidadVentas;
    private BigDecimal montoTotal;
    private BigDecimal montoPromedio;
    private Map<EstadoPedido, Integer> ventasPorEstado;

    public MetricasVentasDTO(List<VentaDTO> ventas) {
        this.cantidadVentas = ventas.size();
        this.montoTotal = BigDecimal.ZERO;
        this.ventasPorEstado = new EnumMap<>(EstadoPedido.class);
        for (VentaDTO venta : ventas) {
            montoTotal = montoTotal.add(venta.getTotal());
            ventasPorEstado.merge(venta.getEstado(), 1, Integer::sum);
        }
        this.montoPromedio = cantidadVentas == 0
                ? BigDecimal.ZERO
                : montoTotal.divide(BigDecimal.valueOf(cantidadVentas), 2, RoundingMode.HALF_UP);
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

    public Map<EstadoPedido, Integer> getVentasPorEstado() {
        return ventasPorEstado;
    }
}
