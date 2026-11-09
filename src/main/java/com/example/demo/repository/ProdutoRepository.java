package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.Produto;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    List<Produto> findAllByOrderByNomeAsc();

    List<Produto> findByNomeContainingIgnoreCaseOrCategoriaContainingIgnoreCaseOrderByNomeAsc(String nome, String categoria);
}
