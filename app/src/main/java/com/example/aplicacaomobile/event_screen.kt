package com.example.aplicacaomobile

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.aplicacaomobile.databinding.ActivityEventScreenBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class event_screen : AppCompatActivity() {

    companion object {
        const val EXTRA_EVENTO_ID = "evento_id"
        const val EXTRA_EVENTO_NOME = "evento_nome"
        const val EXTRA_EVENTO_LOCAL = "evento_local"
        const val EXTRA_EVENTO_DESCRICAO = "evento_descricao"
        const val EXTRA_EVENTO_DATA = "evento_data"
    }

    private lateinit var binding: ActivityEventScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityEventScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.txtEventoNome.text =
            intent.getStringExtra(EXTRA_EVENTO_NOME) ?: "Evento"

        binding.txtEventoLocal.text =
            intent.getStringExtra(EXTRA_EVENTO_LOCAL) ?: "Local não informado"

        binding.txtEventoDescricao.text =
            intent.getStringExtra(EXTRA_EVENTO_DESCRICAO) ?: "Sem descrição"

        val dataMillis = intent.getLongExtra(EXTRA_EVENTO_DATA, -1L)
        binding.txtEventoData.text =
            if (dataMillis > 0) {
                SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
                    .format(Date(dataMillis))
            } else {
                "Data não informada"
            }

        binding.buttonOne.setOnClickListener {
            startActivity(Intent(this, Home::class.java))
            finish()
        }

        binding.buttonExcluir.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Excluir evento?")
                .setMessage("O evento \"${binding.txtEventoNome.text}\" será removido do armazenamento local. Essa ação não pode ser desfeita.")
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Excluir") { _, _ ->
                    val eventoId = intent.getStringExtra(EXTRA_EVENTO_ID)
                    if (!eventoId.isNullOrBlank()) {
                        val storage = EventoStorage(this)
                        storage.remover(eventoId)
                    }

                    startActivity(Intent(this, Home::class.java))
                    finish()
                }
                .show()
        }

        binding.buttonTwo.setOnClickListener {
            startActivity(Intent(this, ToDo::class.java))
        }
    }
}
