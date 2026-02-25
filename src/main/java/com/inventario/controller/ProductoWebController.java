package com.inventario.controller;

import com.inventario.model.Producto;
import com.inventario.repository.CategoriaRepository;
import com.inventario.repository.ProductoRepository;
import com.inventario.repository.ProveedorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/productos")
public class ProductoWebController {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private ProveedorRepository proveedorRepository;

    // GET /productos - Listar todos
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("productos", productoRepository.findAll());
        return "productos";
    }

    // GET /productos/nuevo - Formulario nuevo producto
    @GetMapping("/nuevo")
    public String nuevoForm(Model model) {
        model.addAttribute("producto", new Producto());
        model.addAttribute("categorias", categoriaRepository.findAll());
        model.addAttribute("proveedores", proveedorRepository.findAll());
        return "nuevo_producto";
    }

    // POST /productos/guardar - Guardar nuevo producto
    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Producto producto,
                          @RequestParam(required = false) Long categoriaId,
                          @RequestParam(required = false) Long proveedorId) {
        if (categoriaId != null) categoriaRepository.findById(categoriaId).ifPresent(producto::setCategoria);
        if (proveedorId != null) proveedorRepository.findById(proveedorId).ifPresent(producto::setProveedor);
        productoRepository.save(producto);
        return "redirect:/productos";
    }

    // GET /productos/editar/{id} - Formulario editar
    @GetMapping("/editar/{id}")
    public String editarForm(@PathVariable Long id, Model model) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        model.addAttribute("producto", producto);
        model.addAttribute("categorias", categoriaRepository.findAll());
        model.addAttribute("proveedores", proveedorRepository.findAll());
        return "editar_producto";
    }

    // POST /productos/actualizar - Guardar cambios
    @PostMapping("/actualizar")
    public String actualizar(@ModelAttribute Producto producto,
                             @RequestParam(required = false) Long categoriaId,
                             @RequestParam(required = false) Long proveedorId) {
        if (categoriaId != null) categoriaRepository.findById(categoriaId).ifPresent(producto::setCategoria);
        if (proveedorId != null) proveedorRepository.findById(proveedorId).ifPresent(producto::setProveedor);
        productoRepository.save(producto);
        return "redirect:/productos";
    }

    // GET /productos/eliminar/{id} - Eliminar y redirigir
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        productoRepository.deleteById(id);
        return "redirect:/productos";
    }
}
