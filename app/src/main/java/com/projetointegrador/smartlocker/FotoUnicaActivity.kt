package com.projetointegrador.smartlocker

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.projetointegrador.smartlocker.databinding.ActivityFotoUnicaBinding

class FotoUnicaActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFotoUnicaBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityFotoUnicaBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_foto_dupla_um)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //BOTAO ABAIXO DESCARTA A FOTO TIRADA E ABRE A CAMERA NOVAMENTE

        binding.btnOutraFoto.setOnClickListener {
            val activity = Intent(this, CamPreviewActivity::class.java)
            startActivity(activity)
            finish()
        }

        binding.btnContinue.setOnClickListener {
            val activity = Intent(this, /*LINKAR OUTRA ACTIVITY AQUI*/ ::class.java)
            startActivity(activity)
            finish()
        }

        //BOTAO ABAIXO VOLTA

        binding.btnVoltar.setOnClickListener {
            val activity = Intent(this, /*LINKAR OUTRA ACTIVITY AQUI*/::class.java)
            startActivity(activity)
            finish()
        }
    }
}