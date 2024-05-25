package com.projetointegrador.smartlocker

import android.content.Intent
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.projetointegrador.smartlocker.databinding.ActivityDesvincularNfcBinding
import com.projetointegrador.smartlocker.databinding.ActivityLerNfcBinding
import com.projetointegrador.smartlocker.databinding.ActivityScanQrcodeBinding
import com.projetointegrador.smartlocker.databinding.ActivityVincularNfcBinding

class LerNfcActivity : AppCompatActivity() {

    private lateinit var nfcAdapter: NfcAdapter
    private lateinit var binding: ActivityLerNfcBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLerNfcBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.btnVoltar.setOnClickListener {
            finish()
        }

        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (NfcAdapter.ACTION_NDEF_DISCOVERED == intent.action) {
            val i = Intent(this, ConfirmarUmClienteActivity::class.java)
            startActivity(i)
            intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES, Tag::class.java)?.also { rawMessages ->
                val messages: List<NdefMessage> = rawMessages.map { it as NdefMessage }

            }
        }
    }
}