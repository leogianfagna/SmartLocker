package com.projetointegrador.smartlocker

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.projetointegrador.smartlocker.databinding.ActivityFotoDuplaUmBinding
import java.io.File
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import android.graphics.BitmapFactory

class FotoDuplaUmActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFotoDuplaUmBinding
    private lateinit var userID: String
    private val db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityFotoDuplaUmBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_foto_dupla_um)
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