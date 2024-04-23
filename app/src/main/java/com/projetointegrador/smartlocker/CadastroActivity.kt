package com.projetointegrador.smartlocker

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
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

        setupDateInput(binding.cadastroEditTextDataNasc)

        binding.cadastroTenhoOutraConta.setOnClickListener {
            val i = Intent(this, InicioActivity::class.java)
            startActivity(i)
            finish()
        }



        binding.btnCriarConta.setOnClickListener {

            val nome: String = binding.cadastroEditTextNome.text.toString()
            val cpf:String = binding.cadastroEditTextCPF.text.toString()
            val email:String = binding.cadastroEditTextEmail.text.toString()
            val celular:String = binding.cadastroEditTextCelular.text.toString()
            val dataNasc:String = binding.cadastroEditTextDataNasc.text.toString()
            val senha:String = binding.cadastroEditTextCriarSenha.text.toString()
            val confSenha:String = binding.cadastroEditTextConfirmeSenha.text.toString()

            if (nome.isNotEmpty() && cpf.isNotEmpty() && email.isNotEmpty()
                && email.isNotEmpty() && celular.isNotEmpty() && dataNasc.isNotEmpty()
                && senha.isNotEmpty() && confSenha.isNotEmpty()){
                if (senha == confSenha){ criarConta(nome, cpf, email, celular, dataNasc, senha, confSenha, it) }
                else{
                    binding.cadastroEditTextConfirmeSenha.setError("Confirme se as senhas está corretas")
                    binding.cadastroEditTextCriarSenha.setError("Confirme se a senhas está corretas")
                }
            }
            else{
                if (nome.isEmpty()){binding.cadastroEditTextNome.setError("Coloque seu nome") }
                if (cpf.isEmpty()){binding.cadastroEditTextCPF.setError("Coloque seu cpf") }
                if (email.isEmpty()){binding.cadastroEditTextEmail.setError("Coloque seu email") }
                if (celular.isEmpty()){binding.cadastroEditTextCelular.setError("Coloque seu celular") }
                if (dataNasc.isEmpty()){binding.cadastroEditTextDataNasc.setError("Coloque sua data de nascimento") }
                if (senha.isEmpty()){binding.cadastroEditTextCriarSenha.setError("Coloque sua senha") }
                if (confSenha.isEmpty()){binding.cadastroEditTextConfirmeSenha.setError("Coloque sua senha") }
            }
        }
    }

    private fun criarConta(nome: String, cpf:String, email: String, celular: String, dataNasc:String, senha:String, confSenha:String, it: View) {
        lateinit var snackbar: Snackbar
        auth.createUserWithEmailAndPassword(email, senha)
            .addOnCompleteListener{ cadastro ->
                snackbar = Snackbar.make(it, "Conta criada", Snackbar.LENGTH_SHORT)
                snackbar.show()

                val usuariosMap = hashMapOf(
                    "nome" to nome,
                    "cpf" to cpf,
                    "email" to email,
                    "cel" to celular,
                    "dataNasc" to dataNasc,
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
            }.addOnFailureListener { excessao ->
                snackbar = Snackbar.make(it, "Um erro incomum ocorreu!", Snackbar.LENGTH_SHORT)
                snackbar.show()
            }

    }

    private fun setupDateInput(textfield: EditText) {
        textfield.addTextChangedListener(object : TextWatcher{

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
    }
}