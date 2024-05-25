package com.projetointegrador.smartlocker

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.projetointegrador.smartlocker.databinding.ActivityOpenLockerBinding

class OpenLockerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOpenLockerBinding
    private val nomeCliente = obterNomeDoCliente(this)
    private var documentoCliente: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityOpenLockerBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        preencherDadosDaConfirmacao()
        instanciarBotoes()
    }

    private fun preencherDadosDaConfirmacao() {
        val locacaoCollection = Firebase.firestore.collection("locacao")

        // Realiza a consulta no Firestore
        locacaoCollection
            // Procura qual o documento pertence à esse nome de Cliente. O nome é resgatado pelo SharedPreferences
            .whereEqualTo("nomeCliente", nomeCliente)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    Log.i("Firebase", "Documento encontrado! ${document.id}")

                    documentoCliente = document.id

                    // Resgata os demais dados e preenche na tela
                    val dadosDoFirebase = document.data
                    val numeroArmarioTitle = dadosDoFirebase["armario"].toString()
                    binding.nomeCliente.text = dadosDoFirebase["nomeCliente"].toString()
                    binding.numLocacao.text = dadosDoFirebase["numLocacao"].toString()
                    binding.dateStart.text = dadosDoFirebase["inicioLocacao"].toString()
                    binding.dateEnd.text = dadosDoFirebase["fimLocacao"].toString()
                    binding.numArmarioTitle.text = "ARMÁRIO $numeroArmarioTitle"
                }
            }
            .addOnFailureListener {
                Log.e("Firebase", "Nenhum documento encontrado.")
            }
    }

    private fun instanciarBotoes() {
        binding.btnAbrir.setOnClickListener {
            showAlertDialogAbrir()
        }

        binding.btnEncerrar.setOnClickListener {
            showAlertDialogEncerrar()
        }

        binding.btnVoltar.setOnClickListener {

            val userId = FirebaseAuth.getInstance().currentUser!!.uid
            val docRef = Firebase.firestore.collection("usuarios").document(userId)
            docRef.get()
                .addOnSuccessListener { document -> val unidadeGerente = document.getString("unidade")
                    val intent = Intent(this, GerenteInicioActivity::class.java).apply {
                        putExtra("UNIDADE_GERENTE", unidadeGerente)
                    }
                    startActivity(intent)
                    finish()

                }

        }
    }

    private fun showAlertDialogAbrir() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.alert_open_locker, null)

        val alertDialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        // Abre o armário temporariamente
        dialogView.findViewById<Button>(R.id.btnSim).setOnClickListener {
            alertDialog.dismiss()

            val intent = Intent(this,ArmarioAbertoActivity::class.java).apply {
                putExtra("LOCACAO_ID", FirebaseAuth.getInstance().currentUser!!.uid)
            }
            startActivity(intent)
            finish()
        }

        dialogView.findViewById<Button>(R.id.btnNao).setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

    private fun showAlertDialogEncerrar() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.alert_cancel_rent, null)

        val alertDialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        dialogView.findViewById<Button>(R.id.btnSim).setOnClickListener {
            // TODO: Avançar para a activity X (locação encerrada @?)
            alertDialog.dismiss()
        }

        dialogView.findViewById<Button>(R.id.btnNao).setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

    // Função para resgatar o nome do cliente, guardado no SharedPreferences durante o escaneamento do QRCODE
    private fun obterNomeDoCliente(context: Context): String {
        val sharedPref: SharedPreferences = context.getSharedPreferences("ControleGerente", Context.MODE_PRIVATE)
        val nomeEncontrado = sharedPref.getString("nomeClienteShared", null)


        return nomeEncontrado ?: ""
    }
}