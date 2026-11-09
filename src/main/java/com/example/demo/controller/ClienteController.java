package com.example.demo.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.exception.ApiException;
import com.example.demo.model.Cliente;
import com.example.demo.repository.ClienteRepository;

import jakarta.validation.Valid;

/** CRUD REST de clientes (Tela 1). */
@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteRepository repo;

    public ClienteController(ClienteRepository repo) {
        this.repo = repo;
    }

    /** GET /api/clientes?busca=maria */
    @GetMapping
    public List<Cliente> listar(@RequestParam(required = false) String busca) {
        if (busca == null || busca.isBlank()) {
            return repo.findAllByOrderByNomeAsc();
        }
        return repo.findByNomeContainingIgnoreCaseOrderByNomeAsc(busca.trim());
    }

    /** GET /api/clientes/5 */
    @GetMapping("/{id}")
    public Cliente buscar(@PathVariable Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Cliente não encontrado."));
    }

    /** POST /api/clientes */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Cliente criar(@Valid @RequestBody Cliente dados) {
        dados.setId(null); // garante INSERT, nunca UPDATE
        return repo.save(dados);
    }

    /** PUT /api/clientes/5 */
    @PutMapping("/{id}")
    public Cliente atualizar(@PathVariable Long id, @Valid @RequestBody Cliente dados) {
        Cliente atual = buscar(id);
        atual.setNome(dados.getNome());
        atual.setCep(dados.getCep());
        atual.setEndereco(dados.getEndereco());
        atual.setNumero(dados.getNumero());
        atual.setBairro(dados.getBairro());
        atual.setCidade(dados.getCidade());
        atual.setUf(dados.getUf());
        atual.setTelefone(dados.getTelefone());
        atual.setObservacao(dados.getObservacao());
        return repo.save(atual);
    }

    /** DELETE /api/clientes/5 */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        if (!repo.existsById(id)) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Cliente não encontrado.");
        }
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
