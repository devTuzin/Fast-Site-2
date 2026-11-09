package com.example.demo.controller;

import java.math.BigDecimal;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.repository.ClienteRepository;
import com.example.demo.repository.ProdutoRepository;

/** Números do painel inicial. */
@RestController
@RequestMapping("/api/resumo")
public class DashboardController {

    public record Resumo(long clientes, long produtos, BigDecimal valorEstoque) {
    }

    private final ClienteRepository clientes;
    private final ProdutoRepository produtos;

    public DashboardController(ClienteRepository clientes, ProdutoRepository produtos) {
        this.clientes = clientes;
        this.produtos = produtos;
    }

    @GetMapping
    public Resumo resumo() {
        BigDecimal valorEstoque = produtos.findAll().stream()
                .map(p -> p.getPreco().multiply(BigDecimal.valueOf(p.getEstoque())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new Resumo(clientes.count(), produtos.count(), valorEstoque);
    }
}
