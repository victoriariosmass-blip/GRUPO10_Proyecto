package com.aydsii.demo.exception;

/** no existe el carrito, el envío o el pedido buscado. Responde 404 (solo protección técnica). */
public class RecursoNoEncontradoException extends RuntimeException {

    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
