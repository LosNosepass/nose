package com.adorno;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Test de arranque del contexto Spring.
 * Verifica que la aplicación levanta correctamente con el perfil de test.
 */
@SpringBootTest
@ActiveProfiles("test")
class MovilesApiApplicationTests {

    @Test
    void contextLoads() {
        // Si el contexto arranca sin excepciones, el test pasa
    }
}
