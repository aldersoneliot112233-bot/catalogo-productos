package com.inventario.catalogo_productos.config;

import com.inventario.catalogo_productos.model.entity.Usuario;
import com.inventario.catalogo_productos.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Solo crea el admin si NO existe ningún usuario
        if (usuarioRepository.count() == 0) {
            Usuario admin = new Usuario();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123")); // ¡CAMBIA ESTA CONTRASEÑA LUEGO!
            admin.setRole("ADMIN");

            usuarioRepository.save(admin);
            System.out.println("✅ USUARIO ADMIN CREADO AUTOMÁTICAMENTE");
        }
    }
}