package service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for the EncryptionService utility.
 * Focuses on encryption, decryption, and session key management functionalities.
 */
@DisplayName("Unit Tests for App Class Functionality")
class EncryptionServiceTest {

    /**
     * Tests consistency of secret key derivation for the same password and salt.
     *
     * @throws Exception if an error occurs during key derivation
     */
    @Test
    @DisplayName("Should derive the same key for same password and salt")
    void testGetSecretKeyConsistency() throws Exception {
        String password = "testPassword";
        String salt = "testSalt123";
        var key1 = EncriptacaoService.getSecretKey(password, salt);
        var key2 = EncriptacaoService.getSecretKey(password, salt);
        assertEquals(new String(key1.getEncoded()), new String(key2.getEncoded()));
    }

    /**
     * Tests encryption and decryption using a session key and salt.
     *
     * @throws Exception if an error occurs during encryption/decryption
     */
    @Test
    @DisplayName("Should encrypt and decrypt correctly with session key and salt")
    void testEncryptDecrypt() throws Exception {
        String password = "masterPass";
        String salt = "uniqueSalt!";
        EncriptacaoService.setSessionKeyAndSalt(password, salt);
        String original = "SensitiveData123!";
        String encrypted = EncriptacaoService.encrypt(original);
        String decrypted = EncriptacaoService.decrypt(encrypted);
        assertEquals(original, decrypted);
        EncriptacaoService.clearSessionKeyAndSalt();
    }

    /**
     * Ensures that an exception is thrown when trying to encrypt data
     * without setting a session key and salt.
     */
    @Test
    @DisplayName("Should throw when encrypting without session key and salt")
    void testEncryptWithoutSessionKey() {
        EncriptacaoService.clearSessionKeyAndSalt();
        assertThrows(IllegalStateException.class, () -> EncriptacaoService.encrypt("data"));
    }

    /**
     * Verifies that decryption fails when an incorrect session key is used.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    @DisplayName("Should throw when decrypting with wrong session key")
    void testDecryptWithWrongKey() throws Exception {
        String password = "rightPass";
        String salt = "rightSalt";
        EncriptacaoService.setSessionKeyAndSalt(password, salt);
        String encrypted = EncriptacaoService.encrypt("Secret");
        EncriptacaoService.setSessionKeyAndSalt("wrongPass", salt);
        assertThrows(Exception.class, () -> EncriptacaoService.decrypt(encrypted));
        EncriptacaoService.clearSessionKeyAndSalt();
    }

    /**
     * Tests persistent salt generation and ensures consistency across calls.
     *
     * @throws Exception if an error occurs during salt generation
     */
    @Test
    @DisplayName("Should generate and persist salt")
    void testGetOrCreatePersistentSalt() throws Exception {
        String salt1 = EncriptacaoService.getOrCreatePersistentSalt();
        String salt2 = EncriptacaoService.getOrCreatePersistentSalt();
        assertEquals(salt1, salt2);
        assertEquals(24, salt1.length()); // 16 bytes base64 = 24 chars
    }
}