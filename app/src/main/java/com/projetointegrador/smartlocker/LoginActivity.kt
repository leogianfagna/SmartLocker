package com.projetointegrador.smartlocker

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.projetointegrador.smartlocker.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var snackbar: Snackbar

    private val auth = FirebaseAuth.getInstance()
    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Instanciando e utilizando o ViewBinding
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // Instanciando todos os botões
        instanciarBotoes()
    }

    fun instanciarBotoes() {

        // Página de cadastro (Não tenho uma conta)
        binding.btnCreateAccount.setOnClickListener {
            val activityIniciada = Intent(this, CadastroActivity::class.java)
            startActivity(activityIniciada)
            finish()
        }

        // Esqueceu a senha
        binding.btnRecoveryPassword.setOnClickListener {
            val activityIniciada = Intent(this, RecuperarSenhaActivity::class.java)
            startActivity(activityIniciada)
            finish()
        }

        // Acessar como visitante
        binding.btnVisitante.setOnClickListener {
            // val activityIniciada = Intent(this, RecuperarSenhaActivity::class.java)
            // startActivity(activityIniciada)

            // TODO: Implementar!
            snackbar = Snackbar.make(it, "Essa opção está indisponível no momento", Snackbar.LENGTH_SHORT)
            snackbar.show()
            finish()
        }

        // Botão de logar
        binding.btnLogar.setOnClickListener {
            // Informações preenchidas nos labels
            val inputEmail = binding.cpfInput.text.toString()
            val inputPassword = binding.passInput.text.toString()

            auth.signInWithEmailAndPassword(inputEmail, inputPassword)
                .addOnCompleteListener(this) { task ->
                    snackbar = if (task.isSuccessful) {
                        val user = auth.currentUser
                        Snackbar.make(it, "Usuário autenticado como: ${user?.email}", Snackbar.LENGTH_SHORT)
                    } else {
                        Snackbar.make(it, "Não foi possível autenticar", Snackbar.LENGTH_SHORT)
                    }
                }

            snackbar = Snackbar.make(it, "Não foi possível autenticar", Snackbar.LENGTH_SHORT)
            snackbar.show()
        }
    }
}