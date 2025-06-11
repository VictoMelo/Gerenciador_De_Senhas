package model;

/**
* Representa uma credencial de usuário salva para um serviço específico.
*/
public record Credencial(String nomeServico, String nomeUsuario, String senhaEncriptada) {
	/**
	 * Constructs a new Credential.
	 
	 * Constructs a new Credential.
	 * o nome do serviço (e.g., "Gmail")
	 * o nome de usuário associado ao serviço
	 * senha, já criptografada
	 
	 * @param nomeServico       
	 * @param nomeUsuario          
	 * @param senhaEncriptada 
	 */
	public Credencial {
	}


	@Override
	public String toString() {
		return "Serviço: " + nomeServico + ", Nome de usuário: " + nomeUsuario;
	}






}
