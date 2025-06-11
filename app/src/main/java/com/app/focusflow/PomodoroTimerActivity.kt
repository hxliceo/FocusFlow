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
    private var isPaused = false
    private var isWorkMode = true

    private var workDurationMinutes = 25
    private var breakDurationMinutes = 5

    private var timer: CountDownTimer? = null
    private var remainingTimeMillis: Long = 0
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
            when {
                !isRunning -> {
                    remainingTimeMillis = if (isWorkMode) workDurationMinutes * 60 * 1000L else breakDurationMinutes * 60 * 1000L
                    startTimer(remainingTimeMillis)
                }
                isRunning && !isPaused -> {
                    pauseTimer()
                }
                isPaused -> {
                    startTimer(remainingTimeMillis)
                }
            }
        }

        settingsButton.setOnClickListener {
            showSettingsDialog()
        }
        updateTimerDisplay()
    }

    private fun startTimer(durationMillis: Long) {
        val mode = if (isWorkMode) "Trabajo" else "Descanso"
        modeText.text = "Modo: $mode"
        isRunning = true
        isPaused = false

        timer = object : CountDownTimer(durationMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                remainingTimeMillis = millisUntilFinished
                val minutes = (millisUntilFinished / 1000) / 60
                val seconds = (millisUntilFinished / 1000) % 60
                timerText.text = String.format("%02d:%02d", minutes, seconds)
            }

            override fun onFinish() {
                playSound()
                Toast.makeText(this@PomodoroTimerActivity, "¡$mode terminado!", Toast.LENGTH_SHORT).show()
                isRunning = false
                isPaused = false
                remainingTimeMillis = 0

                if (isWorkMode) {
                    val builder = AlertDialog.Builder(this@PomodoroTimerActivity)
                    builder.setTitle("Descanso")
                    builder.setMessage("¿Quieres comenzar el descanso ahora?")

                    builder.setPositiveButton("Sí") { _, _ ->
                        // Detener sonido antes de iniciar descanso
                        mediaPlayer?.stop()
                        mediaPlayer?.release()
                        mediaPlayer = null

                        isWorkMode = false
                        remainingTimeMillis = breakDurationMinutes * 60 * 1000L
                        startTimer(remainingTimeMillis)
                    }

                    builder.setNegativeButton("No") { dialog, _ ->
                        // Detener sonido antes de cerrar diálogo
                        mediaPlayer?.stop()
                        mediaPlayer?.release()
                        mediaPlayer = null

                        dialog.dismiss()
                        updateTimerDisplay()
                    }

                    builder.setCancelable(false)
                    builder.show()
                } else {
                    // Terminó el descanso, vuelve a modo trabajo automáticamente
                    isWorkMode = true
                    remainingTimeMillis = workDurationMinutes * 60 * 1000L
                    startTimer(remainingTimeMillis)
                }
            }
        }.start()
    }

    private fun pauseTimer() {
        timer?.cancel()
        isPaused = true
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
                updateTimerDisplay()  // actualizar UI y reiniciar timer
                Toast.makeText(this, "Configuración guardada", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Valores inválidos", Toast.LENGTH_SHORT).show()
            }
        }

        builder.setNegativeButton("Cancelar", null)
        builder.show()
    }

    //dadaskdjmak
    private fun updateTimerDisplay() {
        // Siempre muestra el modo correcto
        val mode = if (isWorkMode) "Trabajo" else "Descanso"
        modeText.text = "Modo: $mode"

        // Muestra el tiempo inicial, minutos:00
        val minutes = if (isWorkMode) workDurationMinutes else breakDurationMinutes
        timerText.text = String.format("%02d:00", minutes)

        // Detiene el timer si está corriendo y actualiza estados
        timer?.cancel()
        isRunning = false
        isPaused = false
        remainingTimeMillis = 0
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
