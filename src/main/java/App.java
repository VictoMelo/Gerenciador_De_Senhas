import model.Credencial;
import service.AuthService;
import service.CredencialStorage;
import service.GerenciadorCredential;
import utils.InputSanitizer;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Scanner;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class App {

    /**
     * Principal ponto de entrada do Secure Password Manager.
     * Lida com a autenticação e interage com o usuário por meio da interface de linha de comando.
     * Argumentos de linha de comando (atualmente não utilizados).
     *
     * @param args Command-line arguments (currently unused).
     */
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        try {
            new AuthService(input);
        } catch (Exception e) {
            System.err.println("Authentication failed: " + e.getMessage());
            return;
        }

        List<Credencial> credenciais;
        try {
            credenciais = CredencialStorage.carregaCredenciais();
        } catch (Exception e) {
            System.err.println("Failed to load credentials: " + e.getMessage());
            return;
        }

        GerenciadorCredential gerenciador = new GerenciadorCredential(credenciais);
        gerenciador.showMenu();
    }

    /**
     * Checks if a password hash suffix has been found in known data breaches
     * using the Have I Been Pwned (HIBP) API.
     * The API implements k-anonymity, and only the SHA-1 hash prefix
     * is sent for checking.
     *
     * @param prefixo The first 5 characters of the SHA-1 hash of the password.
     * @param sufixo The remaining characters of the SHA-1 hash of the password.
     * @return {@code true} if the suffix was found in breaches; {@code false} otherwise.
     * @throws Exception If validation or connection fails.
     */
    static boolean checkPwned(String prefixo, String sufixo) throws Exception {
        try {
            prefixo = InputSanitizer.sanitize(prefixo, 5, false);
            sufixo = InputSanitizer.sanitize(sufixo, 100, false);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Input validation failed: " + e.getMessage());
        }

        HttpURLConnection conn = getHttpURLConnection(prefixo, sufixo);

        try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            String linha;
            while ((linha = in.readLine()) != null) {
                String[] parts = linha.split(":");
                if (parts.length > 0 && parts[0].equalsIgnoreCase(sufixo)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Configura uma conexão HTTP para consultar a API HIBP em busca de informações sobre violação de senha.
     * Os primeiros 5 caracteres do hash SHA-1 da senha.
     * Os caracteres restantes do hash SHA-1 (usados ​​apenas para validação).
     * Um objeto {@link HttpURLConnection} configurado, pronto para consultar a API.
     * Se o URI construído for inválido.
     * Se a conexão falhar. 

     * @param prefixo 
     * @param sufixo only).
     * @return 
     * @throws URISyntaxException 
     * @throws IOException 
     */
    private static HttpURLConnection getHttpURLConnection(String prefixo, String sufixo)
            throws URISyntaxException, IOException {

        if (!prefixo.matches("[A-Fa-f0-9]{5}")) {
            throw new IllegalArgumentException("O prefixo deve conter exatamente 5 caracteres hexadecimais.");
        }
        if (!sufixo.matches("[A-Fa-f0-9]+")) {
            throw new IllegalArgumentException("O sufixo deve conter apenas caracteres hexadecimais.");
        }

        URI uri = new URI("https", "api.pwnedpasswords.com", "/range/" + prefixo, null);
        URL url = uri.toURL();

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);
        return conn;
    }
}