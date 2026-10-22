package com.example.demo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UsuarioDAO {

    private static final String URL = "jdbc:mysql://localhost:3306/login_page?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASS = ""; // Insira sua senha do MySQL se houver

    /**
     * Busca os registros salvos na tabela 'cadastrados'
     */
    public List<Map<String, Object>> listarTodas() {
        List<Map<String, Object>> lista = new ArrayList<>();
        String sql = "SELECT nome, endereco, numero, cep, telefone, observacao FROM cadastrados";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> pessoa = new HashMap<>();
                pessoa.put("nome", rs.getString("nome"));
                pessoa.put("endereco", rs.getString("endereco"));
                pessoa.put("numero", rs.getString("numero"));
                pessoa.put("cep", rs.getString("cep"));
                pessoa.put("telefone", rs.getString("telefone"));
                pessoa.put("observacao", rs.getString("observacao"));
                lista.add(pessoa);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    public boolean cadastrarPessoa(String nome, String endereco, String numero, String cep, String telefone, String observacao) {
        String sql = "INSERT INTO cadastrados (nome, endereco, numero, cep, telefone, observacao) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASS)) {
            conn.setAutoCommit(true);

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, nome);
                ps.setString(2, endereco);
                ps.setString(3, numero);
                ps.setString(4, cep);
                ps.setString(5, telefone);
                ps.setString(6, observacao);

                return ps.executeUpdate() > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String buscarHashPorEmail(String email) {
        String sql = "SELECT senha_hash FROM usuarios WHERE email = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("senha_hash");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}