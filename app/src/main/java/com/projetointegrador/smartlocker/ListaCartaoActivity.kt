package com.projetointegrador.smartlocker

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.projetointegrador.smartlocker.databinding.ActivityListaCartaoBinding

class ListaCartaoActivity : AppCompatActivity() {

    private val TAG = "ListaCartaoActivity"

    private lateinit var binding: ActivityListaCartaoBinding
    private lateinit var adapter: CardAdapter
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityListaCartaoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        getCardsFromFirestore()
    }

    fun getCardsFromFirestore() {
        val userID = FirebaseAuth.getInstance().currentUser?.uid
        db.collection("cards") // change "card" to "cards"
            .whereEqualTo("userID", userID)
            .get()
            .addOnSuccessListener { result ->
                val cards = result.map { document ->
                    document.toObject(CreditCard::class.java)
                }
                adapter = CardAdapter(cards.toMutableList(), this)
                binding.recyclerView.adapter = adapter

                if (cards.size >= 3) {
                    binding.btnAddCartao.isEnabled = false
                    Toast.makeText(this, "Limite de 3 cartões atingido.", Toast.LENGTH_LONG).show()
                } else {
                    binding.btnAddCartao.isEnabled = true
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Erro ao receber documentos", exception)
            }
    }
}