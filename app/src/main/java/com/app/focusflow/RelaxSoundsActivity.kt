package com.app.focusflow

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast

class RelaxSoundsActivity : AppCompatActivity() {

    // Declaraciones de variables
    private var mediaPlayer: MediaPlayer? = null // Reproductor de audio
    private var currentDuration: Int = 0 // Duración actual de la meditación en milisegundos
    private var countdownTimer: CountDownTimer? = null // Temporizador de cuenta regresiva

    // Declaraciones de variables para las meditaciones de 5, 10, 15 y 20 minutos
    private lateinit var btnPlay5min: ImageButton
    private lateinit var btnStop5min: ImageButton
    private lateinit var durationText5min: TextView

    private lateinit var btnPlay10min: ImageButton
    private lateinit var btnStop10min: ImageButton
    private lateinit var durationText10min: TextView

    private lateinit var btnPlay15min: ImageButton
    private lateinit var btnStop15min: ImageButton
    private lateinit var durationText15min: TextView

    private lateinit var btnPlay20min: ImageButton
    private lateinit var btnStop20min: ImageButton
    private lateinit var durationText20min: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_relax_sounds)

        // Obtención de referencias a los elementos de la interfaz de usuario
        btnPlay5min = findViewById(R.id.btnPlay)
        btnStop5min = findViewById(R.id.btnStop)
        durationText5min = findViewById(R.id.durationText5min)

        btnPlay10min = findViewById(R.id.btnPlay1)
        btnStop10min = findViewById(R.id.btnStop1)
        durationText10min = findViewById(R.id.durationText10min)

        btnPlay15min = findViewById(R.id.btnPlay2)
        btnStop15min = findViewById(R.id.btnStop2)
        durationText15min = findViewById(R.id.durationText15min)

        btnPlay20min = findViewById(R.id.btnPlay3)
        btnStop20min = findViewById(R.id.btnStop3)
        durationText20min = findViewById(R.id.durationText20min)

        // Configuración de los listeners
        btnPlay5min.setOnClickListener {
            if (mediaPlayer == null) {
                playMeditation(
                    R.raw.minutos5,
                    btnPlay5min,
                    btnStop5min,
                    durationText5min,
                    300000
                )
            } else {
                Toast.makeText(this, "Detén la reproducción actual primero", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        btnStop5min.setOnClickListener {
            stopMeditation(btnPlay5min, btnStop5min, durationText5min)
        }

        btnPlay10min.setOnClickListener {
            if (mediaPlayer == null) {
                playMeditation(
                    R.raw.minutos10,
                    btnPlay10min,
                    btnStop10min,
                    durationText10min,
                    600000
                )
            } else {
                Toast.makeText(this, "Detén la reproducción actual primero", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        btnStop10min.setOnClickListener {
            stopMeditation(btnPlay10min, btnStop10min, durationText10min)
        }

        btnPlay15min.setOnClickListener {
            if (mediaPlayer == null) {
                playMeditation(
                    R.raw.minutos15,
                    btnPlay15min,
                    btnStop15min,
                    durationText15min,
                    900000
                )
            } else {
                Toast.makeText(this, "Detén la reproducción actual primero", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        btnStop15min.setOnClickListener {
            stopMeditation(btnPlay15min, btnStop15min, durationText15min)
        }

        btnPlay20min.setOnClickListener {
            if (mediaPlayer == null) {
                playMeditation(
                    R.raw.minutos20,
                    btnPlay20min,
                    btnStop20min,
                    durationText20min,
                    1200000
                )
            } else {
                Toast.makeText(this, "Detén la reproducción actual primero", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        btnStop20min.setOnClickListener {
            stopMeditation(btnPlay20min, btnStop20min, durationText20min)
        }

    }

    // Método para reproducir una meditación
    private fun playMeditation(
        resourceId: Int,
        playButton: ImageButton,
        stopButton: ImageButton,
        durationText: TextView,
        duration: Long
    ) {
        stopMeditation(playButton, stopButton, durationText)

        mediaPlayer = MediaPlayer.create(this, resourceId)
        mediaPlayer?.start()

        playButton.visibility = View.GONE
        stopButton.visibility = View.VISIBLE

        currentDuration = duration.toInt()
        durationText.text = formatDuration(currentDuration)

        countdownTimer = object : CountDownTimer(duration, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                currentDuration = millisUntilFinished.toInt()
                durationText.text = formatDuration(currentDuration)
            }

            override fun onFinish() {
                stopMeditation(playButton, stopButton, durationText)
            }
        }.start()
    }

    // Método para detener la reproducción de una meditación
    private fun stopMeditation(
        playButton: ImageButton,
        stopButton: ImageButton,
        durationText: TextView
    ) {
        if (mediaPlayer != null) {
            if (mediaPlayer!!.isPlaying) {
                mediaPlayer!!.stop()
            }
            mediaPlayer!!.reset()
            mediaPlayer!!.release()
            mediaPlayer = null
        }

        countdownTimer?.cancel()
        countdownTimer = null

        playButton.visibility = View.VISIBLE
        stopButton.visibility = View.GONE
        durationText.text = ""

        currentDuration = 0
    }

    // Método para formatear la duración de la meditación (milisegundos a minutos y segundos)
    private fun formatDuration(milliseconds: Int): String {
        val seconds = milliseconds / 1000
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return String.format("%02d:%02d", minutes, remainingSeconds)
    }

    // Método llamado cuando se presiona el botón de retroceso del dispositivo
    @Deprecated("Deprecated in Java")
    @Suppress("DEPRECATION")
    override fun onBackPressed() {
        // Detener todas las meditaciones en curso al presionar el botón de retroceso
        stopMeditation(btnPlay5min, btnStop5min, durationText5min)
        stopMeditation(btnPlay10min, btnStop10min, durationText10min)
        stopMeditation(btnPlay15min, btnStop15min, durationText15min)
        stopMeditation(btnPlay20min, btnStop20min, durationText20min)

        // Ir a la actividad del panel de control (Dashboard)
        val intent = Intent(this, DashboardActivity::class.java)
        startActivity(intent)
        finish()

        super.onBackPressed()
    }
}
