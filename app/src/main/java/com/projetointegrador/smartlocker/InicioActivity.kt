package com.projetointegrador.smartlocker

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.projetointegrador.smartlocker.databinding.ActivityInicioBinding
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class InicioActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInicioBinding
    private val auth = FirebaseAuth.getInstance()
    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_inicio)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding = ActivityInicioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnCadastro.setOnClickListener {
            val i = Intent(this, CadastroActivity::class.java)
            startActivity(i)
        }

        binding.btnLogar.setOnClickListener {
            val j = Intent(this, LoginActivity::class.java)
            startActivity(j)
        }

        binding.inicioContaVisitanteTV.setOnClickListener {
            if (!isOnline(this)) {
                Snackbar.make(it, "Conecte-se a internet.", Snackbar.LENGTH_SHORT).show()
            } else {
                // Logar no modo anônimo do Firebase Auth
                auth.signInAnonymously()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val usuariosMap = hashMapOf(
                                "cartao" to false,
                                "gerente" to false
                            )
                            val userId = FirebaseAuth.getInstance().currentUser!!.uid
                            db.collection("usuarios").document(userId).set(usuariosMap)
                            Toast.makeText(this,"Logado no modo anônimo com sucesso!",
                                Toast.LENGTH_SHORT
                            ).show()
                            iniciarMainActivity()
                        } else {
                            Toast.makeText(this, task.exception!!.message.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            }
        }
    }
    private fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                    return true
                }
            }
        }
        return false
    }
    private fun iniciarMainActivity() {
        // Conferir se é um gerente
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        var usuarioGerente = "cliente"
        db.collection("locação").document(userId)
            .get()
            .addOnSuccessListener { document ->
                // Aqui estamos dentro do documento do usuário
                usuarioGerente = document.getString("gerente") ?: "cliente"
            }

        if (usuarioGerente == "gerente") {
            // TODO: @Arthur preencher com a activity principal do gerente
            val i = Intent(this, InicioActivity::class.java)
            startActivity(i)
        } else {
            // Conferir se há pendência de locação pelo usuário
            val docRef = db.collection("locacao").document(userId)
            docRef.get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val existePendencia = document.data?.get("status")

                        if (existePendencia != "confirmado") {
                            val view = findViewById<View>(android.R.id.content)
                            val snackbar = Snackbar.make(view, "Você possui um aluguel em andamento", Snackbar.LENGTH_INDEFINITE)

                            // Ação de um botão dentro da SnackBar
                            snackbar.setAction("Recuperar", View.OnClickListener {
                                val intent = Intent(this, GerarQRcodeActivity::class.java)
                                startActivity(intent)

                                val updates = hashMapOf<String, Any>(
                                    "pendencia" to "confirmado"
                                )

                                // Remover a atual pendência
                                db.collection("locacao").document(userId).set(updates, SetOptions.merge())
                            })

                            snackbar.show()
                        } else {
                            // Não há pendência
                            val iniciarActivity = Intent(this, MapsActivity::class.java)
                            startActivity(iniciarActivity)
                            finish()
                            Log.d("Sessão de login", "Entrou aqui: $existePendencia")
                        }
                    }
                }
                .addOnFailureListener {
                    val iniciarActivity = Intent(this, MapsActivity::class.java)
                    startActivity(iniciarActivity)
                    finish()
                    Log.d("Sessão de login", "Entrou no erro, continuar para tela principal.")
                }
        }
    }

}