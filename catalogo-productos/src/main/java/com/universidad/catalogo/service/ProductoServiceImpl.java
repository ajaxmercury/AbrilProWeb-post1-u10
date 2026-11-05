package com.universidad.catalogo.service;

import com.universidad.catalogo.model.Producto;
import com.universidad.catalogo.repository.ProductoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementacion del servicio de gestion del catalogo de productos.
 * Aplica reglas de negocio y validaciones de dominio.
 */
@Service
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;

    public ProductoServiceImpl(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @Override
    @Transactional
    public Producto crear(Producto producto) {
        if (producto == null) {
            throw new IllegalArgumentException("El producto no puede ser nulo");
        }
        if (producto.getNombre() == null || producto.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del producto no puede estar vacio");
        }
        if (producto.getPrecio() <= 0) {
            throw new IllegalArgumentException("El precio del producto debe ser mayor a cero");
        }
        return productoRepository.save(producto);
    }

    @Override
    @Transactional(readOnly = true)
    public Producto buscarPorId(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado con id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Producto> listarPorCategoria(String categoria) {
        if (categoria == null || categoria.trim().isEmpty()) {
            return productoRepository.findAll();
        }
        return productoRepository.findByCategoria(categoria.trim());
    }

    @Override
    @Transactional
    public Producto actualizarPrecio(Long id, double nuevoPrecio) {
        if (nuevoPrecio <= 0) {
            throw new IllegalArgumentException("El precio del producto debe ser mayor a cero");
        }
        Producto producto = buscarPorId(id);
        producto.setPrecio(nuevoPrecio);
        return productoRepository.save(producto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Producto> listarTodos() {
        return productoRepository.findAll();
    }
}
