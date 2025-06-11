package service;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;

/**
* O EncryptionService fornece criptografia e descriptografia seguras de dados confidenciais usando AES-GCM.
* A chave de criptografia é derivada da senha mestra do usuário e de um salt persistente usando PBKDF2.
* A chave e o salt são mantidos na memória apenas durante a sessão e limpos no desligamento da JVM.
* - Após a autenticação, chame setSessionKeyAndSalt(masterPassword, salt) para inicializar a chave de sessão.
* A  `setSessionKeyAndSalt` deve ser chamado antes de encrypt() ou decrypt() para evitar erros.
* - Use encrypt() e decrypt() para operações de dados seguras.
* - O salt persistente é gerenciado em encryption_salt.dat.
* - Chaves e salts são limpos da memória no desligamento da JVM por meio de um hook de desligamento.
* - AES/GCM/NoPadding é usado para criptografia, garantindo a criptografia autenticada.
*/
public class EncriptacaoService {

	private static String chaveDeSessao = null;
	private static String sessaoSalt = null;

	public static void setSessionKeyAndSalt(String key, String salt) {
		chaveDeSessao = key;
		sessaoSalt = salt;
	}

	private static SecretKey getSessionSecretKey() throws Exception {
		if (chaveDeSessao == null || sessaoSalt == null) {
			throw new IllegalStateException("Session key and salt must be set before encryption/decryption.");
		}
		return getSecretKey(chaveDeSessao, sessaoSalt);
	}

	public static void clearSessionKeyAndSalt() {
		chaveDeSessao = null;
		sessaoSalt = null;
	}

	// Chame este método no desligamento da JVM para limpar dados confidenciais da memória
	static {
		Runtime.getRuntime().addShutdownHook(new Thread(EncriptacaoService::clearSessionKeyAndSalt));
	}

	/**
	* Gera uma SecretKey a partir de uma senha e um salt usando PBKDF2 com HMAC SHA-256.
	* a senha da qual derivar a chave
	* os bytes do salt como string
	* uma SecretKey adequada para criptografia AES
	* se a geração da chave falhar
	 
	 * @param password 
	 * @param salt     
	 * @return 
	 * @throws Exception 
	 */
	public static SecretKey getSecretKey(String password, String salt) throws Exception {
		byte[] saltBytes = salt.getBytes();
		SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
		KeySpec spec = new PBEKeySpec(password.toCharArray(), saltBytes, 65536, 256);
		SecretKey tmp = factory.generateSecret(spec);
		return new SecretKeySpec(tmp.getEncoded(), "AES");
	}

	/**
	* Criptografa uma string de texto simples usando AES/CBC/PKCS5Padding.
	* Um IV aleatório é gerado e anexado aos dados criptografados.
	* O resultado é codificado em Base64.
	* String de texto simples a ser criptografada
	* String codificada em Base64 de IV + dados criptografados
	* se a criptografia falhar
	 
	 * @param strToEncrypt 
	 * @return
	 * @throws Exception 
	 */
	public static String encrypt(String strToEncrypt) throws Exception {
		if (strToEncrypt == null) {
			throw new NullPointerException("A entrada para criptografar não pode ser nula");
		}
		SecretKey key = getSessionSecretKey();
		Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
		byte[] iv = new byte[12];
		SecureRandom sr = new SecureRandom();
		sr.nextBytes(iv);
		GCMParameterSpec gcmSpec = new GCMParameterSpec(128, iv);
		cipher.init(Cipher.ENCRYPT_MODE, key, gcmSpec);
		byte[] encrypted = cipher.doFinal(strToEncrypt.getBytes());
		byte[] encryptedWithIv = new byte[iv.length + encrypted.length];
		System.arraycopy(iv, 0, encryptedWithIv, 0, iv.length);
		System.arraycopy(encrypted, 0, encryptedWithIv, iv.length, encrypted.length);
		return Base64.getEncoder().encodeToString(encryptedWithIv);
	}

	/**
	 * Descriptografa uma string codificada em Base64 que contém um IV de 16 bytes prefixado aos dados criptografados.
	 * String codificada em Base64 contendo IV + dados criptografados
	 * A string de texto plano descriptografada
	 * Se a descriptografia falhar
	 
	 * @param strToDecrypt 
	 * @return 
	 * @throws Exception 
	 */
	public static String decrypt(Object strToDecrypt) throws Exception {
		try {
			SecretKey key = getSessionSecretKey();
			byte[] encryptedIvTextBytes = Base64.getDecoder().decode((String) strToDecrypt);
			if (encryptedIvTextBytes.length < 13) {
				throw new IllegalArgumentException("Invalid encrypted input length");
			}
			byte[] iv = new byte[12];
			System.arraycopy(encryptedIvTextBytes, 0, iv, 0, iv.length);
			byte[] encryptedBytes = new byte[encryptedIvTextBytes.length - iv.length];
			System.arraycopy(encryptedIvTextBytes, iv.length, encryptedBytes, 0, encryptedBytes.length);
			Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
			GCMParameterSpec gcmSpec = new GCMParameterSpec(128, iv);
			cipher.init(Cipher.DECRYPT_MODE, key, gcmSpec);
			byte[] decrypted = cipher.doFinal(encryptedBytes);
			return new String(decrypted);
		} catch (Exception e) {
			throw new Exception("Decryption failed", e);
		}
	}

	//Utilitário para gerar ou carregar um salt persistente para PBKDF2
	public static String getOrCreatePersistentSalt() throws Exception {
		java.nio.file.Path saltPath = java.nio.file.Paths.get("encryption_salt.dat"); // Alterado para .dat
		if (java.nio.file.Files.exists(saltPath)) {
			return java.nio.file.Files.readString(saltPath).trim();
		}
		// Gerar um novo sal aleatório (16 bytes, base64 encoded)
		byte[] saltBytes = new byte[16];
		new SecureRandom().nextBytes(saltBytes);
		String salt = Base64.getEncoder().encodeToString(saltBytes);
		java.nio.file.Files.writeString(saltPath, salt);
		return salt;
	}

}