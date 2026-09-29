package com.example.aplicacaomobile

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.NumberPicker
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isEmpty
import com.example.aplicacaomobile.databinding.ActivityHomeBinding
import android.graphics.Color
import java.util.Calendar
import com.applandeo.materialcalendarview.CalendarDay
import com.applandeo.materialcalendarview.EventDay
import kotlin.collections.mutableListOf
import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import androidx.core.app.ActivityCompat
import android.widget.Toast
import android.location.LocationListener
import android.location.Location
import com.applandeo.materialcalendarview.CalendarView
import java.util.Locale
import com.applandeo.materialcalendarview.listeners.OnDayClickListener
import com.example.aplicacaomobile.databinding.ActivityEventScreenBinding

class Home : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val evento = mutableListOf<EventDay>()
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val numero = (100000..999999).random()

        binding.buttonAdicionar.setOnClickListener {
            this.abrirModal("Add", binding, evento)
        }

        binding.buttonParticipar.setOnClickListener {
            this.abrirModal("Enter", binding, evento)
        }

        binding.buttonTwo.setOnClickListener {
            val intent = Intent(this, ToDo::class.java)
            startActivity(intent)
        }

    }

    private fun abrirModal(modal: String, binding: ActivityHomeBinding, evento: MutableList<EventDay>) {

        val dialog = Dialog(this)
        if(modal == "Add"){
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

                val nome = dialog.findViewById<EditText>(R.id.nameEventInput)
                    .text
                    .toString()
                    .trim()

                val desc = dialog.findViewById<EditText>(R.id.edtDescricao)
                    .text
                    .toString()
                    .trim()

                if (nome.isEmpty()) {
                    dialog.findViewById<EditText>(R.id.nameEventInput)
                        .error = "Este campo é obrigatório"

                    return@setOnClickListener
                }
                if (desc.isEmpty()) {
                    dialog.findViewById<EditText>(R.id.edtDescricao)
                        .error = "Este campo é obrigatório"

                    return@setOnClickListener
                }

                val cardDeEvento = layoutInflater.inflate(
                    R.layout.card_de_evento,
                    binding.containerEventos,
                    false
                )
                cardDeEvento.setOnClickListener {
                    val intent = Intent(this, event_screen::class.java)
                    startActivity(intent)
                }
                cardDeEvento.findViewById<TextView>(R.id.txtNomeEvento).text = nome
                cardDeEvento.findViewById<TextView>(R.id.txtDescricaoEvento).text = desc
                cardDeEvento.findViewById<TextView>(R.id.txtParticipantesEvento).text = "1"
                val calendar = dialog.findViewById<CalendarView>(R.id.calendar)
                var dataSelecionada: Calendar? = null

                calendar.setOnDayClickListener(
                    object : OnDayClickListener {

                        override fun onDayClick(eventDay: EventDay) {
                            dataSelecionada = eventDay.calendar
                        }
                    }
                )

                btnConfirmar.setOnClickListener {
                    if (dataSelecionada != null) {

                        adicionarEvento(
                            dataSelecionada!!.get(Calendar.DAY_OF_MONTH),
                            evento
                        )


                        dialog.dismiss()

                    } else {
                        Toast.makeText(
                            this,
                            "Selecione uma data",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                binding.containerEventos.addView(cardDeEvento)

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
        } else if(modal == "Enter") {
            dialog.setContentView(R.layout.enter_in_event_modal)

            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

            dialog.window?.setLayout(
                (resources.displayMetrics.widthPixels * 0.90).toInt(),
                WindowManager.LayoutParams.WRAP_CONTENT
            )

            val btnCancelar = dialog.findViewById<Button>(R.id.btnCodeEventCancel)
            val btnConfirmar = dialog.findViewById<Button>(R.id.btnCodeEventConfirm)

            btnConfirmar.setOnClickListener {
                val code = dialog.findViewById<EditText>(R.id.codeEventInput)

                if(code.text.length != 6 || code.toString().trim().isEmpty()) {
                    dialog.findViewById<EditText>(R.id.codeEventInput)
                        .error = "Este campo é obrigatório e deve conter um codigo de 6 digitos"

                    return@setOnClickListener
                }
                if(code.text.toString() == "987777") {
                    val cardDeEvento = layoutInflater.inflate(
                        R.layout.card_de_evento,
                        binding.containerEventos,
                        false
                    )
                    cardDeEvento.findViewById<TextView>(R.id.txtNomeEvento).text = "Evento 1"
                    cardDeEvento.findViewById<TextView>(R.id.txtDescricaoEvento).text = "Palestra escolar"

                    adicionarEvento(5, evento)
                    cardDeEvento.setOnClickListener {
                        val intent = Intent(this, event_screen::class.java)
                        startActivity(intent)
                    }
                    binding.calendar.setEvents(evento)

                    binding.containerEventos.addView(cardDeEvento)

                }
                if(code.text.toString() == "977777") {
                    val cardDeEvento = layoutInflater.inflate(
                        R.layout.card_de_evento,
                        binding.containerEventos,
                        false
                    )
                    cardDeEvento.findViewById<TextView>(R.id.txtNomeEvento).text = "Evento 2"
                    cardDeEvento.findViewById<TextView>(R.id.txtDescricaoEvento).text = "Evento da comevap"

                    adicionarEvento(15, evento)
                    cardDeEvento.setOnClickListener {
                        val intent = Intent(this, event_screen::class.java)
                        startActivity(intent)
                    }
                    binding.calendar.setEvents(evento)

                    binding.containerEventos.addView(cardDeEvento)

                }
                if(code.text.toString() == "967777") {
                    val cardDeEvento = layoutInflater.inflate(
                        R.layout.card_de_evento,
                        binding.containerEventos,
                        false
                    )
                    cardDeEvento.findViewById<TextView>(R.id.txtNomeEvento).text = "Evento 3"
                    cardDeEvento.findViewById<TextView>(R.id.txtDescricaoEvento).text = "Reunião de alinhamento"

                    adicionarEvento(25, evento)
                    cardDeEvento.setOnClickListener {
                        val intent = Intent(this, event_screen::class.java)
                        startActivity(intent)
                    }
                    binding.calendar.setEvents(evento)

                    binding.containerEventos.addView(cardDeEvento)

                }
                if(code.text.toString() == "957777") {
                    val cardDeEvento = layoutInflater.inflate(
                        R.layout.card_de_evento,
                        binding.containerEventos,
                        false
                    )
                    cardDeEvento.findViewById<TextView>(R.id.txtNomeEvento).text = "Evento 4"
                    cardDeEvento.findViewById<TextView>(R.id.txtDescricaoEvento).text = "Futebolzin ca rapaziada"

                    adicionarEvento(30, evento)
                    cardDeEvento.setOnClickListener {
                        val intent = Intent(this, event_screen::class.java)
                        startActivity(intent)
                    }
                    binding.calendar.setEvents(evento)

                    binding.containerEventos.addView(cardDeEvento)

                }
                dialog.dismiss()
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

    fun adicionarEvento(dia: Int, evento: MutableList<EventDay>) {
        val calendar = Calendar.getInstance()
        calendar.set(2026, Calendar.SEPTEMBER, dia)

        evento.add(
            EventDay(
                calendar,
                R.drawable.ic_evento
            )
        )
    }


}