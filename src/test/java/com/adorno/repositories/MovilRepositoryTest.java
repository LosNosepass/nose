package com.adorno.repositories;

import com.adorno.TestDataFactory;
import com.adorno.model.entities.Movil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests de integración del MovilRepository.
 * Usa @DataJpaTest con H2 en memoria — no necesita MySQL.
 */
@DataJpaTest
@ActiveProfiles("test")
@DisplayName("MovilRepository — Tests de integración con H2")
class MovilRepositoryTest {

    @Autowired
    private MovilRepository movilRepository;

    private Movil samsung;
    private Movil apple;
    private Movil xiaomi;

    @BeforeEach
    void setUp() {
        movilRepository.deleteAll();

        samsung = movilRepository.save(
                TestDataFactory.buildMovil("Samsung", "Galaxy S24", new BigDecimal("799.99"), 8)
        );
        apple = movilRepository.save(
                TestDataFactory.buildMovil("Apple", "iPhone 15", new BigDecimal("899.00"), 6)
        );
        xiaomi = movilRepository.save(
                TestDataFactory.buildMovil("Xiaomi", "Mi 14", new BigDecimal("499.00"), 12)
        );

        // Simula consultas: samsung es el más popular
        samsung.setConsultas(50);
        apple.setConsultas(30);
        xiaomi.setConsultas(10);
        movilRepository.save(samsung);
        movilRepository.save(apple);
        movilRepository.save(xiaomi);
    }

    // ─── TENDENCIAS ───────────────────────────────────────────────────────────

    @Nested
    @DisplayName("findTop5ByConsultas()")
    class Top5Tests {

        @Test
        @DisplayName("Debe devolver los móviles ordenados por consultas descendente")
        void debeOrdenarPorConsultasDesc() {
            List<Movil> top5 = movilRepository.findTop5ByConsultas(PageRequest.of(0, 5));

            assertThat(top5).hasSize(3);
            assertThat(top5.get(0).getMarca()).isEqualTo("Samsung"); // 50 consultas
            assertThat(top5.get(1).getMarca()).isEqualTo("Apple");   // 30 consultas
            assertThat(top5.get(2).getMarca()).isEqualTo("Xiaomi");  // 10 consultas
        }

        @Test
        @DisplayName("Debe limitar a 5 resultados aunque haya más")
        void debeLimitarA5() {
            // Añadimos más móviles
            for (int i = 1; i <= 5; i++) {
                movilRepository.save(TestDataFactory.buildMovil(
                        "Marca" + i, "Modelo" + i, new BigDecimal("300.00"), 4
                ));
            }

            List<Movil> top5 = movilRepository.findTop5ByConsultas(PageRequest.of(0, 5));

            assertThat(top5).hasSize(5);
        }
    }

    // ─── MARCAS ───────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("findDistinctMarcas()")
    class MarcasTests {

        @Test
        @DisplayName("Debe devolver marcas únicas ordenadas alfabéticamente")
        void debeRetornarMarcasUnicasOrdenadas() {
            List<String> marcas = movilRepository.findDistinctMarcas();

            assertThat(marcas).containsExactly("Apple", "Samsung", "Xiaomi");
        }

        @Test
        @DisplayName("No debe repetir marcas aunque haya varios móviles de la misma")
        void noDebeRepetirMarcas() {
            movilRepository.save(TestDataFactory.buildMovil("Samsung", "Galaxy S23",
                    new BigDecimal("650.00"), 8));

            List<String> marcas = movilRepository.findDistinctMarcas();

            long countSamsung = marcas.stream().filter("Samsung"::equals).count();
            assertThat(countSamsung).isEqualTo(1);
        }
    }

    // ─── INCREMENTAR CONSULTAS ────────────────────────────────────────────────

    @Nested
    @DisplayName("incrementarConsultas(id)")
    class IncrementarConsultasTests {

