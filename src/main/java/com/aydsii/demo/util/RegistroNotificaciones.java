package com.aydsii.demo.util;

import com.aydsii.demo.dto.NotificacionDTO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
