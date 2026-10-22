package com.example.demo;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Classe responsável SOMENTE pela criptografia de senhas (BCrypt).
 */
public class SenhaUtil {

    // Transforma a senha em hash. O BCrypt gera um "salt" aleatório sozinho,
    // então a mesma senha gera hashes diferentes a cada vez (isso é normal).
    public static String criptografar(String senha) {
        return BCrypt.hashpw(senha, BCrypt.gensalt());
    }

    // Confere se a senha digitada bate com o hash salvo no banco.
    // Não dá para usar equals() por causa do salt: o checkpw refaz a conta corretamente.
    public static boolean conferir(String senhaDigitada, String hashSalvo) {
        // Se algum valor for nulo (ex.: email não existe no banco), o login falha
        if (senhaDigitada == null || hashSalvo == null) {
            return false;
        }
        return BCrypt.checkpw(senhaDigitada, hashSalvo);
    }
}
