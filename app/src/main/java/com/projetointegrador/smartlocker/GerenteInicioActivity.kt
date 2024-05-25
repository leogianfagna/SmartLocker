package com.projetointegrador.smartlocker

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.crashlytics.internal.model.CrashlyticsReport
import com.google.firebase.firestore.FirebaseFirestore
import com.projetointegrador.smartlocker.databinding.ActivityGerenteInicioBinding

class GerenteInicioActivity : AppCompatActivity() {

    private lateinit var binding : ActivityGerenteInicioBinding
    private lateinit var db : FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_gerente_inicio)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        db = FirebaseFirestore.getInstance()

        binding = ActivityGerenteInicioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //TODO: Obter a Unidade atribuída ao Gerente

        val unidadeGerente = intent.getStringExtra("UNIDADE_GERENTE")

        if (unidadeGerente != null) {
            inflateArmarioData(unidadeGerente)
        }

    }

    private fun inflateArmarioData(unidadeGerente: String) {

        val layoutInflater = LayoutInflater.from(this)
        val linearLayout = findViewById<androidx.appcompat.widget.LinearLayoutCompat>(R.id.unity)

        db.collection("unidades de locação").document(unidadeGerente)
            .get()
            .addOnSuccessListener { document ->

                val unidade = document.getString("nome")
                binding.tvUnidade.text = "Unidade $unidade"
                binding.tvEndereco.text = document.getString("endereco")?: "N/A"

                val numArmarios = document.getLong("totalArmarios")?.toInt() ?: 0
                val prefix = document.getString("prefixoIndex")?: "N/A"

                for (i in 0 until numArmarios) {
                    val armarioKey = "$prefix$i"

                    val itemContainer = layoutInflater.inflate(R.layout.unidade_container, linearLayout, false)

                    val title = itemContainer.findViewById<androidx.appcompat.widget.AppCompatTextView>(R.id.tvUnidade)
                    val subtitle = itemContainer.findViewById<androidx.appcompat.widget.AppCompatTextView>(R.id.tvDisponivel)
                    val button = itemContainer.findViewById<androidx.appcompat.widget.AppCompatButton>(R.id.btnAction)

                    title.text = "Armário $armarioKey"

                    val isOccupied : Boolean = document.getBoolean(armarioKey)!!

                    if (isOccupied) {
                        subtitle.text = "Situação: Ocupado"
                        button.text =  "Acessar"
                        button.setOnClickListener {
                            // TODO
                        }
                    } else {
                        subtitle.text = "Situação: Disponível"
                        button.text = "Liberar"
                        button.setOnClickListener {
                            // TODO
                        }
                    }

                }
            }
            .addOnFailureListener {exception ->
                Log.e("Firestore", "Erro ao obter dados: ", exception)
            }

    }
}