package com.example.demo.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.CepService;
import com.example.demo.service.CepService.Endereco;
import com.example.demo.service.CotacaoService;
import com.example.demo.service.CotacaoService.Cotacao;

/** Endpoints que entregam para a tela os dados das APIs públicas (ViaCEP e AwesomeAPI). */
@RestController
@RequestMapping("/api")
public class ExternoController {

    private final CepService cepService;
    private final CotacaoService cotacaoService;

    public ExternoController(CepService cepService, CotacaoService cotacaoService) {
        this.cepService = cepService;
        this.cotacaoService = cotacaoService;
    }

    /** GET /api/cep/29930000 */
    @GetMapping("/cep/{cep}")
    public Endereco cep(@PathVariable String cep) {
        return cepService.consultar(cep);
    }

    /** GET /api/cotacao */
    @GetMapping("/cotacao")
    public List<Cotacao> cotacao() {
        return cotacaoService.obter();
    }
}
