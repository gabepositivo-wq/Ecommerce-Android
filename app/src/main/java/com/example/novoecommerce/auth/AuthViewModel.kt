package com.example.novoecommerce.auth

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.novoecommerce.data.Usuario
import com.example.novoecommerce.data.dal.UsuarioDAO
import com.example.novoecommerce.validation.CadastroValidator
import java.security.MessageDigest
import java.security.SecureRandom

class AuthViewModel : ViewModel() {
    var usuarioAtual by mutableStateOf<Usuario?>(null)
        private set

    val autenticado: Boolean get() = usuarioAtual != null

    fun restaurarSessao(context: Context): Boolean {
        val preferences = context.getSharedPreferences(PREFERENCIAS, Context.MODE_PRIVATE)
        val id = preferences.getInt(CHAVE_USUARIO, -1)
        usuarioAtual = if (id > 0) UsuarioDAO(context).buscarPorId(id) else null
        if (usuarioAtual == null) preferences.edit().remove(CHAVE_USUARIO).apply()
        return autenticado
    }

    fun login(context: Context, email: String, senha: String): Boolean {
        val dao = UsuarioDAO(context)
        val salvo = dao.buscarSenha(email) ?: return false
        if (!hashSenha(senha, salvo.salt).contentEquals(salvo.hash)) return false
        val id = dao.buscarIdPorEmail(email) ?: return false
        val usuario = dao.buscarPorId(id) ?: return false
        iniciarSessao(context, usuario)
        return true
    }

    fun cadastrar(context: Context, nome: String, email: String, senha: String) {
        CadastroValidator.validar(nome, email, senha)
        val salt = ByteArray(16).also { SecureRandom().nextBytes(it) }
        UsuarioDAO(context).cadastrar(nome, email, hashSenha(senha, salt), salt)
    }

    fun listarUsuarios(context: Context): List<Usuario> = UsuarioDAO(context).listar()

    fun alterarUsuario(context: Context, usuario: Usuario) {
        CadastroValidator.validarPerfil(usuario.nome, usuario.email)
        val dao = UsuarioDAO(context)
        if (dao.alterar(usuario) == 0) throw IllegalStateException("Usuário não encontrado.")
        if (usuarioAtual?.id == usuario.id) usuarioAtual = dao.buscarPorId(usuario.id)
    }

    fun excluirUsuario(context: Context, id: Int) {
        require(usuarioAtual?.id != id) {
            "Não é possível excluir o usuário da sessão atual."
        }
        if (UsuarioDAO(context).excluir(id) == 0) {
            throw IllegalStateException("Usuário não encontrado.")
        }
    }

    fun encerrarSessao(context: Context) {
        context.getSharedPreferences(PREFERENCIAS, Context.MODE_PRIVATE)
            .edit()
            .remove(CHAVE_USUARIO)
            .apply()
        usuarioAtual = null
    }

    private fun iniciarSessao(context: Context, usuario: Usuario) {
        context.getSharedPreferences(PREFERENCIAS, Context.MODE_PRIVATE)
            .edit()
            .putInt(CHAVE_USUARIO, usuario.id)
            .apply()
        usuarioAtual = usuario
    }

    private fun hashSenha(senha: String, salt: ByteArray): ByteArray =
        MessageDigest.getInstance("SHA-256")
            .apply { update(salt) }
            .digest(senha.toByteArray(Charsets.UTF_8))

    companion object {
        private const val PREFERENCIAS = "sessao"
        private const val CHAVE_USUARIO = "usuario_id"
    }
}
