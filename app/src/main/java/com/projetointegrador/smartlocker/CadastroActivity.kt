package com.projetointegrador.smartlocker

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.snackbar.Snackbar
import com.projetointegrador.smartlocker.databinding.ActivityCadastroBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class CadastroActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCadastroBinding
    private val auth = FirebaseAuth.getInstance()
    private val db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cadastro)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding = ActivityCadastroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.cadastroEditTextDataNasc.addTextChangedListener(object : TextWatcher{

            var sb : StringBuilder = StringBuilder("")

            var _ignore = false

            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                if(_ignore){
                    _ignore = false
                    return
                }

                sb.clear()
                sb.append(if(s!!.length > 10){ s.subSequence(0,10) }else{ s })

                if(sb.lastIndex == 2){
                    if(sb[2] != '/'){
                        sb.insert(2,"/")
                    }
                } else if(sb.lastIndex == 5){
                    if(sb[5] != '/'){
                        sb.insert(5,"/")
                    }
                }

                _ignore = true
                binding.cadastroEditTextDataNasc.setText(sb.toString())
                binding.cadastroEditTextDataNasc.setSelection(sb.length)

            }
        })

        binding.cadastroTenhoOutraConta.setOnClickListener {
            val i = Intent(this, InicioActivity::class.java)
            startActivity(i)
            finish()
        }

        binding.btnCriarConta.setOnClickListener {

            val nome = binding.cadastroEditTextNome.text.toString()
            val cpf = binding.cadastroEditTextCPF.text.toString()
            val email = binding.cadastroEditTextEmail.text.toString()
            val celular = binding.cadastroEditTextCelular.text.toString()
            val dataNasc = binding.cadastroEditTextDataNasc.text.toString()
            val senha = binding.cadastroEditTextConfirmeSenha.text.toString()



            lateinit var snackbar: Snackbar
            auth.createUserWithEmailAndPassword(email, senha)
                .addOnCompleteListener{ cadastro ->
                    snackbar = Snackbar.make(it, "Conta criada", Snackbar.LENGTH_SHORT)
                    snackbar.show()
                }.addOnFailureListener { excessao ->
                    snackbar = Snackbar.make(it, "Um erro incomum ocorreu!", Snackbar.LENGTH_SHORT)
                    snackbar.show()
                }
            val usuariosMap = hashMapOf(
                "nome" to nome,
                "cpf" to cpf,
                "email" to email,
                "cel" to celular,
                "dataNasc" to dataNasc,
                "cartao" to false

            )


            db.collection("usuarios").document(cpf)
                .set(usuariosMap).addOnSuccessListener {
                    Log.d("db", "sucesso ao salvar os dados")
                    print("mensagem sucesso")
                }.addOnFailureListener{e ->
                    Log.w("db", "deu erro ao salvar os dados", e)
                }
        }


    }
}