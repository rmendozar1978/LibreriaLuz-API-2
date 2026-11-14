package com.librerialuz;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.librerialuz.model.Usuario;
import com.librerialuz.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Pruebas de integración de todos los servicios de la API con MockMvc y H2 en memoria.
 * Cada prueba parte de una base de datos vacía.
 */
@SpringBootTest
@AutoConfigureMockMvc
class LibreriaLuzApiTests {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private VentaRepository ventaRepository;
    @Autowired
    private LibroRepository libroRepository;
    @Autowired
    private CategoriaRepository categoriaRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;

    @BeforeEach
    void limpiarBaseDeDatos() {
        ventaRepository.deleteAll();
        libroRepository.deleteAll();
        categoriaRepository.deleteAll();
        usuarioRepository.deleteAll();
    }

    // ---------- Autenticación ----------

    @Test
    @DisplayName("Registro correcto devuelve 201, no expone la contraseña y guarda hash BCrypt")
    void registroCorrecto() throws Exception {
        registrar("Juan Avila", "Juan@Correo.com", "ClaveSegura123!")
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.correo").value("juan@correo.com"))
                .andExpect(jsonPath("$.contrasena").doesNotExist())
                .andExpect(jsonPath("$.contrasenaHash").doesNotExist());

        Usuario guardado = usuarioRepository.findByCorreoIgnoreCase("juan@correo.com").orElseThrow(AssertionError::new);
        assertNotEquals("ClaveSegura123!", guardado.getContrasenaHash());
        assertTrue(passwordEncoder.matches("ClaveSegura123!", guardado.getContrasenaHash()));
    }

    @Test
    @DisplayName("Registro con correo repetido (sin importar mayúsculas) devuelve 409")
    void registroDuplicado() throws Exception {
        registrar("Juan Avila", "juan@correo.com", "ClaveSegura123!").andExpect(status().isCreated());
        registrar("Otro Nombre", "JUAN@correo.com", "OtraClave123!")
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.mensaje").value("El correo ya está registrado."));
    }

