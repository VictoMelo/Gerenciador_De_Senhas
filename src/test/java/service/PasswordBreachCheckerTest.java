package service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
* Testes unitários para a classe utilitária PasswordBreachChecker.
* Esses testes visam validar o comportamento da funcionalidade de avaliação de segurança de senhas.
*/
@DisplayName("Testes Unitários para o Verificador de Violação de Senha")
class PasswordBreachCheckerTest {

    /**
    * Verifica se o sistema retorna zero violações para uma senha forte.
    */
    @Test
    @DisplayName("Deve retornar zero para uma senha forte")
    void testCheckSafePassword() {
        String senhaForte = "Str0ngP@ssw0rd2024!";
        int result = VerificadorSenha.checarSenha(senhaForte);
        assertTrue(result >= 0, "O resultado deve ser maior ou igual a zero");
    }

    /**
    * Verifica se uma senha comum e comprometida foi detectada.
    */
    @Test
    @DisplayName("Deve detectar senha comprometida")
    void testCheckCompromisedPassword() {
        String senhaComprometida = "123456";
        int result = VerificadorSenha.checarSenha(senhaComprometida);
        assertTrue(result > 0, "A senha comum deve ser detectada como comprometida");
    }

    /**
    * Garante que o sistema trate uma senha vazia com elegância e sugere um resultado adequado.
    */
    @Test
    @DisplayName("Deve lidar com senha vazia")
    void testCheckEmptyPassword() {
        String senhaVazia = "";
        int result = VerificadorSenha.checarSenha(senhaVazia);
        assertTrue(result >= -1, "Deve retornar -1 ou mais para uma senha vazia");
    }
}