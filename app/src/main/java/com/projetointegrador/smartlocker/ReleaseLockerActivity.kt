package com.projetointegrador.smartlocker

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.projetointegrador.smartlocker.databinding.ActivityReleaseLockerBinding
import com.projetointegrador.smartlocker.databinding.ActivityRentLockerBinding
import kotlin.properties.Delegates

class ReleaseLockerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReleaseLockerBinding

    private var numeroClientes by Delegates.notNull<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityReleaseLockerBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.btnVoltar.setOnClickListener {
            finish()
        }

        binding.btnUmaPessoa.setOnClickListener {
            numeroClientes = 1
            cameraProviderResult.launch(android.Manifest.permission.CAMERA)
        }

        binding.btnDuasPessoas.setOnClickListener {
            numeroClientes = 2
            cameraProviderResult.launch(android.Manifest.permission.CAMERA)
        }
    }

    private val cameraProviderResult = registerForActivityResult(ActivityResultContracts.RequestPermission()){

        if(it){
            abrirTelaPreview()
        }

    }

    private fun abrirTelaPreview(){
        val intentCamPreview = Intent(this, CamPreviewActivity::class.java)
        intentCamPreview.putExtra("ordem", "primeiro");
        if (numeroClientes==1){
            intentCamPreview.putExtra("numClientes", "um")
        }else{
            intentCamPreview.putExtra("numClientes", "dois")
        }

        startActivity(intentCamPreview)
    }
}