package com.app.focusflow

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {

    // Declaración de variables miembro
    private lateinit var firebaseAuth: FirebaseAuth
    private var isPasswordVisible = false

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Inicialización de FirebaseAuth
        firebaseAuth = FirebaseAuth.getInstance()

        // Obtención de referencias a los elementos de la interfaz de usuario
        val tvIniciarSesion = findViewById<TextView>(R.id.tvIniciarSesion)
        val etEmail = findViewById<EditText>(R.id.etEmail1)
        val etPassword = findViewById<EditText>(R.id.etPassword1)
        val etConfirmarPassword = findViewById<EditText>(R.id.etConfirmPassword)
        val btnRegistro = findViewById<Button>(R.id.btnRegistro)

        // Acciones para ocultar/desocultar contraseña mediante el icono del editText Contraseña
        etPassword.setOnTouchListener { _, event ->
            if (event.action == android.view.MotionEvent.ACTION_UP) {
                val drawableRight = 2
                if (event.rawX >= (etPassword.right - etPassword.compoundDrawables[drawableRight].bounds.width())) {
                    togglePasswordVisibility(etPassword)
                    return@setOnTouchListener true
                }
            }
            false
        }

        etConfirmarPassword.setOnTouchListener { _, event ->
            if (event.action == android.view.MotionEvent.ACTION_UP) {
                val drawableRight = 2
                if (event.rawX >= (etConfirmarPassword.right - etPassword.compoundDrawables[drawableRight].bounds.width())) {
                    togglePasswordVisibility(etConfirmarPassword)
                    return@setOnTouchListener true
                }
            }
            false
        }

        // Acción para el textview, en caso de clicar en él, vuelve al inicio de sesión.
        tvIniciarSesion.setOnClickListener {
            intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Acción para el botón de registro
        btnRegistro.setOnClickListener {
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()
            val confirmPassword = etConfirmarPassword.text.toString()

            // Validación de campos
            if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Rellene todos los campos", Toast.LENGTH_LONG).show()
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Correo electrónico inválido", Toast.LENGTH_LONG).show()
            } else {
                val errorMessage = StringBuilder()

                // Validación de formato de email y contraseña
                if (!email.contains('@')) {
                    errorMessage.append("El correo electrónico debe contener el símbolo '@'.\n")
                } else if (email.substring(email.indexOf('@')).isEmpty()) {
                    errorMessage.append("El correo electrónico debe tener un dominio válido.\n")
                }

                if (password != confirmPassword) {
                    errorMessage.append("Las contraseñas no coinciden.\n")
                }

                if (password.length < 9) {
                    errorMessage.append("La contraseña debe tener al menos 9 caracteres.\n")
                }
                if (password.count { it.isDigit() } < 1) {
                    errorMessage.append("La contraseña debe contener al menos 1 número.\n")
                }
                if (password.count { it.isLetter() } < 1) {
                    errorMessage.append("La contraseña debe contener al menos 1 letra.\n")
                }

                // Si hay errores, mostrar mensaje. Si no, intentar registro.
                if (errorMessage.isNotEmpty()) {
                    Toast.makeText(this, errorMessage.toString().trim(), Toast.LENGTH_LONG).show()
                } else {
                    firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val user = firebaseAuth.currentUser
                                // Enviar email de verificación
                                user?.sendEmailVerification()?.addOnCompleteListener { emailTask ->
                                    if (emailTask.isSuccessful) {
                                        Toast.makeText(
                                            this,
                                            "Se ha registrado correctamente. Por favor, verifica tu correo electrónico.",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        firebaseAuth.signOut()
                                        val intent = Intent(this, LoginActivity::class.java)
                                        startActivity(intent)
                                    } else {
                                        Toast.makeText(
                                            this,
                                            "Error al enviar el correo de verificación: ${emailTask.exception}",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }
                            } else {
                                Toast.makeText(
                                    this,
                                    "Error al registrar el usuario: ${task.exception}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                }
            }
        }
    }

    // Método para ocultar/desocultar la visibilidad de la contraseña
    private fun togglePasswordVisibility(etPassword: EditText) {
        isPasswordVisible = !isPasswordVisible

        etPassword.inputType = if (isPasswordVisible) {
            android.text.InputType.TYPE_CLASS_TEXT
        } else {
            android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
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
