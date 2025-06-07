package com.app.focusflow

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class CreateNotesActivity : AppCompatActivity() {

    // Declaración de variables de vistas y Firebase
    private lateinit var mcreatetitleofnote: EditText
    private lateinit var mcreatecontentofnote: EditText
    private lateinit var msavenote: FloatingActionButton
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseFirestore: FirebaseFirestore
    private var selectedColor: Int = R.color.white

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_notes)

        // Inicialización de botones de selección de color
        var btnGray = findViewById<Button>(R.id.btnGray)
        var btnGreen = findViewById<Button>(R.id.btnGreen)
        var btnLightGreen = findViewById<Button>(R.id.btnLightGreen)
        var btnSkyeblue = findViewById<Button>(R.id.btnSkyeblue)
        var btnPink = findViewById<Button>(R.id.btnPink)
        var btnColor1 = findViewById<Button>(R.id.btnColor1)
        var btnColor2 = findViewById<Button>(R.id.btnColor2)
        var btnColor3 = findViewById<Button>(R.id.btnColor3)
        var btnColor4 = findViewById<Button>(R.id.btnColor4)
        var btnColor5 = findViewById<Button>(R.id.btnColor5)

        // Definición de acciones para los botones de selección de color
        btnGray.setOnClickListener {
            selectedColor = R.color.gray
        }

        btnGreen.setOnClickListener {
            selectedColor = R.color.green
        }

        btnLightGreen.setOnClickListener {
            selectedColor = R.color.lightgreen
        }

        btnSkyeblue.setOnClickListener {
            selectedColor = R.color.skyeblue
        }

        btnPink.setOnClickListener {
            selectedColor = R.color.pink
        }

        btnColor1.setOnClickListener {
            selectedColor = R.color.color1
        }

        btnColor2.setOnClickListener {
            selectedColor = R.color.color2
        }

        btnColor3.setOnClickListener {
            selectedColor = R.color.color3
        }

        btnColor4.setOnClickListener {
            selectedColor = R.color.color4
        }

        btnColor5.setOnClickListener {
            selectedColor = R.color.color5
        }

        // Obtención de referencias a los elementos de la interfaz de usuario
        msavenote=findViewById(R.id.savenote)
        mcreatetitleofnote=findViewById(R.id.createtitleofnote)
        mcreatecontentofnote=findViewById(R.id.createcontentofnote)

        // Configuración de la barra de herramientas
        val toolbar: Toolbar = findViewById(R.id.toolbarofcreatenote) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Inicialización de Firebase Auth y Firestore
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseFirestore = FirebaseFirestore.getInstance()
        val firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser

        // Acción al hacer clic en el botón para guardar la nota
        msavenote.setOnClickListener {
            val titulo = mcreatetitleofnote.text.toString()
            val contenido = mcreatecontentofnote.text.toString()

            // Verificar que los campos no estén vacíos
            if (titulo.isEmpty() || contenido.isEmpty()) {
                Toast.makeText(applicationContext, "Ambos campos son obligatorios", Toast.LENGTH_SHORT).show()
            } else {
                val userId = firebaseUser?.uid

                // Verificar que el usuario esté autenticado
                if (userId != null) {
                    val documentReference = firebaseFirestore.collection("notas")
                        .document(userId)
                        .collection("Mis_notas")
                        .document()

                    // Crear mapa de datos para la nota
                    val note = hashMapOf(
                        "Titulo" to titulo,
                        "Contenido" to contenido,
                        "Color" to selectedColor
                    )

                    // Guardar la nota en Firestore
                    documentReference.set(note)
                        .addOnSuccessListener {
                            Toast.makeText(applicationContext, "Nota creada", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, NotepadActivity::class.java)
                            intent.putExtra("selectedColor", selectedColor)
                            startActivity(intent)
                            finish()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(applicationContext, "Error al crear la nota", Toast.LENGTH_SHORT).show()
                        }

                    //documentReference.set(note + hashMapOf("Color" to selectedColor))
                } else {
                    // Mostrar mensaje si el usuario no está autenticado
                    Toast.makeText(applicationContext, "FirebaseUser es nulo", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Acción al presionar el botón de retroceso en la barra de herramientas
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    // Acción al presionar el botón de retroceso del sistema
    @Deprecated("Deprecated in Java")
    @Suppress("DEPRECATION")
    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, NotepadActivity::class.java)
        startActivity(intent)
        finish()
    }
}