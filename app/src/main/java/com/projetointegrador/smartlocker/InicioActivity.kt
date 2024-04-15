package com.projetointegrador.smartlocker

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class InicioActivity : AppCompatActivity() {

    private lateinit var btnCadastro: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_inicio)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        btnCadastro = findViewById(R.id.btnCadastro)

        btnCadastro.setOnClickListener {
            val i = Intent(this, RecuperarSenhaActivity::class.java)
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) // Call Only, if you wants to clears the activity stack else ignore it.
            startActivity(i)
            finish()
        }
    }
}