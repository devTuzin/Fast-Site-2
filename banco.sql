-- ============================================================
-- OPCIONAL: a aplicação cria o banco e as tabelas sozinha ao iniciar
-- (createDatabaseIfNotExist=true + spring.jpa.hibernate.ddl-auto=update).
-- Este script serve apenas como referência/documentação do esquema
-- gerado pelas entidades JPA, ou para criar tudo manualmente.
-- ============================================================
CREATE DATABASE IF NOT EXISTS login_page CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE login_page;

-- Entidade Usuario
CREATE TABLE IF NOT EXISTS usuarios (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    email      VARCHAR(150) NOT NULL UNIQUE,
    senha_hash VARCHAR(255) NOT NULL            -- hash BCrypt, nunca a senha pura
);

-- Entidade Cliente (Tela 1)
CREATE TABLE IF NOT EXISTS clientes (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome       VARCHAR(120) NOT NULL,
    cep        VARCHAR(9)   NOT NULL,
    endereco   VARCHAR(150) NOT NULL,
    numero     VARCHAR(20)  NOT NULL,
    bairro     VARCHAR(100),
    cidade     VARCHAR(100),
    uf         VARCHAR(2),
    telefone   VARCHAR(15)  NOT NULL,
    observacao VARCHAR(500)
);

-- Entidade Produto (Tela 2)
CREATE TABLE IF NOT EXISTS produtos (
    id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome      VARCHAR(120)   NOT NULL,
    categoria VARCHAR(80),
    preco     DECIMAL(10,2)  NOT NULL,
    estoque   INT            NOT NULL,
    descricao VARCHAR(500)
);

-- ------------------------------------------------------------
-- MIGRAÇÃO (só se você já tinha dados na antiga tabela "cadastrados"):
-- descomente e execute uma única vez para copiar para "clientes".
-- ------------------------------------------------------------
-- INSERT INTO clientes (nome, endereco, numero, cep, telefone, observacao)
-- SELECT nome, endereco, numero, cep, telefone, observacao FROM cadastrados;

-- O usuário administrador (admin@email.com / admin123) é criado pela própria aplicação
-- na primeira execução, caso a tabela "usuarios" esteja vazia.
