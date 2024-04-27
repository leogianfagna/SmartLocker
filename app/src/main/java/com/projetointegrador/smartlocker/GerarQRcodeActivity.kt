package com.projetointegrador.smartlocker

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Point
import android.os.Bundle
import android.view.Display
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.collection.LLRBNode
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import com.projetointegrador.smartlocker.databinding.ActivityCadastroBinding
import com.projetointegrador.smartlocker.databinding.ActivityGerarQrcodeBinding
import java.util.EnumMap

class GerarQRcodeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGerarQrcodeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_gerar_qrcode)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding = ActivityGerarQrcodeBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val userId = FirebaseAuth.getInstance().currentUser!!.uid

        binding.gerarqrcodeImageView.setImageBitmap(generateQRCode(userId))


    }

    override fun onStop() {
        super.onStop()
        val i = Intent(this, InicioActivity::class.java)
        startActivity(i)
        finish()
    }

    private fun generateQRCode(data: String): Bitmap? {
        val bitMatrix: BitMatrix = try {
            val hints = EnumMap<EncodeHintType, Any>(EncodeHintType::class.java)
            hints[EncodeHintType.CHARACTER_SET] = "UTF-8"
            MultiFormatWriter().encode(
                data,
                BarcodeFormat.QR_CODE,
                273,
                300,
                hints
            )
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }

        val qrCodeWidth = bitMatrix.width
        val qrCodeHeight = bitMatrix.height
        val pixels = IntArray(qrCodeWidth * qrCodeHeight)

        for (y in 0 until qrCodeHeight) {
            val offset = y * qrCodeWidth
            for (x in 0 until qrCodeWidth) {
                pixels[offset + x] = if (bitMatrix[x, y]) {
                    resources.getColor(R.color.black, theme) // QR code color
                } else {
                    resources.getColor(R.color.transparente, theme) // Background color
                }
            }
        }

        val bitmap = Bitmap.createBitmap(qrCodeWidth, qrCodeHeight, Bitmap.Config.RGB_565)
        bitmap.setPixels(pixels, 0, qrCodeWidth, 0, 0, qrCodeWidth, qrCodeHeight)

        return bitmap
    }


}