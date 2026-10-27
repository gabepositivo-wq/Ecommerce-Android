package com.example.novoecommerce.validation

import android.util.Patterns

object CadastroValidator {
    fun validar(nome: String, email: String, senha: String) {
        validarPerfil(nome, email)
        require(senha.length in 6..128 && senha.isNotBlank()) {
            "Use uma senha entre 6 e 128 caracteres."
        }
    }

    fun validarPerfil(nome: String, email: String) {
        require(nome.length in 3..100) {
            "Informe um nome com pelo menos 3 e no máximo 100 caracteres."
        }
        require(email.length <= 254 && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            "Informe um e-mail válido."
        }
    }
}
