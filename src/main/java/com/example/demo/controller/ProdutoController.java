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
import com.example.demo.model.Produto;
import com.example.demo.repository.ProdutoRepository;

import jakarta.validation.Valid;

/** CRUD REST de produtos (Tela 2). */
@RestController
@RequestMapping("/api/produtos")
public class ProdutoController {

    private final ProdutoRepository repo;

    public ProdutoController(ProdutoRepository repo) {
        this.repo = repo;
    }

    /** GET /api/produtos?busca=mouse (procura no nome e na categoria) */
    @GetMapping
    public List<Produto> listar(@RequestParam(required = false) String busca) {
        if (busca == null || busca.isBlank()) {
            return repo.findAllByOrderByNomeAsc();
        }
        String termo = busca.trim();
        return repo.findByNomeContainingIgnoreCaseOrCategoriaContainingIgnoreCaseOrderByNomeAsc(termo, termo);
    }

    /** GET /api/produtos/5 */
    @GetMapping("/{id}")
    public Produto buscar(@PathVariable Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Produto não encontrado."));
    }

    /** POST /api/produtos */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Produto criar(@Valid @RequestBody Produto dados) {
        dados.setId(null); // garante INSERT, nunca UPDATE
        return repo.save(dados);
    }

    /** PUT /api/produtos/5 */
    @PutMapping("/{id}")
    public Produto atualizar(@PathVariable Long id, @Valid @RequestBody Produto dados) {
        Produto atual = buscar(id);
        atual.setNome(dados.getNome());
        atual.setCategoria(dados.getCategoria());
        atual.setPreco(dados.getPreco());
        atual.setEstoque(dados.getEstoque());
        atual.setDescricao(dados.getDescricao());
        return repo.save(atual);
    }

    /** DELETE /api/produtos/5 */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        if (!repo.existsById(id)) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Produto não encontrado.");
        }
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
