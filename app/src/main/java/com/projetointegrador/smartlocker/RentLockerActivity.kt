package com.projetointegrador.smartlocker

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
import androidx.appcompat.app.AppCompatActivity
import com.projetointegrador.smartlocker.databinding.ActivityRentLockerBinding
import android.widget.Toast.LENGTH_SHORT
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.zxing.qrcode.encoder.QRCode
import java.lang.Exception
import kotlin.math.log

class RentLockerActivity() : AppCompatActivity() {

    private lateinit var binding: ActivityRentLockerBinding
    private lateinit var itemSelecionadoNoSpinner: String
    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Instanciando e utilizando o ViewBinding
        binding = ActivityRentLockerBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // Instanciando todos os botões da interface
        instanciarBotoes()
        resgatarValoresLocacaoDoFirebase()

    }

    private fun instanciarBotoes() {

        // Voltar uma activity
        binding.btnBack.setOnClickListener {
            val i = Intent(this, MapsActivity::class.java)
            startActivity(i)
            finish()
        }

        // Adicionar as opções do spinner
        val spinner: Spinner = findViewById(R.id.spinnerOpcoesValores)

        // Criar um ArrayAdapter usando a array de strings em strings.xml e um layout padrão
        ArrayAdapter.createFromResource(
            this,
            R.array.spinner_opcoes_valores_string,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) // Especifica o layout
            spinner.adapter = adapter // Aplica o adaptador ao spinner
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

        //Pegar nome da unidade no bundle
        val bundle = intent.extras
        var nomeDaLocalizacao = bundle!!.getString("uni", "Default")

        //Colocar nome da unidade no text view
        binding.rentlockerNomeUnidade.text="Unidade - $nomeDaLocalizacao"

        // Confirmar locação
        binding.btnConfirmRent.setOnClickListener {
            if (itemSelecionadoNoSpinner == "Selecione a opção") {
                Toast.makeText(this, "Você deve preencher uma opção!", LENGTH_SHORT).show()
            } else {
                registrarLocacaoFirebase(itemSelecionadoNoSpinner, nomeDaLocalizacao)
            }
        }
    }

    private fun resgatarValoresLocacaoDoFirebase() {

        // Resgatando os documentos referentes aos valores de cada locação
        val docRef = db.collection("tabela de preço").document("preços")
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val dadosDoFirebase = document.data

                    // Recebe os diferentes tipos de valores por tempo
                    if (dadosDoFirebase != null) {
                        val opcao1 = String.format("%.2f", dadosDoFirebase["30 minutos"].toString().toDouble())
                        val opcao2 = String.format("%.2f", dadosDoFirebase["1 hora"].toString().toDouble())
                        val opcao3 = String.format("%.2f", dadosDoFirebase["Diaria"].toString().toDouble())
                        val opcao4 = String.format("%.2f", dadosDoFirebase["Pernoite"].toString().toDouble())


                        // Atualizando os valores dos TextViews com os valores obtidos
                        findViewById<TextView>(R.id.opcaoLocacaoMinuto).text = "R$ $opcao1"
                        findViewById<TextView>(R.id.opcaoLocacaoHora).text = "R$ $opcao2"
                        findViewById<TextView>(R.id.opcaoLocacaoDiaria).text = "R$ $opcao3"
                        findViewById<TextView>(R.id.opcaoLocacaoPernoite).text = "R$ $opcao4"
                    }

                } else {
                    Log.d(TAG, "Documento inexistente")
                    Toast.makeText(this, "Documento inexistente", LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                problemaDataBase(exception)
            }
    }

    private fun registrarLocacaoFirebase(itemSelecionadoNoSpinner: String, nomeDaUnidade: String) {
        // Formatar o nome da unidade removendo espaços e pontos
        val nomeUnidadePascoalCase = nomeDaUnidade.replace("\\s|\\.".toRegex(), "")
        Log.d(TAG, "Nome da unidade: $nomeUnidadePascoalCase")

        // Confere se há lockers vagos e qual é o primeiro
        val docRef = db.collection("unidades de locação").document(nomeUnidadePascoalCase)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val totalDeArmarios = document.getDouble("totalArmarios")?.toInt()
                    var armarioEncontrado : String = "nenhum"
                    var posicaoArmario : String = "String"
                    var statusArmarioAtual : Boolean?

                    // Verificar armário por armário qual está vazio
                    if (totalDeArmarios != null) {
                        for (i in 0 until totalDeArmarios) {
                            posicaoArmario = "A$i"
                            statusArmarioAtual = document.getBoolean(posicaoArmario)

                            if (statusArmarioAtual == false) {
                                armarioEncontrado = posicaoArmario
                                break
                            }
                        }
                    } else {
                        Toast.makeText(this, "Nenhum valor encontrado", LENGTH_SHORT).show()
                    }

                    // Há disponibilidade, reservar o armário no Firebase
                    if (armarioEncontrado != "nenhum") {

                        // Define as mudanças no campo do Firebase
                        val updates = hashMapOf<String, Any>(
                            armarioEncontrado to true
                        )

                        // Confirma a reserva: Aplica a mudança de valor no campo do armário reservado
                        docRef.set(updates, SetOptions.merge())
                            .addOnSuccessListener {
                                Log.d(TAG, "Armário $armarioEncontrado reservado.")

                                val prosseguirActivity = Intent(this, GerarQRcodeActivity::class.java)
                                startActivity(prosseguirActivity)
                                finish()

                            }
                            .addOnFailureListener { exception ->
                                problemaDataBase(exception)
                            }

                        /* Adiantamento para o segundo módulo!!! Quando precisar, completar esse código @leogianfagna
                        // Todo: Salvar na outra coleção
                        val idDoUsuarioAutenticado = FirebaseAuth.getInstance().currentUser!!.uid
                        val userDocRef = db.collection("usuarios").document(idDoUsuarioAutenticado)
                        val reservaColecaoRef = userDocRef.collection("reserva")

                        val updatesDois = hashMapOf<String, Any>(
                            "unidade" to nomeUnidadePascoalCase,
                            "armario" to armarioEncontrado,
                            "aluguel" to itemSelecionadoNoSpinner
                        )

                        reservaColecaoRef.set(updatesDois, SetOptions.merge())
                            .addOnSuccessListener {
                                Log.d(TAG, "Dados salvos.")
                            }
                        */


                    } else {
                        // Não há armários disponíveis
                        Toast.makeText(this, "Nenhum armário disponível encontrado!", LENGTH_SHORT).show()
                    }
                }
            }
            .addOnFailureListener { exception ->
                problemaDataBase(exception)
            }
    }

    // Função que diminui redundância das chamadas addOnFailureListener
    private fun problemaDataBase(exception: Exception) {
        val i = Intent(this, LoginActivity::class.java)
        startActivity(i)
        finish()
        Toast.makeText(this, "Um problema de conexão ocorreu. Por favor, recomece.", LENGTH_SHORT).show()
        Log.d(TAG, "Problema: $exception")
    }
}