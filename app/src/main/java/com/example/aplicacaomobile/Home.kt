package com.example.aplicacaomobile

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.InputEvent
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.aplicacaomobile.databinding.ActivityHomeBinding
import org.w3c.dom.Text

class Home : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val numero = (100000..999999).random()

        binding.buttonAdicionar.setOnClickListener {
            this.abrirModal()
        }

        binding.buttonTwo.setOnClickListener {
            val intent = Intent(this, ToDo::class.java)
            startActivity(intent)
        }

    }

    private fun abrirModal() {

        val dialog = Dialog(this)

        dialog.setContentView(R.layout.create_event_modal)

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        dialog.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.90).toInt(),
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        val btnCancelar = dialog.findViewById<Button>(R.id.btnCancelar)
        val btnConfirmar = dialog.findViewById<Button>(R.id.btnConfirmar)

        btnConfirmar.setOnClickListener {
            val numero = (100000..999999).random()

            val nome = dialog.findViewById<EditText>(R.id.editTextText)
                .text
                .toString()
                .trim()

            if (nome.isEmpty()) {
                dialog.findViewById<EditText>(R.id.editTextText)
                    .error = "Este campo é obrigatório"

                return@setOnClickListener
            }

            dialog.dismiss()

            AlertDialog.Builder(this)
                .setTitle("Evento criado!")
                .setMessage("Seu código é: $numero\n\n"+
                        "Não publique este codigo em lugar algum")
                .setPositiveButton("OK", null)
                .show()


        }

        btnCancelar.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()

        dialog.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.90).toInt(),
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }
}