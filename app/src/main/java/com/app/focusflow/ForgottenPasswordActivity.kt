package com.app.focusflow

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class ForgottenPasswordActivity : AppCompatActivity() {

    // Declaración de variable miembro para la instancia de FirebaseAuth
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgotten_password)

        // Inicialización de FirebaseAuth
        firebaseAuth = FirebaseAuth.getInstance()

        // Obtención de referencias a los elementos de la interfaz de usuario
        var etEmail = findViewById<EditText>(R.id.emailET)
        var btnRestablecer = findViewById<Button>(R.id.btnRestaurar)

        // Acción para el botón de restablecer contraseña
        btnRestablecer.setOnClickListener {
            var email = etEmail.text.toString()

            // Verificar si el campo de correo electrónico no está vacío
            if (email.isNotEmpty()){
                // Si no está vacío, llamar al método para restablecer la contraseña
                restablecerContrasena(email)
            }else{
                // Si está vacío, mostrar un mensaje de advertencia
                Toast.makeText(this, "Por favor, ingrese su dirección de correo electrónico",Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Método para restablecer la contraseña
    private fun restablecerContrasena(email: String) {
        firebaseAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Si el restablecimiento de contraseña es exitoso, mostrar un mensaje y volver a la pantalla de inicio de sesión
                    Toast.makeText(
                        this,
                        "Se ha enviado un correo electrónico para restablecer la contraseña",
                        Toast.LENGTH_SHORT
                    ).show()
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // Si hay algún error, mostrar un mensaje de error
                    Toast.makeText(
                        this,
                        "Error al enviar el correo electrónico de restablecimiento de contraseña",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    // Método para que al darle atrás me vuelva a la pantalla de inicio
    @Deprecated("Deprecated in Java")
    @Suppress("DEPRECATION")
    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}
