package com.inventario.controller;

import com.inventario.model.Proveedor;
import com.inventario.repository.ProveedorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/proveedores")
@CrossOrigin(origins = "*")
public class ProveedorRestController {

    @Autowired
    private ProveedorRepository proveedorRepository;

    @GetMapping
    public List<Proveedor> listarTodos() {
        return proveedorRepository.findAll();
    }

    @PostMapping
    public Proveedor crear(@RequestBody Proveedor proveedor) {
        return proveedorRepository.save(proveedor);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Proveedor> actualizar(@PathVariable Long id, @RequestBody Proveedor detalles) {
        return proveedorRepository.findById(id)
                .map(prov -> {
                    prov.setNombre(detalles.getNombre());
                    prov.setTelefono(detalles.getTelefono());
                    prov.setDireccion(detalles.getDireccion());
                    return ResponseEntity.ok(proveedorRepository.save(prov));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        return proveedorRepository.findById(id)
                .map(prov -> {
                    proveedorRepository.delete(prov);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
