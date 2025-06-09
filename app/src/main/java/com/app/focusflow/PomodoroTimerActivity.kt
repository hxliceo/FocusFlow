package com.app.focusflow

import android.app.AlertDialog
import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class PomodoroTimerActivity : AppCompatActivity() {

    private lateinit var tomatoImage: ImageView
    private lateinit var timerText: TextView
    private lateinit var modeText: TextView
    private lateinit var settingsButton: Button

    private var isRunning = false
    private var isWorkMode = true

    private var workDurationMinutes = 25
    private var breakDurationMinutes = 5

    private var timer: CountDownTimer? = null
    private var mediaPlayer: MediaPlayer? = null
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pomodoro_timer)

        tomatoImage = findViewById(R.id.tomatoImage)
        timerText = findViewById(R.id.timerText)
        modeText = findViewById(R.id.modeText)
        settingsButton = findViewById(R.id.settingsButton)

        loadPreferences()

        tomatoImage.setOnClickListener {
            if (!isRunning) {
                startTimer()
            }
        }

        settingsButton.setOnClickListener {
            showSettingsDialog()
        }
    }

    private fun startTimer() {
        val durationMinutes = if (isWorkMode) workDurationMinutes else breakDurationMinutes
        val durationMillis = durationMinutes * 60 * 1000L
        val mode = if (isWorkMode) "Trabajo" else "Descanso"

        modeText.text = "Modo: $mode"
        isRunning = true

        timer = object : CountDownTimer(durationMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val minutes = (millisUntilFinished / 1000) / 60
                val seconds = (millisUntilFinished / 1000) % 60
                timerText.text = String.format("%02d:%02d", minutes, seconds)
            }

            override fun onFinish() {
                playSound()
                Toast.makeText(this@PomodoroTimerActivity, "¡$mode terminado!", Toast.LENGTH_SHORT).show()
                isWorkMode = !isWorkMode
                isRunning = false
                startTimer()
            }
        }.start()
    }

    private fun playSound() {
        mediaPlayer = MediaPlayer.create(this, R.raw.pomodoro)
        mediaPlayer?.start()

        handler.postDelayed({
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
        }, 10_000)
    }

    private fun showSettingsDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Configurar Duraciones")

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(50, 40, 50, 10)

        val workInput = EditText(this)
        workInput.hint = "Minutos de trabajo"
        workInput.inputType = android.text.InputType.TYPE_CLASS_NUMBER
        workInput.setText(workDurationMinutes.toString())

        val breakInput = EditText(this)
        breakInput.hint = "Minutos de descanso"
        breakInput.inputType = android.text.InputType.TYPE_CLASS_NUMBER
        breakInput.setText(breakDurationMinutes.toString())

        layout.addView(workInput)
        layout.addView(breakInput)

        builder.setView(layout)

        builder.setPositiveButton("Guardar") { _, _ ->
            val work = workInput.text.toString().toIntOrNull()
            val rest = breakInput.text.toString().toIntOrNull()
            if (work != null && rest != null && work > 0 && rest > 0) {
                workDurationMinutes = work
                breakDurationMinutes = rest
                savePreferences()
                Toast.makeText(this, "Configuración guardada", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Valores inválidos", Toast.LENGTH_SHORT).show()
            }
        }

        builder.setNegativeButton("Cancelar", null)
        builder.show()
    }

    private fun savePreferences() {
        val prefs = getSharedPreferences("PomodoroPrefs", Context.MODE_PRIVATE)
        prefs.edit()
            .putInt("workDuration", workDurationMinutes)
            .putInt("breakDuration", breakDurationMinutes)
            .apply()
    }

    private fun loadPreferences() {
        val prefs = getSharedPreferences("PomodoroPrefs", Context.MODE_PRIVATE)
        workDurationMinutes = prefs.getInt("workDuration", 25)
        breakDurationMinutes = prefs.getInt("breakDuration", 5)
    }

    override fun onDestroy() {
        timer?.cancel()
        mediaPlayer?.release()
        super.onDestroy()
    }
}
