package com.aydsii.demo.service;

import com.aydsii.demo.dto.CancelacionDTO;
import com.aydsii.demo.dto.CompraRequestDTO;
import com.aydsii.demo.dto.PedidoResponseDTO;
import com.aydsii.demo.exception.RecursoNoEncontradoException;
import com.aydsii.demo.exception.ReglaNegocioException;
import com.aydsii.demo.model.*;
import com.aydsii.demo.repository.CarritoRepository;
import com.aydsii.demo.repository.EnvioRepository;
import com.aydsii.demo.repository.ParametroRepository;
import com.aydsii.demo.repository.PedidoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class PedidoService {

    // SLF4J: la clase solo conoce la interfaz Logger; la implementación (Log4j2) se configura en pom.xml
    private static final Logger log = LoggerFactory.getLogger(PedidoService.class);

    private static final String HORAS_VENCIMIENTO = "HORAS_VENCIMIENTO";
    private static final String COSTO_ENVIO = "COSTO_ENVIO";
    private static final String COSTO_PERSONALIZACION = "COSTO_PERSONALIZACION";

    private final CarritoRepository carritoRepository;
    private final PedidoRepository pedidoRepository;
    private final ParametroRepository parametroRepository;
    private final EnvioRepository envioRepository;
    private final NotificacionService notificacionService;

    public PedidoService(CarritoRepository carritoRepository,
                         PedidoRepository pedidoRepository,
                         ParametroRepository parametroRepository,
                         EnvioRepository envioRepository,
                         NotificacionService notificacionService) {
        this.carritoRepository = carritoRepository;
        this.pedidoRepository = pedidoRepository;
        this.parametroRepository = parametroRepository;
        this.envioRepository = envioRepository;
        this.notificacionService = notificacionService;
    }

    /** Todo o nada: si algo falla, no queda stock reservado ni pedido guardado. */
    @Transactional
    public PedidoResponseDTO crearPedido(CompraRequestDTO dto) {
        // carrito del cliente (precondición: activo y con al menos un producto)
        Carrito carrito = carritoRepository.findByClienteId(dto.getClienteId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "El cliente " + dto.getClienteId() + " no tiene un carrito activo"));
        if (carrito.estaVacio()) {
            throw new ReglaNegocioException("El carrito está vacío");
        }
        Cliente cliente = carrito.getCliente();

        // plazo de vencimiento parametrizado (RN-04)
        int horas = buscarParametro(HORAS_VENCIMIENTO).getValorEntero();
        LocalDateTime vencimiento = LocalDateTime.now().plusHours(horas);

        // «create» Pedido
        Pedido pedido = new Pedido(cliente, dto.getComprobante(), dto.isPersonalizado(),
                dto.getTipoEntrega(), vencimiento);

        // loop sobre los ítems: arma el detalle y reserva stock
        for (ItemCarrito item : carrito.getItems()) {
            Producto producto = item.getProducto();
            int cantidad = item.getCantidad();
            pedido.agregarDetalle(producto, cantidad);
            producto.reservarStock(cantidad);
            if (producto.bajoStockMinimo()) {
                notificacionService.notificarStockMinimo(producto);
            }
        }

        // costos de entrega y personalización (paso 4 del CU)
        boolean aDomicilio = pedido.esADomicilio();
        BigDecimal costoEnvio = BigDecimal.ZERO;
        if (aDomicilio) {
            costoEnvio = buscarParametro(COSTO_ENVIO).getValorDecimal();
        }
        BigDecimal costoPersonalizacion = BigDecimal.ZERO;
        if (pedido.isPersonalizado()) {
            costoPersonalizacion = buscarParametro(COSTO_PERSONALIZACION).getValorDecimal();
        }
        pedido.aplicarCostos(costoEnvio, costoPersonalizacion);

        // estado ACEPTADO con fecha y hora, y se guarda
        pedido.cambiarEstado(EstadoPedido.ACEPTADO);
        pedido = pedidoRepository.save(pedido);

        // envío solo si es a domicilio (RN-06)
        Envio envio = null;
        if (aDomicilio) {
            String direccion = cliente.getDireccionEntrega();
            envio = envioRepository.save(new Envio(pedido, direccion));
        }

        // se vacía el carrito
        carrito.vaciar();
        carritoRepository.save(carrito);

        // notificaciones y respuesta
        notificacionService.notificarNuevoPedido(pedido);
        String enlace = notificacionService.generarEnlaceWhatsApp(pedido);
        log.info("Pedido #{} creado: cliente #{}, {}, total ${}",
                pedido.getId(), cliente.getId(), pedido.getTipoEntrega(), pedido.getTotal());
        return new PedidoResponseDTO(pedido, envio, enlace);
    }

    @Transactional
    public CancelacionDTO cancelarPedido(Long idPedido) {
        // buscar el pedido
        // el orElseThrow es por si se prueba un id inventado desde Swagger
        Pedido pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new RecursoNoEncontradoException("Pedido no encontrado"));

        // break [false] -> FE-01 (RN-01)
        if (!pedido.sePuedeCancelar()) {
            throw new ReglaNegocioException("El pedido no se puede cancelar en su estado actual ("
                    + pedido.getEstado() + "). Solo se cancela antes de estar LISTO.");
        }

        // se pregunta ANTES de cancelar, porque después el estado ya es CANCELADO
        boolean enProduccion = pedido.estaEnProduccion();
        boolean pagoAcreditado = pedido.tienePagoAcreditado();

        // cancelar() -> cambiarEstado(CANCELADO) -> «create» CambioEstado (RN-02)
        pedido.cancelar();

        // loop sobre el detalle, se libera el stock reservado (RN-03).
        /* El producto se actualiza solo porque el metodo es @Transactional (no hace falta ProductoRepository). */
        for (DetallePedido detalle : pedido.getDetalles()) {
            Producto producto = detalle.getProducto();
            int cantidad = detalle.getCantidad();
            producto.liberarStock(cantidad);
        }

        // guardar
        pedido = pedidoRepository.save(pedido);

        // notificaciones
        notificacionService.notificarCancelacion(pedido);
        if (enProduccion) {
            notificacionService.notificarCancelacionProduccion(pedido);        // FA-01
        }
        String enlace = null;
        if (pagoAcreditado) {
            enlace = notificacionService.generarEnlaceDevolucion(pedido);     // FA-02
        }

        // respuesta
        log.info("Pedido #{} cancelado (pago acreditado: {}, estaba en producción: {})",
                pedido.getId(), pagoAcreditado, enProduccion);
        return new CancelacionDTO(pedido, pagoAcreditado, enlace);
    }

    private ParametroSistema buscarParametro(String clave) {
        return parametroRepository.findByClave(clave)
                .orElseThrow(() -> new IllegalStateException("Falta el parámetro del sistema " + clave));
    }
}
