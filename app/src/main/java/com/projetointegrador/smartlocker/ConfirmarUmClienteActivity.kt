package com.projetointegrador.smartlocker

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.graphics.BitmapFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.projetointegrador.smartlocker.databinding.ActivityConfirmarUmClienteBinding
import java.io.File

class ConfirmarUmClienteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityConfirmarUmClienteBinding
    private lateinit var userID: String
    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityConfirmarUmClienteBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_confirmar_um_cliente)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        userID = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        val imageFile = File(getExternalMediaDirs()[0], userID)

        if(imageFile.exists()){
            val myBitmap = BitmapFactory.decodeFile(imageFile.absolutePath)
            binding.imageViewLocatario.setImageBitmap(myBitmap)
        }
    }
}

//TODO @Arthur linkar com a activity de liberar armário