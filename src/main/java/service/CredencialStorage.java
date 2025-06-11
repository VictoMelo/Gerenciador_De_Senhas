package service;

import model.Credencial;
import utils.InputSanitizer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

/**
 * Responsável por salvar e carregar credenciais de/para um arquivo criptografado.
 */
public class CredencialStorage {
    private static final Path FILE_PATH = Paths.get("credentials.dat");

    /**
     * Salva uma lista de credenciais em um arquivo criptografado.
     * A lista de credenciais a ser salva.
     * Se ocorrer um erro durante a criptografia ou gravação do arquivo.
     * 
     * @param credentials 
     * @throws Exception 
     */
    public static void saveCredenciais(List<Credencial> credenciais) throws Exception {
        List<String> encryptedLines = new ArrayList<>();

        for (Credencial cred : credenciais) {
            String nomeServico;
            String nomeUsuario;
            String senhaEncriptada;

            try {
                // Garante que todos os campos estão sanitizados
                nomeServico = InputSanitizer.sanitize(cred.nomeServico(), 50, false);
                nomeUsuario = InputSanitizer.sanitize(cred.nomeUsuario(), 50, false);
                senhaEncriptada = InputSanitizer.sanitize(cred.senhaEncriptada(), 128, false);

                String line = String.format("%s,%s,%s", nomeServico, nomeUsuario, senhaEncriptada);
                encryptedLines.add(EncriptacaoService.encrypt(line));
            } catch (IllegalArgumentException e) {
                System.err.println("Ignorando credencial inválida: " + e.getMessage());
            }
        }

        // Cria um backup do arquivo atual se ele existir
        if (Files.exists(FILE_PATH)) {
            Files.copy(FILE_PATH, Paths.get("credentials_backup.dat"), StandardCopyOption.REPLACE_EXISTING);
        }

        try (BufferedWriter writer = Files.newBufferedWriter(FILE_PATH)) {
            for (String line : encryptedLines) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            throw new IOException("Erro ao gravar no arquivo de credenciais: " + e.getMessage(), e);
        }
    }

    /**
     * Carrega e descriptografa credenciais do arquivo.
     *
     * @return Uma lista de credenciais descriptografadas.
     * @throws Exception Se ocorrer um erro durante a descriptografia ou leitura do arquivo.
     */
    public static List<Credencial> carregaCredenciais() throws Exception {
        List<Credencial> credenciais = new ArrayList<>();

        if (!Files.exists(FILE_PATH)) {
            return credenciais;
        }

        try (BufferedReader reader = Files.newBufferedReader(FILE_PATH)) {
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    String decrypted = EncriptacaoService.decrypt(line);
                    String[] parts = decrypted.split(",", 3);

                    if (parts.length == 3) {
                        // Sanitiza e valida as partes descriptografadas
                        String serviceName = InputSanitizer.sanitize(parts[0], 50, false);
                        String username = InputSanitizer.sanitize(parts[1], 50, false);
                        String senhaEncriptada = InputSanitizer.sanitize(parts[2], 128, false);

                        credenciais.add(new Credencial(serviceName, username, senhaEncriptada));
                    } else {
                        System.err.println("Formato de linha inválido: " + decrypted);
                    }
                } catch (IllegalArgumentException ex) {
                    System.err.println("Formato de credencial inválido: " + ex.getMessage());
                } catch (Exception ex) {
                    System.err.println("Erro ao descriptografar linha: " + ex.getMessage());
                }
            }
        } catch (IOException e) {
            throw new IOException("Erro ao ler o arquivo de credenciais: " + e.getMessage(), e);
        }

        return credenciais;
    }
}