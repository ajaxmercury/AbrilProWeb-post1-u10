package com.universidad.catalogo.repository;

import com.universidad.catalogo.model.Producto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas de integracion sobre la capa de persistencia utilizando @DataJpaTest.
 * Ejecuta contra una base de datos H2 en memoria con transacciones atomicas
 * que se revierten automaticamente al finalizar cada metodo de prueba.
 */
@DataJpaTest
class ProductoRepositoryTest {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("findByCategoria con categoria existente retorna productos correspondientes")
    void findByCategoria_conCategoriaExistente_retornaProductos() {
        Producto p1 = new Producto("Smart TV 55", 450.0, "Televisores");
        Producto p2 = new Producto("Smart TV 65", 650.0, "Televisores");
        Producto p3 = new Producto("Microfono USB", 70.0, "Audio");

        entityManager.persist(p1);
        entityManager.persist(p2);
        entityManager.persist(p3);
        entityManager.flush();

        List<Producto> resultado = productoRepository.findByCategoria("Televisores");

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertTrue(resultado.stream().allMatch(p -> "Televisores".equals(p.getCategoria())));
    }

    @Test
    @DisplayName("findByCategoria con categoria sin productos retorna lista vacia")
    void findByCategoria_conCategoriaInexistente_retornaListaVacia() {
        Producto p1 = new Producto("Mouse Pad XL", 20.0, "Accesorios");
        entityManager.persist(p1);
        entityManager.flush();

        List<Producto> resultado = productoRepository.findByCategoria("Electrodomesticos");

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("save con producto valido persiste en base de datos H2 y genera ID autoincremental")
    void save_productoValido_persisteEnBaseDeDatos() {
        Producto nuevo = new Producto("Memoria RAM 16GB", 60.0, "Componentes");

        Producto persistido = productoRepository.save(nuevo);

        assertNotNull(persistido.getId());
        Producto halladoEnBD = entityManager.find(Producto.class, persistido.getId());
        assertNotNull(halladoEnBD);
        assertEquals("Memoria RAM 16GB", halladoEnBD.getNombre());
    }

    @Test
    @DisplayName("findById con producto persistido retorna entidad en Optional")
    void findById_productoPersistido_retornaEntidad() {
        Producto producto = new Producto("Disco SSD 1TB", 95.0, "Almacenamiento");
        Producto guardado = entityManager.persistFlushFind(producto);

        Optional<Producto> encontrado = productoRepository.findById(guardado.getId());

        assertTrue(encontrado.isPresent());
        assertEquals("Disco SSD 1TB", encontrado.get().getNombre());
        assertEquals(95.0, encontrado.get().getPrecio());
    }
}
