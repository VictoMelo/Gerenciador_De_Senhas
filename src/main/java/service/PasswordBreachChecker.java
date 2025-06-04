package service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.net.URI;

public class PasswordBreachChecker {

    /**
     * Verifica se uma senha foi encontrada em vazamentos de dados conhecidos usando a "Have I Been Pwned API".
     * @param senha A senha a ser verificada.
     * @return Número de vezes que a senha foi encontrada em vazamentos (0 = segura).
     */
    public static int checarSenha(String senha) {
        try {
            // Passo 1: Hash SHA-1 da senha
            MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
            byte[] hashBytes = sha1.digest(senha.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02X", b));
            }
            String sha1Hash = sb.toString();
            String prefixo = sha1Hash.substring(0, 5);
            String sufixo = sha1Hash.substring(5);

            // Passo 2: Consulta a API com o prefixo
            URI uri = new URI("https", "api.pwnedpasswords.com", "/range/" + prefixo, null);
            URL url = uri.toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith(sufixo)) {
                    String[] parts = line.split(":");
                    return Integer.parseInt(parts[1].trim());
                }
            }
            // Não encontrado em vazamentos
            reader.close();
            return 0; 

        } catch (Exception e) {
            System.err.println("Erro ao verificar violação de senha: " + e.getMessage());
            return -1;
        }
    }
}