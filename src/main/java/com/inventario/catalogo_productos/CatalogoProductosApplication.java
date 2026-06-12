package com.inventario.catalogo_productos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class CatalogoProductosApplication {

	public static void main(String[] args) {
		// Generar hash fresco
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		String hash = encoder.encode("admin123");

		System.out.println("\n🔐 HASH PARA ADMIN123: " + hash + "\n");

		SpringApplication.run(CatalogoProductosApplication.class, args);
	}
}