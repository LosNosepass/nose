package com.adorno.controller;

import com.adorno.TestDataFactory;
import com.adorno.model.dtos.MovilCreateDTO;
import com.adorno.model.dtos.MovilDetalleDTO;
import com.adorno.model.dtos.MovilResumenDTO;
import com.adorno.services.MovilService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests de integración del MovilController usando MockMvc.
 * Prueba los endpoints REST: rutas, códigos HTTP, seguridad y JSON de respuesta.
 */
@WebMvcTest(MovilController.class)
@DisplayName("MovilController — Tests de integración")
class MovilControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MovilService movilService;

    private ObjectMapper objectMapper;
    private MovilResumenDTO resumen1;
    private MovilResumenDTO resumen2;
    private MovilDetalleDTO detalleDTO;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        resumen1 = new MovilResumenDTO(1, "Samsung", "Galaxy S24", 8, 8, 256, new BigDecimal("799.99"));
        resumen2 = new MovilResumenDTO(2, "Apple", "iPhone 15", 6, 6, 128, new BigDecimal("899.00"));
        detalleDTO = new MovilDetalleDTO(
                1, "Samsung", "Galaxy S24",
                "Snapdragon 8 Gen 3", 8, new BigDecimal("3.30"),
                256, 8, new BigDecimal("6.20"), "AMOLED",
                new BigDecimal("14.70"), new BigDecimal("7.06"), new BigDecimal("0.76"),
                167, "50 Mpx + 12 Mpx + 10 Mpx", 4000, true,
                new BigDecimal("799.99"), null
        );
    }

    // ─── TENDENCIAS ───────────────────────────────────────────────────────────

    @Nested
    @DisplayName("GET /api/moviles/tendencias")
    class TendenciasTests {

        @Test
        @DisplayName("Endpoint público — debe devolver 200 y lista de móviles")
        void debeRetornar200ConListaMoviles() throws Exception {
            when(movilService.getTendencias()).thenReturn(List.of(resumen1, resumen2));

            mockMvc.perform(get("/api/moviles/tendencias"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].marca", is("Samsung")))
                    .andExpect(jsonPath("$[0].modelo", is("Galaxy S24")))
                    .andExpect(jsonPath("$[0].ramGb", is(8)))
                    .andExpect(jsonPath("$[1].marca", is("Apple")));
        }

        @Test
        @DisplayName("Endpoint público — no requiere autenticación")
        void noRequiereAutenticacion() throws Exception {
            when(movilService.getTendencias()).thenReturn(List.of());

            mockMvc.perform(get("/api/moviles/tendencias"))
                    .andExpect(status().isOk());
        }
    }

    // ─── MARCAS Y TECNOLOGÍAS ─────────────────────────────────────────────────

    @Nested
    @DisplayName("GET /api/moviles/marcas y /tecnologias-pantalla")
    class CatalogoTests {

        @Test
        @DisplayName("GET /marcas — debe devolver lista de marcas sin autenticación")
        void debeRetornarMarcas() throws Exception {
            when(movilService.getMarcas()).thenReturn(List.of("Apple", "Samsung", "Xiaomi"));

            mockMvc.perform(get("/api/moviles/marcas"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(3)))
                    .andExpect(jsonPath("$[0]", is("Apple")))
                    .andExpect(jsonPath("$[2]", is("Xiaomi")));
        }

        @Test
        @DisplayName("GET /tecnologias-pantalla — debe devolver tecnologías disponibles")
        void debeRetornarTecnologias() throws Exception {
            when(movilService.getTecnologiasPantalla()).thenReturn(List.of("AMOLED", "IPS", "OLED"));

            mockMvc.perform(get("/api/moviles/tecnologias-pantalla"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(3)))
                    .andExpect(jsonPath("$[0]", is("AMOLED")));
        }
    }

    // ─── DETALLE ──────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("GET /api/moviles/{id}")
    class DetalleTests {

        @Test
        @DisplayName("Debe devolver 200 con el detalle completo del móvil")
        void debeRetornarDetalle() throws Exception {
            when(movilService.getDetalle(1)).thenReturn(detalleDTO);

            mockMvc.perform(get("/api/moviles/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.marca", is("Samsung")))
                    .andExpect(jsonPath("$.modelo", is("Galaxy S24")))
                    .andExpect(jsonPath("$.cpuTipo", is("Snapdragon 8 Gen 3")))
                    .andExpect(jsonPath("$.cpuNucleos", is(8)))
                    .andExpect(jsonPath("$.ramGb", is(8)))
                    .andExpect(jsonPath("$.nfc", is(true)))
                    .andExpect(jsonPath("$.pantallaTecnologia", is("AMOLED")));
        }
    }

    // ─── BÚSQUEDA ─────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("GET /api/moviles/buscar")
    class BuscarTests {

        @Test
        @DisplayName("Búsqueda con precio (obligatorio) devuelve resultados")
        void buscarConPrecioObligatorio() throws Exception {
            when(movilService.buscar(isNull(), any(), any(), isNull(), isNull(), isNull(), isNull()))
                    .thenReturn(List.of(resumen1, resumen2));

            mockMvc.perform(get("/api/moviles/buscar")
                            .param("precioMin", "0")
                            .param("precioMax", "1000"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)));
        }

        @Test
        @DisplayName("Búsqueda con marca + precio devuelve resultado filtrado")
        void buscarConMarcaYPrecio() throws Exception {
            when(movilService.buscar(eq("Samsung"), any(), any(), isNull(), isNull(), isNull(), isNull()))
                    .thenReturn(List.of(resumen1));

            mockMvc.perform(get("/api/moviles/buscar")
                            .param("marca", "Samsung")
                            .param("precioMin", "500")
                            .param("precioMax", "900"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].marca", is("Samsung")));
        }

        @Test
        @DisplayName("Búsqueda sin precioMin debe devolver 400")
        void buscarSinPrecioMinDebe400() throws Exception {
            mockMvc.perform(get("/api/moviles/buscar")
                            .param("precioMax", "1000"))
                    .andExpect(status().isBadRequest());
        }
    }

    // ─── COMPARAR ─────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("GET /api/moviles/comparar")
    class CompararTests {

        @Test
        @DisplayName("Debe devolver los datos de dos móviles para comparar")
        void debeRetornarDosMoviles() throws Exception {
            MovilDetalleDTO detalle2 = new MovilDetalleDTO(
                    2, "Apple", "iPhone 15", "Apple A16 Bionic", 6,
                    new BigDecimal("3.46"), 128, 6, new BigDecimal("6.10"),
                    "OLED", null, null, null, 171, "48 Mpx + 12 Mpx",
                    3877, true, new BigDecimal("899.00"), null
            );

            when(movilService.comparar(1, 2)).thenReturn(List.of(detalleDTO, detalle2));

            mockMvc.perform(get("/api/moviles/comparar")
                            .param("id1", "1")
                            .param("id2", "2"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].marca", is("Samsung")))
                    .andExpect(jsonPath("$[1].marca", is("Apple")));
        }
    }

    // ─── CRUD ADMIN ───────────────────────────────────────────────────────────

    @Nested
    @DisplayName("CRUD Admin — Seguridad y respuestas")
    class CrudAdminTests {

        @Test
        @DisplayName("POST /api/moviles sin autenticación debe devolver 401/403")
        void postSinAutenticacionDebe403() throws Exception {
            MovilCreateDTO dto = TestDataFactory.buildMovilCreateDTO();

            mockMvc.perform(post("/api/moviles")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("POST /api/moviles con ROLE_ADMIN debe devolver 201")
        @WithMockUser(roles = "ADMIN")
        void postConAdminDebe201() throws Exception {
            MovilCreateDTO dto = TestDataFactory.buildMovilCreateDTO();
            when(movilService.create(any(MovilCreateDTO.class))).thenReturn(detalleDTO);

            mockMvc.perform(post("/api/moviles")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.marca", is("Samsung")));
        }

        @Test
        @DisplayName("DELETE /api/moviles/{id} sin ADMIN debe devolver 403")
        void deleteSinAdminDebe403() throws Exception {
            mockMvc.perform(delete("/api/moviles/1"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("DELETE /api/moviles/{id} con ROLE_ADMIN debe devolver 204")
        @WithMockUser(roles = "ADMIN")
        void deleteConAdminDebe204() throws Exception {
            mockMvc.perform(delete("/api/moviles/1").with(csrf()))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("GET /api/moviles (listado completo) sin ADMIN debe devolver 403")
        void getAllSinAdminDebe403() throws Exception {
            mockMvc.perform(get("/api/moviles"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("GET /api/moviles con ROLE_ADMIN debe devolver 200")
        @WithMockUser(roles = "ADMIN")
        void getAllConAdminDebe200() throws Exception {
            when(movilService.getAll()).thenReturn(List.of(resumen1, resumen2));

            mockMvc.perform(get("/api/moviles"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)));
        }
    }
}
