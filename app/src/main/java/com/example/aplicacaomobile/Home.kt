package com.example.aplicacaomobile

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.applandeo.materialcalendarview.CalendarView
import com.applandeo.materialcalendarview.EventDay
import com.applandeo.materialcalendarview.listeners.OnDayClickListener
import com.example.aplicacaomobile.databinding.ActivityHomeBinding
import java.util.Calendar

class Home : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var storage: EventoStorage
    private val eventosCalendario = mutableListOf<EventDay>()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        storage = EventoStorage(this)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.buttonAdicionar.setOnClickListener {
            abrirModalCriarEvento()
        }

        binding.buttonParticipar.setOnClickListener {
            abrirModalParticipar()
        }

        binding.buttonTwo.setOnClickListener {
            startActivity(Intent(this, ToDo::class.java))
        }

        carregarEventosSalvos()
    }

    override fun onResume() {
        super.onResume()
        if (::storage.isInitialized) {
            carregarEventosSalvos()
        }
    }

    private fun carregarEventosSalvos() {
        binding.containerEventos.removeAllViews()
        eventosCalendario.clear()

        storage.listar().forEach { evento ->
            adicionarCardEvento(evento)

            evento.dataMillis?.let { millis ->
                val calendar = Calendar.getInstance().apply {
                    timeInMillis = millis
                }
                eventosCalendario.add(
                    EventDay(calendar, R.drawable.ic_evento)
                )
            }
        }

        binding.calendar.setEvents(eventosCalendario)
    }

    private fun adicionarCardEvento(evento: Evento) {
        val card = layoutInflater.inflate(
            R.layout.card_de_evento,
            binding.containerEventos,
            false
        )

        card.findViewById<TextView>(R.id.txtNomeEvento).text = evento.nome
        card.findViewById<TextView>(R.id.txtDescricaoEvento).text = evento.descricao
        card.findViewById<TextView>(R.id.txtParticipantesEvento).text =
            if (evento.localizacao.isBlank()) "Local não informado"
            else "📍 ${evento.localizacao}"

        card.setOnClickListener {
            abrirDetalhesEvento(evento)
        }

        binding.containerEventos.addView(card)
    }

    private fun abrirDetalhesEvento(evento: Evento) {
        val intent = Intent(this, event_screen::class.java).apply {
            putExtra(event_screen.EXTRA_EVENTO_ID, evento.id)
            putExtra(event_screen.EXTRA_EVENTO_NOME, evento.nome)
            putExtra(event_screen.EXTRA_EVENTO_LOCAL, evento.localizacao)
            putExtra(event_screen.EXTRA_EVENTO_DESCRICAO, evento.descricao)
            putExtra(event_screen.EXTRA_EVENTO_DATA, evento.dataMillis ?: -1L)
        }
        startActivity(intent)
    }

    private fun abrirModalCriarEvento() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.create_event_modal)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val nomeInput = dialog.findViewById<EditText>(R.id.nameEventInput)
        val localInput = dialog.findViewById<EditText>(R.id.edtLocalizacao)
        val descricaoInput = dialog.findViewById<EditText>(R.id.edtDescricao)
        val calendar = dialog.findViewById<CalendarView>(R.id.calendar)
        val btnCancelar = dialog.findViewById<Button>(R.id.btnCancelar)
        val btnConfirmar = dialog.findViewById<Button>(R.id.btnConfirmar)

        var dataSelecionada: Calendar? = null

        calendar.setOnDayClickListener(object : OnDayClickListener {
            override fun onDayClick(eventDay: EventDay) {
                dataSelecionada = eventDay.calendar
            }
        })

        btnCancelar.setOnClickListener {
            dialog.dismiss()
        }

        btnConfirmar.setOnClickListener {
            val nome = nomeInput.text.toString().trim()
            val localizacao = localInput.text.toString().trim()
            val descricao = descricaoInput.text.toString().trim()

            if (nome.isEmpty()) {
                nomeInput.error = "Este campo é obrigatório"
                return@setOnClickListener
            }

            if (localizacao.isEmpty()) {
                localInput.error = "Este campo é obrigatório"
                return@setOnClickListener
            }

            if (descricao.isEmpty()) {
                descricaoInput.error = "Este campo é obrigatório"
                return@setOnClickListener
            }

            if (dataSelecionada == null) {
                Toast.makeText(
                    this,
                    "Selecione uma data",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val evento = Evento(
                nome = nome,
                localizacao = localizacao,
                descricao = descricao,
                dataMillis = dataSelecionada!!.timeInMillis
            )

            storage.salvar(evento)
            dialog.dismiss()

            carregarEventosSalvos()

            AlertDialog.Builder(this)
                .setTitle("Evento criado!")
                .setMessage("O evento foi salvo no armazenamento local.")
                .setPositiveButton("Ver evento") { _, _ ->
                    abrirDetalhesEvento(evento)
                }
                .setNegativeButton("Fechar", null)
                .show()
        }

        dialog.show()

        dialog.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.90).toInt(),
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    private fun abrirModalParticipar() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.enter_in_event_modal)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val btnCancelar = dialog.findViewById<Button>(R.id.btnCodeEventCancel)
        val btnConfirmar = dialog.findViewById<Button>(R.id.btnCodeEventConfirm)

        btnConfirmar.setOnClickListener {
            val codeInput = dialog.findViewById<EditText>(R.id.codeEventInput)
            val code = codeInput.text.toString().trim()

            if (code.length != 6) {
                codeInput.error = "Digite um código de 6 dígitos"
                return@setOnClickListener
            }

            val eventosExemplo = mapOf(
                "987777" to Evento(
                    nome = "Festa de formatura do Ronaldo",
                    localizacao = "Palacio sunset",
                    descricao = "levar troca de roupa"
                ),
                "977777" to Evento(
                    nome = "Festa em noronha",
                    localizacao = "Fernando de Noronha",
                    descricao = "esquecer alianca"
                ),
                "967777" to Evento(
                    nome = "Madrugadao",
                    localizacao = "Casa do Pedro",
                    descricao = "Separar notebook e levar os refrigerantes"
                ),
                "957777" to Evento(
                    nome = "evento da lorem",
                    localizacao = "profundezas da internet",
                    descricao = "Lorem ipsum dolor sit amet consectetur adipiscing elit. Quisque faucibus ex sapien vitae pellentesque sem placerat. In id cursus mi pretium tellus duis convallis. Tempus leo eu aenean sed diam urna tempor. Pulvinar vivamus fringilla lacus nec metus bibendum egestas. Iaculis massa nisl malesuada lacinia integer nunc posuere. Ut hendrerit semper vel class aptent taciti sociosqu. Ad litora torquent per conubia nostra inceptos himenaeos.\n" +
                            "\n" +
                            "Lorem ipsum dolor sit amet consectetur adipiscing elit. Quisque faucibus ex sapien vitae pellentesque sem placerat. In id cursus mi pretium tellus duis convallis. Tempus leo eu aenean sed diam urna tempor. Pulvinar vivamus fringilla lacus nec metus bibendum egestas. Iaculis massa nisl malesuada lacinia integer nunc posuere. Ut hendrerit semper vel class aptent taciti sociosqu. Ad litora torquent per conubia nostra inceptos himenaeos.\n" +
                            "\n" +
                            "Lorem ipsum dolor sit amet consectetur adipiscing elit. Quisque faucibus ex sapien vitae pellentesque sem placerat. In id cursus mi pretium tellus duis convallis. Tempus leo eu aenean sed diam urna tempor. Pulvinar vivamus fringilla lacus nec metus bibendum egestas. Iaculis massa nisl malesuada lacinia integer nunc posuere. Ut hendrerit semper vel class aptent taciti sociosqu. Ad litora torquent per conubia nostra inceptos himenaeos.\n" +
                            "\n" +
                            "Lorem ipsum dolor sit amet consectetur adipiscing elit. Quisque faucibus ex sapien vitae pellentesque sem placerat. In id cursus mi pretium tellus duis convallis. Tempus leo eu aenean sed diam urna tempor. Pulvinar vivamus fringilla lacus nec metus bibendum egestas. Iaculis massa nisl malesuada lacinia integer nunc posuere. Ut hendrerit semper vel class aptent taciti sociosqu. Ad litora torquent per conubia nostra inceptos himenaeos.\n" +
                            "\n" +
                            "Lorem ipsum dolor sit amet consectetur adipiscing elit. Quisque faucibus ex sapien vitae pellentesque sem placerat. In id cursus mi pretium tellus duis convallis. Tempus leo eu aenean sed diam urna tempor. Pulvinar vivamus fringilla lacus nec metus bibendum egestas. Iaculis massa nisl malesuada lacinia integer nunc posuere. Ut hendrerit semper vel class aptent taciti sociosqu. Ad litora torquent per conubia nostra inceptos himenaeos."
                )
            )

            val evento = eventosExemplo[code]

            if (evento != null) {
                storage.salvar(evento)
                dialog.dismiss()
                carregarEventosSalvos()
                abrirDetalhesEvento(evento)
            } else {
                codeInput.error = "Código de evento inválido"
            }
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
