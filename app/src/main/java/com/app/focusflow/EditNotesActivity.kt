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

class EditNotesActivity : AppCompatActivity() {

    // Declaración de variables
    private lateinit var data: Intent
    private lateinit var medittitleofnote: EditText
    private lateinit var meditcontentofnote: EditText
    private lateinit var msaveeditnote: FloatingActionButton

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseFirestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_notes)

        // Inicializacion de variables
        medittitleofnote=findViewById(R.id.edittitleofnote)
        meditcontentofnote=findViewById(R.id.editcontentofnote)
        msaveeditnote=findViewById(R.id.saveeditnote)

        // Obtener datos de la nota seleccionada
        var selectedColor = intent.getIntExtra("Color", R.color.white)
        data = intent

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

        // Inicialización de Firebase Firestore
        firebaseFirestore = FirebaseFirestore.getInstance()

        // Configuración de la barra de herramientas
        val toolbar: Toolbar = findViewById(R.id.toolbarofeditnote)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Acción al hacer clic en el botón para guardar la edición de la nota
        msaveeditnote.setOnClickListener {
            val newtitle = medittitleofnote.text.toString()
            val newcontent = meditcontentofnote.text.toString()

            // Verificar que los campos no estén vacíos
            if (newtitle.isEmpty() || newcontent.isEmpty()) {
                Toast.makeText(applicationContext, "Rellene ambos campos", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            } else {
                val firebaseUser = FirebaseAuth.getInstance().currentUser

                firebaseUser?.let { user ->
                    val idNota = data.getStringExtra("idNota") ?: ""
                    val documentReference = firebaseFirestore.collection("notas")
                        .document(user.uid)
                        .collection("Mis_notas")
                        .document(idNota)

                    // Crear mapa de datos para la nota editada
                    val note = hashMapOf(
                        "Titulo" to newtitle,
                        "Contenido" to newcontent,
                        "Color" to selectedColor // Guarda el color original o el nuevo color seleccionado
                    )

                    // Actualizar la nota en Firestore
                    documentReference.set(note)
                        .addOnSuccessListener {
                            Toast.makeText(
                                applicationContext,
                                "La nota ha sido actualizada",
                                Toast.LENGTH_SHORT
                            ).show()
                                val intent = Intent(this@EditNotesActivity, NotepadActivity::class.java)
                            startActivity(intent)
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(
                                applicationContext,
                                "Fallo al actualizar la nota: $e",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                } ?: run {
                    Toast.makeText(applicationContext, "Usuario nulo", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Mostrar el título y contenido de la nota seleccionada
        val notetitle = data.getStringExtra("Titulo")
        val notecontent = data.getStringExtra("Contenido")
        meditcontentofnote.setText(notecontent)
        medittitleofnote.setText(notetitle)
    }

    // Acción: volver atrás al seleccionar un elemento del menú de la barra de herramientas
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }
}