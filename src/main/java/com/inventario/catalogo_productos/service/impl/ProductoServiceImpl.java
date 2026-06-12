package com.inventario.catalogo_productos.service.impl;

import com.inventario.catalogo_productos.model.entity.Producto;
import com.inventario.catalogo_productos.repository.ProductoRepository;
import com.inventario.catalogo_productos.service.CloudinaryService;
import com.inventario.catalogo_productos.service.ProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;
    private final CloudinaryService cloudinaryService;

    @Override
    public List<Producto> findAllActivos() {
        return productoRepository.findByActivoTrue();
    }

    @Override
    public List<Producto> searchByName(String termino) {
        if (termino == null || termino.isBlank()) {
            return findAllActivos(); // Si no hay texto, trae todos
        }
        // Ahora usa el método que busca en nombre Y descripción
        return productoRepository.findByNombreOrDescripcionContainingIgnoreCase(termino);
    }

    @Override
    public Producto findById(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
    }

    @Override
    public Producto save(Producto producto, MultipartFile imagen) {
        try {
            // Si hay nueva imagen, subirla a Cloudinary
            if (imagen != null && !imagen.isEmpty()) {
                // Si ya tenía imagen, borrarla de Cloudinary primero
                if (producto.getImagenUrl() != null && !producto.getImagenUrl().isEmpty()) {
                    cloudinaryService.deleteImage(producto.getImagenUrl());
                }
                String url = cloudinaryService.uploadImage(imagen);
                producto.setImagenUrl(url);
            }

            return productoRepository.save(producto);
        } catch (IOException e) {
            throw new RuntimeException("Error al subir imagen: " + e.getMessage());
        }
    }

    @Override
    public void delete(Long id) {
        Producto producto = findById(id);
        // Borrar imagen de Cloudinary antes de eliminar de BD
        try {
            if (producto.getImagenUrl() != null) {
                cloudinaryService.deleteImage(producto.getImagenUrl());
            }
        } catch (IOException e) {
            System.err.println("No se pudo eliminar imagen de Cloudinary: " + e.getMessage());
        }
        productoRepository.delete(producto);
    }

    @Override
    public long countActivos() {
        return productoRepository.countByActivoTrue();
    }

    @Override
    public double calculateTotalInventoryValue() {
        return productoRepository.findAll().stream()
                .mapToDouble(p -> p.getPrecio().doubleValue() * p.getStock())
                .sum();
    }

    @Override
    public long countLowStock(int threshold) {
        return productoRepository.findAll().stream()
                .filter(p -> p.getStock() < threshold)
                .count();
    }

    @Override
    public List<Producto> findAll() {
        return productoRepository.findAll();
    }


}