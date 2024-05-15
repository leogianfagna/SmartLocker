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
import com.projetointegrador.smartlocker.databinding.ActivityCamPreviewBinding
import java.io.File
import java.lang.Exception
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CamPreviewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCamPreviewBinding

    //Controlar estado do driver da camera
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    //Selecionar camera

    //Imagem capturada
    private lateinit var cameraSelector: CameraSelector

    //Salvar image
    private var imageCapture: ImageCapture? = null

    private lateinit var imgCaptureExecuter: ExecutorService
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cam_preview)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding = ActivityCamPreviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        imgCaptureExecuter = Executors.newSingleThreadExecutor()

        startCAM()

        binding.butao.setOnClickListener {
            tirarFoto()
        }
    }

    private fun tirarFoto(){



        imageCapture?.let {

            //nome do arquivo para gravar arquivo
            val fileName = "JPEG_1"
            val file = File(externalMediaDirs[0], fileName)

            val outputFileOptions =  ImageCapture.OutputFileOptions.Builder(file).build()

            it.takePicture(
                outputFileOptions,
                imgCaptureExecuter,
                object : ImageCapture.OnImageSavedCallback{
                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                        Log.i("Camera", "Image Salva ${file.toURI()}")


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