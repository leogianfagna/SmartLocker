package com.projetointegrador.smartlocker

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.projetointegrador.smartlocker.databinding.ActivityFotoDuplaDoisBinding
import android.graphics.BitmapFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.io.File

class FotoDuplaDoisActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFotoDuplaDoisBinding
    private lateinit var userID: String
    private val db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityFotoDuplaDoisBinding.inflate(layoutInflater)
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

        binding.btnOutraFoto.setOnClickListener {
            val activity = Intent(this, CamPreviewActivity::class.java)
            startActivity(activity)
            finish()
        }
    }
}