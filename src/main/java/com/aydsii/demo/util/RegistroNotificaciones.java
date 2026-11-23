package com.aydsii.demo.util;

import com.aydsii.demo.dto.NotificacionDTO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Patrón Singleton: garantiza que exista una única instancia de la clase en toda la aplicación
 * y ofrece un punto de acceso global a ella. Acá se usa para tener un solo registro en memoria
 * con todas las notificaciones que emite el sistema.
 *
 * Cómo se garantiza la instancia única (implementación clásica, sin Spring):
 * - Constructor privado: nadie fuera de la clase puede hacer "new RegistroNotificaciones()".
 * - Atributo estático "instancia": guarda la única instancia, compartida por toda la aplicación.
 * - getInstance() es el único acceso: la crea la primera vez que se pide (inicialización perezosa)
 *   y después devuelve siempre la misma.
 * - synchronized: como el servidor atiende varias peticiones en paralelo, evita que dos hilos
 *   entren a la vez la primera vez y creen dos instancias distintas.
 * - La clase es final para que no se pueda heredar y saltear estas reglas.
 */
public final class RegistroNotificaciones {

    private static RegistroNotificaciones instancia;

    // Lista sincronizada: varios hilos pueden registrar notificaciones al mismo tiempo
    private final List<NotificacionDTO> notificaciones = Collections.synchronizedList(new ArrayList<>());

    private RegistroNotificaciones() {
    }

    public static synchronized RegistroNotificaciones getInstance() {
        if (instancia == null) {
            instancia = new RegistroNotificaciones();
        }
        return instancia;
    }

    public void registrar(String destinatario, String mensaje) {
        notificaciones.add(new NotificacionDTO(destinatario, mensaje));
    }

    /** Devuelve una copia, para que desde afuera no se pueda modificar el registro. */
    public List<NotificacionDTO> getNotificaciones() {
        synchronized (notificaciones) {
            return new ArrayList<>(notificaciones);
        }
    }

    public int cantidad() {
        return notificaciones.size();
    }
}
