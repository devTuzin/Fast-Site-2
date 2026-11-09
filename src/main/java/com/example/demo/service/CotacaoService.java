package com.example.demo.service;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import com.example.demo.exception.ApiException;

/**
 * Consome a API pública AwesomeAPI (https://docs.awesomeapi.com.br) para mostrar a cotação do dia.
 * O resultado fica em cache por 60 segundos para não exceder o limite de requisições da API.
 */
@Service
public class CotacaoService {

    public record Cotacao(String codigo, String nome, double valor, double variacao) {
    }

    private static final Logger log = LoggerFactory.getLogger(CotacaoService.class);
    private static final long VALIDADE_CACHE_MS = 60_000;
    private static final ParameterizedTypeReference<Map<String, Map<String, Object>>> TIPO = new ParameterizedTypeReference<>() {
    };

    private final RestClient http;
    private volatile List<Cotacao> cache = List.of();
    private volatile long cacheEm = 0;

    public CotacaoService() {
        HttpClient cliente = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(4)).build();
        JdkClientHttpRequestFactory fabrica = new JdkClientHttpRequestFactory(cliente);
        fabrica.setReadTimeout(Duration.ofSeconds(5));
        this.http = RestClient.builder().requestFactory(fabrica).build();
    }

    public List<Cotacao> obter() {
        long agora = System.currentTimeMillis();
        if (!cache.isEmpty() && agora - cacheEm < VALIDADE_CACHE_MS) {
            return cache;
        }

        try {
            Map<String, Map<String, Object>> r = http.get()
                    .uri("https://economia.awesomeapi.com.br/json/last/USD-BRL,EUR-BRL,BTC-BRL")
                    .retrieve()
                    .body(TIPO);

            if (r != null && !r.isEmpty()) {
                List<Cotacao> lista = new ArrayList<>();
                adicionar(lista, r, "USDBRL", "USD", "Dólar");
                adicionar(lista, r, "EURBRL", "EUR", "Euro");
                adicionar(lista, r, "BTCBRL", "BTC", "Bitcoin");
                if (!lista.isEmpty()) {
                    cache = lista;
                    cacheEm = agora;
                    return lista;
                }
            }
        } catch (RestClientException | NumberFormatException e) {
            log.warn("Cotação indisponível: {}", e.getMessage());
        }

        // Se a API falhou mas já temos um valor antigo, mostra ele em vez de erro
        if (!cache.isEmpty()) {
            return cache;
        }
        throw new ApiException(HttpStatus.BAD_GATEWAY, "Cotação indisponível no momento.");
    }

    private static void adicionar(List<Cotacao> lista, Map<String, Map<String, Object>> r,
                                  String chave, String codigo, String nome) {
        Map<String, Object> dados = r.get(chave);
        if (dados == null) {
            return;
        }
        double valor = Double.parseDouble(String.valueOf(dados.get("bid")));
        double variacao = Double.parseDouble(String.valueOf(dados.get("pctChange")));
        lista.add(new Cotacao(codigo, nome, valor, variacao));
    }
}
