package com.projetointegrador.smartlocker

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.projetointegrador.smartlocker.databinding.ActivityFotoDuplaUmBinding

class FotoDuplaUmActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFotoDuplaUmBinding
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

        //BOTAO ABAIXO ABRE A CAMERA NOVAMENTE E TIRA A FOTO DE OUTRO CLIENTE

        binding.btnContinue.setOnClickListener {
            val activity = Intent(this, CamPreviewActivity::class.java)
            startActivity(activity)
            finish()
        }

        //BOTAO ABAIXO DESCARTA A FOTO TIRADA E ABRE A CAMERA NOVAMENTE

        binding.btnOutraFoto.setOnClickListener {
            val activity = Intent(this, CamPreviewActivity::class.java)
            startActivity(activity)
            finish()
        }
    }
}