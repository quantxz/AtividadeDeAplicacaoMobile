package com.example.aplicacaomobile

import android.content.Intent
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.aplicacaomobile.databinding.ActivityEventScreenBinding
import com.example.aplicacaomobile.databinding.ActivityHomeBinding
import android.view.View
class event_screen : AppCompatActivity() {
    private lateinit var binding: ActivityEventScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityEventScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.buttonOne.setOnClickListener {
            val intent = Intent(this, Home::class.java)
            startActivity(intent)
        }
        binding.buttonTwo.setOnClickListener {
            val intent = Intent(this, ToDo::class.java)
            startActivity(intent)
        }
        defineItem(binding)
    }

    private fun defineItem(binding: ActivityEventScreenBinding) {
        val numero = (100000..999999).random()
        binding.CodigoAleatorio.text = numero.toString()
    }
}