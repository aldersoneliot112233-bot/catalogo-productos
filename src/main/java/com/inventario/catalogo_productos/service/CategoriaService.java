package com.inventario.catalogo_productos.service;

import com.inventario.catalogo_productos.model.entity.Categoria;
import com.inventario.catalogo_productos.repository.CategoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoriaService {
    private final CategoriaRepository repo;

    public List<Categoria> findAll() { return repo.findAll(); }
    public Categoria findById(Long id) {
        return repo.findById(id).orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
    }
    public Categoria save(Categoria cat) { return repo.save(cat); }
    public void delete(Long id) { repo.deleteById(id); }
    public long count() { return repo.count(); }
}