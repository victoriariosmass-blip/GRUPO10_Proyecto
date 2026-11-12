package com.aydsii.demo.dto;

import java.time.LocalDate;
import java.util.List;

/** CU-03: respuesta completa del historial de ventas (lista + métricas). */
public class HistorialVentasDTO {

    private LocalDate desde;
    private LocalDate hasta;
    private List<VentaDTO> ventas;
    private MetricasVentasDTO metricas;

    public HistorialVentasDTO(LocalDate desde, LocalDate hasta, List<VentaDTO> ventas, MetricasVentasDTO metricas) {
        this.desde = desde;
        this.hasta = hasta;
        this.ventas = ventas;
        this.metricas = metricas;
    }

    public LocalDate getDesde() {
        return desde;
    }

    public LocalDate getHasta() {
        return hasta;
    }

    public List<VentaDTO> getVentas() {
        return ventas;
    }

    public MetricasVentasDTO getMetricas() {
        return metricas;
    }
}
