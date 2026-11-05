package com.universidad.catalogo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.universidad.catalogo.model.Producto;
import com.universidad.catalogo.service.ProductoService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Pruebas de la capa web utilizando @WebMvcTest sobre ProductoController.
 * Enfocado en serializacion JSON, enrutamiento HTTP y mapeo de excepciones.
 */
@WebMvcTest(ProductoController.class)
class ProductoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductoService productoService;

    @Test
    @DisplayName("get con producto existente retorna HTTP 200 y cuerpo JSON esperado")
    void get_productoExiste_retorna200() throws Exception {
        Long id = 1L;
        Producto producto = new Producto(id, "Tablet Pro 11", 500.0, "Dispositivos");

        when(productoService.buscarPorId(id)).thenReturn(producto);

        mockMvc.perform(get("/api/productos/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.nombre").value("Tablet Pro 11"))
                .andExpect(jsonPath("$.precio").value(500.0))
                .andExpect(jsonPath("$.categoria").value("Dispositivos"));

        verify(productoService).buscarPorId(id);
    }

    @Test
    @DisplayName("get con producto inexistente retorna HTTP 404 Not Found")
    void get_noExiste_retorna404() throws Exception {
        Long idInexistente = 999L;

        when(productoService.buscarPorId(idInexistente))
                .thenThrow(new EntityNotFoundException("Producto no encontrado con id: 999"));

        mockMvc.perform(get("/api/productos/{id}", idInexistente))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Producto no encontrado con id: 999"));

        verify(productoService).buscarPorId(idInexistente);
    }

    @Test
    @DisplayName("post con datos validos retorna HTTP 201 Created y producto persistido")
    void post_conDatosValidos_retorna201() throws Exception {
        Producto entrada = new Producto("Camara Web HD", 45.0, "Video");
        Producto guardado = new Producto(10L, "Camara Web HD", 45.0, "Video");

        when(productoService.crear(any(Producto.class))).thenReturn(guardado);

        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(entrada)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.nombre").value("Camara Web HD"))
                .andExpect(jsonPath("$.precio").value(45.0));

        verify(productoService).crear(any(Producto.class));
    }

    @Test
    @DisplayName("post con precio invalido lanza IllegalArgumentException y retorna HTTP 400 Bad Request")
    void post_conPrecioInvalido_retorna400() throws Exception {
        Producto productoInvalido = new Producto("Mouse Optico", -5.0, "Perifericos");

        when(productoService.crear(any(Producto.class)))
                .thenThrow(new IllegalArgumentException("El precio del producto debe ser mayor a cero"));

        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productoInvalido)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("El precio del producto debe ser mayor a cero"));
    }

    @Test
    @DisplayName("get filtrado por categoria retorna HTTP 200 y lista de elementos")
    void get_porCategoria_retorna200YLista() throws Exception {
        String categoria = "Perifericos";
        when(productoService.listarPorCategoria(categoria)).thenReturn(Arrays.asList(
                new Producto(1L, "Teclado", 50.0, categoria),
                new Producto(2L, "Mouse", 25.0, categoria)
        ));

        mockMvc.perform(get("/api/productos")
                        .param("categoria", categoria))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nombre").value("Teclado"))
                .andExpect(jsonPath("$[1].nombre").value("Mouse"));

        verify(productoService).listarPorCategoria(categoria);
    }

    @Test
    @DisplayName("patch actualizar precio con valor valido retorna HTTP 200")
    void patch_actualizarPrecioValido_retorna200() throws Exception {
        Long id = 1L;
        double nuevoPrecio = 75.0;
        Producto actualizado = new Producto(id, "Teclado", nuevoPrecio, "Perifericos");

        when(productoService.actualizarPrecio(eq(id), eq(nuevoPrecio))).thenReturn(actualizado);

        mockMvc.perform(patch("/api/productos/{id}/precio", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("precio", nuevoPrecio))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.precio").value(nuevoPrecio));

        verify(productoService).actualizarPrecio(eq(id), eq(nuevoPrecio));
    }

    @Test
    @DisplayName("patch actualizar precio sin body precio retorna HTTP 400")
    void patch_actualizarPrecioInvalido_retorna400() throws Exception {
        Long id = 1L;

        mockMvc.perform(patch("/api/productos/{id}/precio", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Debe suministrar el campo 'precio'"));
    }
}
