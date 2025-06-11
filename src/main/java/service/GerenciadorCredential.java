package service;

import model.Credencial;
import utils.InputSanitizer;
import utils.PasswordGenerator;
import org.mindrot.jbcrypt.BCrypt;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.Toolkit;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

/**
 * Gerencia a interação do usuário para o gerenciamento de credenciais, incluindo
 * listar, adicionar, remover, buscar, descriptografar e copiar senhas.
 */
public class GerenciadorCredential {
	private final List<Credencial> credenciais;
	private final Scanner input = new Scanner(System.in);

	/**
	 * Inicializa o gerenciador de credenciais com uma lista de credenciais.
	 * As credenciais a serem gerenciadas.
	 * @param credenciais 
	 */
	public GerenciadorCredential(List<Credencial> credenciais) {
		this.credenciais = credenciais;
	}

	/**
	 * Exibe o menu interativo para gerenciamento de credenciais.
	 */
	public void showMenu() {
		while (true) {
			System.out.println("\n=== Gerenciador de Credenciais ===");
			System.out.println("1. Listar todas as credenciais");
			System.out.println("2. Adicione nova credencial");
			System.out.println("3. Delete a credencial");
			System.out.println("4. Copie uma senha para o clipboard");
			System.out.println("5. Verifique se alguma senha foi comprometida");
			System.out.println("6. Encerrar");
			System.out.print("Escolha uma opção: ");
			String option = input.nextLine();

			// Opções do menu tratadas usando expressão switch
			switch (option) {
				case "1":
					listCredentials();
					break;
				case "2":
					addCredential();
					break;
				case "3":
					removeCredential();
					break;
				case "4":
					copyPasswordToClipboard();
					break;
				case "5":
					checkCompromisedPasswords();
					break;
				case "6":
					saveAndExit();
					return;
				default:
					System.out.println("Opção inválida. Tente novamente.");
			}
		}
	}

	/**
	 * Lista todas as credenciais armazenadas com índice, nome do serviço e nome de usuário.
	 */
	private void listCredentials() {
		if (credenciais.isEmpty()) {
			System.out.println("Nenhuma credencial armazenada.");
			return;
		}
		System.out.println("Credenciais Armazenadas:");
		for (int i = 0; i < credenciais.size(); i++) {
			Credencial c = credenciais.get(i);
			System.out.printf("%d. Serviço: %s | Usuário: %s%n", i + 1, c.nomeServico(), c.nomeUsuario());
		}
	}

	/**
	 * Adiciona uma nova credencial, com opção de gerar uma senha segura.
	 */
	void addCredential() {
    String service;
    String username;
    String escolha;

    try {
        System.out.print("Digite o nome do serviço: ");
        service = InputSanitizer.sanitize(input.nextLine(), 50, false);

        System.out.print("Digite o nome de usuário: ");
        username = InputSanitizer.sanitize(input.nextLine(), 50, false);

        System.out.print("Gerar senha forte? (s/n): ");
        escolha = InputSanitizer.sanitize(input.nextLine().toLowerCase(), 1, false);

        // Validação de entrada para escolha do usuário
        while (!escolha.equals("s") && !escolha.equals("n")) {
            System.out.print("Entrada inválida. Por favor, digite 's' para sim ou 'n' para não: ");
            escolha = InputSanitizer.sanitize(input.nextLine().toLowerCase(), 1, false);
        }
    } catch (IllegalArgumentException ex) {
        System.out.println("Entrada inválida. " + ex.getMessage());
        return;
    }

    // Define a senha com base na escolha do usuário
    String senha;
    if (escolha.equals("s")) {
        // Permite ao usuário definir o tamanho e características da senha
        int tamanhoSenha = askPasswordLength();
        boolean letrasMaiusculas = askIncludeOption("Incluir letras maiúsculas ?");
        boolean letrasMinusculas = askIncludeOption("Incluir letras minusculas ?");
        boolean inclueNumero = askIncludeOption("Incluir numeros ?");
        boolean inclueSimbolo = askIncludeOption("Incluir simbolos ?");

        // Valida se pelo menos uma opção foi selecionada
        if (!letrasMaiusculas && !letrasMinusculas && !inclueNumero && !inclueSimbolo) {
            System.out.println("Erro: Pelo menos um tipo de caractere deve ser selecionado.");
            return;
        }

        senha = PasswordGenerator.generate(tamanhoSenha, letrasMaiusculas, letrasMinusculas, inclueNumero, inclueSimbolo);

    } else {
        System.out.print("Digite a senha: ");
        try {
            senha = InputSanitizer.sanitize(input.nextLine(), 64, false);
        } catch (IllegalArgumentException ex) {
            System.out.println("Senha inválida. " + ex.getMessage());
            return;
        }
    }

    // Criptografa a senha e armazena a nova credencial
    try {
        String senhaEncriptada = EncriptacaoService.encrypt(senha);
        credenciais.add(new Credencial(service, username, senhaEncriptada));
        System.out.println("Credencial adicionada com sucesso.");
    } catch (Exception e) {
        System.err.println("Erro ao criptografar a senha: " + e.getMessage());
    }
}

/**
 * Solicita ao usuário o comprimento da senha e valida a entrada.
 * O comprimento da senha.
 * @return 
 */
private int askPasswordLength() {
    int tamanho = 0;
    while (tamanho <= 0) {
        try {
            System.out.print("Digite o comprimento da senha (mínimo 8): ");
            tamanho = Integer.parseInt(input.nextLine());
            if (tamanho < 8) {
                System.out.println("O comprimento da senha deve ter pelo menos 8 caracteres.");
                tamanho = 0;
            }
        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida. Por favor, digite um número válido.");
        }
    }
    return tamanho;
}

/**
 * Pergunta ao usuário se deve incluir um conjunto específico de caracteres na senha.
 * Mensagem a ser exibida ao usuário.
 * True se o usuário deseja incluir o conjunto de caracteres, senão false.
 * @param message 
 * @return 
 */
private boolean askIncludeOption(String message) {
    while (true) {
        System.out.print(message + " (s/n): ");
        String entrada = input.nextLine().toLowerCase();
        if (entrada.equals("s")) {
            return true;
        } else if (entrada.equals("n")) {
            return false;
        } else {
            System.out.println("Entrada inválida. Por favor, digite 's' para sim ou 'n' para não.");
        }
    }
}

