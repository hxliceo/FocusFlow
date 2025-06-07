package com.app.focusflow

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class NotepadActivity : AppCompatActivity() {

    private lateinit var mcreatenotesfab: FloatingActionButton
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var mrecyclerview: RecyclerView

    private lateinit var firebaseFirestore: FirebaseFirestore

    private lateinit var noteAdapter: FirestoreRecyclerAdapter<firebasemodel, NoteViewHolder>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notepad)

        // Inicialización de vistas y componentes
        mcreatenotesfab = findViewById(R.id.createnotefab)
        firebaseAuth=FirebaseAuth.getInstance()
        val firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
        firebaseFirestore = FirebaseFirestore.getInstance()
        supportActionBar?.title = "Todas las notas: "

        // Listeners
        mcreatenotesfab.setOnClickListener{
            var intent = Intent(this, CreateNotesActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Consulta para obtener todas las notas del usuario actual
        val query = firebaseFirestore.collection("notas")
            .document(firebaseUser!!.uid)
            .collection("Mis_notas")
            .orderBy("Titulo", Query.Direction.ASCENDING)

        // Opciones para el adaptador de Firestore
        val allUserNotes = FirestoreRecyclerOptions.Builder<firebasemodel>()
            .setQuery(query, firebasemodel::class.java)
            .build()

        // Configuración del adaptador de RecyclerView
        noteAdapter = object : FirestoreRecyclerAdapter<firebasemodel, NoteViewHolder>(allUserNotes) {

            @RequiresApi(Build.VERSION_CODES.M)
            override fun onBindViewHolder(noteViewHolder: NoteViewHolder, i: Int, firebasemodel: firebasemodel) {
                // Obtención de referencias a vistas
                val popupButton: ImageView = noteViewHolder.itemView.findViewById(R.id.menupopbutton)
                val colorResource: Int = firebasemodel.Color ?: R.color.defaultColor
                noteViewHolder.mnote.setBackgroundColor(ContextCompat.getColor(noteViewHolder.itemView.context, colorResource))
                noteViewHolder.tituloNota.text = firebasemodel.Titulo
                noteViewHolder.contenidoNota.text = firebasemodel.Contenido
                val docId: String = noteAdapter.snapshots.getSnapshot(i).id

                // Evento al hacer clic en una nota para ver detalles
                noteViewHolder.itemView.setOnClickListener {
                    val intent = Intent(it.context, DetailsNotesActivity::class.java)
                    intent.putExtra("Titulo", firebasemodel.Titulo)
                    intent.putExtra("Contenido", firebasemodel.Contenido)
                    intent.putExtra("idNota", docId)
                    intent.putExtra("Color", colorResource)
                    it.context.startActivity(intent)
                }

                // Menú emergente para editar o eliminar la nota
                popupButton.setOnClickListener { v ->
                    val popupMenu = PopupMenu(v.context, v)
                    popupMenu.gravity = Gravity.END
                    popupMenu.menu.add("Editar").setOnMenuItemClickListener {
                        val intent = Intent(v.context, EditNotesActivity::class.java)
                        intent.putExtra("Titulo", firebasemodel.Titulo)
                        intent.putExtra("Contenido", firebasemodel.Contenido)
                        intent.putExtra("idNota", docId)
                        intent.putExtra("Color", colorResource)
                        v.context.startActivity(intent)
                        false
                    }
                    popupMenu.menu.add("Eliminar").setOnMenuItemClickListener {
                        val documentReference = firebaseFirestore.collection("notas")
                            .document(firebaseUser.uid)
                            .collection("Mis_notas")
                            .document(docId)
                        documentReference.delete().addOnSuccessListener {
                            Toast.makeText(v.context, "Nota eliminada", Toast.LENGTH_SHORT).show()
                        }.addOnFailureListener {
                            Toast.makeText(v.context, "Fallo al eliminar la nota", Toast.LENGTH_SHORT).show()
                        }
                        false
                    }
                    popupMenu.show()
                }
            }

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.notes_layout, parent, false)
                return NoteViewHolder(view)
            }
        }

        // Configuración del RecyclerView
        mrecyclerview = findViewById(R.id.recyclerView)
        mrecyclerview.setHasFixedSize(true)
        val staggeredGridLayoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        mrecyclerview.layoutManager = staggeredGridLayoutManager
        mrecyclerview.adapter = noteAdapter
    }

    // Clase ViewHolder para las notas
    class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tituloNota: TextView = itemView.findViewById(R.id.notetitle)
        val contenidoNota: TextView = itemView.findViewById(R.id.notecontent)
        val mnote: LinearLayout = itemView.findViewById(R.id.note)
    }

    // Creación del menú de opciones (logout)
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_logout, menu)
        return true
    }

    // Evento al seleccionar una opción del menú
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_logout -> {
                FirebaseAuth.getInstance().signOut()
                Toast.makeText(this, "Se ha cerrado la sesión correctamente", Toast.LENGTH_SHORT).show()
                var intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    // Inicio del ciclo de vida
    override fun onStart() {
        super.onStart()
        noteAdapter.startListening()
    }

    // Fin del ciclo de vida
    override fun onStop() {
        super.onStop()
        if(noteAdapter!=null){
            noteAdapter.stopListening()
        }
    }

    // Manejo del botón de retroceso
    @Deprecated("Deprecated in Java")
    @Suppress("DEPRECATION")
    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, DashboardActivity::class.java)
        startActivity(intent)
        finish()
    }
}
