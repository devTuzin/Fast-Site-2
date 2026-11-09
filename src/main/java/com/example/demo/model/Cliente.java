package com.example.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** Tabela "clientes" (CRUD da Tela 1). O endereço é preenchido pela API pública ViaCEP. */
@Entity
@Table(name = "clientes")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Informe o nome")
    @Size(max = 120, message = "Nome com no máximo 120 caracteres")
    @Column(nullable = false, length = 120)
    private String nome;

    @NotBlank(message = "Informe o CEP")
    @Pattern(regexp = "^\\d{5}-\\d{3}$", message = "CEP deve estar no formato 00000-000")
    @Column(nullable = false, length = 9)
    private String cep;

    @NotBlank(message = "Informe o endereço")
    @Size(max = 150, message = "Endereço com no máximo 150 caracteres")
    @Column(nullable = false, length = 150)
    private String endereco;

    @NotBlank(message = "Informe o número")
    @Size(max = 20, message = "Número com no máximo 20 caracteres")
    @Column(nullable = false, length = 20)
    private String numero;

    @Size(max = 100, message = "Bairro com no máximo 100 caracteres")
    @Column(length = 100)
    private String bairro;

    @Size(max = 100, message = "Cidade com no máximo 100 caracteres")
    @Column(length = 100)
    private String cidade;

    @Size(max = 2, message = "UF deve ter 2 letras")
    @Column(length = 2)
    private String uf;

    @NotBlank(message = "Informe o telefone")
    @Pattern(regexp = "^\\(\\d{2}\\) \\d{4,5}-\\d{4}$", message = "Telefone deve estar no formato (00) 00000-0000")
    @Column(nullable = false, length = 15)
    private String telefone;

    @Size(max = 500, message = "Observação com no máximo 500 caracteres")
    @Column(length = 500)
    private String observacao;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCep() { return cep; }
    public void setCep(String cep) { this.cep = cep; }

    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }

    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }

    public String getBairro() { return bairro; }
    public void setBairro(String bairro) { this.bairro = bairro; }

    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }

    public String getUf() { return uf; }
    public void setUf(String uf) { this.uf = uf; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }
}
