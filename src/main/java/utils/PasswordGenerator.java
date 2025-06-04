package utils;

import service.PasswordBreachChecker;
import java.security.SecureRandom;

public class PasswordGenerator {
    private static final String MAIUSCULO = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String MINUSCULO = "abcdefghijklmnopqrstuvwxyz";
    private static final String NUMEROS = "0123456789";
    private static final String SIMBOLOS = "!@#$%&*()-_=+[]{}";
    private static final SecureRandom random = new SecureRandom();

    /**
    * Gera uma senha forte com base nas preferências do usuário.
    * O comprimento da senha gerada.
    * Se deve incluir letras maiúsculas.
    * Se deve incluir letras minúsculas.
    * Se deve incluir dígitos numéricos.
    * Se deve incluir caracteres especiais.
    * Uma senha gerada aleatoriamente como uma String.
     
     * @param length           
     * @param includeUppercase 
     * @param includeLowercase 
     * @param includeNumbers   
     * @param includeSymbols   
     * @return 
     */
    public static String generate(int tamanho, boolean incluiMaisculo, boolean incluiMinusculo, boolean incluiNumeros, boolean incluiSimbolos) {

        StringBuilder characterPool = new StringBuilder();
        if (incluiMaisculo) characterPool.append(MAIUSCULO);
        if (incluiMinusculo) characterPool.append(MINUSCULO);
        if (incluiNumeros) characterPool.append(NUMEROS);
        if (incluiSimbolos) characterPool.append(SIMBOLOS);

        if (characterPool.isEmpty() || tamanho <= 0) {
            throw new IllegalArgumentException("Invalid parameters for password generation.");
        }

        String senha;
        int contador;
        do {
            StringBuilder passwordBuilder = new StringBuilder(tamanho);
            for (int i = 0; i < tamanho; i++) {
                int index = random.nextInt(characterPool.length());
                passwordBuilder.append(characterPool.charAt(index));
            }
            senha = passwordBuilder.toString();
            
            // Verifique com o verificador de violação de senha
            contador = PasswordBreachChecker.checarSenha(senha);

            if (contador > 0) {
                System.out.printf("Senha gerada encontrada em %d violação(ões). Regenerando uma senha mais segura...%n", contador);
            }
        } while (contador > 0); // Regenerar se a senha for comprometida

        return senha;
    }
}