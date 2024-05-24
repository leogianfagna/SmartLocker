package com.projetointegrador.smartlocker

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
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
import java.text.SimpleDateFormat
import java.util.Locale


class CadastroActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCadastroBinding
    private val auth = FirebaseAuth.getInstance()
    private val db = Firebase.firestore
    private fun validateInputLength(cpf: String, celular: String, dataNasc: String): Boolean {
        val cpfLength = 14
        val phoneNumberLength = 15
        val birthDateLength = 10

        return cpf.length == cpfLength && celular.length == phoneNumberLength && dataNasc.length == birthDateLength
    }
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
        setupCPFInput(binding.cadastroEditTextCPF)
        setupPhoneNumberInput(binding.cadastroEditTextCelular)


        binding.cadastroTenhoOutraConta.setOnClickListener {
            val i = Intent(this, InicioActivity::class.java)
            startActivity(i)
            finish()
        }



        binding.btnCriarConta.setOnClickListener {

            if (!isOnline(this)){
                var snackbar = Snackbar.make(it, "Conecte-se a internet", Snackbar.LENGTH_SHORT)
                snackbar.show()
            }else{
                val nome: String = binding.cadastroEditTextNome.text.toString()
                val cpf:String = binding.cadastroEditTextCPF.text.toString()
                val email:String = binding.cadastroEditTextEmail.text.toString()
                val celular:String = binding.cadastroEditTextCelular.text.toString()
                val dataNasc:String = binding.cadastroEditTextDataNasc.text.toString()
                val senha:String = binding.cadastroEditTextCriarSenha.text.toString()
                val confSenha:String = binding.cadastroEditTextConfirmeSenha.text.toString()
                if (!isValidBirthDate(dataNasc)) {
                    Toast.makeText(this, "Data de nascimento inválida", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                if (!isValidBirthDate(dataNasc)) {
                    Toast.makeText(this, "Data de nascimento inválida", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                if (!validateInputLength(cpf, celular, dataNasc)) {
                    Toast.makeText(this, "Por favor, preencha todos os campos corretamente", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

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
    }

    private fun isValidBirthDate(date: String): Boolean {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
        dateFormat.isLenient = false
        return try {
            dateFormat.parse(date)
            true
        } catch (e: Exception) {
            false
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
                        Log.d("db", "Dados salvos com sucesso!")
                        print("mensagem sucesso")
                    }.addOnFailureListener{e ->
                        Log.w("db", "Erro ao salvar os dados", e)
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

    private fun setupCPFInput(textfield: EditText) {
        textfield.addTextChangedListener(object : TextWatcher {

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
                sb.append(if(s!!.length > 14){ s.subSequence(0,14) }else{ s })

                if(sb.lastIndex == 3){
                    if(sb[3] != '.'){
                        sb.insert(3,".")
                    }
                } else if(sb.lastIndex == 7){
                    if(sb[7] != '.'){
                        sb.insert(7,".")
                    }
                } else if(sb.lastIndex == 11){
                    if(sb[11] != '-'){
                        sb.insert(11,"-")
                    }
                }

                _ignore = true
                binding.cadastroEditTextCPF.setText(sb.toString())
                binding.cadastroEditTextCPF.setSelection(sb.length)

            }
        })
    }

    private fun setupPhoneNumberInput(textfield: EditText) {
        textfield.addTextChangedListener(object : TextWatcher {

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
                sb.append(if(s!!.length > 15){ s.subSequence(0,15) }else{ s })

                if(sb.lastIndex == 0){
                    if(sb[0] != '('){
                        sb.insert(0,"(")
                    }
                } else if(sb.lastIndex == 5){
                    if(sb[5] != ')'){
                        sb.insert(3,")")
                    }
                } else if(sb.lastIndex == 3){
                    if(sb[3] != ' '){
                        sb.insert(3," ")
                    }
                } else if(sb.lastIndex == 10){
                    if(sb[10] != '-'){
                        sb.insert(10,"-")
                    }
                }

                _ignore = true
                binding.cadastroEditTextCelular.setText(sb.toString())
                binding.cadastroEditTextCelular.setSelection(sb.length)

            }
        })
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