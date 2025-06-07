package com.app.focusflow

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton

class DetailsNotesActivity : AppCompatActivity() {

    // Declaración de variables
    private lateinit var mtitleofnotedetail: TextView
    private lateinit var mcontentofnotedetail: TextView
    private lateinit var mgotoeditnote: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details_notes)

        // Inicialización de variables
        mtitleofnotedetail=findViewById(R.id.titleofnotedetail)
        mcontentofnotedetail=findViewById(R.id.contentofnotedetail)
        mgotoeditnote=findViewById(R.id.gotoeditnote)

        // Configuración de la barra de herramientas
        val toolbar: Toolbar = findViewById(R.id.toolbarofnotedetail)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Obtención del color de la nota desde la actividad anterior
        var selectedColor = intent.getIntExtra("Color", R.color.white)

        // Obtención de los datos de la nota desde la actividad anterior
        val data = intent

        // Acción al hacer clic en el botón para editar la nota
        mgotoeditnote.setOnClickListener {
            val intent = Intent(it.context, EditNotesActivity::class.java).apply {
                // Pasar los datos de la nota a la actividad de edición
                putExtra("Titulo", data.getStringExtra("Titulo"))
                putExtra("Contenido", data.getStringExtra("Contenido"))
                putExtra("idNota", data.getStringExtra("idNota"))
                putExtra("Color", selectedColor)
            }
            it.context.startActivity(intent)
        }

        // Mostrar el contenido y el título de la nota
        mcontentofnotedetail.text = data.getStringExtra("Contenido")
        mtitleofnotedetail.text = data.getStringExtra("Titulo")
    }

    // Acción al seleccionar un elemento en la barra de herramientas
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }
}
