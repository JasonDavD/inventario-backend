package com.inventario.controller;

import com.inventario.model.Proveedor;
import com.inventario.repository.ProveedorRepository;
import com.inventario.storage.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/proveedores")
@CrossOrigin(origins = "*")
public class ProveedorRestController {

    @Autowired
    private ProveedorRepository proveedorRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

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
                    if (prov.getLogoPublicId() != null) {
                        try {
                            cloudinaryService.eliminar(prov.getLogoPublicId());
                        } catch (IOException ignored) {
                        }
                    }
                    proveedorRepository.delete(prov);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/logo")
    public ResponseEntity<?> subirLogo(@PathVariable Long id, @RequestParam("archivo") MultipartFile archivo) {
        return proveedorRepository.findById(id)
                .map(proveedor -> {
                    String logoPublicIdAnterior = proveedor.getLogoPublicId();
                    try {
                        Map<String, Object> subida = cloudinaryService.subir(archivo);
                        proveedor.setLogoUrl((String) subida.get("secure_url"));
                        proveedor.setLogoPublicId((String) subida.get("public_id"));
                        Proveedor guardado = proveedorRepository.save(proveedor);
                        if (logoPublicIdAnterior != null) {
                            cloudinaryService.eliminar(logoPublicIdAnterior);
                        }
                        return ResponseEntity.ok(guardado);
                    } catch (IOException e) {
                        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                                .body(Map.of("status", 502, "error", "Error de almacenamiento",
                                        "mensaje", "No se pudo subir el logo"));
                    }
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
