package com.aydsii.demo.service;

import com.aydsii.demo.dto.FiltroVentasDTO;
import com.aydsii.demo.dto.HistorialVentasDTO;
import com.aydsii.demo.dto.MetricasVentasDTO;
import com.aydsii.demo.dto.VentaDTO;
import com.aydsii.demo.exception.ReglaNegocioException;
import com.aydsii.demo.model.EstadoPedido;
import com.aydsii.demo.model.Pedido;
import com.aydsii.demo.repository.PedidoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    // SLF4J: la clase solo conoce la interfaz Logger; la implementación (Log4j2) la elige el pom.xml
    private static final Logger log = LoggerFactory.getLogger(VentaService.class);

    private final PedidoRepository pedidoRepository;

    public VentaService(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    @Transactional(readOnly = true)
    public HistorialVentasDTO obtenerHistorial(FiltroVentasDTO filtro) {
        // excepcion 01
        if (!filtro.periodoValido()) {
            throw new ReglaNegocioException("El período es inválido: la fecha desde es posterior a la fecha hasta");
        }

        // período (mes en curso si no viene, FA-01) y estados de venta (RN-01)
        LocalDate desde = filtro.getDesde();
        LocalDate hasta = filtro.getHasta();
        List<EstadoPedido> estados = filtro.getEstados();
        log.debug("Consulta de historial de ventas: desde {}, hasta {}, estados {}", desde, hasta, estados);

        // "hasta" es inclusive, por eso se busca hasta el inicio del día siguiente
        List<Pedido> pedidos =
                 pedidoRepository.buscarVentas(desde.atStartOfDay(), hasta.plusDays(1).atStartOfDay(), estados);

        // loop pedido en pedidos -> «create» VentaDTO
        List<VentaDTO> ventas = new ArrayList<>();
        for (Pedido pedido : pedidos) {
            ventas.add(new VentaDTO(pedido));
        }

        // métricas y respuesta
        MetricasVentasDTO metricas = new MetricasVentasDTO(ventas);
        log.info("Historial de ventas consultado: {} ventas entre {} y {}", ventas.size(), desde, hasta);
        return new HistorialVentasDTO(desde, hasta, ventas, metricas);
    }
}
