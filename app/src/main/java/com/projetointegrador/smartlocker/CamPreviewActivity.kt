package com.projetointegrador.smartlocker

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

    private lateinit var imgCaptureExecuter: ExecutorService
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCamPreviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        imgCaptureExecuter = Executors.newSingleThreadExecutor()

        startCAM()

        val int = intent

        val numCliente = int.extras?.getInt("numeroCliente")!!

        binding.butao.setOnClickListener {
            tirarFoto(numCliente)
        }
    }

    private fun tirarFoto(numCliente: Int){



        imageCapture?.let {

            val userId = FirebaseAuth.getInstance().currentUser!!.uid

            //nome do arquivo para gravar arquivo

            if (numCliente==1){
                val fileName = "$userId"
                file = File(externalMediaDirs[0], fileName)
            }else if (numCliente==2){
                val fileName = "${userId}-2"
                file = File(externalMediaDirs[0], fileName)
            }




            val outputFileOptions =  ImageCapture.OutputFileOptions.Builder(file).build()

            it.takePicture(
                outputFileOptions,
                imgCaptureExecuter,
                object : ImageCapture.OnImageSavedCallback{
                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                        //COLOCAR CODIGO PARA MUDAR DE ACTIVITY
                        Log.i("Camera", "Imagem ${file.toURI()} salva.")

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