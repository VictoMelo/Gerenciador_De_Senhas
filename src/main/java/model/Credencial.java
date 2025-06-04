package model;

/**
* Representa uma credencial de usuário salva para um serviço específico.
*/
public record Credencial(String nomeServico, String nomeUsuario, String encryptedPassword) {
	/**
	 * Constructs a new Credential.
	 
	 * Constructs a new Credential.
	 * o nome do serviço (e.g., "Gmail")
	 * o nome de usuário associado ao serviço
	 * senha, já criptografada
	 
	 * @param serviceName       
	 * @param username          
	 * @param encryptedPassword 
	 */
	public Credencial {
	}


	@Override
	public String toString() {
		return "Serviço: " + nomeServico + ", Nome de usuário: " + nomeUsuario;
	}


	public String senhaEncriptada() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'senhaEncriptada'");
	}


    public Object senhaCriptografada() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'senhaCriptografada'");
    }
}
