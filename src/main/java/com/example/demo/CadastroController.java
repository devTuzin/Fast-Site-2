package com.example.demo;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class CadastroController {

    private final UsuarioDAO dao = new UsuarioDAO();

    /**
     * Rota para salvar os cadastros vindo do cadastro.html
     */
    @PostMapping("/cadastro")
    public ResponseEntity<String> cadastrar(
            @RequestParam String nome,
            @RequestParam String endereco,
            @RequestParam String numero,
            @RequestParam String cep,
            @RequestParam String telefone,
            @RequestParam(required = false, defaultValue = "") String observacao) {

        boolean cadastrado = dao.cadastrarPessoa(nome, endereco, numero, cep, telefone, observacao);

        if (cadastrado) {
            return ResponseEntity.ok("Cadastro realizado com sucesso!");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao salvar no banco de dados.");
        }
    }

    /**
     * Rota que resolve o erro 404: envia a lista do banco MySQL para o home.html
     */
    @GetMapping("/cadastrados")
    public ResponseEntity<List<Map<String, Object>>> listar() {
        return ResponseEntity.ok(dao.listarTodas());
    }
}