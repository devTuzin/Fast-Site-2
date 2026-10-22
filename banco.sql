-- ============================================================
-- PASSO 1: rode este script no MySQL (Workbench, DBeaver ou terminal)
-- ============================================================
CREATE DATABASE IF NOT EXISTS meu_banco;
USE meu_banco;

CREATE TABLE IF NOT EXISTS usuarios (
    id         INT AUTO_INCREMENT PRIMARY KEY,  -- identificador gerado automaticamente
    email      VARCHAR(150) NOT NULL UNIQUE,    -- UNIQUE: não permite email repetido
    senha_hash VARCHAR(255) NOT NULL            -- guarda o HASH da senha, nunca a senha pura
);

-- ============================================================
-- PASSO 3 (depois de rodar o GerarHash.java):
-- cole o comando INSERT que o GerarHash imprimiu no console aqui.
-- Exemplo (o hash abaixo é só ilustrativo, NÃO funciona):
--
-- INSERT INTO usuarios (email, senha_hash)
-- VALUES ('admin@email.com', '$2a$10$xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx');
-- ============================================================
