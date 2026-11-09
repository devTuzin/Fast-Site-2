package com.example.demo.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.demo.SenhaUtil;
import com.example.demo.model.Usuario;
import com.example.demo.repository.UsuarioRepository;

/**
 * Na primeira execução (tabela de usuários vazia) cria um administrador para que seja possível
 * entrar no sistema sem rodar nenhum SQL. Substitui a antiga classe GerarHash.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UsuarioRepository usuarios;
    private final String adminEmail;
    private final String adminSenha;

    public DataInitializer(UsuarioRepository usuarios,
                           @Value("${app.admin.email}") String adminEmail,
                           @Value("${app.admin.senha}") String adminSenha) {
        this.usuarios = usuarios;
        this.adminEmail = adminEmail;
        this.adminSenha = adminSenha;
    }

    @Override
    public void run(String... args) {
        if (usuarios.count() == 0) {
            usuarios.save(new Usuario(adminEmail.trim().toLowerCase(), SenhaUtil.criptografar(adminSenha)));
            log.info("Usuário administrador criado: {} (a senha está em application.properties)", adminEmail);
        }
    }
}
