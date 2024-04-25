package com.projetointegrador.smartlocker

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.projetointegrador.smartlocker.databinding.ActivityChangeBinding

class ChangeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChangeBinding
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnCardConfirm.setOnClickListener {
            val cardNumber = binding.etCardNumber.text.toString()
            val cardCpf = binding.etCardCpf.text.toString()
            val cardCvv = binding.etCardCvv.text.toString()
            val cardValidity = binding.etCardExpirity.text.toString()
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
                    Toast.makeText(this, "Card registered successfully", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error adding card", Toast.LENGTH_SHORT).show()
                }
        }
    }
}