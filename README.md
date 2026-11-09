# Sistema de Login e Cadastro

Projeto com **tela de login**, **tela inicial** e **cadastro de pessoas**, feito com **Spring Boot**, **MySQL** e **HTML/CSS/JavaScript**. As senhas são protegidas com **BCrypt** (nunca ficam salvas em texto puro).

## Funcionalidades

- Login com e-mail e senha (a senha é conferida com o hash salvo no banco)
- Sessão de usuário: só quem está logado vê a tela inicial
- Botão **Novo cadastro** na tela inicial, que abre um formulário com Nome, Endereço, CEP, Número, Telefone e Observação
- Aviso de sucesso ou erro ao cadastrar
- Tabela com todos os cadastros realizados, que atualiza sozinha após cada novo cadastro
- Botão **Sair** para encerrar a sessão

## Tecnologias

| Parte | Tecnologia |
|---|---|
| Backend | Java 25, Spring Boot |
| Banco de dados | MySQL |
| Criptografia | jBCrypt |
| Frontend | HTML, CSS e JavaScript puros |

## Pré-requisitos

Antes de começar, instale:

- [JDK 25](https://jdk.java.net/) (a versão definida no `pom.xml`)
- [Maven](https://maven.apache.org/download.cgi) (ou use a extensão *Extension Pack for Java* do VS Code, que já roda o projeto)
- [MySQL](https://dev.mysql.com/downloads/) rodando na porta `3306`

## Como instalar e usar

### 1. Baixe o projeto

```bash
git clone https://github.com/<seu-usuario>/<seu-repositorio>.git
cd <seu-repositorio>/demo
```

### 2. Crie o banco de dados

Abra o MySQL (Workbench, DBeaver ou terminal) e execute o conteúdo do arquivo [`banco.sql`](demo/banco.sql). Ele cria o banco `login_page` com as tabelas `usuarios` e `cadastrados`.

### 3. Configure a conexão com o MySQL

No arquivo `src/main/java/com/example/demo/UsuarioDAO.java`, ajuste o usuário e a senha do seu MySQL:

```java
private static final String USER = "root";
private static final String PASS = ""; // coloque a senha do seu MySQL, se houver
```

### 4. Crie o seu primeiro usuário

O sistema não tem tela de criar conta, então o primeiro usuário é criado direto no banco:

1. Abra `src/main/java/com/example/demo/GerarHash.java` e troque `email` e `senha` pelos dados que você quer usar para entrar.
2. Execute a classe `GerarHash` (no VS Code: botão **Run** acima do `main`).
3. Copie o comando `INSERT` que apareceu no console.
4. Cole e execute esse `INSERT` no MySQL.

> Depois de criar o usuário, apague os dados de exemplo do `GerarHash.java` antes de publicar o projeto.

### 5. Rode o servidor

Pelo VS Code, execute a classe `DemoApplication`, ou pelo terminal, dentro da pasta `demo`:

```bash
mvn spring-boot:run
```

Espere aparecer no console que o servidor iniciou na porta **8080**.

### 6. Abra o sistema

> [!IMPORTANT]
> ## ⚠️ Para funcionar, é obrigatório acessar pelo endereço:
> # `http://localhost:8080/criptografia.html`
>
> **Não abra o arquivo HTML clicando duas vezes nele, nem pelo Live Server do VS Code (porta 5500 ou 5501).** O login, o cadastro e a lista dependem do servidor Java, que roda na porta **8080**. Abrindo de outra forma, o botão **Entrar** não funciona e aparece o erro `405`.

### 7. Use o sistema

1. Na tela de login, digite o e-mail e a senha que você cadastrou no passo 4 e clique em **Entrar**.
2. Na tela inicial, você vê a mensagem de boas-vindas e a tabela de cadastros.
3. Clique em **Novo cadastro**, preencha todos os campos e clique em **Cadastrar**. Aparece o aviso "Cadastro realizado com sucesso!" e a pessoa entra na tabela.
4. Clique em **Sair** para encerrar a sessão e voltar ao login.

## Estrutura do projeto

```
demo/
├── banco.sql                          # Script que cria o banco e as tabelas
├── pom.xml                            # Dependências do Maven
└── src/main/
    ├── java/com/example/demo/
    │   ├── DemoApplication.java       # Inicia o servidor
    │   ├── LoginController.java       # Login, usuário logado e logout
    │   ├── CadastroController.java    # Salvar e listar cadastros
    │   ├── UsuarioDAO.java            # Acesso ao banco MySQL
    │   ├── SenhaUtil.java             # Criptografia da senha (BCrypt)
    │   └── GerarHash.java             # Gera o INSERT do primeiro usuário
    └── resources/static/
        ├── criptografia.html          # Tela de login
        └── home.html                  # Tela inicial (cadastros)
```

## Rotas do servidor

| Método | Rota | O que faz |
|---|---|---|
| `POST` | `/login` | Confere e-mail e senha e inicia a sessão |
| `GET` | `/usuario` | Devolve o e-mail de quem está logado |
| `POST` | `/logout` | Encerra a sessão |
| `POST` | `/cadastro` | Salva uma nova pessoa no banco |
| `GET` | `/cadastrados` | Lista todas as pessoas cadastradas |

## Problemas comuns

| Problema | Causa e solução |
|---|---|
| Erro `405` ao clicar em Entrar | Você abriu a página fora do servidor Java. Use `http://localhost:8080/criptografia.html`. |
| `Cannot GET /home.html` ou erro 404 | Os arquivos HTML precisam estar em `src/main/resources/static`. |
| "E-mail ou senha inválidos." | O usuário não existe no banco ou a senha está errada. Refaça o passo 4. |
| "Não foi possível conectar ao servidor." | O servidor Java não está rodando. Execute o passo 5. |
| Login ou cadastro dá erro e o console mostra `SQLException` | O MySQL não está ligado, o banco `login_page` não foi criado ou o usuário e a senha do `UsuarioDAO.java` estão errados. |
| "Não foi possível carregar os cadastros." | Confira se a tabela `cadastrados` existe e se a tela busca a rota `/cadastrados`. |
