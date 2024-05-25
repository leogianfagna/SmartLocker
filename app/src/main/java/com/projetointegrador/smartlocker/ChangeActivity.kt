package com.projetointegrador.smartlocker

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.projetointegrador.smartlocker.databinding.ActivityChangeBinding
import java.text.SimpleDateFormat
import java.util.Locale

class ChangeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChangeBinding
    private val db = FirebaseFirestore.getInstance()

    private fun isValidExpiryDate(date: String): Boolean {
        val dateFormat = SimpleDateFormat("MM/yy", Locale("pt", "BR"))
        dateFormat.isLenient = false
        return try {
            dateFormat.parse(date)
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun setupCVVInput(textfield: EditText) {
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
                sb.append(if(s!!.length > 3){ s.subSequence(0,3) }else{ s })

                _ignore = true
                binding.etCardCvv.setText(sb.toString())
                binding.etCardCvv.setSelection(sb.length)

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
                binding.etCardCpf.setText(sb.toString())
                binding.etCardCpf.setSelection(sb.length)

            }
        })
    }

    private fun setupExpirityDateInput(textfield: EditText) {
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
                sb.append(if(s!!.length > 5){ s.subSequence(0,5) }else{ s })

                if(sb.lastIndex == 2){
                    if(sb[2] != '/'){
                        sb.insert(2,"/")
                    }
                }

                _ignore = true
                binding.etCardExpirity.setText(sb.toString())
                binding.etCardExpirity.setSelection(sb.length)

            }
        })
    }

    private fun setupNumInput(textfield: EditText) {
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
                sb.append(if(s!!.length > 19){ s.subSequence(0,19) }else{ s })

                if(sb.lastIndex == 4){
                    if(sb[4] != ' '){
                        sb.insert(4," ")
                    }
                } else if(sb.lastIndex == 9){
                    if(sb[9] != ' '){
                        sb.insert(9," ")
                    }
                } else if(sb.lastIndex == 14){
                    if(sb[14] != ' '){
                        sb.insert(14," ")
                    }
                }

                _ignore = true
                binding.etCardNumber.setText(sb.toString())
                binding.etCardNumber.setSelection(sb.length)

            }
        })
    }

    private fun validateInputLength(cpf: String, cvv: String, cardNum: String): Boolean {
        val cpfLength = 14
        val cvvLength = 3
        val cardNumLength = 19

        return cpf.length == cpfLength && cvv.length == cvvLength && cardNum.length == cardNumLength
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupExpirityDateInput(binding.etCardExpirity)
        setupCVVInput(binding.etCardCvv)
        setupCPFInput(binding.etCardCpf)
        setupNumInput(binding.etCardNumber)

        binding.btnCardConfirm.setOnClickListener {
            val cardNumber = binding.etCardNumber.text.toString()
            val cardCpf = binding.etCardCpf.text.toString()
            val cardCvv = binding.etCardCvv.text.toString()
            val cardValidity = binding.etCardExpirity.text.toString()
            if (!isValidExpiryDate(cardValidity)) {
                Toast.makeText(this, "Data inválida", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!validateInputLength(cardCpf, cardCvv, cardNumber)) {
                Toast.makeText(this, "Por favor, preencha todos os campos corretamente", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val cardName = binding.etCardHolder.text.toString()

            val card = hashMapOf(
                "cardNumber" to cardNumber,
                "cardCpf" to cardCpf,
                "cardCvv" to cardCvv,
                "cardValidity" to cardValidity,
                "cardName" to cardName
            )

            db.collection("cards")
                .add(card)
                .addOnSuccessListener { documentReference ->
                    Toast.makeText(this, "Cartão registrado com sucesso", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Erro ao adicionar cartão", Toast.LENGTH_SHORT).show()
                }
            val activity = Intent(this, ListaCartaoActivity::class.java)
            startActivity(activity)
            finish()
        }


        binding.cancelarAdd.setOnClickListener {
            val activity = Intent(this, ListaCartaoActivity::class.java)
            startActivity(activity)
            finish()
        }
    }

    override fun onStop() {
        super.onStop()
        val i = Intent(this, ListaCartaoActivity::class.java)
        startActivity(i)
        finish()
    }
}