package com.example.demo.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.SenhaUtil;
import com.example.demo.config.AuthInterceptor;
import com.example.demo.exception.ApiException;
import com.example.demo.model.Usuario;
import com.example.demo.repository.UsuarioRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    public record LoginRequest(
            @NotBlank(message = "Informe o e-mail") String email,
            @NotBlank(message = "Informe a senha") String senha) {
    }

    public record RegistroRequest(
            @NotBlank(message = "Informe o e-mail")
            @Email(message = "E-mail inválido")
            @Size(max = 150, message = "E-mail com no máximo 150 caracteres") String email,
            @NotBlank(message = "Informe a senha")
            @Size(min = 6, max = 72, message = "A senha deve ter de 6 a 72 caracteres") String senha) {
    }

    private final UsuarioRepository usuarios;

    public AuthController(UsuarioRepository usuarios) {
        this.usuarios = usuarios;
    }

    /** POST /api/auth/login: confere e-mail e senha (BCrypt) e abre a sessão. */
    @PostMapping("/login")
    public Map<String, String> login(@Valid @RequestBody LoginRequest dados, HttpServletRequest request) {
        String email = dados.email().trim().toLowerCase();
        String hash = usuarios.findByEmail(email).map(Usuario::getSenhaHash).orElse(null);

        if (!SenhaUtil.conferir(dados.senha(), hash)) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "E-mail ou senha inválidos.");
        }

        // Troca o id da sessão ao logar (evita fixação de sessão)
        if (request.getSession(false) != null) {
            request.changeSessionId();
        }
        request.getSession(true).setAttribute(AuthInterceptor.ATRIBUTO_SESSAO, email);
        return Map.of("email", email);
    }

    /** POST /api/auth/registro: cria uma nova conta de acesso. */
    @PostMapping("/registro")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, String> registrar(@Valid @RequestBody RegistroRequest dados) {
        String email = dados.email().trim().toLowerCase();

        if (usuarios.existsByEmail(email)) {
            throw new ApiException(HttpStatus.CONFLICT, "Já existe uma conta com este e-mail.");
        }

        usuarios.save(new Usuario(email, SenhaUtil.criptografar(dados.senha())));
        return Map.of("email", email);
    }

    /** GET /api/auth/me: quem está logado (o interceptor já devolve 401 se ninguém estiver). */
    @GetMapping("/me")
    public Map<String, String> me(HttpSession sessao) {
        return Map.of("email", String.valueOf(sessao.getAttribute(AuthInterceptor.ATRIBUTO_SESSAO)));
    }

    /** POST /api/auth/logout: encerra a sessão. */
    @PostMapping("/logout")
    public Map<String, String> logout(HttpServletRequest request) {
        HttpSession sessao = request.getSession(false);
        if (sessao != null) {
            sessao.invalidate();
        }
        return Map.of("mensagem", "Você saiu do sistema.");
    }
}
