package com.example.aplicacaomobile

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

data class Evento(
    val id: String = UUID.randomUUID().toString(),
    val nome: String,
    val localizacao: String,
    val descricao: String,
    val dataMillis: Long? = null
)

class EventoStorage(context: Context) {
    private val prefs = context.getSharedPreferences("eventos_storage", Context.MODE_PRIVATE)
    private val chave = "eventos"

    fun listar(): MutableList<Evento> {
        val eventos = mutableListOf<Evento>()
        val json = prefs.getString(chave, "[]") ?: "[]"

        return try {
            val array = JSONArray(json)
            for (i in 0 until array.length()) {
                val item = array.getJSONObject(i)
                eventos.add(
                    Evento(
                        id = item.optString("id"),
                        nome = item.optString("nome"),
                        localizacao = item.optString("localizacao"),
                        descricao = item.optString("descricao"),
                        dataMillis = if (item.has("dataMillis") && !item.isNull("dataMillis")) {
                            item.optLong("dataMillis")
                        } else {
                            null
                        }
                    )
                )
            }
            eventos
        } catch (_: Exception) {
            mutableListOf()
        }
    }

    fun salvar(evento: Evento) {
        val eventos = listar()
        eventos.add(evento)
        salvarTodos(eventos)
    }

    fun remover(id: String): Boolean {
        val eventos = listar()
        val removido = eventos.removeAll { it.id == id }

        if (removido) {
            salvarTodos(eventos)
        }

        return removido
    }

    private fun salvarTodos(eventos: List<Evento>) {
        val array = JSONArray()

        eventos.forEach { evento ->
            array.put(
                JSONObject().apply {
                    put("id", evento.id)
                    put("nome", evento.nome)
                    put("localizacao", evento.localizacao)
                    put("descricao", evento.descricao)
                    if (evento.dataMillis != null) {
                        put("dataMillis", evento.dataMillis)
                    } else {
                        put("dataMillis", JSONObject.NULL)
                    }
                }
            )
        }

        prefs.edit().putString(chave, array.toString()).apply()
    }
}
