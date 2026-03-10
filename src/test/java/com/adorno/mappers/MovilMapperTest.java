package com.adorno.mappers;

import com.adorno.TestDataFactory;
import com.adorno.model.dtos.MovilCreateDTO;
import com.adorno.model.dtos.MovilDetalleDTO;
import com.adorno.model.dtos.MovilResumenDTO;
import com.adorno.model.entities.Movil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests unitarios del MovilMapper.
 * No necesitan Spring ni BD — son tests puros de Java.
 */
@DisplayName("MovilMapper — Tests unitarios")
class MovilMapperTest {

    private MovilMapper mapper;

    @BeforeEach
    void setUp() {
        // Instancia directa sin Spring (es una clase abstracta con implementación manual)
        mapper = new MovilMapper() {};
    }

    // ─── toEntity ─────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("toEntity(MovilCreateDTO)")
    class ToEntityTests {

        @Test
        @DisplayName("Debe mapear todos los campos correctamente")
        void debeMapearTodosLosCampos() {
            MovilCreateDTO dto = TestDataFactory.buildMovilCreateDTO();

            Movil resultado = mapper.toEntity(dto);

            assertThat(resultado).isNotNull();
            assertThat(resultado.getMarca()).isEqualTo("Apple");
            assertThat(resultado.getModelo()).isEqualTo("iPhone 15");
            assertThat(resultado.getCpuTipo()).isEqualTo("Apple A16 Bionic");
            assertThat(resultado.getCpuNucleos()).isEqualTo(6);
            assertThat(resultado.getCpuGhz()).isEqualByComparingTo("3.46");
            assertThat(resultado.getAlmacenamientoGb()).isEqualTo(128);
            assertThat(resultado.getRamGb()).isEqualTo(6);
            assertThat(resultado.getPantallaPulgadas()).isEqualByComparingTo("6.10");
            assertThat(resultado.getPantallaTecnologia()).isEqualTo("OLED");
            assertThat(resultado.getPesoGr()).isEqualTo(171);
            assertThat(resultado.getCamaraCapacidad()).isEqualTo("48 Mpx + 12 Mpx");
            assertThat(resultado.getBateriaMah()).isEqualTo(3877);
            assertThat(resultado.getNfc()).isTrue();
            assertThat(resultado.getPrecio()).isEqualByComparingTo("899.00");
        }

        @Test
        @DisplayName("Consultas debe inicializarse a 0")
        void consultasDebeInicializarseACero() {
            Movil resultado = mapper.toEntity(TestDataFactory.buildMovilCreateDTO());
            assertThat(resultado.getConsultas()).isEqualTo(0);
        }

        @Test
        @DisplayName("NFC null debe convertirse a false")
        void nfcNullDebeSerFalse() {
            MovilCreateDTO dto = TestDataFactory.buildMovilCreateDTO();
            dto.setNfc(null);

            Movil resultado = mapper.toEntity(dto);

            assertThat(resultado.getNfc()).isFalse();
        }

        @Test
        @DisplayName("DTO null debe devolver null")
        void dtoNullDebeRetornarNull() {
            assertThat(mapper.toEntity(null)).isNull();
        }
    }

    // ─── toDetalleDTO ─────────────────────────────────────────────────────────

    @Nested
    @DisplayName("toDetalleDTO(Movil)")
    class ToDetalleDTOTests {

        @Test
        @DisplayName("Debe mapear todos los campos de la entidad al DTO detalle")
        void debeMapearTodosLosCampos() {
            Movil movil = TestDataFactory.buildMovil();

            MovilDetalleDTO resultado = mapper.toDetalleDTO(movil);

            assertThat(resultado).isNotNull();
            assertThat(resultado.marca()).isEqualTo("Samsung");
            assertThat(resultado.modelo()).isEqualTo("Galaxy S24");
            assertThat(resultado.cpuTipo()).isEqualTo("Snapdragon 8 Gen 3");
            assertThat(resultado.cpuNucleos()).isEqualTo(8);
            assertThat(resultado.cpuGhz()).isEqualByComparingTo("3.30");
            assertThat(resultado.almacenamientoGb()).isEqualTo(256);
            assertThat(resultado.ramGb()).isEqualTo(8);
            assertThat(resultado.pantallaPulgadas()).isEqualByComparingTo("6.20");
            assertThat(resultado.pantallaTecnologia()).isEqualTo("AMOLED");
            assertThat(resultado.nfc()).isTrue();
            assertThat(resultado.precio()).isEqualByComparingTo("799.99");
        }

        @Test
        @DisplayName("Entidad null debe devolver null")
        void entidadNullDebeRetornarNull() {
            assertThat(mapper.toDetalleDTO(null)).isNull();
        }
    }

    // ─── toResumenDTO ─────────────────────────────────────────────────────────

    @Nested
    @DisplayName("toResumenDTO(Movil)")
    class ToResumenDTOTests {

        @Test
        @DisplayName("Debe mapear solo los campos del resumen")
        void debeMapearCamposResumen() {
            Movil movil = TestDataFactory.buildMovil();

            MovilResumenDTO resultado = mapper.toResumenDTO(movil);

            assertThat(resultado).isNotNull();
            assertThat(resultado.marca()).isEqualTo("Samsung");
            assertThat(resultado.modelo()).isEqualTo("Galaxy S24");
            assertThat(resultado.cpuNucleos()).isEqualTo(8);
            assertThat(resultado.ramGb()).isEqualTo(8);
            assertThat(resultado.almacenamientoGb()).isEqualTo(256);
            assertThat(resultado.precio()).isEqualByComparingTo("799.99");
        }

        @Test
        @DisplayName("Entidad null debe devolver null")
        void entidadNullDebeRetornarNull() {
            assertThat(mapper.toResumenDTO(null)).isNull();
        }
    }

    // ─── updateEntityFromDTO ──────────────────────────────────────────────────

    @Nested
    @DisplayName("updateEntityFromDTO(dto, movil)")
    class UpdateEntityTests {

        @Test
        @DisplayName("Debe actualizar los campos de la entidad con los del DTO")
        void debeActualizarCampos() {
            Movil movil = TestDataFactory.buildMovil(); // Samsung S24
            MovilCreateDTO dto = TestDataFactory.buildMovilCreateDTO(); // iPhone 15
            dto.setMarca("Google");
            dto.setModelo("Pixel 9");
            dto.setPrecio(new BigDecimal("799.00"));

            mapper.updateEntityFromDTO(dto, movil);

            assertThat(movil.getMarca()).isEqualTo("Google");
            assertThat(movil.getModelo()).isEqualTo("Pixel 9");
            assertThat(movil.getPrecio()).isEqualByComparingTo("799.00");
        }

        @Test
        @DisplayName("DTO null no debe modificar la entidad")
        void dtoNullNoDebeModificarEntidad() {
            Movil movil = TestDataFactory.buildMovil();
            String marcaOriginal = movil.getMarca();

            mapper.updateEntityFromDTO(null, movil);

            assertThat(movil.getMarca()).isEqualTo(marcaOriginal);
        }
    }
}
