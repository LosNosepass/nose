package com.adorno.services;

import com.adorno.TestDataFactory;
import com.adorno.mappers.MovilMapper;
import com.adorno.model.dtos.MovilCreateDTO;
import com.adorno.model.dtos.MovilDetalleDTO;
import com.adorno.model.dtos.MovilResumenDTO;
import com.adorno.model.entities.Movil;
import com.adorno.repositories.ConsultaLogRepository;
import com.adorno.repositories.MovilRepository;
import com.adorno.repositories.MovilSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios de MovilService.
 * Usa Mockito para simular repositorios — sin BD real.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("MovilService — Tests unitarios")
class MovilServiceTest {

    @Mock
    private MovilRepository movilRepository;

    @Mock
    private ConsultaLogRepository consultaLogRepository;

    @Mock
    private MovilMapper movilMapper;

    @InjectMocks
    private MovilService movilService;

    private Movil movilSamsung;
    private Movil movilApple;

    @BeforeEach
    void setUp() {
        movilSamsung = TestDataFactory.buildMovil();
        movilApple = TestDataFactory.buildMovil("Apple", "iPhone 15",
                new BigDecimal("899.00"), 6);
    }

    // ─── TENDENCIAS ───────────────────────────────────────────────────────────

    @Nested
    @DisplayName("getTendencias()")
    class GetTendenciasTests {

        @Test
        @DisplayName("Debe devolver los 5 móviles más consultados como ResumenDTO")
        void debeRetornarTop5() {
            List<Movil> top5 = List.of(movilSamsung, movilApple);
            MovilResumenDTO resumen1 = new MovilResumenDTO(1, "Samsung", "Galaxy S24", 8, 8, 256, new BigDecimal("799.99"));
            MovilResumenDTO resumen2 = new MovilResumenDTO(2, "Apple", "iPhone 15", 6, 6, 128, new BigDecimal("899.00"));

            when(movilRepository.findTop5ByConsultas(any(Pageable.class))).thenReturn(top5);
            when(movilMapper.toResumenDTO(movilSamsung)).thenReturn(resumen1);
            when(movilMapper.toResumenDTO(movilApple)).thenReturn(resumen2);

            List<MovilResumenDTO> resultado = movilService.getTendencias();

            assertThat(resultado).hasSize(2);
            assertThat(resultado.get(0).marca()).isEqualTo("Samsung");
            assertThat(resultado.get(1).marca()).isEqualTo("Apple");
            verify(movilRepository).findTop5ByConsultas(any(Pageable.class));
        }

        @Test
        @DisplayName("Debe devolver lista vacía si no hay móviles")
        void debeRetornarListaVaciaSiNoHayMoviles() {
            when(movilRepository.findTop5ByConsultas(any(Pageable.class))).thenReturn(List.of());

            List<MovilResumenDTO> resultado = movilService.getTendencias();

            assertThat(resultado).isEmpty();
        }
    }

    // ─── DETALLE ──────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("getDetalle(id)")
    class GetDetalleTests {

        @Test
        @DisplayName("Debe devolver el detalle e incrementar el contador de consultas")
        void debeRetornarDetalleEIncrementarConsultas() {
            Integer id = 1;
            MovilDetalleDTO detalleDTO = new MovilDetalleDTO(
                    id, "Samsung", "Galaxy S24",
                    "Snapdragon 8 Gen 3", 8, new BigDecimal("3.30"),
                    256, 8, new BigDecimal("6.20"), "AMOLED",
                    new BigDecimal("14.70"), new BigDecimal("7.06"), new BigDecimal("0.76"),
                    167, "50 Mpx", 4000, true,
                    new BigDecimal("799.99"), null
            );

            when(movilRepository.findById(id)).thenReturn(Optional.of(movilSamsung));
            when(movilMapper.toDetalleDTO(movilSamsung)).thenReturn(detalleDTO);

            MovilDetalleDTO resultado = movilService.getDetalle(id);

            assertThat(resultado).isNotNull();
            assertThat(resultado.marca()).isEqualTo("Samsung");
            // Verifica que se incrementó el contador
            verify(movilRepository).incrementarConsultas(id);
            // Verifica que se guardó en consultas_log
            verify(consultaLogRepository).save(any());
        }

