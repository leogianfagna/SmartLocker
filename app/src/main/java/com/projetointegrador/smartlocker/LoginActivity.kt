package com.projetointegrador.smartlocker

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.projetointegrador.smartlocker.databinding.ActivityLoginBinding
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.SetOptions

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val auth = FirebaseAuth.getInstance()
    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Instanciando e utilizando o ViewBinding
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // Instanciando todos os botões
        instanciarBotoes()
    }

    private fun instanciarBotoes() {

        // Página de cadastro (Não tenho uma conta)
        binding.btnCreateAccount.setOnClickListener {
            val activityIniciada = Intent(this, CadastroActivity::class.java)
            startActivity(activityIniciada)
            finish()
        }

        // Esqueceu a senha
        binding.btnRecoveryPassword.setOnClickListener {
            val activityIniciada = Intent(this, RecuperarSenhaActivity::class.java)
            startActivity(activityIniciada)
            finish()
        }

        // Acessar como visitante (modo anônimo no Auth)
        binding.btnVisitante.setOnClickListener {

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
                            Toast.makeText(this,"Logado no modo anônimo com sucesso!", LENGTH_SHORT).show()
                            iniciarMainActivity()
                        } else {
                            Toast.makeText(this, task.exception!!.message.toString(), LENGTH_SHORT).show()
                        }
                    }
            }
        }

        // Botão de logar com email
        binding.btnLogar.setOnClickListener {
            // Informações preenchidas nos labels
            // Método Trim para remover os espaços em brancos se houver
            val inputEmail : String = binding.emailInput.text.toString().trim{it <= ' '}
            val inputPassword : String = binding.passInput.text.toString().trim{it <= ' '}

            // Autenticação com Firebase
            auth.signInWithEmailAndPassword(inputEmail, inputPassword)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val firebaseUser: FirebaseUser = task.result!!.user!! // Objeto do tipo task com a propriedade "result" (!! = Forçar que não é nulo)

                        Toast.makeText(this,"Logado com sucesso com o email ${firebaseUser.email}!", LENGTH_SHORT).show()
                        iniciarMainActivity()
                    } else {
                        Toast.makeText(this, task.exception!!.message.toString(), LENGTH_SHORT).show()
                    }
                }
        }
    }

    // Essa função vai iniciar uma activity dependendo se é gerente ou não. Vai resgatar o dado dentro do documento da pessoa
    private fun iniciarMainActivity() {
        // Conferir se é um gerente
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        var usuarioGerente: Boolean? = false
        db.collection("usuarios").document(userId)
            .get()
            .addOnSuccessListener { document ->
                // Aqui estamos dentro do documento do usuário
                usuarioGerente = document.getBoolean("gerente")
            }

        if (usuarioGerente == true) {
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

    // Função que confere se o usuário está conectado à internet
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
}