	/**
	 * Removes a credential from the list based on user input.
	 */
	void removeCredential() {
		listCredentials();
		if (credenciais.isEmpty()) return;

		System.out.print("Digite o número a ser removido: ");
		int index = getIntInput() - 1;

		if (index >= 0 && index < credenciais.size()) {
			Credencial removed = credenciais.remove(index);
			System.out.println("Removido: " + removed.nomeServico());
		} else {
			System.out.println("Índice inválido.");
		}
	}

	/**
	 * Copia uma senha descriptografada para a área de transferência após verificar a senha mestra.
	 */
	private void copyPasswordToClipboard() {
		if (credenciais.isEmpty()) {
			System.out.println("Nenhuma credencial armazenada.");
			return;
		}

		listCredentials();
		System.out.print("Digite o número para copiar a senha: ");
		int index = getIntInput() - 1;

		if (index < 0 || index >= credenciais.size()) {
			System.out.println("Índice inválido.");
			return;
		}

		System.out.print("Digite a senha mestra novamente para confirmar: ");
		String inputPassword = input.nextLine().trim();

		try {
			// Carrega o hash armazenado de um arquivo
			java.nio.file.Path passwordPath = java.nio.file.Paths.get("master_password.dat");
			if (!java.nio.file.Files.exists(passwordPath)) {
				System.out.println("Arquivo master_password.txt não encontrado. Configure sua senha mestra novamente.");
				return;
			}

			String storedHash = java.nio.file.Files.readAllLines(passwordPath).getFirst();
			if (!BCrypt.checkpw(inputPassword, storedHash)) {
				System.out.println("Senha mestra incorreta. Acesso negado..");
				return;
			}

			// Descriptografa e copia a senha
			Credencial selected = credenciais.get(index);
			String decrypted = EncriptacaoService.decrypt(selected.senhaEncriptada());
			copyToClipboard(decrypted);
			System.out.printf("Senha para %s copiada para a área de transferência.%n", selected.nomeServico());
		} catch (IOException e) {
			System.err.println("Erro ao ler master_password.dat: " + e.getMessage());
		} catch (Exception e) {
			System.err.println("Erro ao descriptografar a senha: " + e.getMessage());
		}
	}

	/**
	 * Copia uma string para a área de transferência do sistema.
	 *
	 * @param text O texto a ser copiado.
	 */
	private void copyToClipboard(String text) {
		try {
			StringSelection selection = new StringSelection(text);
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			clipboard.setContents(selection, null);
		} catch (Exception e) {
			System.err.println("Operação de área de transferência não suportada: " + e.getMessage());
		}
	}

	/**
	 * Verifica todas as senhas armazenadas quanto a vazamentos de dados via API.
	 */
	private void checkCompromisedPasswords() {
		if (credenciais.isEmpty()) {
			System.out.println("Nenhuma credencial armazenada.");
			return;
		}
		System.out.println("Verificando todas as senhas armazenadas quanto a vazamentos...");
		boolean anyCompromised = false;

		for (Credencial c : credenciais) {
			try {
				String decrypted = EncriptacaoService.decrypt(c.senhaEncriptada());
				int contador = VerificadorSenha.checarSenha(decrypted);
				if (contador > 0) {
					System.out.printf(
							"AVISO: A senha do serviço '%s' (nome de usuário: %s) foi encontrada %d vezes em vazamentos!%n",
							c.nomeServico(), c.nomeUsuario(), contador
					);
					anyCompromised = true;
				}
			} catch (Exception e) {
				System.err.println("Erro ao verificar a senha do serviço '" + c.nomeServico() + "': " + e.getMessage());
			}
		}

		if (!anyCompromised) {
			System.out.println("Nenhuma senha comprometida encontrada em suas credenciais.");
		}
	}

	/**
	 * Salva as credenciais e encerra a aplicação.
	 */
	private void saveAndExit() {
		try {
			CredencialStorage.saveCredenciais(credenciais);
			System.out.println("Credenciais salvas. Saindo...");
		} catch (Exception e) {
			System.err.println("Erro ao salvar credenciais: " + e.getMessage());
		}
		
	}
	

	/**
	 * Lê e valida a entrada inteira do usuário.
	 *
	 * @return Inteiro validado ou -1 se a entrada for inválida.
	 */
	private int getIntInput() {
		try {
			return Integer.parseInt(input.nextLine().trim());
		} catch (NumberFormatException e) {
			System.out.println("Entrada inválida. Por favor, digite um número.");
			return -1;
		}
	}

}