        @Test
        @DisplayName("Debe lanzar 404 si el móvil no existe")
        void debeLanzar404SiNoExiste() {
            when(movilRepository.findById(999)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> movilService.getDetalle(999))
                    .isInstanceOf(ResponseStatusException.class)
                    .hasMessageContaining("404");
        }
    }

    // ─── BÚSQUEDA ─────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("buscar(criterios)")
    class BuscarTests {

        @Test
        @DisplayName("Búsqueda por marca y precio devuelve resultados correctos")
        void buscarPorMarcaYPrecio() {
            MovilResumenDTO resumen = new MovilResumenDTO(1, "Samsung", "Galaxy S24",
                    8, 8, 256, new BigDecimal("799.99"));

            when(movilRepository.findAll(any(Specification.class))).thenReturn(List.of(movilSamsung));
            when(movilMapper.toResumenDTO(movilSamsung)).thenReturn(resumen);

            List<MovilResumenDTO> resultado = movilService.buscar(
                    "Samsung",
                    new BigDecimal("500"),
                    new BigDecimal("1000"),
                    null, null, null, null
            );

            assertThat(resultado).hasSize(1);
            assertThat(resultado.get(0).marca()).isEqualTo("Samsung");
        }

        @Test
        @DisplayName("Búsqueda sin resultados devuelve lista vacía")
        void buscarSinResultados() {
            when(movilRepository.findAll(any(Specification.class))).thenReturn(List.of());

            List<MovilResumenDTO> resultado = movilService.buscar(
                    "MarcaInexistente",
                    BigDecimal.ZERO,
                    new BigDecimal("100"),
                    null, null, null, null
            );

            assertThat(resultado).isEmpty();
        }
    }

    // ─── MARCAS Y TECNOLOGÍAS ─────────────────────────────────────────────────

    @Nested
    @DisplayName("getMarcas() y getTecnologiasPantalla()")
    class CatalogoTests {

        @Test
        @DisplayName("getMarcas() debe devolver lista de marcas únicas")
        void debeRetornarMarcas() {
            when(movilRepository.findDistinctMarcas()).thenReturn(List.of("Apple", "Samsung", "Xiaomi"));

            List<String> marcas = movilService.getMarcas();

            assertThat(marcas).containsExactly("Apple", "Samsung", "Xiaomi");
        }

        @Test
        @DisplayName("getTecnologiasPantalla() debe devolver tecnologías únicas")
        void debeRetornarTecnologias() {
            when(movilRepository.findDistinctTecnologias()).thenReturn(List.of("AMOLED", "IPS", "OLED"));

            List<String> tecnologias = movilService.getTecnologiasPantalla();

            assertThat(tecnologias).containsExactly("AMOLED", "IPS", "OLED");
        }
    }

    // ─── COMPARACIÓN ─────────────────────────────────────────────────────────

    @Nested
    @DisplayName("comparar(id1, id2)")
    class CompararTests {

