package com.inventario.catalogo_productos.model.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "configuracion")
@Data
public class Configuracion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "clave", unique = true, nullable = false)
    private String clave; // Ej: "FOTO_NEGOCIO_HERO"

    @Column(name = "valor", columnDefinition = "TEXT")
    private String valor; // La URL de Cloudinary
}