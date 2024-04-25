package com.projetointegrador.smartlocker

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.projetointegrador.smartlocker.databinding.ActivityCadastroBinding
import com.projetointegrador.smartlocker.databinding.ActivityRecuperarSenhaBinding

class RecuperarSenhaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecuperarSenhaBinding
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRecuperarSenhaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.recuperarBtnRecuperar.setOnClickListener {
            val email = binding.recuperarEditTextEmail.text.toString()
            if (email.isNotEmpty()) {
                recoverPassword(email)
            } else {
                Toast.makeText(this, "Por favor, insira o e-mail", Toast.LENGTH_SHORT).show()
            }
        }

        binding.recuperarVoltarLogin.setOnClickListener {
            val activity = Intent(this, LoginActivity::class.java)
            startActivity(activity)
            finish()
        }

        // Botão de voltar para a tela de cadastro
        binding.recuperarVoltarCadastro.setOnClickListener {
            val activity = Intent(this, CadastroActivity::class.java)
            startActivity(activity)
            finish()
        }

    }

    private fun recoverPassword(email: String) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "E-mail de recuperação de senha enviado", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Erro ao enviar e-mail de recuperação de senha", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
