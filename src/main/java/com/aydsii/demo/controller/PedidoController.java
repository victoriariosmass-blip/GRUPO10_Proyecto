package com.aydsii.demo.controller;

import com.aydsii.demo.dto.ApiResponse;
import com.aydsii.demo.dto.CancelacionDTO;
import com.aydsii.demo.dto.CompraRequestDTO;
import com.aydsii.demo.dto.PedidoResponseDTO;
import com.aydsii.demo.service.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pedidos")
@Tag(name = "Pedidos (CU-01 Comprar y CU-04 Cancelar pedido)",
        description = "Registra un pedido a partir del carrito del cliente y permite cancelarlo antes de que esté listo")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @PostMapping
    @Operation(summary = "Comprar",
            description = "Convierte el carrito del cliente en un pedido ACEPTADO: guarda el detalle, reserva el stock, "
                    + "suma el costo de envío (DOMICILIO) y de personalización si corresponde, crea el envío si es a "
                    + "domicilio y vacía el carrito. El límite y el stock ya se controlaron al armar el carrito. "
                    + "El pago no se implementa: el comprobante es solo una referencia.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Pedido creado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400",
                    description = "Falta el comprobante o el tipo de entrega, o el carrito está vacío"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404",
                    description = "El cliente no tiene carrito")
    })
    public ResponseEntity<ApiResponse<PedidoResponseDTO>> comprar(@Valid @RequestBody CompraRequestDTO dto) {
        PedidoResponseDTO respuesta = pedidoService.crearPedido(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(HttpStatus.CREATED.value(), "Pedido creado con éxito", respuesta));
    }

    @PutMapping("/{idPedido}/cancelar")
    @Operation(summary = "CU-04 Cancelar pedido",
            description = "Se invoca desde el historial del cliente, después de que confirma la cancelación. "
                    + "Pasa el pedido a CANCELADO, devuelve el stock reservado y avisa a Ventas (y a Producción si "
                    + "estaba en producción). Si el pago ya estaba acreditado, devuelve un enlace de WhatsApp para "
                    + "coordinar la devolución del dinero. Solo se puede cancelar en ACEPTADO, CONFIRMADO o EN_PRODUCCION.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Pedido cancelado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400",
                    description = "El pedido no se puede cancelar en su estado actual (FE-01)")
    })
    public ResponseEntity<ApiResponse<CancelacionDTO>> cancelar(
            @Parameter(description = "Id del pedido a cancelar", example = "101") @PathVariable Long idPedido) {
        CancelacionDTO respuesta = pedidoService.cancelarPedido(idPedido);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Pedido cancelado", respuesta));
    }
}
