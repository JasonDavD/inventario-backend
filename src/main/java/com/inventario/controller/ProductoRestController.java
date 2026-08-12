package com.inventario.controller;

import com.inventario.model.Producto;
import com.inventario.model.ProductoImagen;
import com.inventario.repository.CategoriaRepository;
import com.inventario.repository.ProductoImagenRepository;
import com.inventario.repository.ProductoRepository;
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
@RequestMapping("/api/productos")
@CrossOrigin(origins = "*")
public class ProductoRestController {

    private static final int MAX_IMAGENES_POR_PRODUCTO = 5;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private ProveedorRepository proveedorRepository;

    @Autowired
    private ProductoImagenRepository productoImagenRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

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
                    for (ProductoImagen imagen : producto.getImagenes()) {
                        eliminarDeCloudinarySilencioso(imagen.getPublicId());
                    }
                    productoRepository.delete(producto);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/imagenes")
    public ResponseEntity<?> subirImagen(@PathVariable Long id, @RequestParam("archivo") MultipartFile archivo) {
        return productoRepository.findById(id)
                .map(producto -> {
                    long actuales = productoImagenRepository.countByProductoId(id);
                    if (actuales >= MAX_IMAGENES_POR_PRODUCTO) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(Map.of("status", 400, "error", "Limite alcanzado",
                                        "mensaje", "Un producto admite maximo " + MAX_IMAGENES_POR_PRODUCTO + " imagenes"));
                    }
                    try {
                        Map<String, Object> subida = cloudinaryService.subir(archivo);
                        ProductoImagen imagen = new ProductoImagen();
                        imagen.setUrl((String) subida.get("secure_url"));
                        imagen.setPublicId((String) subida.get("public_id"));
                        imagen.setOrden((int) actuales);
                        imagen.setProducto(producto);
                        return ResponseEntity.ok(productoImagenRepository.save(imagen));
                    } catch (IOException e) {
                        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                                .body(Map.of("status", 502, "error", "Error de almacenamiento",
                                        "mensaje", "No se pudo subir la imagen"));
                    }
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}/imagenes/{imagenId}")
    public ResponseEntity<Void> eliminarImagen(@PathVariable Long id, @PathVariable Long imagenId) {
        return productoImagenRepository.findById(imagenId)
                .filter(imagen -> imagen.getProducto().getId().equals(id))
                .map(imagen -> {
                    eliminarDeCloudinarySilencioso(imagen.getPublicId());
                    productoImagenRepository.delete(imagen);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    private void eliminarDeCloudinarySilencioso(String publicId) {
        try {
            cloudinaryService.eliminar(publicId);
        } catch (IOException ignored) {
        }
    }
}