        @Test
        @DisplayName("Debe devolver el detalle de dos móviles")
        void debeRetornarDosMoviles() {
            MovilDetalleDTO detalle1 = new MovilDetalleDTO(1, "Samsung", "Galaxy S24",
                    null, 8, null, 256, 8, null, "AMOLED",
                    null, null, null, 167, null, 4000, true,
                    new BigDecimal("799.99"), null);
            MovilDetalleDTO detalle2 = new MovilDetalleDTO(2, "Apple", "iPhone 15",
                    null, 6, null, 128, 6, null, "OLED",
                    null, null, null, 171, null, 3877, true,
                    new BigDecimal("899.00"), null);

            when(movilRepository.findById(1)).thenReturn(Optional.of(movilSamsung));
            when(movilRepository.findById(2)).thenReturn(Optional.of(movilApple));
            when(movilMapper.toDetalleDTO(movilSamsung)).thenReturn(detalle1);
            when(movilMapper.toDetalleDTO(movilApple)).thenReturn(detalle2);

            List<MovilDetalleDTO> resultado = movilService.comparar(1, 2);

            assertThat(resultado).hasSize(2);
            assertThat(resultado.get(0).marca()).isEqualTo("Samsung");
            assertThat(resultado.get(1).marca()).isEqualTo("Apple");
        }
    }

    // ─── CRUD ADMIN ───────────────────────────────────────────────────────────

    @Nested
    @DisplayName("CRUD Admin (create, update, delete)")
    class CrudTests {

        @Test
        @DisplayName("create() debe guardar el móvil y devolver su detalle")
        void debeCrearMovil() {
            MovilCreateDTO dto = TestDataFactory.buildMovilCreateDTO();
            MovilDetalleDTO detalleEsperado = new MovilDetalleDTO(
                    1, "Apple", "iPhone 15", "Apple A16 Bionic", 6,
                    new BigDecimal("3.46"), 128, 6, new BigDecimal("6.10"),
                    "OLED", null, null, null, 171, "48 Mpx + 12 Mpx",
                    3877, true, new BigDecimal("899.00"), null
            );

            when(movilMapper.toEntity(dto)).thenReturn(movilApple);
            when(movilRepository.save(any(Movil.class))).thenReturn(movilApple);
            when(movilMapper.toDetalleDTO(movilApple)).thenReturn(detalleEsperado);

            MovilDetalleDTO resultado = movilService.create(dto);

            assertThat(resultado.marca()).isEqualTo("Apple");
            assertThat(resultado.precio()).isEqualByComparingTo("899.00");
            verify(movilRepository).save(any(Movil.class));
        }

        @Test
        @DisplayName("update() debe modificar y guardar el móvil existente")
        void debeActualizarMovil() {
            Integer id = 1;
            MovilCreateDTO dto = TestDataFactory.buildMovilCreateDTO();
            dto.setPrecio(new BigDecimal("750.00"));
            MovilDetalleDTO detalleActualizado = new MovilDetalleDTO(
                    id, "Apple", "iPhone 15", null, 6, null,
                    128, 6, null, "OLED", null, null, null, 171,
                    null, 3877, true, new BigDecimal("750.00"), null
            );

            when(movilRepository.findById(id)).thenReturn(Optional.of(movilApple));
            when(movilRepository.save(any(Movil.class))).thenReturn(movilApple);
            when(movilMapper.toDetalleDTO(movilApple)).thenReturn(detalleActualizado);

            MovilDetalleDTO resultado = movilService.update(id, dto);

            assertThat(resultado.precio()).isEqualByComparingTo("750.00");
            verify(movilMapper).updateEntityFromDTO(eq(dto), eq(movilApple));
            verify(movilRepository).save(movilApple);
        }

        @Test
        @DisplayName("delete() debe eliminar el móvil si existe")
        void debeEliminarMovilExistente() {
            Integer id = 1;
            when(movilRepository.existsById(id)).thenReturn(true);

            movilService.delete(id);

            verify(movilRepository).deleteById(id);
        }

        @Test
        @DisplayName("delete() debe lanzar 404 si el móvil no existe")
        void debeLanzar404AlEliminarInexistente() {
            when(movilRepository.existsById(999)).thenReturn(false);

            assertThatThrownBy(() -> movilService.delete(999))
                    .isInstanceOf(ResponseStatusException.class)
                    .hasMessageContaining("404");

            verify(movilRepository, never()).deleteById(any());
        }
    }
}
