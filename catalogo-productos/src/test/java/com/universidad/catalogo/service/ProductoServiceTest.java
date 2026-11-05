package com.universidad.catalogo.service;

import com.universidad.catalogo.model.Producto;
import com.universidad.catalogo.repository.ProductoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Pruebas unitarias puras y aisladas para ProductoServiceImpl utilizando Mockito.
 * Sigue rigurosamente el patron de nomenclatura metodo_condicion_resultado.
 */
@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private ProductoServiceImpl productoService;

    @Test
    @DisplayName("crear con datos validos guarda en repositorio y retorna el producto")
    void crear_conDatosValidos_guardaYRetorna() {
        Producto entrada = new Producto("Teclado Mecanico", 85.0, "Perifericos");
        Producto persistido = new Producto(1L, "Teclado Mecanico", 85.0, "Perifericos");

        when(productoRepository.save(entrada)).thenReturn(persistido);

        Producto resultado = productoService.crear(entrada);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Teclado Mecanico", resultado.getNombre());
        assertEquals(85.0, resultado.getPrecio());
        verify(productoRepository).save(entrada);
    }

    @Test
    @DisplayName("crear con precio negativo o cero lanza IllegalArgumentException y nunca invoca save")
    void crear_conPrecioNegativo_lanzaIllegalArgumentException() {
        Producto productoPrecioNegativo = new Producto("Mouse Inalambrico", -10.0, "Perifericos");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                productoService.crear(productoPrecioNegativo)
        );

        assertEquals("El precio del producto debe ser mayor a cero", ex.getMessage());
        verify(productoRepository, never()).save(any());
    }

    @Test
    @DisplayName("crear con nombre en blanco lanza IllegalArgumentException y nunca invoca save")
    void crear_conNombreVacio_lanzaIllegalArgumentException() {
        Producto productoNombreVacio = new Producto("   ", 45.0, "Perifericos");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                productoService.crear(productoNombreVacio)
        );

        assertEquals("El nombre del producto no puede estar vacio", ex.getMessage());
        verify(productoRepository, never()).save(any());
    }

    @Test
    @DisplayName("buscarPorId con ID existente retorna el producto encontrado")
    void buscarPorId_existe_retornaProducto() {
        Long id = 1L;
        Producto producto = new Producto(id, "Monitor 4K", 350.0, "Pantallas");

        when(productoRepository.findById(id)).thenReturn(Optional.of(producto));

        Producto resultado = productoService.buscarPorId(id);

        assertNotNull(resultado);
        assertEquals(id, resultado.getId());
        assertEquals("Monitor 4K", resultado.getNombre());
        verify(productoRepository).findById(id);
    }

    @Test
    @DisplayName("buscarPorId con ID inexistente lanza EntityNotFoundException")
    void buscarPorId_noExiste_lanzaEntityNotFoundException() {
        Long idInexistente = 999L;

        when(productoRepository.findById(idInexistente)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () ->
                productoService.buscarPorId(idInexistente)
        );

        assertEquals("Producto no encontrado con id: 999", ex.getMessage());
        verify(productoRepository).findById(idInexistente);
    }

    @Test
    @DisplayName("listarPorCategoria retorna lista de productos de dicha categoria")
    void listarPorCategoria_retornaLista() {
        String categoria = "Computadores";
        List<Producto> productosMock = Arrays.asList(
                new Producto(1L, "Laptop Gamer", 1500.0, categoria),
                new Producto(2L, "PC Escritorio", 900.0, categoria)
        );

        when(productoRepository.findByCategoria(categoria)).thenReturn(productosMock);

        List<Producto> resultado = productoService.listarPorCategoria(categoria);

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("Laptop Gamer", resultado.get(0).getNombre());
        verify(productoRepository).findByCategoria(categoria);
    }

    @Test
    @DisplayName("actualizarPrecio con valor valido busca por ID, actualiza y guarda")
    void actualizarPrecio_conValorValido_actualizaYGuarda() {
        Long id = 1L;
        double nuevoPrecio = 120.0;
        Producto existente = new Producto(id, "Audifonos Bluetooth", 100.0, "Audio");
        Producto actualizado = new Producto(id, "Audifonos Bluetooth", nuevoPrecio, "Audio");

        when(productoRepository.findById(id)).thenReturn(Optional.of(existente));
        when(productoRepository.save(existente)).thenReturn(actualizado);

        Producto resultado = productoService.actualizarPrecio(id, nuevoPrecio);

        assertNotNull(resultado);
        assertEquals(nuevoPrecio, resultado.getPrecio());
        verify(productoRepository).findById(id);
        verify(productoRepository).save(existente);
    }

    @Test
    @DisplayName("actualizarPrecio con valor no positivo lanza IllegalArgumentException y nunca invoca save")
    void actualizarPrecio_conPrecioInvalido_lanzaIllegalArgumentException() {
        Long id = 1L;
        double precioInvalido = 0.0;

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                productoService.actualizarPrecio(id, precioInvalido)
        );

        assertEquals("El precio del producto debe ser mayor a cero", ex.getMessage());
        verify(productoRepository, never()).findById(any());
        verify(productoRepository, never()).save(any());
    }

    @Test
    @DisplayName("actualizarPrecio con producto inexistente lanza EntityNotFoundException")
    void actualizarPrecio_productoNoExiste_lanzaEntityNotFoundException() {
        Long idInexistente = 404L;
        double nuevoPrecio = 200.0;

        when(productoRepository.findById(idInexistente)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                productoService.actualizarPrecio(idInexistente, nuevoPrecio)
        );

        verify(productoRepository).findById(idInexistente);
        verify(productoRepository, never()).save(any());
    }
}
