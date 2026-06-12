package com.inventario.catalogo_productos.repository;

import com.inventario.catalogo_productos.model.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    // Búsqueda por nombre (case insensitive)
    List<Producto> findByNombreContainingIgnoreCase(String nombre);

    // Filtrar por categoría
    List<Producto> findByCategoriaId(Long categoriaId);

    // Buscar activos para el catálogo público
    List<Producto> findByActivoTrue();

    // Contar productos para estadísticas
    long countByActivoTrue();

    @Query("SELECT p FROM Producto p WHERE LOWER(p.nombre) LIKE LOWER(CONCAT('%', :termino, '%')) OR LOWER(p.descripcion) LIKE LOWER(CONCAT('%', :termino, '%'))")
    List<Producto> findByNombreOrDescripcionContainingIgnoreCase(@Param("termino") String termino);

}