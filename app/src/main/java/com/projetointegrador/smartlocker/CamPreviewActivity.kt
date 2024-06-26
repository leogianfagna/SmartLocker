package com.projetointegrador.smartlocker

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.common.util.concurrent.ListenableFuture
import com.google.firebase.auth.FirebaseAuth
import com.projetointegrador.smartlocker.databinding.ActivityCamPreviewBinding
import java.io.File
import java.lang.Exception
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CamPreviewActivity(): AppCompatActivity() {

/*
    //CODIGO PARA QUANDO FOR O PRIMEIRO CLIENTE E O SEGUNDO CLIENTE

    val i = Intent(this, CamPreviewActivity::class.java)
    i.putExtra("numeroCliente", 1) ESTE É PARA O PRIMEIRO CLIENTE
    i.putExtra("numeroCliente", 2) ESTE É PARA O SEGUNDO CLIENTE
    startActivity(i)
*/
    private lateinit var binding: ActivityCamPreviewBinding

    private lateinit var file: File

    // Controla as instâncias PROVIDER, não deixa abrir mais de uma tela de permissão
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>

    // Selecionar qual câmera iremos trabalhar
    private lateinit var cameraSelector: CameraSelector

    // Imagem capturada, já nasce como nula
    private var imageCapture: ImageCapture? = null

// Objeto do android que cria uma thread para gravar a imagem (não podemos usar a mesma thread pois se não pararia o preview)
    private lateinit var imgCaptureExecuter: ExecutorService
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCamPreviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        imgCaptureExecuter = Executors.newSingleThreadExecutor()

        startCAM()

        // @léo

        binding.butao.setOnClickListener {
            tirarFoto()
        }
    }

    private fun mudarActivityUmCliente(){
        //Colocar activity de um cliente

        finish()


    }
    private fun mudarActivityPrimeiroCliente(){
        //Colocar activity do primeiro cliente
        finish()
    }

    private fun mudarActivitySegundoCliente(){
        //Colocar activity do segundo cliente
        finish()
    }

    private fun tirarFoto(){

        val i = intent

        val userId = FirebaseAuth.getInstance().currentUser!!.uid

        var ordem = i.extras?.getString("ordem")
        var numClientes = i.extras?.getString("numClientes")

        var fileName = ""

        if(ordem=="primeiro"){
            fileName = "$userId"
        }else{
            fileName = "$userId-2"
        }

        imageCapture?.let {

            val file = File(externalMediaDirs[0], fileName)

            val outputFileOptions =  ImageCapture.OutputFileOptions.Builder(file).build()

            it.takePicture(
                outputFileOptions,
                imgCaptureExecuter,
                object : ImageCapture.OnImageSavedCallback{
                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                        Log.i("Camera", "Image Salva ${file.toURI()}")

                        if(ordem=="primeiro"&&numClientes=="um"){
                            mudarActivityUmCliente()
                        }else if(ordem=="primeiro"&&numClientes=="dois"){
                            mudarActivityPrimeiroCliente()
                        }else if(ordem=="segundo"&&numClientes=="dois"){
                            mudarActivitySegundoCliente()
                        }


                    }

                    override fun onError(exception: ImageCaptureException) {
                        Toast.makeText(binding.root.context, "Erro ao salvar", Toast.LENGTH_LONG).show()

                    }

                }

            )

        }



    }

    private fun startCAM(){
        cameraProviderFuture.addListener({

            imageCapture = ImageCapture.Builder().build()

            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also{
                it.setSurfaceProvider(binding.previewCam.surfaceProvider)
            }

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
            }catch (e: Exception){
                Log.e("CameraPreview", "falha")
            }

        }, ContextCompat.getMainExecutor(this))


    }
}