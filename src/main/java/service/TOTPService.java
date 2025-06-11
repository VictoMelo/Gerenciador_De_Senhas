package service;

import org.apache.commons.codec.binary.Base32;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;

public class TOTPService {
	private static final long TEMPO_PASSO_SEGUNDOS = 30;
	private static final int DIGITA_CODIGO = 6;
	private static final String ALGORITMO_HMAC = "HmacSHA1";
	private static final String ARQUIVO_SECRETO = "totp_secret.dat"; // Alterado para .dat

	/**
	 * Gera uma nova chave secreta codificada em Base64 (uso interno).
	 */
	public static String generateSecret() {
		byte[] randomBytes = new byte[20]; // 160 bits
		new SecureRandom().nextBytes(randomBytes);
		return Base64.getEncoder().encodeToString(randomBytes);
	}

	/**
	 * Converte um segredo codificado em Base64 para o formato Base32 (compatível com Google Authenticator).
	 */
	public static String getBase32Secret(String base64Secret) {
		Base32 base32 = new Base32();
		byte[] decodedBytes = Base64.getDecoder().decode(base64Secret);
		return base32.encodeToString(decodedBytes).replace("=", "").replace(" ", "");
	}

	/**
	 * Gera uma URL para adicionar o segredo ao Google Authenticator via QR code ou entrada manual.
	 *
	 * @param base64Secret O segredo codificado em Base64.
	 * @param accountName  O nome da conta do usuário (ex: user@example.com).
	 * @param issuer       O nome do aplicativo ou serviço (ex: SecurePasswordManager).
	 * @return Uma URL completa otpauth://.
	 */
	public static String getOtpAuthUrl(String base64Secret, String accountName, String issuer) {
		String base32Secret = getBase32Secret(base64Secret);
		return String.format("otpauth://totp/%s:%s?secret=%s&issuer=%s",
				issuer, accountName, base32Secret, issuer);
	}

	/**
	 * Valida um código TOTP digitado pelo usuário.
	 */
	public static boolean validateCode(String base64Secret, String inputCode) {
	    if (inputCode == null || inputCode.length() != DIGITA_CODIGO) {
	        System.out.println("Código TOTP inválido. Seu código deve conter " + DIGITA_CODIGO + " dígitos.");
	        return false;
	    }

	    if (!inputCode.matches("\\d{" + DIGITA_CODIGO + "}")) {
	        System.out.println("Código TOTP inválido. O código TOTP deve conter apenas dígitos numéricos.");
	        return false;
	    }

	    try {
	        long currentWindow = Instant.now().getEpochSecond() / TEMPO_PASSO_SEGUNDOS;
	        for (long offset = -1; offset <= 1; offset++) {
	            String expectedCode = generateCodeAtTime(base64Secret, currentWindow + offset);
	            if (expectedCode.equals(inputCode)) {
	                return true;
	            }
	        }
	    } catch (Exception e) {
	        System.err.println("TOTP validação falhou: " + e.getMessage());
	        return false;
	    }

	    System.out.println("Por favor, tente novamente.");
	    return false;
	}

	private static String generateCodeAtTime(String base64Secret, long timeWindow) throws Exception {
		byte[] key = Base64.getDecoder().decode(base64Secret);
		byte[] data = new byte[8];
		for (int i = 7; i >= 0; i--) {
			data[i] = (byte) (timeWindow & 0xFF);
			timeWindow >>= 8;
		}

		Mac mac = Mac.getInstance(ALGORITMO_HMAC);
		mac.init(new SecretKeySpec(key, ALGORITMO_HMAC));
		byte[] hmac = mac.doFinal(data);

		int offset = hmac[hmac.length - 1] & 0xF;
		int binary = ((hmac[offset] & 0x7F) << 24)
				| ((hmac[offset + 1] & 0xFF) << 16)
				| ((hmac[offset + 2] & 0xFF) << 8)
				| (hmac[offset + 3] & 0xFF);

		int otp = binary % (int) Math.pow(10, DIGITA_CODIGO);
		return String.format("%0" + DIGITA_CODIGO + "d", otp);
	}

	/**
	 * Carrega o segredo TOTP de um arquivo ou gera um novo e salva.
	 *
	 * @return String secreta codificada em Base64.
	 */
	public static String loadOrCreateSecret() {
		Path path = Paths.get(ARQUIVO_SECRETO);
		if (Files.exists(path)) {
			try {
				String secret = Files.readString(path).trim();
				if (!secret.isEmpty()) {
					return secret;
				}
			} catch (IOException e) {
				System.err.println("Falha ao ler o arquivo secreto: " + e.getMessage());
			}
		}
		// Generate a new secret and save it
		String newSecret = generateSecret();
		try {
			Files.writeString(path, newSecret);
		} catch (IOException e) {
			System.err.println("Falha ao gravar arquivo secreto: " + e.getMessage());
		}
		return newSecret;
	}
}