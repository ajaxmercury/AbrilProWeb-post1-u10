package com.universidad.catalogo.repository;

import com.universidad.catalogo.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio Spring Data JPA para la persistencia y consulta de entidades Producto.
 */
@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    /**
     * Busca todos los productos que pertenecen a una categoria especifica.
     * @param categoria nombre de la categoria a filtrar.
     * @return lista de productos coincidentes.
     */
    List<Producto> findByCategoria(String categoria);
}
