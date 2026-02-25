package com.inventario.controller;

import com.inventario.model.Categoria;
import com.inventario.repository.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/categorias")
public class CategoriaWebController {

    @Autowired
    private CategoriaRepository categoriaRepository;

    // GET /categorias - Listar todas
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("categorias", categoriaRepository.findAll());
        return "categorias";
    }

    // GET /categorias/nuevo - Formulario nueva categoria
    @GetMapping("/nuevo")
    public String nuevoForm(Model model) {
        model.addAttribute("categoria", new Categoria());
        return "nueva_categoria";
    }

    // POST /categorias/guardar - Guardar nueva categoria
    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Categoria categoria) {
        categoriaRepository.save(categoria);
        return "redirect:/categorias";
    }

    // GET /categorias/editar/{id} - Formulario editar
    @GetMapping("/editar/{id}")
    public String editarForm(@PathVariable Long id, Model model) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoria no encontrada"));
        model.addAttribute("categoria", categoria);
        return "editar_categoria";
    }

    // POST /categorias/actualizar - Guardar cambios
    @PostMapping("/actualizar")
    public String actualizar(@ModelAttribute Categoria categoria) {
        categoriaRepository.save(categoria);
        return "redirect:/categorias";
    }

    // GET /categorias/eliminar/{id} - Eliminar y redirigir
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        categoriaRepository.deleteById(id);
        return "redirect:/categorias";
    }
}
