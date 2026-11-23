package com.aydsii.demo.dto;

import java.time.LocalDateTime;

/** Notificación emitida por el sistema, guardada en el RegistroNotificaciones (Singleton). */
public class NotificacionDTO {

    private final LocalDateTime fechaHora;
    private final String destinatario;
    private final String mensaje;

    public NotificacionDTO(String destinatario, String mensaje) {
        this.fechaHora = LocalDateTime.now();
        this.destinatario = destinatario;
        this.mensaje = mensaje;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public String getDestinatario() {
        return destinatario;
    }

    public String getMensaje() {
        return mensaje;
    }
}
