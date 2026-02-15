package com.inventario.controller;

import com.inventario.model.Producto;
import com.inventario.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/productos")
public class ProductoWebController {

    @Autowired
    private ProductoRepository productoRepository;

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
        return "nuevo_producto";
    }

    // POST /productos/guardar - Guardar nuevo producto
    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Producto producto) {
        productoRepository.save(producto);
        return "redirect:/productos";
    }

    // GET /productos/editar/{id} - Formulario editar
    @GetMapping("/editar/{id}")
    public String editarForm(@PathVariable Long id, Model model) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        model.addAttribute("producto", producto);
        return "editar_producto";
    }

    // POST /productos/actualizar - Guardar cambios
    @PostMapping("/actualizar")
    public String actualizar(@ModelAttribute Producto producto) {
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
