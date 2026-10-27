package com.example.demo;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

    private final UsuarioDAO dao = new UsuarioDAO();

    /**
     * Endpoint para Login (Lê a tabela 'usuarios')
     */
    @PostMapping("/login")
    public ResponseEntity<String> login(
            @RequestParam String email,
            @RequestParam String senha,
            HttpSession session) {

        String emailLimpo = email.trim().toLowerCase();
        String hashSalvo = dao.buscarHashPorEmail(emailLimpo);

        if (hashSalvo != null && SenhaUtil.conferir(senha, hashSalvo)) {
            session.setAttribute("usuarioLogado", emailLimpo);
            return ResponseEntity.ok("Login realizado com sucesso!");
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("E-mail ou senha inválidos.");
    }

    @GetMapping("/usuario")
    public ResponseEntity<String> usuarioLogado(HttpSession session) {
        Object usuario = session.getAttribute("usuarioLogado");

        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Não logado");
        }

        return ResponseEntity.ok(usuario.toString());
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok("Saiu com sucesso");
    }
}