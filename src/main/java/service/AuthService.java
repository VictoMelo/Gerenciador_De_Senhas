package service;

import utils.InputSanitizer;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.regex.Pattern;
import org.mindrot.jbcrypt.BCrypt;

/**
* Gerencia a autenticação do usuário com senha mestra e verificação TOTP.
* Garante acesso seguro usando credenciais com hash e códigos baseados em tempo.
*/
public class AuthService {

    private static final String ARQUIVO_DE_SENHA = "master_password.dat";
    private static final int MAX_TENTATIVAS = 3;
    private static final int MAX_SENHA_TAMANHO = 64;
    private static final int MAX_TOTP_TAMANHO = 6;
    private static final Pattern NUMERO_PADRAO = Pattern.compile("\\d+");

    private final Scanner input;

    /**
     * Constrói o AuthService, inicializando a autenticação com
     * validação de senha e TOTP.
     * Gera ou carrega um segredo seguro.
     * Scanner usado para ler a entrada do usuário
     * se a autenticação falhar após o número máximo de tentativas

     * @param input 
     * @throws Exception 
     */
    public AuthService(Scanner input) throws Exception {
        this.input = input;
        String senhaMestreHash = loadOrCreatePassword();
        String segredoTotp = TOTPService.loadOrCreateSecret();

        System.out.println("\nA autenticação de dois fatores está habilitada.");
        System.out.println("Use este segredo em seu aplicativo autenticador, se ainda não estiver registrado:");
        System.out.println(TOTPService.getBase32Secret(segredoTotp));
        System.out.println("Como alternativa, escaneie um código QR usando este URL:");
        System.out.println(TOTPService.getOtpAuthUrl(segredoTotp, "usuario@exemplo.com", "Gerenciador de Senhas Seguras"));

        String sessionPassword = null;

        for (int tentativas = 1; tentativas <= MAX_TENTATIVAS; tentativas++) {
            try {
                System.out.print("\nDigite a senha mestra: ");
                String inputPassword = InputSanitizer.sanitize(input.nextLine(), MAX_SENHA_TAMANHO, false);

                if (!BCrypt.checkpw(inputPassword, senhaMestreHash)) {
                    System.out.println("Senha incorreta.");
                    continue;
                }

                System.out.print("Digite o código TOTP atual: ");
                String inputCode = InputSanitizer.sanitize(input.nextLine(), MAX_TOTP_TAMANHO, true);

                if (!NUMERO_PADRAO.matcher(inputCode).matches()) {
                    throw new IllegalArgumentException("Somente números são permitidos neste campo.");
                }

                if (TOTPService.validateCode(segredoTotp, inputCode)) {
                    System.out.println("Autenticação bem-sucedida.");
                    sessionPassword = inputPassword;
                    break;
                }
            } catch (IllegalArgumentException ex) {
                System.out.println("Entrada inválida. " + InputSanitizer.escapeForLog(ex.getMessage()));
            }
        }

        if (sessionPassword == null) {
            throw new Exception(" Autenticação falhou após o número máximo de tentativas.");
        }

        String salt = EncryptionService.getOrCreatePersistentSalt();
        EncryptionService.setSessionKeyAndSalt(sessionPassword, salt);
    }

    /**
     * Carrega o hash da senha mestra existente ou solicita que o usuário crie uma nova.
     * Verifica a senha em relação a violações de dados conhecidas e impõe um comprimento mínimo.
     * A senha mestra com hash
     * Se estiver lendo ou escrevendo, o arquivo de senha falha
     
     * @return 
     * @throws Exception 
     */
    String loadOrCreatePassword() throws Exception {
        Path path = Paths.get(ARQUIVO_DE_SENHA);

        if (Files.exists(path)) {
            return Files.readString(path).trim();
        }

        System.out.println("Nenhuma senha mestra encontrada. Crie uma agora.");

        String novaSenha;

        while (true) {
            try {
                System.out.print("Nova senha: ");
                novaSenha = InputSanitizer.sanitize(input.nextLine(), MAX_SENHA_TAMANHO, false);

                int count = PasswordBreachChecker.checarSenha(novaSenha);
                if (count < 0) {
                    System.out.println("Erro ao verificar a senha em busca de violações conhecidas. Tente novamente.");
                    continue;
                } else if (count > 0) {
                    System.out.printf(
                            "Esta senha apareceu em %d violações. Escolha uma senha mais forte.%n",
                            count
                    );
                    continue;
                }

                if (novaSenha.length() < 8) {
                    System.out.println("A senha deve ter pelo menos 8 caracteres. Tente novamente.");
                    continue;
                }

                System.out.print("Digite novamente a senha mestra para confirmar: ");
                String inputSenha = InputSanitizer.sanitize(input.nextLine(), MAX_SENHA_TAMANHO, false);

                if (!novaSenha.equals(inputSenha)) {
                    System.out.println("As senhas não coincidem. Tente novamente.");
                    continue;
                }

                break;
            } catch (IllegalArgumentException ex) {
                System.out.println("Entrada inválida. " + InputSanitizer.escapeForLog(ex.getMessage()));
            }
        }

        String hash = BCrypt.hashpw(novaSenha, BCrypt.gensalt());
        Files.writeString(path, hash);
        System.out.println("Senha mestra salva.");
        return hash;
    }
}