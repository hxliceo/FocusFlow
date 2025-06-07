package com.app.focusflow

// Definición de la clase firebasemodel
class firebasemodel {
    // Propiedades de la clase para almacenar el título, contenido y color de la nota
    var Titulo: String? = null // Título de la nota
    var Contenido: String? = null // Contenido de la nota
    var Color: Int? = null // Color de la nota representado como un entero

    // Constructor vacío
    constructor() {}

    // Constructor con parámetros para inicializar las propiedades de la clase
    constructor(Titulo: String?, Contenido: String?, Color: Int?) {
        this.Titulo = Titulo
        this.Contenido = Contenido
        this.Color = Color
    }
}
