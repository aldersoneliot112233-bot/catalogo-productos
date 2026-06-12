package com.inventario.catalogo_productos.controller;

import com.inventario.catalogo_productos.model.entity.Producto;
import com.inventario.catalogo_productos.service.CategoriaService;
import com.inventario.catalogo_productos.service.ConfiguracionService;
import com.inventario.catalogo_productos.service.ProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import com.inventario.catalogo_productos.model.entity.Producto;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor // Inyecta automáticamente los servicios
public class PublicController {

    private final ProductoService productoService;
    private final CategoriaService categoriaService;
    private final ConfiguracionService configService;

    @GetMapping("/")
    public String home(Model model) {
        // Traemos los últimos 3 productos activos
        List<Producto> destacados = productoService.findAllActivos()
                .stream().limit(3).toList();

        // AGREGAR ESTAS LÍNEAS 👇
        String fotoNegocio = configService.getValor("FOTO_NEGOCIO_HERO");
        model.addAttribute("fotoNegocio", fotoNegocio);

        model.addAttribute("titulo", "Inicio - Catálogo");
        model.addAttribute("destacados", destacados);
        return "public/index";
    }

    @GetMapping("/catalogo")
    public String catalogo(@RequestParam(required = false) String busqueda,
                           @RequestParam(required = false) Long categoriaId,
                           Model model) {

        List<Producto> productos;

        // Lógica de filtrado REAL
        if ((busqueda != null && !busqueda.isBlank()) || categoriaId != null) {
            productos = productoService.searchByName(busqueda != null ? busqueda : "");

            // Si hay filtro de categoría, aplicamos sobre los resultados de búsqueda
            if (categoriaId != null) {
                productos = productos.stream()
                        .filter(p -> p.getCategoria().getId().equals(categoriaId))
                        .collect(Collectors.toList());
            }
        } else {
            // Sin filtros: traer todos los activos
            productos = productoService.findAllActivos();
        }

        model.addAttribute("titulo", "Catálogo de Productos");
        model.addAttribute("productos", productos);
        model.addAttribute("categorias", categoriaService.findAll()); // Para el dropdown de filtros
        model.addAttribute("busqueda", busqueda);
        model.addAttribute("categoriaId", categoriaId);

        return "public/catalogo";
    }

    @GetMapping("/producto/{id}")
    public String verDetalle(@PathVariable Long id, Model model) {
        Producto producto = productoService.findById(id);

        // Traemos productos relacionados (misma categoría) para sugerir abajo
        List<Producto> relacionados = productoService.findAllActivos().stream()
                .filter(p -> p.getCategoria().getId().equals(producto.getCategoria().getId())
                        && !p.getId().equals(producto.getId()))
                .limit(3)
                .toList();

        model.addAttribute("producto", producto);
        model.addAttribute("relacionados", relacionados);
        model.addAttribute("titulo", producto.getNombre());

        return "public/producto-detalle";
    }

        @GetMapping("/login")
    public String loginPage() {
        return "public/login";
    }
}