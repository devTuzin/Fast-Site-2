package com.example.demo.config;

import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/** Bloqueia qualquer rota /api/** para quem não fez login (responde 401). */
public class AuthInterceptor implements HandlerInterceptor {

    public static final String ATRIBUTO_SESSAO = "usuarioLogado";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession sessao = request.getSession(false);
        if (sessao != null && sessao.getAttribute(ATRIBUTO_SESSAO) != null) {
            return true;
        }

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"mensagem\":\"Sessão expirada. Faça login novamente.\"}");
        return false;
    }
}
