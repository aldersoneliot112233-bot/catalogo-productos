package com.inventario.catalogo_productos.repository;

import com.inventario.catalogo_productos.model.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    // Busca por username (necesario para autenticación)
    Optional<Usuario> findByUsername(String username);

    // Verifica si existe (útil para validaciones futuras)
    boolean existsByUsername(String username);
}