package com.inventario.catalogo_productos.controller;

import com.inventario.catalogo_productos.model.entity.Categoria;
import com.inventario.catalogo_productos.model.entity.Producto;
import com.inventario.catalogo_productos.model.entity.ProductoImagen;
import com.inventario.catalogo_productos.service.CategoriaService;
import com.inventario.catalogo_productos.service.CloudinaryService;
import com.inventario.catalogo_productos.service.ProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import com.inventario.catalogo_productos.service.ConfiguracionService; // <--- AGREGAR ESTE IMPORT
import java.io.IOException; // <--- NECESARIO PARA EL TRY-CATCH

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor // Inyecta automáticamente TODOS los servicios finales
public class AdminController {

    private final ProductoService productoService;
    private final CategoriaService categoriaService;
    private final CloudinaryService cloudinaryService; // <--- AQUÍ ESTABA EL ERROR
    private final ConfiguracionService configService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        List<Producto> todos = productoService.findAll();
        long activos = todos.stream().filter(Producto::getActivo).count();
        long inactivos = todos.size() - activos;
        long categoriasUsadas = todos.stream()
                .map(p -> p.getCategoria().getId())
                .distinct()
                .count();

        Producto ultimoProducto = todos.isEmpty() ? null : todos.get(todos.size() - 1);

        model.addAttribute("totalProductos", todos.size());
        model.addAttribute("productosActivos", activos);
        model.addAttribute("productosInactivos", inactivos);
        model.addAttribute("categoriasUsadas", categoriasUsadas);
        model.addAttribute("ultimoProducto", ultimoProducto);
        model.addAttribute("valorInventario", String.format("S/ %.2f", productoService.calculateTotalInventoryValue()));
        model.addAttribute("activePage", "dashboard");

        // AGREGAR ESTAS DOS LÍNEAS 👇
        String fotoNegocio = configService.getValor("FOTO_NEGOCIO_HERO");
        model.addAttribute("fotoActual", fotoNegocio);

        model.addAttribute("activePage", "dashboard");

        return "admin/dashboard";
    }

    @GetMapping("/productos")
    public String listarProductos(Model model) {
        model.addAttribute("productos", productoService.findAllActivos());
        model.addAttribute("activePage", "productos");
        return "admin/productos";
    }

    @GetMapping("/productos/nuevo")
    public String formularioNuevoProducto(Model model) {
        model.addAttribute("producto", new Producto());
        model.addAttribute("categorias", categoriaService.findAll());
        model.addAttribute("modo", "crear");
        model.addAttribute("activePage", "productos");
        return "admin/productos-form";
    }

    @GetMapping("/productos/editar/{id}")
    public String formularioEditarProducto(@PathVariable Long id, Model model) {
        Producto producto = productoService.findById(id);
        model.addAttribute("producto", producto);
        model.addAttribute("categorias", categoriaService.findAll());
        model.addAttribute("modo", "editar");
        model.addAttribute("activePage", "productos");
        return "admin/productos-form";
    }

    @PostMapping("/productos/guardar")
    public String guardarProducto(
            @ModelAttribute Producto producto,
            @RequestParam(required = false) MultipartFile imagenPrincipal,
            @RequestParam(required = false) List<MultipartFile> imagenesExtra) {

        try {
            // 1. LÓGICA DE IMAGEN PRINCIPAL (Ya corregida antes)
            if (imagenPrincipal != null && !imagenPrincipal.isEmpty()) {
                if (producto.getId() != null) {
                    Producto existente = productoService.findById(producto.getId());
                    if (existente.getImagenUrl() != null) {
                        cloudinaryService.deleteImage(existente.getImagenUrl());
                    }
                }
                String url = cloudinaryService.uploadImage(imagenPrincipal);
                producto.setImagenUrl(url);
            } else {
                if (producto.getId() != null) {
                    Producto existente = productoService.findById(producto.getId());
                    producto.setImagenUrl(existente.getImagenUrl());
                }
            }

            // 2. PRESERVAR GALERÍA EXISTENTE SI NO SE SUBEN NUEVAS
            if (imagenesExtra == null || imagenesExtra.stream().allMatch(MultipartFile::isEmpty)) {
                if (producto.getId() != null) {
                    Producto existente = productoService.findById(producto.getId());
                    // Traemos las fotos viejas y se las asignamos al objeto que se va a guardar
                    producto.setImagenesAdicionales(existente.getImagenesAdicionales());
                }
            }

            // 3. Guardar producto base
            Producto guardado = productoService.save(producto, null);

            // 4. Procesar SOLO si hay archivos nuevos seleccionados
            if (imagenesExtra != null) {
                for (MultipartFile file : imagenesExtra) {
                    if (!file.isEmpty()) {
                        String url = cloudinaryService.uploadImage(file);
                        ProductoImagen nuevaImg = new ProductoImagen();
                        nuevaImg.setUrl(url);
                        nuevaImg.setProducto(guardado);
                        guardado.getImagenesAdicionales().add(nuevaImg);
                    }
                }
                if (!imagenesExtra.isEmpty()) {
                    productoService.save(guardado, null);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return "redirect:/admin/productos";
    }

    @GetMapping("/productos/eliminar/{id}")
    public String eliminarProducto(@PathVariable Long id) {
        productoService.delete(id);
        return "redirect:/admin/productos";
    }

    @GetMapping("/categorias")
    public String listarCategorias(Model model) {
        model.addAttribute("categorias", categoriaService.findAll());
        model.addAttribute("activePage", "categorias");
        return "admin/categorias";
    }

    @PostMapping("/categorias/guardar")
    public String guardarCategoria(@ModelAttribute Categoria categoria) {
        categoriaService.save(categoria);
        return "redirect:/admin/categorias";
    }

    @GetMapping("/categorias/eliminar/{id}")
    public String eliminarCategoria(@PathVariable Long id) {
        categoriaService.delete(id);
        return "redirect:/admin/categorias";
    }

    @GetMapping("/ajustes") // Opcional: si quieres una página dedicada
    public String verAjustes(Model model) {
        model.addAttribute("fotoActual", configService.getValor("FOTO_NEGOCIO_HERO"));
        model.addAttribute("activePage", "dashboard"); // O 'ajustes' si creas el link
        return "admin/dashboard"; // O admin/ajustes.html
    }

    @PostMapping("/ajustes/guardar-foto")
    public String guardarFotoNegocio(@RequestParam MultipartFile fotoNegocio) {
        try {
            if (fotoNegocio != null && !fotoNegocio.isEmpty()) {
                String anterior = configService.getValor("FOTO_NEGOCIO_HERO");
                if (anterior != null) cloudinaryService.deleteImage(anterior);

                String url = cloudinaryService.uploadImage(fotoNegocio);
                configService.setValor("FOTO_NEGOCIO_HERO", url);

                // Redirigir con parámetro de éxito
                return "redirect:/admin/dashboard?success=true";
            }
        } catch (IOException e) {
            System.err.println("Error al subir foto: " + e.getMessage());
        }
        return "redirect:/admin/dashboard";
    }
}