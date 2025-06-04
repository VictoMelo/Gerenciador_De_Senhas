<div align="center">

# Gerenciador de senhas seguro

<!-- ========== Informações do projeto ========== -->

[![License: MIT](https://img.shields.io/badge/License-MIT-purple.svg)](LICENSE)
[![Java](https://img.shields.io/badge/Java-22-blue.svg)](https://www.oracle.com/java/technologies/javase/22-relnote-issues.html)
[![Build with Maven](https://img.shields.io/badge/Build-Maven-brightgreen.svg)](https://maven.apache.org/)
![Maven Central](https://img.shields.io/badge/Maven-dependencies-blue?logo=apachemaven)

<!-- ========== Funcionalidades ========== -->

![2FA](https://img.shields.io/badge/2FA-TOTP-green?style=flat)
![AES Encryption](https://img.shields.io/badge/Encryption-AES256-blue?style=flat)
![Terminal App](https://img.shields.io/badge/Interface-Terminal-informational?style=flat)

<!-- ========== Status do projeto ========== -->

![Project Status](https://img.shields.io/badge/status-active-brightgreen?style=flat)
![Contributions Welcome](https://img.shields.io/badge/contributions-welcome-orange?style=flat)



Um gerenciador de senhas seguro, de linha de comando, escrito em Java. Ele permite que você armazene, gere e gerencie suas credenciais com segurança, com criptografia robusta e recursos de segurança modernos.

</div>

## Índice
- [Recursos](#recursos)
- [Notas de Segurança](#notas-de-segurança)
- [Pré-requisitos](#pré-requisitos)
- [Tecnologias Utilizadas](#tecnologias-utilizadas)
- [Geração de Código QR TOTP](#geração-de-código-qr-totp)
- [Instalação](#instalação)
- [Uso](#uso)
- [Executando Testes](#executando-testes)
- [Estrutura de Arquivos](#estrutura-de-arquivos)
- [Contribuindo](#contribuindo)
- [Licença](#licença)
- [Aviso Legal](#aviso-Legal)

## Recursos

- **Geração de Senhas**: Opção para gerar automaticamente senhas fortes com comprimento e conjuntos de caracteres personalizáveis.

  - **Verificação de Violação de Senha**: As senhas geradas são verificadas em bancos de dados de violações (como o HaveIBeenPwned) para garantir que não tenham sido comprometidas.
  - **Segurança de Senhas**:
  - Criptografia de senhas armazenadas usando algoritmos padrão do setor (AES-256).
  - Integração com a API HaveIBeenPwned para verificar senhas comprometidas.
  - Operações seguras da área de transferência para cópia de senhas (a área de transferência é limpa após um curto período).
  - **Interface Amigável**: Interface de linha de comando com opções de menu claras para adicionar, recuperar, atualizar e excluir credenciais.
  - **Autenticação de Dois Fatores (2FA)**: Suporte para TOTP (Senha de Uso Único Baseada em Tempo) para maior segurança da conta.
  - **Senha Mestra**: Protege o acesso a todas as credenciais armazenadas.
  - **Auditoria e Verificação de Violações**: Verifique facilmente se suas senhas foram expostas em violações de dados conhecidas.

## Notas de Segurança

  - **Criptografia Avançada**: Todas as credenciais armazenadas são protegidas usando AES-GCM para criptografia autenticada.
  - **Saneamento de Entradas**: As entradas fornecidas pelo usuário são rigorosamente validadas para evitar ataques de injeção ou entradas inseguras.
  - **Limpeza de Dados Sensíveis**: Existem mecanismos para limpar chaves de criptografia e dados sensíveis da memória quando o aplicativo é encerrado.
  - A senha mestra nunca é armazenada; apenas um hash é mantido usando o BCrypt.
  - As operações da área de transferência são limpas após um curto tempo limite para evitar vazamentos.
  - As senhas nunca são registradas ou exibidas em texto simples.

## Pré-requisitos

- Java Development Kit (JDK) 22 ou superior
- Maven 3.6.0 ou superior
- Git (opcional, para controle de versão)

## Tecnologias Utilizadas

  - **Java 22**: Linguagem de programação principal
  - **Maven**: Ferramenta de gerenciamento e construção de projetos
  - **Dependências**:
  - [jBCrypt](https://www.mindrot.org/projects/jBCrypt/): Para hash de senhas
  - [Gson](https://github.com/google/gson): Para serialização JSON
  - [Apache Commons Codec](https://commons.apache.org/proper/commons-codec/): Para utilitários de codificação/decodificação
  - [JUnit 5](https://junit.org/junit5/): Para testes unitários

## Geração de Código QR TOTP

Para configurar facilmente a Autenticação de Dois Fatores (2FA) com aplicativos autenticadores (como Google Authenticator, Microsoft Authenticator ou Authy), você pode converter sua URL TOTP em um código QR usando uma das seguintes ferramentas online gratuitas:

  - [https://www.qr-code-generator.com/](https://www.qr-code-generator.com/)
  - [https://www.the-qrcode-generator.com/](https://www.the-qrcode-generator.com/)
  - [https://www.qrstuff.com/](https://www.qrstuff.com/)
  - [https://www.unitag.io/qrcode](https://www.unitag.io/qrcode)
  - [https://www.google.com/chart?cht=qr&chs=300x300&chl=SUA_URL_TOTP](https://www.google.com/chart?cht=qr&chs=300x300&chl=SUA_URL_TOTP) (substitua `SUA_URL_TOTP` pela sua URL TOTP)

**Instruções:**
  1. Copie a URL do seu TOTP (por exemplo, `otpauth://totp/YourApp:username?secret=BASE32SECRET&issuer=YourApp`).
  2. Cole-a em um dos sites geradores de QR code acima.
  3. Escaneie o QR code gerado com seu aplicativo autenticador.

## Instalação

1. **Clone o repositório:**
```sh
git clone https https://github.com/VictoMelo/Gerenciador_De_Senhas.git
cd Gerenciador_De_Senhas
```

2. **Compile o projeto com o Maven:**
```sh
mvn clean package
```
O JAR executável será gerado no diretório `target/`.

## Uso

1. **Execute o aplicativo:**
```sh
java -jar target/secure-password-manager-1.0-SNAPSHOT-jar-with-dependencies.jar
```

2. **Primeira configuração:**
  - Você será solicitado a criar uma senha mestra. Essa senha é necessária para acessar suas credenciais.

3. **Autenticação de dois fatores (2FA):**
  - Configure o TOTP para uma camada extra de segurança. Armazene seu segredo TOTP com segurança.

4. **Opções do menu principal:**
  - Listar todas as credenciais
  - Adicionar nova credencial
  - Excluir uma credencial
  - Copiar a senha para a área de transferência
  - Verificar se alguma senha foi comprometida
  - Sair

5. **Geração de Senha:**
  - Escolha o comprimento da senha e os tipos de caracteres (maiúsculas, minúsculas, dígitos, símbolos).

6. **Verificação de Violação de Senha:**
  - Insira uma senha para verificar se ela foi exposta em violações de dados conhecidas usando a API HaveIBeenPwned.

## Observações de Segurança

  - Todas as credenciais são criptografadas em repouso usando AES-256.
  - A senha mestra nunca é armazenada; apenas um hash é mantido usando o BCrypt.
  - As operações da área de transferência são apagadas após um curto período de tempo para evitar vazamentos.
  - As senhas nunca são registradas ou exibidas em texto simples.

## Executando Testes

Para executar todos os testes unitários:
```sh
mvn test
```

## Estrutura do Arquivo

- `src/main/java/` - Código-fonte da aplicação
- `src/test/java/` - Testes unitários
- `lib/` - Bibliotecas externas (se houver)
- `target/` - Binários compilados e JARs empacotados

## Contribuindo

Contribuições são bem-vindas! Por favor, faça um fork do repositório e envie um pull request. Para grandes mudanças, abra uma issue primeiro para discutir o que você gostaria de mudar.

## Licença

Este projeto está licenciado sob a Licença MIT. Consulte o arquivo [LICENSE](LICENSE) para obter detalhes.

## Aviso Legal

Este projeto é para fins educacionais. Use por sua conta e risco. Sempre faça backup de suas credenciais e nunca compartilhe sua senha mestra.
