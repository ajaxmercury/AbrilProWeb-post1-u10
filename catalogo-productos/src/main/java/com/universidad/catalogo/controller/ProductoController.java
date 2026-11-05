package com.universidad.catalogo.controller;

import com.universidad.catalogo.model.Producto;
import com.universidad.catalogo.service.ProductoService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST para la gestion del catalogo de productos.
 * Expone endpoints HTTP y maneja localmente las excepciones de negocio.
 */
@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    /**
     * Obtiene un producto por su ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Producto> obtenerPorId(@PathVariable Long id) {
        Producto producto = productoService.buscarPorId(id);
        return ResponseEntity.ok(producto);
    }

    /**
     * Lista productos con filtro opcional por categoria.
     */
    @GetMapping
    public ResponseEntity<List<Producto>> listar(
            @RequestParam(name = "categoria", required = false) String categoria) {
        List<Producto> productos;
        if (categoria != null && !categoria.trim().isEmpty()) {
            productos = productoService.listarPorCategoria(categoria.trim());
        } else {
            productos = productoService.listarTodos();
        }
        return ResponseEntity.ok(productos);
    }

    /**
     * Crea un nuevo producto y retorna HTTP 201 Created.
     */
    @PostMapping
    public ResponseEntity<Producto> crear(@Valid @RequestBody Producto producto) {
        Producto creado = productoService.crear(producto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    /**
     * Actualiza el precio de un producto existente.
     */
    @PatchMapping("/{id}/precio")
    public ResponseEntity<Producto> actualizarPrecio(
            @PathVariable Long id,
            @RequestBody Map<String, Double> body) {
        if (body == null || !body.containsKey("precio")) {
            throw new IllegalArgumentException("Debe suministrar el campo 'precio'");
        }
        double nuevoPrecio = body.get("precio");
        Producto actualizado = productoService.actualizarPrecio(id, nuevoPrecio);
        return ResponseEntity.ok(actualizado);
    }

    /**
     * Manejador local de excepcion para recursos no encontrados (HTTP 404).
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, Object>> manejarEntityNotFound(EntityNotFoundException ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", Instant.now().toString());
        error.put("status", HttpStatus.NOT_FOUND.value());
        error.put("error", "Not Found");
        error.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * Manejador local de excepcion para argumentos o datos invalidos (HTTP 400).
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> manejarIllegalArgument(IllegalArgumentException ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", Instant.now().toString());
        error.put("status", HttpStatus.BAD_REQUEST.value());
        error.put("error", "Bad Request");
        error.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Manejador local para errores de validacion Jakarta Bean Validation (@Valid) (HTTP 400).
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> manejarMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", Instant.now().toString());
        error.put("status", HttpStatus.BAD_REQUEST.value());
        error.put("error", "Bad Request");
        String mensaje = ex.getBindingResult().getFieldErrors().isEmpty()
                ? "Error de validacion en la solicitud"
                : ex.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
        error.put("message", mensaje);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}
