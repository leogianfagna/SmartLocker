package com.projetointegrador.smartlocker

import android.content.ContentValues.TAG
import android.content.Intent
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
         if (userId!=result.contents && result.contents.length == 28){
            Toast.makeText(this, "Usuário Invalido", Toast.LENGTH_LONG)
                .show()
        } else if (userId==result.contents){

            db.collection("locação")
                .document(userId).get()
                .addOnSuccessListener {document ->
                    val unidade = document.getString("unidade")!!
                    if (unidade=="UNICAMP"){
                        /*val i = Intent(this, InicioActivity::class.java)
                        startActivity(i)*/
                        Toast.makeText(
                            this,
                            "Usuário confirmado",
                            Toast.LENGTH_LONG
                        ).show()
                    }else{
                        // TODO: Implementar mensagem fixa com botão de confirmação. Toast e Snackbar serão difíceis de ler.
                        Toast.makeText(this, "Local inválido, confirme a unidade de locação com o cliente.", Toast.LENGTH_LONG)
                            .show()
                    }
                }

        }else {
             Toast.makeText(this, "QRCode Invalido", Toast.LENGTH_LONG)
                 .show()

         }
    }

    // Launch
    fun onButtonClick(view: View?) {
        barcodeLauncher.launch(ScanOptions())
    }
}