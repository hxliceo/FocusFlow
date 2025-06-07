package com.app.focusflow

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider

class LoginActivity : AppCompatActivity() {

    // Declaración de variables miembro
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var inicioSesionGoogle: GoogleSignInClient
    private var isPasswordVisible = false

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Inicialización de FirebaseAuth
        firebaseAuth = FirebaseAuth.getInstance()

        // Obtención de referencias a los elementos de la interfaz de usuario
        val tvRegistrarse = findViewById<TextView>(R.id.tvRegistrarse)
        val btnIniciarSesion = findViewById<Button>(R.id.btnIniciarSesion)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val tvResetPass = findViewById<TextView>(R.id.tvResetPassword)
        val imgISGoogle = findViewById<ImageView>(R.id.imgGoogleLogin)

        // Acción para ocultar/desocultar la contraseña al hacer clic en el icono del EditText de contraseña
        etPassword.setOnTouchListener { _, event ->
            if(event.action == android.view.MotionEvent.ACTION_UP){
                val drawableRight = 2
                if (event.rawX >= (etPassword.right - etPassword.compoundDrawables[drawableRight].bounds.width())){
                    // Si el clic está en el icono del ojo
                    togglePasswordVisibility(etPassword)
                    return@setOnTouchListener true
                }
            }
            false
        }

        // Acción para iniciar sesión mediante correo y contraseña
        btnIniciarSesion.setOnClickListener {
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()

            if(email.isNotEmpty() && password.isNotEmpty()){
                // Iniciar sesión con Firebase
                firebaseAuth.signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener{ loginTask ->
                        if (loginTask.isSuccessful){
                            val user = firebaseAuth.currentUser
                            // Comprobar si el email está verificado y si el usuario no es nulo
                            if(user != null && user.isEmailVerified){
                                // Pasar a otro Activity
                                val intent = Intent(this, DashboardActivity::class.java)
                                startActivity(intent)
                                finish() // Cerrar el activity actual
                            } else { // En caso de que no haya verificado su cuenta
                                Toast.makeText(
                                    this,
                                    "Verifica tu cuenta antes de iniciar sesión",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        } else{ // En caso de que la tarea de inicio sesión haya fallado
                            Toast.makeText(this, loginTask.exception.toString(), Toast.LENGTH_LONG).show()
                        }
                    }
            }else{ // En caso de que no se hayan rellenado los campos
                Toast.makeText(this, "Rellene todos los campos", Toast.LENGTH_LONG).show()
            }
        }

        // Acción para pasar a otro activity para el registro
        tvRegistrarse.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Acción para pasar a otro activity para la recuperación de la contraseña
        tvResetPass.setOnClickListener {
            val intent = Intent(this, ForgottenPasswordActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Configuración de las opciones de inicio de sesión con Google
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            // Solicitar el ID del token de Google para autenticarse
            .requestIdToken(getString(R.string.default_web_client_id))
            // Solicitar acceso al correo
            .requestEmail()
            // Construir las opciones para el inicio de sesión
            .build()

        // Crear un cliente de inicio de sesión con Google utilizando las opciones configuradas
        inicioSesionGoogle = GoogleSignIn.getClient(this,gso)

        // Acción para el inicio de sesión mediante Google
        imgISGoogle.setOnClickListener {
            iniciarSesionGoogle()
        }
    }

    // Método para ocultar/desocultar la visibilidad de la contraseña
    private fun togglePasswordVisibility(etPassword: EditText){
        isPasswordVisible = !isPasswordVisible

        etPassword.inputType = if(isPasswordVisible){
            android.text.InputType.TYPE_CLASS_TEXT
        } else {
            android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
    }

    // Métodos para el inicio de sesión con Google
    companion object {
        private const val RC_SIGN_IN = 9001
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    intent = Intent(this, DashboardActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Error al iniciar sesión con Google", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun iniciarSesionGoogle() {
        val signInIntent = inicioSesionGoogle.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            try {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                Toast.makeText(this, "Error al iniciar sesión con Google", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
