package com.app.focusflow

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class DashboardActivity : AppCompatActivity() {

    private lateinit var notepadCV: CardView
    private lateinit var todolistCV: CardView
    private lateinit var pomodorotimerCV: CardView
    private lateinit var relaxsoundsCV: CardView
    private lateinit var salirCV: CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)  // Cambia al nombre real de tu layout XML

        // Inicializamos los CardViews
        notepadCV = findViewById(R.id.notepadCV)
        todolistCV = findViewById(R.id.todolistCV)
        pomodorotimerCV = findViewById(R.id.pomodorotimerCV)
        relaxsoundsCV = findViewById(R.id.relaxsoundsCV)
        salirCV = findViewById(R.id.salirCV)

        //
        notepadCV.setOnClickListener {
            val intent = Intent(this, NotepadActivity::class.java)
            startActivity(intent)
        }

        todolistCV.setOnClickListener {
            val intent = Intent(this, TodoListActivity::class.java)
            startActivity(intent)
        }

        pomodorotimerCV.setOnClickListener {
            val intent = Intent(this, PomodoroTimerActivity::class.java)
            startActivity(intent)
        }

        relaxsoundsCV.setOnClickListener {
            val intent = Intent(this, RelaxSoundsActivity::class.java)
            startActivity(intent)
        }

        salirCV.setOnClickListener {
            cerrarSesion()  // o lo que uses para salir/cerrar sesión
        }


    }

    private fun cerrarSesion() {
        // Ejemplo: borrar SharedPreferences donde guardas sesión
        val preferences = getSharedPreferences("mi_prefs", Context.MODE_PRIVATE)
        preferences.edit().clear().apply()

        // Redirigir al login
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)

        // Finalizar la actividad actual
        finish()
    }
}
