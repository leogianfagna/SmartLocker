package com.projetointegrador.smartlocker

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.projetointegrador.smartlocker.databinding.ActivityReleaseLockerBinding
import com.projetointegrador.smartlocker.databinding.ActivityRentLockerBinding

class ReleaseLockerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReleaseLockerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityReleaseLockerBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        val iniciarActivity = Intent(this, CamPreviewActivity::class.java)

        binding.btnUmaPessoa.setOnClickListener {
            iniciarActivity.putExtra("clientes", "1");
            startActivity(iniciarActivity);
        }

        binding.btnDuasPessoas.setOnClickListener {
            iniciarActivity.putExtra("clientes", "2")
            startActivity(iniciarActivity)
        }
    }
}