    @Test
    @DisplayName("Registro con datos inválidos devuelve 400 con mensaje")
    void registroInvalido() throws Exception {
        registrar("Juan Avila", "no-es-correo", "ClaveSegura123!")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("El correo no tiene un formato válido."));
        registrar("Juan Avila", "juan@correo.com", "corta")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("La contraseña debe tener entre 8 y 100 caracteres."));
    }

    @Test
    @DisplayName("Login correcto devuelve 200; clave errada o usuario inexistente devuelven 401")
    void login() throws Exception {
        registrar("Juan Avila", "juan@correo.com", "ClaveSegura123!").andExpect(status().isCreated());

        enviar(post("/api/auth/login"), "{\"correo\":\"juan@correo.com\",\"contrasena\":\"ClaveSegura123!\"}")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Autenticación satisfactoria."))
                .andExpect(jsonPath("$.usuario.nombre").value("Juan Avila"));

        enviar(post("/api/auth/login"), "{\"correo\":\"juan@correo.com\",\"contrasena\":\"ClaveErrada123!\"}")
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.mensaje").value("Error en la autenticación."));

        enviar(post("/api/auth/login"), "{\"correo\":\"nadie@correo.com\",\"contrasena\":\"ClaveSegura123!\"}")
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("JSON mal formado devuelve 400")
    void jsonMalFormado() throws Exception {
        enviar(post("/api/auth/registro"), "{ nombre: ")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("El cuerpo de la solicitud es obligatorio y debe tener formato JSON válido."));
    }

    // ---------- Usuarios ----------

    @Test
    @DisplayName("Usuarios: listar, consultar, 404 y eliminar")
    void usuarios() throws Exception {
        long id = idDe(registrar("Juan Avila", "juan@correo.com", "ClaveSegura123!"));

        mockMvc.perform(get("/api/usuarios")).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1)));
        mockMvc.perform(get("/api/usuarios/" + id)).andExpect(status().isOk())
                .andExpect(jsonPath("$.correo").value("juan@correo.com"));
        mockMvc.perform(get("/api/usuarios/999999")).andExpect(status().isNotFound());
        mockMvc.perform(get("/api/usuarios/abc")).andExpect(status().isBadRequest());
        mockMvc.perform(delete("/api/usuarios/" + id)).andExpect(status().isOk());
        mockMvc.perform(get("/api/usuarios/" + id)).andExpect(status().isNotFound());
    }

    // ---------- Categorías ----------

    @Test
    @DisplayName("Categorías: CRUD completo, nombre repetido 409 y borrado con libros 409")
    void categorias() throws Exception {
        long id = idDe(crearCategoria("Novela").andExpect(status().isCreated()));
        crearCategoria("novela").andExpect(status().isConflict());

        mockMvc.perform(get("/api/categorias")).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1)));
        enviar(put("/api/categorias/" + id), "{\"nombre\":\"Novela clásica\",\"descripcion\":\"Obras clásicas\"}")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Novela clásica"));
        enviar(post("/api/categorias"), "{\"nombre\":\"\"}").andExpect(status().isBadRequest());

        crearLibro("Cien años de soledad", "978-0307474728", "45000", 5, id).andExpect(status().isCreated());
        mockMvc.perform(delete("/api/categorias/" + id))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.mensaje").value("No se puede eliminar la categoría porque tiene libros asociados."));

        long vacia = idDe(crearCategoria("Poesía"));
        mockMvc.perform(delete("/api/categorias/" + vacia)).andExpect(status().isOk());
        mockMvc.perform(get("/api/categorias/" + vacia)).andExpect(status().isNotFound());
    }

    // ---------- Libros ----------

    @Test
    @DisplayName("Libros: crear, buscar, actualizar, validar y eliminar")
    void libros() throws Exception {
        long categoria = idDe(crearCategoria("Programación"));
        long id = idDe(crearLibro("Clean Code", "978-0132350884", "120000", 10, categoria)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.categoria.nombre").value("Programación")));
        crearLibro("Otro", "978-0132350884", "50000", 1, null).andExpect(status().isConflict());
        crearLibro("Sin categoría válida", "111-222", "50000", 1, 999999L).andExpect(status().isNotFound());
        crearLibro("Precio negativo", "333-444", "-5", 1, null)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("El precio debe ser mayor que cero."));
        crearLibro("Refactoring", "978-0134757599", "98000", 3, null).andExpect(status().isCreated());

        mockMvc.perform(get("/api/libros")).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(2)));
        mockMvc.perform(get("/api/libros").param("buscar", "clean")).andExpect(jsonPath("$", hasSize(1)));
        mockMvc.perform(get("/api/libros").param("categoriaId", String.valueOf(categoria))).andExpect(jsonPath("$", hasSize(1)));

        enviar(put("/api/libros/" + id), libroJson("Clean Code (2a ed.)", "978-0132350884", "130000", 8, categoria))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.precio").value(130000))
                .andExpect(jsonPath("$.stock").value(8));

        mockMvc.perform(delete("/api/libros/" + id)).andExpect(status().isOk());
        mockMvc.perform(get("/api/libros/" + id)).andExpect(status().isNotFound());
    }

    // ---------- Ventas ----------

    @Test
    @DisplayName("Ventas: registrar descuenta stock y calcula total; stock insuficiente 409 sin cambios")
    void ventas() throws Exception {
        long usuario = idDe(registrar("Juan Avila", "juan@correo.com", "ClaveSegura123!"));
        long libroA = idDe(crearLibro("Libro A", "1000-1", "20000", 5, null));
        long libroB = idDe(crearLibro("Libro B", "1000-2", "15000.50", 2, null));

        String venta = "{\"usuarioId\":" + usuario + ",\"items\":["
                + "{\"libroId\":" + libroA + ",\"cantidad\":2},"
                + "{\"libroId\":" + libroB + ",\"cantidad\":1}]}";
        long ventaId = idDe(enviar(post("/api/ventas"), venta)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.total").value(55000.50))
                .andExpect(jsonPath("$.detalles", hasSize(2))));

        mockMvc.perform(get("/api/libros/" + libroA)).andExpect(jsonPath("$.stock").value(3));

        String excede = "{\"usuarioId\":" + usuario + ",\"items\":["
                + "{\"libroId\":" + libroA + ",\"cantidad\":1},"
                + "{\"libroId\":" + libroB + ",\"cantidad\":5}]}";
        enviar(post("/api/ventas"), excede).andExpect(status().isConflict());
        mockMvc.perform(get("/api/libros/" + libroA)).andExpect(jsonPath("$.stock").value(3));

        enviar(post("/api/ventas"), "{\"usuarioId\":" + usuario + ",\"items\":[]}").andExpect(status().isBadRequest());
        enviar(post("/api/ventas"), "{\"usuarioId\":999999,\"items\":[{\"libroId\":" + libroA + ",\"cantidad\":1}]}")
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/api/ventas")).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1)));
        mockMvc.perform(get("/api/ventas").param("usuarioId", String.valueOf(usuario))).andExpect(jsonPath("$", hasSize(1)));
        mockMvc.perform(get("/api/ventas/" + ventaId)).andExpect(status().isOk())
                .andExpect(jsonPath("$.usuario.correo").value("juan@correo.com"));
        mockMvc.perform(get("/api/ventas/999999")).andExpect(status().isNotFound());

        mockMvc.perform(delete("/api/libros/" + libroA)).andExpect(status().isConflict());
        mockMvc.perform(delete("/api/usuarios/" + usuario)).andExpect(status().isConflict());
    }

    @Test
    @DisplayName("La documentación OpenAPI está disponible")
    void documentacionOpenApi() throws Exception {
        mockMvc.perform(get("/v3/api-docs")).andExpect(status().isOk())
                .andExpect(jsonPath("$.info.title").value("Librería Luz - API REST"));
    }

    // ---------- Utilidades ----------

    private ResultActions registrar(String nombre, String correo, String contrasena) throws Exception {
        return enviar(post("/api/auth/registro"), "{\"nombre\":\"" + nombre + "\",\"correo\":\"" + correo
                + "\",\"contrasena\":\"" + contrasena + "\"}");
    }

    private ResultActions crearCategoria(String nombre) throws Exception {
        return enviar(post("/api/categorias"), "{\"nombre\":\"" + nombre + "\",\"descripcion\":\"Prueba\"}");
    }

    private ResultActions crearLibro(String titulo, String isbn, String precio, int stock, Long categoriaId) throws Exception {
        return enviar(post("/api/libros"), libroJson(titulo, isbn, precio, stock, categoriaId));
    }

    private String libroJson(String titulo, String isbn, String precio, int stock, Long categoriaId) {
        return "{\"titulo\":\"" + titulo + "\",\"autor\":\"Autor de prueba\",\"isbn\":\"" + isbn
                + "\",\"precio\":" + precio + ",\"stock\":" + stock + ",\"categoriaId\":" + categoriaId + "}";
    }

    private ResultActions enviar(org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder peticion,
                                 String json) throws Exception {
        return mockMvc.perform(peticion.contentType(MediaType.APPLICATION_JSON).content(json));
    }

    private long idDe(ResultActions resultado) throws Exception {
        JsonNode cuerpo = objectMapper.readTree(resultado.andReturn().getResponse().getContentAsString());
        return cuerpo.get("id").asLong();
    }
}
