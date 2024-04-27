package com.projetointegrador.smartlocker

import android.app.Activity
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
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

    fun instanciarBotoes() {

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

            if (!isOnline(this)){
                var snackbar = Snackbar.make(it, "Conecte-se a internet", Snackbar.LENGTH_SHORT)
                snackbar.show()
            }else{
                // Logar no modo anônimo do Firebase Auth
                auth.signInAnonymously()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val usuariosMap = hashMapOf(
                                "cartao" to false
                            )
                            val userId = FirebaseAuth.getInstance().currentUser!!.uid
                            db.collection("usuarios").document(userId)
                                .set(usuariosMap).addOnSuccessListener {
                                    Log.d("db", "sucesso ao salvar os dados")
                                    print("mensagem sucesso")
                                }.addOnFailureListener{e ->
                                    Log.w("db", "deu erro ao salvar os dados", e)
                                }
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


    private fun iniciarMainActivity() {
        // Conferir se há pendência
        val userId = FirebaseAuth.getInstance().currentUser!!.uid

        val docRef = db.collection("usuarios").document(userId)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val existePendencia = document.data?.get("pendencia")

                    if (existePendencia == true) {
                        val view = findViewById<View>(android.R.id.content)
                        val snackbar = Snackbar.make(view, "Você possui um aluguel em andamento", Snackbar.LENGTH_INDEFINITE)

                        // Ação de um botão dentro da SnackBar
                        snackbar.setAction("Recuperar", View.OnClickListener {
                            val intent = Intent(this, GerarQRcodeActivity::class.java)
                            startActivity(intent)

                            val updates = hashMapOf<String, Any>(
                                "pendencia" to false
                            )

                            // Remover a atual pendência
                            db.collection("usuarios").document(userId).set(updates, SetOptions.merge())
                        })

                        snackbar.show()
                    } else {
                        val iniciarActivity = Intent(this, MapsActivity::class.java)
                        startActivity(iniciarActivity)
                        finish()
                        Log.d(TAG, "Entrou aqui: $existePendencia")
                    }
                }
            }
            .addOnFailureListener {
                val iniciarActivity = Intent(this, MapsActivity::class.java)
                startActivity(iniciarActivity)
                finish()
                Log.d(TAG, "Entrou no erro")
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
}