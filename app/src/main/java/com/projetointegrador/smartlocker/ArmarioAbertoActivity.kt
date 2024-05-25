package com.projetointegrador.smartlocker

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.Intent
import com.google.firebase.firestore.FirebaseFirestore
import com.projetointegrador.smartlocker.databinding.ActivityArmarioAbertoBinding

class ArmarioAbertoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityArmarioAbertoBinding
    private lateinit var db: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_armario_aberto)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        db = FirebaseFirestore.getInstance()

        binding = ActivityArmarioAbertoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // TODO: Obtendo ID da locação passada pela Intent @Leo
        val locacaoId = intent.getStringExtra("LOCACAO_ID")

        if (locacaoId != null) {
            fetchLocacaoData(locacaoId)
        }

        binding.btnVoltar.setOnClickListener {
            val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun fetchLocacaoData(locacaoId: String) {
        db.collection("locacao").document(locacaoId).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val numLocacao = document.getString("numLocacao") ?: "N/A"
                    val nomeCliente = document.getString("nomeCliente") ?: "N/A"
                    val inicioLocacao = document.getString("inicioLocacao") ?: "N/A"
                    val fimLocacao = document.getString("fimLocacao") ?: "N/A"

                    binding.tvNumeroLocacao.text = "Número da locação: $numLocacao"
                    binding.tvCliente.text = "Cliente: $nomeCliente"
                    binding.tvInicio.text = "Início: $inicioLocacao"
                    binding.tvTermino.text = "Término: $fimLocacao"
                }
            }
            .addOnFailureListener { exception ->
                // Tratar falha ao obter dados
            }
    }

}