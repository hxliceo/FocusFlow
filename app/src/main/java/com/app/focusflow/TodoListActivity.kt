package com.app.focusflow

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.focusflow.Task
import com.app.focusflow.TaskAdapter
import com.google.firebase.firestore.FirebaseFirestore

class TodoListActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val tasks = mutableListOf<Task>()
    private lateinit var adapter: TaskAdapter

    private lateinit var editTextTask: EditText
    private lateinit var buttonAddTask: Button
    private lateinit var recyclerViewTasks: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_todo_list)

        editTextTask = findViewById(R.id.editTextTask)
        buttonAddTask = findViewById(R.id.buttonAddTask)
        recyclerViewTasks = findViewById(R.id.recyclerViewTasks)

        recyclerViewTasks.layoutManager = LinearLayoutManager(this)
        adapter = TaskAdapter(tasks,
            onDelete = { task ->
                db.collection("tasks").document(task.id)
                    .delete()
                    .addOnSuccessListener {
                        Toast.makeText(this, "Tarea eliminada", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Error eliminando tarea", Toast.LENGTH_SHORT).show()
                    }
            },
            onToggle = { task ->
                val updatedTask = task.copy(isDone = !task.isDone)
                db.collection("tasks").document(task.id)
                    .set(updatedTask)
                    .addOnSuccessListener {
                        // Opcional: mostrar mensaje
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Error actualizando tarea", Toast.LENGTH_SHORT).show()
                    }
            }
        )
        recyclerViewTasks.adapter = adapter

        buttonAddTask.setOnClickListener {
            val title = editTextTask.text.toString().trim()
            if (title.isNotEmpty()) {
                addTask(title)
                editTextTask.text.clear()
            } else {
                Toast.makeText(this, "Escribe una tarea", Toast.LENGTH_SHORT).show()
            }
        }

        listenTasks()
    }

    private fun addTask(title: String) {
        val newDoc = db.collection("tasks").document()
        val task = Task(id = newDoc.id, title = title, isDone = false)

        newDoc.set(task)
            .addOnSuccessListener {
                Log.d("Firestore", "Tarea añadida con ID: ${task.id}")
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error al añadir tarea", e)
                Toast.makeText(this, "Error al guardar tarea", Toast.LENGTH_SHORT).show()
            }
    }

    private fun listenTasks() {
        db.collection("tasks")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("Firestore", "Error en listener", error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    tasks.clear()
                    for (doc in snapshot.documents) {
                        val task = doc.toObject(Task::class.java)
                        if (task != null) {
                            tasks.add(task)
                        }
                    }
                    adapter.notifyDataSetChanged()
                }
            }
    }
}
