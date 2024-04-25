package com.projetointegrador.smartlocker

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.projetointegrador.smartlocker.databinding.ActivityLoginBinding
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

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

        // Acessar como visitante (modo anônimo no Auth)
        binding.btnVisitante.setOnClickListener {

            // Logar no modo anônimo do Firebase Auth
            auth.signInAnonymously()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this,"Logado no modo anônimo com sucesso!", LENGTH_SHORT).show()
                        iniciarMainActivity()
                    } else {
                        Toast.makeText(this, task.exception!!.message.toString(), LENGTH_SHORT).show()
                    }
                }
        }

        // Botão de logar com email
        binding.btnLogar.setOnClickListener {
            // Informações preenchidas nos labels
            // Método Trim para remover os espaços em brancos se houver
            val inputEmail : String = binding.emailInput.text.toString().trim{it <= ' '}
            val inputPassword : String = binding.passInput.text.toString().trim{it <= ' '}

            // Autenticação com Firebase
            auth.signInWithEmailAndPassword(inputEmail, inputPassword)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val firebaseUser: FirebaseUser = task.result!!.user!! // Objeto do tipo task com a propriedade "result" (!! = Forçar que não é nulo)

                        Toast.makeText(this,"Logado com sucesso com o email ${firebaseUser.email}!", LENGTH_SHORT).show()
                        iniciarMainActivity()
                    } else {
                        Toast.makeText(this, task.exception!!.message.toString(), LENGTH_SHORT).show()
                    }
                }
        }
    }

    // TODO: Implementar a activity correta, pois ainda é inexistente!
    fun iniciarMainActivity() {
        val iniciarActivity = Intent(this, MapsActivity::class.java)
        startActivity(iniciarActivity)
        finish()
    }
}