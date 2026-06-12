package com.inventario.catalogo_productos.service;

import com.inventario.catalogo_productos.model.entity.Producto;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface ProductoService {
    List<Producto> findAllActivos();
    List<Producto> searchByName(String nombre);
    Producto findById(Long id);
    Producto save(Producto producto, MultipartFile imagen);
    void delete(Long id);
    long countActivos();
    double calculateTotalInventoryValue();
    long countLowStock(int threshold);
    List<Producto> findAll();

}