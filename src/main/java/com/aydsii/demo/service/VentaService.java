package com.aydsii.demo.service;

import com.aydsii.demo.dto.FiltroVentasDTO;
import com.aydsii.demo.dto.HistorialVentasDTO;
import com.aydsii.demo.dto.MetricasVentasDTO;
import com.aydsii.demo.dto.VentaDTO;
import com.aydsii.demo.exception.ReglaNegocioException;
import com.aydsii.demo.model.EstadoPedido;
import com.aydsii.demo.model.Pedido;
import com.aydsii.demo.repository.PedidoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * CU-03 Consultar historial de ventas.
 * Sigue el mismo orden de mensajes que el diagrama de secuencia. La consulta no modifica datos.
 */
@Service
public class VentaService {

    private final PedidoRepository pedidoRepository;

    public VentaService(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    @Transactional(readOnly = true)
    public HistorialVentasDTO obtenerHistorial(FiltroVentasDTO filtro) {
        // 1.1.1: break [false] -> FE-01
        if (!filtro.periodoValido()) {
            throw new ReglaNegocioException("El período es inválido: la fecha desde es posterior a la fecha hasta");
        }

        // 1.1.2 a 1.1.4: período (mes en curso si no viene, FA-01) y estados de venta (RN-01)
        LocalDate desde = filtro.getDesde();
        LocalDate hasta = filtro.getHasta();
        List<EstadoPedido> estados = filtro.getEstados();

        // 1.1.5: una sola consulta. "hasta" es inclusive, por eso se busca hasta el inicio del día siguiente.
        // Si se filtró por un estado que no es de venta, la lista de estados viene vacía y no hay nada que buscar.
        List<Pedido> pedidos = estados.isEmpty()
                ? List.of()
                : pedidoRepository.buscarVentas(desde.atStartOfDay(), hasta.plusDays(1).atStartOfDay(), estados);

        // 1.1.6: loop [pedido en pedidos] -> «create» VentaDTO
        List<VentaDTO> ventas = new ArrayList<>();
        for (Pedido pedido : pedidos) {
            ventas.add(new VentaDTO(pedido));
        }

        // 1.1.7 y 1.1.8: métricas y respuesta
        MetricasVentasDTO metricas = new MetricasVentasDTO(ventas);
        return new HistorialVentasDTO(desde, hasta, ventas, metricas);
    }
}
