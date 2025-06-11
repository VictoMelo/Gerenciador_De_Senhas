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
            System.err.println("Autenticação falhou: " + e.getMessage());
            return;
        }

        List<Credencial> credenciais;
        try {
            credenciais = CredencialStorage.carregaCredenciais();
        } catch (Exception e) {
            System.err.println("Falha ao carregar credenciais: " + e.getMessage());
            return;
        }

        GerenciadorCredential gerenciador = new GerenciadorCredential(credenciais);
        gerenciador.showMenu();
    }

    /**
    * Verifica se um sufixo de hash de senha foi encontrado em violações de dados conhecidas
    * usando a API Have I Been Pwned (HIBP).
    * A API implementa o k-anonimato e apenas o prefixo de hash SHA-1
    * é enviado para verificação.
    * Os primeiros 5 caracteres do hash SHA-1 da senha.
    * Os caracteres restantes do hash SHA-1 da senha.
    * Se a validação ou a conexão falhar.
     
     * @param prefixo 
     * @param sufixo 
     * @return {@code true} se o sufixo foi encontrado em violações; {@code false} de outra forma.
     * @throws Exception 
     */
    static boolean checkPwned(String prefixo, String sufixo) throws Exception {
        try {
            prefixo = InputSanitizer.sanitize(prefixo, 5, false);
            sufixo = InputSanitizer.sanitize(sufixo, 100, false);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Falha na validação de entrada: " + e.getMessage());
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