package com.inventario.repository;

import com.inventario.model.ProductoImagen;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoImagenRepository extends JpaRepository<ProductoImagen, Long> {

    long countByProductoId(Long productoId);
}
