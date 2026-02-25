package com.inventario.controller;

import com.inventario.model.Proveedor;
import com.inventario.repository.ProveedorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/proveedores")
public class ProveedorWebController {

    @Autowired
    private ProveedorRepository proveedorRepository;

    // GET /proveedores - Listar todos
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("proveedores", proveedorRepository.findAll());
        return "proveedores";
    }

    // GET /proveedores/nuevo - Formulario nuevo proveedor
    @GetMapping("/nuevo")
    public String nuevoForm(Model model) {
        model.addAttribute("proveedor", new Proveedor());
        return "nuevo_proveedor";
    }

    // POST /proveedores/guardar - Guardar nuevo proveedor
    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Proveedor proveedor) {
        proveedorRepository.save(proveedor);
        return "redirect:/proveedores";
    }

    // GET /proveedores/editar/{id} - Formulario editar
    @GetMapping("/editar/{id}")
    public String editarForm(@PathVariable Long id, Model model) {
        Proveedor proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado"));
        model.addAttribute("proveedor", proveedor);
        return "editar_proveedor";
    }

    // POST /proveedores/actualizar - Guardar cambios
    @PostMapping("/actualizar")
    public String actualizar(@ModelAttribute Proveedor proveedor) {
        proveedorRepository.save(proveedor);
        return "redirect:/proveedores";
    }

    // GET /proveedores/eliminar/{id} - Eliminar y redirigir
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        proveedorRepository.deleteById(id);
        return "redirect:/proveedores";
    }
}