        @Test
        @DisplayName("Debe incrementar el campo consultas en 1")
        void debeIncrementarConsultasEnUno() {
            Integer consultasAntes = samsung.getConsultas(); // 50

            movilRepository.incrementarConsultas(samsung.getId());

            Movil actualizado = movilRepository.findById(samsung.getId()).orElseThrow();
            assertThat(actualizado.getConsultas()).isEqualTo(consultasAntes + 1);
        }
    }

    // ─── SPECIFICATIONS (BÚSQUEDA DINÁMICA) ──────────────────────────────────

    @Nested
    @DisplayName("Búsqueda con Specifications")
    class SpecificationTests {

        @Test
        @DisplayName("Filtrar por marca devuelve solo móviles de esa marca")
        void filtrarPorMarca() {
            var spec = MovilSpecification.buscar(
                    "Samsung", BigDecimal.ZERO, new BigDecimal("9999"),
                    null, null, null, null
            );

            List<Movil> resultado = movilRepository.findAll(spec);

            assertThat(resultado).hasSize(1);
            assertThat(resultado.get(0).getMarca()).isEqualTo("Samsung");
        }

        @Test
        @DisplayName("Filtrar por rango de precio devuelve móviles dentro del rango")
        void filtrarPorRangoPrecio() {
            var spec = MovilSpecification.buscar(
                    null, new BigDecimal("400"), new BigDecimal("800"),
                    null, null, null, null
            );

            List<Movil> resultado = movilRepository.findAll(spec);

            assertThat(resultado).hasSize(2); // Samsung 799.99 + Xiaomi 499
            resultado.forEach(m ->
                    assertThat(m.getPrecio())
                            .isBetween(new BigDecimal("400"), new BigDecimal("800"))
            );
        }

        @Test
        @DisplayName("Filtrar por RAM mínima devuelve solo móviles con suficiente RAM")
        void filtrarPorRamMinima() {
            var spec = MovilSpecification.buscar(
                    null, BigDecimal.ZERO, new BigDecimal("9999"),
                    8, null, null, null
            );

            List<Movil> resultado = movilRepository.findAll(spec);

            // Solo Samsung (8GB) y Xiaomi (12GB) tienen >= 8GB
            assertThat(resultado).hasSize(2);
            resultado.forEach(m -> assertThat(m.getRamGb()).isGreaterThanOrEqualTo(8));
        }

        @Test
        @DisplayName("Filtrar por NFC devuelve solo móviles con/sin NFC")
        void filtrarPorNfc() {
            // Los móviles creados con buildMovil() tienen nfc=false
            var specSinNfc = MovilSpecification.buscar(
                    null, BigDecimal.ZERO, new BigDecimal("9999"),
                    null, null, false, null
            );

            List<Movil> resultado = movilRepository.findAll(specSinNfc);

            assertThat(resultado).isNotEmpty();
            resultado.forEach(m -> assertThat(m.getNfc()).isFalse());
        }

        @Test
        @DisplayName("Combinación de criterios devuelve resultado más específico")
        void combinarCriterios() {
            var spec = MovilSpecification.buscar(
                    "Samsung",
                    new BigDecimal("700"),
                    new BigDecimal("850"),
                    8, null, null, null
            );

            List<Movil> resultado = movilRepository.findAll(spec);

            assertThat(resultado).hasSize(1);
            assertThat(resultado.get(0).getMarca()).isEqualTo("Samsung");
            assertThat(resultado.get(0).getPrecio()).isEqualByComparingTo("799.99");
        }

        @Test
        @DisplayName("Criterios sin resultados devuelve lista vacía")
        void sinResultados() {
            var spec = MovilSpecification.buscar(
                    "MarcaQueNoExiste",
                    BigDecimal.ZERO, new BigDecimal("9999"),
                    null, null, null, null
            );

            List<Movil> resultado = movilRepository.findAll(spec);

            assertThat(resultado).isEmpty();
        }
    }
}
