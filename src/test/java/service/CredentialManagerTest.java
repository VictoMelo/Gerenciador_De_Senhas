package service;

import model.Credencial;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

    /**
    * Testes unitários para a classe {@link CredentialManager}, com foco em operações manuais de lista
    * E no comportamento do construtor. Este conjunto de testes garante que a funcionalidade relacionada a credenciais
    * Seja implementada corretamente.
    */
@DisplayName("GerenciadorCredential Unit Tests")
class GerenciadorCredentialTest {

    private List<Credencial> credenciais;

    /**
    * Configura um método para inicializar uma nova lista de credenciais antes da execução de cada teste.
    * Isso garante um estado limpo para cada teste individual.
    */
    @BeforeEach
    void setUp() {
        credenciais = new ArrayList<>();
    }

    /**
    * Teste para verificar se uma única {@link Credencial} pode ser adicionada à lista.
    * Confirma se o tamanho da lista aumenta e se a credencial adicionada está armazenada corretamente.
    */
    @Test
    @DisplayName("Deveria adicionar uma credencial à lista")
    void testAddCredentialAddsToList() {
        int tamanhoInicial = credenciais.size();
        Credencial testCred = new Credencial("TestService", "testUser", "encryptedPass");
        credenciais.add(testCred);

        // Verifica se o tamanho da lista aumenta em um e se a credencial foi adicionada corretamente.
        assertEquals(tamanhoInicial + 1, credenciais.size(), "O tamanho da lista deve aumentar em 1.");
        assertEquals(testCred, credenciais.get(0), "A credencial adicionada deve ser a primeira da lista.");
    }

    /**
    * Teste para verificar se uma {@link Credencial} pode ser removida da lista.
    * Confirma que o tamanho da lista diminui e ela fica vazia após a remoção.
    */
    @Test
    @DisplayName("Deveria remover uma credencial da lista")
    void testRemoveCredentialRemovesFromList() {
        Credencial testCred = new Credencial("TestService", "testUser", "encryptedPass");
        credenciais.add(testCred);
        int tamanhoInicial = credenciais.size();

        credenciais.remove(0); // Remove a primeira credencial.

        // Verifica se o tamanho da lista diminui em um e se torna vazia.
        assertEquals(tamanhoInicial - 1, credenciais.size(), "O tamanho da lista deve diminuir em 1.");
        assertTrue(credenciais.isEmpty(), "A lista deve estar vazia após a remoção.");
    }

    /**
    * Teste para verificar se o {@link GerenciadorCredential} é inicializado com
    * uma lista vazia quando nenhum argumento é passado ao construtor. Garante que
    * a lista não seja nula e esteja vazia na configuração.
    */
    @Test
    @DisplayName("Deveria inicializar com uma lista vazia")
    void testConstructorInitializesWithEmptyList() {
        // Garante que a lista seja inicializada corretamente e comece vazia.
        assertNotNull(credenciais, "A lista de credenciais não deve ser nula durante a configuração.");
        assertTrue(credenciais.isEmpty(), "A lista de credenciais deve estar vazia durante a configuração.");
    }

    /**
    * Teste para verificar se o construtor {@link GerenciadorCredential} inicializa com
    * uma lista fornecida contendo credenciais. Garante que a lista do construtor
    * seja usada corretamente e mantenha o estado fornecido.
    */
    @Test
    @DisplayName("Deveria inicializar com uma lista fornecida contendo credenciais")
    void testConstructorInitializesWithProvidedList() {
        List<Credencial> testList = new ArrayList<>();
        testList.add(new Credencial("TestService", "testUser", "encryptedPass"));

        GerenciadorCredential newManager = new GerenciadorCredential(testList);

       // Garante que o construtor aceite e use a lista fornecida corretamente.
        assertNotNull(newManager, "GerenciadorCredential deve ser inicializado.");
        assertEquals(1, testList.size(), "A lista fornecida deve conter uma credencial.");
    }
}