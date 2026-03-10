package com.adorno;

import com.adorno.model.dtos.MovilCreateDTO;
import com.adorno.model.entities.Movil;
import com.adorno.model.entities.Usuario;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Clase de utilidades compartidas entre todos los tests.
 * Contiene métodos factory para crear objetos de prueba de forma consistente.
 */
public class TestDataFactory {

    // ─── Móviles ─────────────────────────────────────────────────────────────

    public static Movil buildMovil() {
        return Movil.builder()
                .marca("Samsung")
                .modelo("Galaxy S24")
                .cpuTipo("Snapdragon 8 Gen 3")
                .cpuNucleos(8)
                .cpuGhz(new BigDecimal("3.30"))
                .almacenamientoGb(256)
                .ramGb(8)
                .pantallaPulgadas(new BigDecimal("6.20"))
                .pantallaTecnologia("AMOLED")
                .dimAlto(new BigDecimal("14.70"))
                .dimAncho(new BigDecimal("7.06"))
                .dimGrosor(new BigDecimal("0.76"))
                .pesoGr(167)
                .camaraCapacidad("50 Mpx + 12 Mpx + 10 Mpx")
                .bateriaMah(4000)
                .nfc(true)
                .precio(new BigDecimal("799.99"))
                .fechaLanzamiento(LocalDate.of(2024, 1, 17))
                .consultas(0)
                .build();
    }

    public static Movil buildMovil(String marca, String modelo, BigDecimal precio, Integer ramGb) {
        return Movil.builder()
                .marca(marca)
                .modelo(modelo)
                .cpuTipo("Procesador Test")
                .cpuNucleos(8)
                .cpuGhz(new BigDecimal("2.80"))
                .almacenamientoGb(128)
                .ramGb(ramGb)
                .pantallaPulgadas(new BigDecimal("6.10"))
                .pantallaTecnologia("OLED")
                .dimAlto(new BigDecimal("15.00"))
                .dimAncho(new BigDecimal("7.10"))
                .dimGrosor(new BigDecimal("0.80"))
                .pesoGr(180)
                .camaraCapacidad("48 Mpx")
                .bateriaMah(4500)
                .nfc(false)
                .precio(precio)
                .consultas(0)
                .build();
    }

    public static MovilCreateDTO buildMovilCreateDTO() {
        return MovilCreateDTO.builder()
                .marca("Apple")
                .modelo("iPhone 15")
                .cpuTipo("Apple A16 Bionic")
                .cpuNucleos(6)
                .cpuGhz(new BigDecimal("3.46"))
                .almacenamientoGb(128)
                .ramGb(6)
                .pantallaPulgadas(new BigDecimal("6.10"))
                .pantallaTecnologia("OLED")
                .dimAlto(new BigDecimal("14.74"))
                .dimAncho(new BigDecimal("7.12"))
                .dimGrosor(new BigDecimal("0.78"))
                .pesoGr(171)
                .camaraCapacidad("48 Mpx + 12 Mpx")
                .bateriaMah(3877)
                .nfc(true)
                .precio(new BigDecimal("899.00"))
                .fechaLanzamiento(LocalDate.of(2023, 9, 22))
                .build();
    }

    // ─── Usuarios ─────────────────────────────────────────────────────────────

    public static Usuario buildUsuarioAdmin() {
        return Usuario.builder()
                .nombreUsuario("admin")
                .email("admin@test.com")
                .password("$2a$10$hashedpassword") // BCrypt simulado
                .rol("admin")
                .build();
    }

    public static Usuario buildUsuarioInvitado() {
        return Usuario.builder()
                .nombreUsuario("invitado")
                .email("invitado@test.com")
                .password("$2a$10$hashedpassword")
                .rol("invitado")
                .build();
    }
}
