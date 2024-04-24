package com.projetointegrador.smartlocker

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
    private val db = Firebase.firestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_recuperar_senha)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding = ActivityRecuperarSenhaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recuperarBtnRecuperar.setOnClickListener {
            val email = binding.recuperarEditTextEmail.text.toString()
            if (!binding.recuperarEditTextEmail.text?.isEmpty()!!){
                auth.sendPasswordResetEmail(email).addOnSuccessListener {
                    Toast.makeText(this@RecuperarSenhaActivity,  "Sucesso", Toast.LENGTH_LONG).show()
                }.addOnFailureListener {
                    Toast.makeText(this@RecuperarSenhaActivity,  "Erro", Toast.LENGTH_LONG).show()

                }
            }else{
                binding.recuperarEditTextEmail.error = "Preencha o campo"
            }
        }
    }
}