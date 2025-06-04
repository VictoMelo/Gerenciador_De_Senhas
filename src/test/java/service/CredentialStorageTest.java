package service;

import model.Credencial;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the CredentialStorage class.
 * These tests validate the saving and loading of encrypted credentials.
 */
@DisplayName("CredentialStorage Unit Tests")
class CredentialStorageTest {

    private static final Path TEST_FILE_PATH = Paths.get("credentials.dat");
    private static final Path BACKUP_FILE_PATH = Paths.get("credentials_backup.dat");

    /**
     * Sets a session key and salt before each test to enable encryption/decryption.
     */
    @BeforeEach
    void setUp() {
        EncryptionService.setSessionKeyAndSalt("testMasterPassword", "testSalt123");
    }

    /**
     * Tests that credentials are saved to disk and properly reloaded,
     * preserving the original data after decryption.
     */
    @Test
    @DisplayName("Should correctly save and load credentials")
    void testSaveAndLoadCredentials() throws Exception {
        List<Credencial> credentials = new ArrayList<>();
        credentials.add(new Credencial("service1", "user1", EncryptionService.encrypt("pass1")));
        credentials.add(new Credencial("service2", "user2", EncryptionService.encrypt("pass2")));

        CredencialStorage.saveCredenciais(credentials);

        List<Credencial> loadedCredentials = CredencialStorage.carregaCredenciais();

        assertEquals(2, loadedCredentials.size());
        assertEquals("service1", loadedCredentials.get(0).nomeServico());
        assertEquals("user1", loadedCredentials.get(0).nomeUsuario());
        assertEquals("pass1", EncryptionService.decrypt(loadedCredentials.get(0).senhaCriptografada()));
    }

    /**
     * Tests that loading credentials returns an empty list when no file exists.
     */
    @Test
    @DisplayName("Should return an empty list when the credentials file does not exist")
    void testLoadCredentialsWhenFileDoesNotExist() throws Exception {
        Files.deleteIfExists(TEST_FILE_PATH);

        List<Credencial> loadedCredentials = CredencialStorage.carregaCredenciais();

        assertNotNull(loadedCredentials, "Returned list should not be null");
        assertTrue(loadedCredentials.isEmpty(), "Returned list should be empty when file is missing");
    }

    /**
     * Deletes any leftover files and clears the encryption session after each test.
     */
    @AfterEach
    void tearDown() throws Exception {
        Files.deleteIfExists(TEST_FILE_PATH);
        Files.deleteIfExists(BACKUP_FILE_PATH);
        EncryptionService.clearSessionKeyAndSalt();
    }
}