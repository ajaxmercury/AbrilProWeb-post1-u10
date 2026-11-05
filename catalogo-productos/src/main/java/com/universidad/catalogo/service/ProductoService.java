package com.universidad.catalogo.service;

import com.universidad.catalogo.model.Producto;

import java.util.List;

/**
 * Contrato de servicio de negocio para el Catalogo de Productos.
 */
public interface ProductoService {

    /**
     * Valida y crea un nuevo producto en el catalogo.
     * @param producto entidad con los datos a persistir.
     * @return producto persistido con su ID generado.
     * @throws IllegalArgumentException si el nombre es nulo/blanco o si el precio es <= 0.
     */
    Producto crear(Producto producto);

    /**
     * Busca un producto por su identificador unico.
     * @param id identificador a buscar.
     * @return producto hallado.
     * @throws jakarta.persistence.EntityNotFoundException si no existe.
     */
    Producto buscarPorId(Long id);

    /**
     * Lista todos los productos pertenecientes a una categoria.
     * @param categoria nombre de la categoria.
     * @return lista de productos de dicha categoria.
     */
    List<Producto> listarPorCategoria(String categoria);

    /**
     * Valida y actualiza el precio de un producto existente.
     * @param id identificador del producto.
     * @param nuevoPrecio nuevo precio positivo.
     * @return producto actualizado.
     * @throws IllegalArgumentException si el nuevo precio es <= 0.
     * @throws jakarta.persistence.EntityNotFoundException si el producto no existe.
     */
    Producto actualizarPrecio(Long id, double nuevoPrecio);

    /**
     * Lista la totalidad de productos del catalogo.
     * @return lista completa de productos.
     */
    List<Producto> listarTodos();
}
