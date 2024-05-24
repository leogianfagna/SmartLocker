package com.projetointegrador.smartlocker

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.projetointegrador.smartlocker.databinding.ActivityConfirmRentBinding

class ConfirmRentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityConfirmRentBinding
    private val nomeCliente = obterNomeDoCliente(this)
    private var documentoCliente: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityConfirmRentBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        preencherDadosDaConfirmacao()
        instanciarBotoes()
    }

    private fun preencherDadosDaConfirmacao() {
        val locacaoCollection = Firebase.firestore.collection("locacao")

        // Realiza a consulta no Firestore
        locacaoCollection
            // Procura qual o documento pertence à esse nome de Cliente
            .whereEqualTo("nomeCliente", nomeCliente)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    Log.i("Firebase", "Documento encontrado! ${document.id}")

                    documentoCliente = document.id

                    // Resgata os demais dados e preenche na tela
                    val dadosDoFirebase = document.data
                    binding.nomeCliente.text = dadosDoFirebase["nomeCliente"].toString()
                    binding.nomeUnidade.text = dadosDoFirebase["unidade"].toString()
                    binding.numArmario.text = dadosDoFirebase["armario"].toString()
                    binding.tempoUtilizacao.text = dadosDoFirebase["tempoUso"].toString()
                }
            }
            .addOnFailureListener {
                Log.e("Firebase", "Nenhum documento encontrado.")
            }
    }

    private fun instanciarBotoes() {
        binding.btnConfirmar.setOnClickListener {
            showAlertDialogConfirm()
        }

        binding.btnCancelar.setOnClickListener {
            showAlertDialogCancel()
        }
    }

    private fun showAlertDialogConfirm() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.alert_confirm_rent, null)

        val alertDialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        // Confirma a locação
        dialogView.findViewById<Button>(R.id.btnSim).setOnClickListener {
            val docRef = Firebase.firestore.collection("locacao").document(documentoCliente)

            // Define os campos que serão atualizados
            val updates = hashMapOf<String, Any>(
                "status" to "confirmado"
            )

            // Atualiza o documento
            docRef
                .update(updates)
                .addOnFailureListener {
                    Toast.makeText(this, "Não foi possível confirmar. Tente novamente.", Toast.LENGTH_LONG).show()
                }

            alertDialog.dismiss()
            // TODO: Ir para a tela de locação confirmada
        }

        dialogView.findViewById<Button>(R.id.btnNao).setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

    private fun showAlertDialogCancel() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.alert_cancel_rent, null)

        val alertDialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        dialogView.findViewById<Button>(R.id.btnSim).setOnClickListener {
            /* TODO: Voltar para a activity depois de cancelar @Arthur
            val voltarActivityPrincipalGerente = Intent(this, ScanQRcodeActivity::class.java)
            startActivity(voltarActivityPrincipalGerente)
            finish()
            */
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