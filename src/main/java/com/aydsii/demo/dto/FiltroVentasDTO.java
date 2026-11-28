package com.aydsii.demo.dto;

import com.aydsii.demo.model.EstadoPedido;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

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

    //sin período se consulta el mes en curso
    public LocalDate getDesde() {
        return desde != null ? desde : LocalDate.now().withDayOfMonth(1);
    }

    public LocalDate getHasta() {
        return hasta != null ? hasta : LocalDate.now();
    }

    /** la fecha desde no puede ser posterior a la fecha hasta, las fechas
     * ingresadas no pueden ser posteriores a la fecha actual.  */
    public boolean periodoValido() {
        return !getDesde().isAfter(getHasta())
                && !getDesde().isAfter(LocalDate.now())
                && !getHasta().isAfter(LocalDate.now());
    }

    /**
     * sin filtro se buscan todos los estados de venta.
     * si se filtra por un estado que no es de venta, la lista queda vacía y no se devuelven pedidos.
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
