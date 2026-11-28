package com.aydsii.demo.repository;

import com.aydsii.demo.model.EstadoPedido;
import com.aydsii.demo.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/** save(pedido) y findById(id) se heredan de JpaRepository. */
@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    /**
     * pedidos creados entre desde (inclusive) y hasta (exclusive) que están en
     * alguno de los estados indicados, del más reciente al más antiguo.
     */
    @Query("SELECT p FROM Pedido p "
            + "WHERE p.fechaCreacion >= :desde AND p.fechaCreacion < :hasta "
            + "AND p.estado IN :estados "
            + "ORDER BY p.fechaCreacion DESC")
    List<Pedido> buscarVentas(@Param("desde") LocalDateTime desde,
                              @Param("hasta") LocalDateTime hasta,
                              @Param("estados") Collection<EstadoPedido> estados);
}
