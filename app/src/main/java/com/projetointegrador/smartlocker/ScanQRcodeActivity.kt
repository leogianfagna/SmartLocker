package com.projetointegrador.smartlocker

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.firestore
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import com.projetointegrador.smartlocker.databinding.ActivityScanQrcodeBinding

class ScanQRcodeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScanQrcodeBinding
    val db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanQrcodeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.btnVoltar.setOnClickListener {
            finish()
        }
        binding.scanBtn.setOnClickListener{
            onButtonClick(view)
        }

    }

    // Register the launcher and result handler
    private val barcodeLauncher = registerForActivityResult<ScanOptions, ScanIntentResult>(
        ScanContract()
    ) { result: ScanIntentResult ->
        val userId = FirebaseAuth.getInstance().currentUser!!.uid


        // Usuário da leitura não condiz com o userId
        if (userId != result.contents && result.contents.length == 28) {
            Toast.makeText(this, "Usuário Inválido", Toast.LENGTH_LONG).show()
        } else if (userId == result.contents) {

            // Usuário válido, processar condições
            db.collection("locação")
                .document(userId).get()
                .addOnSuccessListener { document ->
                    val unidade = document.getString("unidade")!!
                    val nomeCliente = document.getString("nomeCliente")!! // Todo: Checar, não seria "locacao"?
                    salvarUserNoSharedPreferences(this, nomeCliente)

                    // Unidade confirmada, todos os processos de verificações foram atingidos
                    // TODO: Lógica para validar o QRCode com a localização
                    if (unidade == "UNICAMP") {
                        Toast.makeText(this, "Usuário confirmado", Toast.LENGTH_LONG).show()
                        val i = Intent(this, ReleaseLockerActivity::class.java)

                        startActivity(i)
                    } else {
                        // TODO: Implementar mensagem fixa com botão de confirmação. Toast e Snackbar serão difíceis de ler.
                        Toast.makeText(this, "Local inválido, confirme a unidade de locação com o cliente.", Toast.LENGTH_LONG)
                            .show()
                    }
                }

        } else {
             // TODO: Implementar mensagem fixa para conseguir ler melhor com botão de confirmação. Além disso, implementar mensagem do que fazer nesse caso (não leu o QRCODE, e ai?)
             Toast.makeText(this, "QRCode inválido.", Toast.LENGTH_LONG)
                 .show()

         }
    }

    // Launch
    fun onButtonClick(view: View?) {
        barcodeLauncher.launch(ScanOptions())
    }

    // Função que vai salvar o nome do cliente no SharedPreferences, para conseguir pegar as demais
    // informações desse cliente de forma mais fácil em outras activities
    private fun salvarUserNoSharedPreferences(context: Context, userEscaneado: String) {
        val sharedPref: SharedPreferences = context.getSharedPreferences("ControleGerente", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPref.edit()

        editor.putString("nomeClienteShared", userEscaneado)
        editor.apply()
    }
}