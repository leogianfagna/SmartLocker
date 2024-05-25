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
import com.projetointegrador.smartlocker.databinding.ActivityConfirmarDuplaBinding
import java.io.File

class ConfirmarDuplaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityConfirmarDuplaBinding
    private lateinit var userID: String
    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityConfirmarDuplaBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_confirmar_dupla)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        userID = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        val imageFile1 = File(getExternalMediaDirs()[0], userID)
        val imageFile2 = File(getExternalMediaDirs()[0], "$userID-2")

        if(imageFile1.exists()){
            val myBitmap1 = BitmapFactory.decodeFile(imageFile1.absolutePath)
            binding.imageViewLocatario1.setImageBitmap(myBitmap1)
        }

        if(imageFile2.exists()){
            val myBitmap2 = BitmapFactory.decodeFile(imageFile2.absolutePath)
            binding.imageViewLocatario2.setImageBitmap(myBitmap2)
        }
    }
}