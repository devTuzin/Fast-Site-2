package com.example.demo.service;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import com.example.demo.exception.ApiException;

/**
 * Consome a API pública ViaCEP (https://viacep.com.br) para descobrir o endereço a partir do CEP.
 * Se o ViaCEP estiver fora do ar, tenta a BrasilAPI (https://brasilapi.com.br) como reserva.
 */
@Service
public class CepService {

    public record Endereco(String cep, String endereco, String bairro, String cidade, String uf) {
    }

    private static final Logger log = LoggerFactory.getLogger(CepService.class);
    private static final ParameterizedTypeReference<Map<String, Object>> MAPA = new ParameterizedTypeReference<>() {
    };

    private final RestClient http;

    public CepService() {
        HttpClient cliente = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(4)).build();
        JdkClientHttpRequestFactory fabrica = new JdkClientHttpRequestFactory(cliente);
        fabrica.setReadTimeout(Duration.ofSeconds(5));
        this.http = RestClient.builder().requestFactory(fabrica).build();
    }

    public Endereco consultar(String cepInformado) {
        String cep = cepInformado == null ? "" : cepInformado.replaceAll("\\D", "");
        if (cep.length() != 8) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "CEP inválido. Digite os 8 números.");
        }
        String formatado = cep.substring(0, 5) + "-" + cep.substring(5);

        // 1) ViaCEP
        try {
            Map<String, Object> r = http.get()
                    .uri("https://viacep.com.br/ws/{cep}/json/", cep)
                    .retrieve()
                    .body(MAPA);
            if (r != null) {
                if (r.containsKey("erro")) {
                    throw new ApiException(HttpStatus.NOT_FOUND, "CEP não encontrado.");
                }
                return new Endereco(formatado, texto(r, "logradouro"), texto(r, "bairro"),
                        texto(r, "localidade"), texto(r, "uf"));
            }
        } catch (RestClientException e) {
            log.warn("ViaCEP indisponível ({}). Tentando BrasilAPI...", e.getMessage());
        }

        // 2) BrasilAPI (reserva)
        try {
            Map<String, Object> r = http.get()
                    .uri("https://brasilapi.com.br/api/cep/v2/{cep}", cep)
                    .retrieve()
                    .body(MAPA);
            if (r != null) {
                return new Endereco(formatado, texto(r, "street"), texto(r, "neighborhood"),
                        texto(r, "city"), texto(r, "state"));
            }
        } catch (HttpClientErrorException.NotFound e) {
            throw new ApiException(HttpStatus.NOT_FOUND, "CEP não encontrado.");
        } catch (RestClientException e) {
            log.warn("BrasilAPI indisponível: {}", e.getMessage());
        }

        throw new ApiException(HttpStatus.BAD_GATEWAY,
                "Serviço de consulta de CEP indisponível no momento. Preencha o endereço manualmente.");
    }

    private static String texto(Map<String, Object> mapa, String chave) {
        Object valor = mapa.get(chave);
        return valor == null ? "" : valor.toString();
    }
}
