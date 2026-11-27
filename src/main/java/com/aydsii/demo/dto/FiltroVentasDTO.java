package com.aydsii.demo.dto;

import com.aydsii.demo.model.EstadoPedido;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

/**
 * CU-03: filtros de la consulta (llegan como query params, los tres opcionales).
 * Resuelve las reglas del período y de los estados para que el service solo las pida.
 */
public class FiltroVentasDTO {

    @Schema(description = "Fecha desde (inclusive). Si no viene, se toma el primer día del mes en curso.",
            example = "2026-10-01")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate desde;

    @Schema(description = "Fecha hasta (inclusive). Si no viene, se toma el día de hoy.", example = "2026-11-30")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate hasta;

    public FiltroVentasDTO() {
    }

    /** FA-01: sin período se consulta el mes en curso (RN-04). */
    public LocalDate getDesde() {
        return desde != null ? desde : LocalDate.now().withDayOfMonth(1);
    }

    public LocalDate getHasta() {
        return hasta != null ? hasta : LocalDate.now();
    }

    /** FE-01: la fecha desde no puede ser posterior a la fecha hasta, las fechas
     * ingresadas no pueden ser posteriores a la fecha actual.  */
    public boolean periodoValido() {
        return !getDesde().isAfter(getHasta())
                && !getDesde().isAfter(LocalDate.now())
                && !getHasta().isAfter(LocalDate.now());
    }

    /**
     * RN-01: sin filtro se buscan todos los estados de venta.
     * Si se filtra por un estado que no es de venta, la lista queda vacía y no se devuelven pedidos.
     */
    public List<EstadoPedido> getEstados() {
        return Arrays.stream(EstadoPedido.values())
                .filter(EstadoPedido::esVenta)
                .toList();
    }

    // setters: los usa Spring para cargar los query params
    public void setDesde(LocalDate desde) {
        this.desde = desde;
    }

    public void setHasta(LocalDate hasta) {
        this.hasta = hasta;
    }
}
