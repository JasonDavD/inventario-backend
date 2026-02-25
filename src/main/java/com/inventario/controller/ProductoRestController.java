package com.inventario.controller;

import com.inventario.model.Producto;
import com.inventario.repository.CategoriaRepository;
import com.inventario.repository.ProductoRepository;
import com.inventario.repository.ProveedorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = "*")
public class ProductoRestController {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private ProveedorRepository proveedorRepository;

    @GetMapping
    public List<Producto> listarTodos() {
        return productoRepository.findAll();
    }

    @PostMapping
    public Producto crear(@RequestBody Producto req) {
        Producto producto = new Producto();
        producto.setNombre(req.getNombre());
        producto.setPrecio(req.getPrecio());
        producto.setStock(req.getStock());

        if (req.getCategoria() != null && req.getCategoria().getId() != null) {
            categoriaRepository.findById(req.getCategoria().getId())
                    .ifPresent(producto::setCategoria);
        }
        if (req.getProveedor() != null && req.getProveedor().getId() != null) {
            proveedorRepository.findById(req.getProveedor().getId())
                    .ifPresent(producto::setProveedor);
        }

        return productoRepository.save(producto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Producto> actualizar(@PathVariable Long id, @RequestBody Producto req) {
        return productoRepository.findById(id)
                .map(producto -> {
                    producto.setNombre(req.getNombre());
                    producto.setPrecio(req.getPrecio());
                    producto.setStock(req.getStock());

                    if (req.getCategoria() != null && req.getCategoria().getId() != null) {
                        categoriaRepository.findById(req.getCategoria().getId())
                                .ifPresent(producto::setCategoria);
                    } else {
                        producto.setCategoria(null);
                    }
                    if (req.getProveedor() != null && req.getProveedor().getId() != null) {
                        proveedorRepository.findById(req.getProveedor().getId())
                                .ifPresent(producto::setProveedor);
                    } else {
                        producto.setProveedor(null);
                    }

                    return ResponseEntity.ok(productoRepository.save(producto));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        return productoRepository.findById(id)
                .map(producto -> {
                    productoRepository.delete(producto);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
