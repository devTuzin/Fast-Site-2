/* =========================================================
 * Funções compartilhadas por todas as telas:
 *  - api():       chamadas REST com tratamento de erro
 *  - toast():     notificações visuais
 *  - confirmar(): janela de confirmação (usada nas exclusões)
 *  - iniciarPagina(): valida o login e desenha o menu de navegação
 *  - máscaras de CEP e telefone, formatadores
 * ========================================================= */

/** Faz a chamada à API. Retorna o JSON (ou null se vier vazio). Em erro, lança Error com a mensagem do servidor. */
async function api(url, opcoes = {}) {
    const config = { method: "GET", headers: {}, ...opcoes };

    if (config.body !== undefined && typeof config.body !== "string") {
        config.headers["Content-Type"] = "application/json";
        config.body = JSON.stringify(config.body);
    }

    let resposta;
    try {
        resposta = await fetch(url, config);
    } catch (e) {
        throw new Error("Sem conexão com o servidor. Verifique se a aplicação está rodando.");
    }

    // Sessão expirada em qualquer tela interna: volta para o login
    if (resposta.status === 401 && !config.semRedirecionar && !location.pathname.endsWith("index.html")
            && location.pathname !== "/" && !location.pathname.endsWith("cadastro.html")) {
        location.href = "/index.html";
        throw new Error("Sessão expirada.");
    }

    let corpo = null;
    const texto = await resposta.text();
    if (texto) {
        try { corpo = JSON.parse(texto); } catch (e) { corpo = { mensagem: texto }; }
    }

    if (!resposta.ok) {
        const erro = new Error((corpo && corpo.mensagem) || ("Erro " + resposta.status));
        erro.status = resposta.status;
        erro.campos = corpo && corpo.erros ? corpo.erros : {};
        throw erro;
    }
    return corpo;
}

/** Mostra uma notificação no canto da tela. tipo: sucesso | erro | aviso | info */
function toast(mensagem, tipo = "info") {
    let area = document.getElementById("toasts");
    if (!area) {
        area = document.createElement("div");
        area.id = "toasts";
        area.setAttribute("aria-live", "polite");
        document.body.appendChild(area);
    }
    const item = document.createElement("div");
    item.className = "toast " + tipo;
    item.textContent = mensagem;
    area.appendChild(item);

    const remover = () => {
        item.classList.add("saindo");
        setTimeout(() => item.remove(), 300);
    };
    item.addEventListener("click", remover);
    setTimeout(remover, tipo === "erro" ? 6000 : 3500);
}

/** Janela de confirmação. Retorna uma Promise<boolean>. */
function confirmar(mensagem, { titulo = "Confirmar", textoOk = "Excluir", perigo = true } = {}) {
    return new Promise((resolve) => {
        const janela = document.createElement("dialog");
        janela.innerHTML = `
            <h2></h2>
            <p class="texto"></p>
            <div class="rodape-janela">
                <button type="button" class="btn claro" data-r="nao">Cancelar</button>
                <button type="button" class="btn ${perigo ? "perigo" : ""}" data-r="sim"></button>
            </div>`;
        janela.querySelector("h2").textContent = titulo;
        janela.querySelector("p").textContent = mensagem;
        janela.querySelector('[data-r="sim"]').textContent = textoOk;

        let resultado = false;
        janela.addEventListener("click", (e) => {
            const r = e.target.dataset && e.target.dataset.r;
            if (r) { resultado = r === "sim"; janela.close(); }
        });
        janela.addEventListener("close", () => { janela.remove(); resolve(resultado); });

        document.body.appendChild(janela);
        janela.showModal();
        janela.querySelector('[data-r="nao"]').focus();
    });
}

/** Escapa texto para colocar com segurança dentro de innerHTML. */
function esc(valor) {
    return String(valor ?? "").replace(/[&<>"']/g, (c) =>
        ({ "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;" }[c]));
}

const moeda = new Intl.NumberFormat("pt-BR", { style: "currency", currency: "BRL" });

/* ---------- Máscaras ---------- */
function mascaraCep(campo) {
    campo.addEventListener("input", () => {
        let v = campo.value.replace(/\D/g, "").slice(0, 8);
        if (v.length > 5) v = v.slice(0, 5) + "-" + v.slice(5);
        campo.value = v;
    });
}

function mascaraTelefone(campo) {
    campo.addEventListener("input", () => {
        let v = campo.value.replace(/\D/g, "").slice(0, 11);
        if (v.length > 10) v = `(${v.slice(0, 2)}) ${v.slice(2, 7)}-${v.slice(7)}`;
        else if (v.length > 6) v = `(${v.slice(0, 2)}) ${v.slice(2, 6)}-${v.slice(6)}`;
        else if (v.length > 2) v = `(${v.slice(0, 2)}) ${v.slice(2)}`;
        else if (v.length > 0) v = `(${v}`;
        campo.value = v;
    });
}

/** Marca em vermelho os campos que o servidor recusou (resposta 400 com "erros"). */
function mostrarErrosCampos(form, campos) {
    limparErrosCampos(form);
    for (const [nome, mensagem] of Object.entries(campos || {})) {
        const campo = form.elements[nome];
        if (!campo) continue;
        campo.classList.add("erro");
        const aviso = document.createElement("div");
        aviso.className = "msg-campo";
        aviso.textContent = mensagem;
        // Se o campo estiver dentro de .com-botao, o aviso vai depois do grupo
        const alvo = campo.closest(".com-botao") || campo;
        alvo.insertAdjacentElement("afterend", aviso);
    }
}

function limparErrosCampos(form) {
    form.querySelectorAll(".msg-campo").forEach((e) => e.remove());
    form.querySelectorAll(".erro").forEach((e) => e.classList.remove("erro"));
}

/* ---------- Menu de navegação + proteção das telas internas ---------- */
const TELAS = [
    { href: "/home.html", nome: "Início", id: "home" },
    { href: "/clientes.html", nome: "Clientes", id: "clientes" },
    { href: "/produtos.html", nome: "Produtos", id: "produtos" }
];

/** Chame no começo de toda tela interna. Retorna o e-mail logado. */
async function iniciarPagina(telaAtiva) {
    let usuario;
    try {
        usuario = await api("/api/auth/me");
    } catch (e) {
        location.href = "/index.html";
        return null;
    }

    const menu = document.createElement("nav");
    menu.className = "menu";
    menu.innerHTML = `<span class="marca">Meu Sistema</span>` +
        TELAS.map((t) => `<a class="link ${t.id === telaAtiva ? "ativo" : ""}" href="${t.href}">${t.nome}</a>`).join("") +
        `<span class="espaco"></span>
         <span class="usuario">${esc(usuario.email)}</span>
         <button type="button" class="btn sair mini" id="btnSair">Sair</button>`;
    document.body.prepend(menu);

    document.getElementById("btnSair").addEventListener("click", async () => {
        try { await api("/api/auth/logout", { method: "POST" }); } catch (e) { /* ignora */ }
        location.href = "/index.html";
    });

    document.body.classList.remove("oculto");
    return usuario.email;
}
