package com.aydsii.demo.service;

import com.aydsii.demo.model.Pedido;
import com.aydsii.demo.model.Producto;
import com.aydsii.demo.util.RegistroNotificaciones;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class NotificacionService {

    // SLF4J: la clase solo conoce la interfaz Logger, la implementación (Log4j2) esta en pom.xml
    private static final Logger log = LoggerFactory.getLogger(NotificacionService.class);

    private static final String VENTAS = "VENTAS";
    private static final String PRODUCCION = "PRODUCCION";
    private static final String CLIENTE = "CLIENTE";

    private final String telefonoEmpresa;

    public NotificacionService(@Value("${verdegreen.whatsapp.telefono}") String telefonoEmpresa) {
        this.telefonoEmpresa = telefonoEmpresa;
    }

    /** aviso a ventas: nuevo pedido con comprobante (estado ACEPTADO) */
    public void notificarNuevoPedido(Pedido pedido) {
        log.info("[NOTIFICACION -> VENTAS] Nuevo pedido #{} con comprobante '{}' (total ${})",
                pedido.getId(), pedido.getComprobante(), pedido.getTotal());
        RegistroNotificaciones.getInstance().registrar(VENTAS,
                "Nuevo pedido #" + pedido.getId() + " con comprobante '" + pedido.getComprobante()
                        + "' (total $" + pedido.getTotal() + ")");
    }

    /** aviso a producción: el producto quedó debajo de su stock mínimo */
    public void notificarStockMinimo(Producto producto) {
        log.warn("[NOTIFICACION -> PRODUCCION] Stock mínimo: '{}' ({}) quedó con {} unidades",
                producto.getNombre(), producto.getCodigo(), producto.getStock());
        RegistroNotificaciones.getInstance().registrar(PRODUCCION,
                "Stock mínimo: '" + producto.getNombre() + "' (" + producto.getCodigo() + ") quedó con "
                        + producto.getStock() + " unidades");
    }

    /** enlace wa.me con el mensaje que corresponde según el pedido sea personalizado o no */
    public String generarEnlaceWhatsApp(Pedido pedido) {
        String mensaje = pedido.isPersonalizado()
                ? "Pedido " + pedido.getId() + " enviado, comuníquese por WhatsApp"
                : "Pedido " + pedido.getId() + " enviado, aguarde acreditación del pago";
        log.debug("Enlace de WhatsApp generado para el pedido #{} (personalizado: {}): '{}'",
                pedido.getId(), pedido.isPersonalizado(), mensaje);
        RegistroNotificaciones.getInstance().registrar(CLIENTE, mensaje);
        return armarEnlace(mensaje);
    }

    /** aviso a ventas de que el cliente canceló el pedido */
    public void notificarCancelacion(Pedido pedido) {
        log.info("[NOTIFICACION -> VENTAS] El cliente canceló el pedido #{} (total ${})",
                pedido.getId(), pedido.getTotal());
        RegistroNotificaciones.getInstance().registrar(VENTAS,
                "El cliente canceló el pedido #" + pedido.getId() + " (total $" + pedido.getTotal() + ")");
    }

    /** el pedido estaba en producción, producción tiene que frenar la preparación */
    public void notificarCancelacionProduccion(Pedido pedido) {
        log.warn("[NOTIFICACION -> PRODUCCION] El pedido #{} estaba en producción y fue cancelado: detener la preparación",
                pedido.getId());
        RegistroNotificaciones.getInstance().registrar(PRODUCCION,
                "El pedido #" + pedido.getId() + " estaba en producción y fue cancelado: detener la preparación");
    }

    /** enlace wa.me para que el cliente coordine con ventas la devolución del dinero */
    public String generarEnlaceDevolucion(Pedido pedido) {
        String mensaje = "Cancelé el pedido " + pedido.getId() + ", quiero coordinar la devolución del pago";
        RegistroNotificaciones.getInstance().registrar(CLIENTE, mensaje);
        return armarEnlace(mensaje);
    }

    private String armarEnlace(String mensaje) {
        return "https://wa.me/" + telefonoEmpresa + "?text=" + URLEncoder.encode(mensaje, StandardCharsets.UTF_8);
    }
}
