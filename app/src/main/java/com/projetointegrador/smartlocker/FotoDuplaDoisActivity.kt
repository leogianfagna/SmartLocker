package com.projetointegrador.smartlocker

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.projetointegrador.smartlocker.databinding.ActivityFotoDuplaDoisBinding

class FotoDuplaDoisActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFotoDuplaDoisBinding
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

        binding.btnContinue.setOnClickListener {
            val activity = Intent(this, /*LINKAR OUTRA ACTIVITY AQUI*/ ::class.java)
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