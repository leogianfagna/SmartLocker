package com.projetointegrador.smartlocker

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import com.projetointegrador.smartlocker.databinding.ActivityScanQrcodeBinding

class ScanQRcodeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScanQrcodeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanQrcodeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.scanBtn.setOnClickListener{
            onButtonClick(view)
        }

    }

    // Register the launcher and result handler
    private val barcodeLauncher = registerForActivityResult<ScanOptions, ScanIntentResult>(
        ScanContract()
    ) { result: ScanIntentResult ->
        if (result.contents == null) {
            Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG)
                .show()
        } else {
            Toast.makeText(
                this,
                "Scanned: " + result.contents,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    // Launch
    fun onButtonClick(view: View?) {
        barcodeLauncher.launch(ScanOptions())
    }
}