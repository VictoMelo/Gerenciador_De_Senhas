package model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link Credencial} record.
 * <p>
 * These tests validate the proper creation and usage of {@link Credencial},
 * including its {@code toString()}, equality logic, and {@code hashCode} behavior.
 */
@DisplayName("CredencialTest") // Display the name for the test class
class CredencialTest {

    /**
     * Tests the correct creation of a {@link Credencial} object with valid field values.
     */
    @Test
    @DisplayName("Deve criar Credencial com valores de campo corretos")
    void testCredencialCreation() {
        Credencial credenciais = new Credencial("Gmail", "user@example.com", "encryptedPass123");

        assertNotNull(credenciais);
        assertEquals("Gmail", credenciais.nomeServico());
        assertEquals("user@example.com", credenciais.nomeUsuario());
        assertEquals("encryptedPass123", credenciais.senhaEncriptada());
    }

    /**
     * Verifies the {@code toString()} method returns a correctly formatted string representation.
     */
    @Test
    @DisplayName("Deve retornar a string esperada do toString()")
    void testToString() {
        Credencial credenciais = new Credencial("Gmail", "user@example.com", "encryptedPass123");
        String expected = "Serviço: Gmail, Nome de Usuário: user@example.com";

        assertEquals(expected, credenciais.toString());
    }

    /**
    * Valida a igualdade entre dois objetos {@link Credencial} com os mesmos campos
    * e garante que objetos com campos diferentes não sejam considerados iguais.
    */
    @Test
    @DisplayName("Deve considerar credenciais iguais quando os campos corresponderem")
    void testEquality() {
        Credencial credencial1 = new Credencial("Gmail", "user@example.com", "encryptedPass123");
        Credencial credencial2 = new Credencial("Gmail", "user@example.com", "encryptedPass123");
        Credencial differentCredencial = new Credencial("Outlook", "user@example.com", "encryptedPass123");

        assertEquals(credencial1, credencial2);
        assertNotEquals(credencial1, differentCredencial);
    }

    /**
    * Confirma que o método {@code hashCode()} produz resultados consistentes para objetos {@link Credencial}
    * com os mesmos valores de campo.
    */
    @Test
    @DisplayName("Should return same hashCode for equal credentials")
    void testHashCode() {
        Credencial credencial1 = new Credencial("Gmail", "user@example.com", "encryptedPass123");
        Credencial credencial2 = new Credencial("Gmail", "user@example.com", "encryptedPass123");

        assertEquals(credencial1.hashCode(), credencial2.hashCode());
    }
}