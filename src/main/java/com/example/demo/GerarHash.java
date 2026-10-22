package com.example.demo;

/**
 * Classe TEMPORÁRIA: gera o comando SQL para criar um usuário.
 * Como não existe tela de cadastro, o usuário é inserido direto no banco.
 *
 * Como usar:
 *   1) Troque EMAIL e SENHA abaixo pelos dados que você quer.
 *   2) Rode este main.
 *   3) Copie o INSERT que aparecer no console e execute no MySQL.
 *   4) Depois pode apagar esta classe.
 */
public class GerarHash {

    public static void main(String[] args) {
        String email = "arthurdojava@gmail.com";
        String senha = "oJavaDomina";

        String hash = SenhaUtil.criptografar(senha);

        System.out.println("USE login_page;");
        System.out.println("INSERT INTO usuarios (email, senha_hash)");
        System.out.println("VALUES ('" + email.trim().toLowerCase() + "', '" + hash + "');");
    }
}
