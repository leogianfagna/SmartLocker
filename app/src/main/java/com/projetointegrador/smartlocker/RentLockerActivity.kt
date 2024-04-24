package com.projetointegrador.smartlocker

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.projetointegrador.smartlocker.databinding.ActivityLoginBinding
import com.projetointegrador.smartlocker.databinding.ActivityRentLockerBinding
import android.widget.Toast.LENGTH_SHORT
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson

class RentLockerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRentLockerBinding
    private lateinit var itemSelecionadoNoSpinner: String

    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Instanciando e utilizando o ViewBinding
        binding = ActivityRentLockerBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // Instanciando todos os botões
        instanciarBotoes()
        resgatarValoresLocacaoDoFirebase()

    }

    private fun instanciarBotoes() {

        // Voltar
        // TODO: Implementar a activity correta (atualmente inexistente)
        binding.btnBack.setOnClickListener {
            val i = Intent(this, LoginActivity::class.java)
            startActivity(i)
            finish()
        }

        // Opções do spinner
        val spinner: Spinner = findViewById(R.id.spinnerOpcoesValores)

        // Create an ArrayAdapter using the string array and a default spinner layout.
        ArrayAdapter.createFromResource(
            this,
            R.array.spinner_opcoes_valores_string,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears.
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner.
            spinner.adapter = adapter
        }

        // Ouvinte de seleção de item para o spinner
        // Documentação: https://developer.android.com/develop/ui/views/components/spinner?hl=pt-br
        binding.spinnerOpcoesValores.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // Recebe o item selecionado na variável itemSelecionadoNoSpinner
                itemSelecionadoNoSpinner = parent?.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                itemSelecionadoNoSpinner = "vazio"
            }
        }

        // Confirmar locação
        binding.btnConfirmRent.setOnClickListener {
            val i = Intent(this, LoginActivity::class.java)
            startActivity(i)
            finish()
            Toast.makeText(this, itemSelecionadoNoSpinner, LENGTH_SHORT).show()

        }
    }

    private fun resgatarValoresLocacaoDoFirebase() {
        val docRef = db.collection("tabela de preço").document("preços")

        // Resgatando os documentos referentes aos valores de cada locação
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val dadosDoFirebase = document.data

                    if (dadosDoFirebase != null) {
                        val opcao1 = dadosDoFirebase["30 minutos"]
                        val opcao2 = dadosDoFirebase["1 hora"]
                        val opcao3 = dadosDoFirebase["Diaria"]
                        val opcao4 = dadosDoFirebase["Pernoite"]

                        // Atualizando os valores dos TextViews com os valores obtidos
                        findViewById<TextView>(R.id.opcaoLocacaoMinuto).text = "R$ " + opcao1.toString()
                        findViewById<TextView>(R.id.opcaoLocacaoHora).text = "R$ " + opcao2.toString()
                        findViewById<TextView>(R.id.opcaoLocacaoDiaria).text = "R$ " + opcao3.toString()
                        findViewById<TextView>(R.id.opcaoLocacaoPernoite).text = "R$ " + opcao4.toString()
                        // Toast.makeText(this, "Dados em ordem: $opcao1 $opcao2 $opcao3 $opcao4", LENGTH_SHORT).show()
                    }

                } else {
                    Log.d(TAG, "Documento inexistente")
                    Toast.makeText(this, "Documento inexistente", LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Problema: $exception")
                Toast.makeText(this, "Deu problema!", LENGTH_SHORT).show()
            }
    }
}