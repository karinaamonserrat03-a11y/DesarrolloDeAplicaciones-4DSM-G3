package com.kmro.edunote.ui.model



// * Modelo de UI que concentra el resumen general del rendimiento académico del estudiante en la pantalla de notas.
data class ResumenAcademico(
    val promedioGeneral: Double,
    val aprobatoriaMinima: Double,
    val aprobadas: Int,
    val enRiesgo: Int
) {
    fun calcularProgreso(): Float {
        return (promedioGeneral / 10.0).toFloat().coerceIn(0f, 1f)
    }
}

data class MateriaCalificacion(
    val nombre: String,
    val calificacion: Double,
    val estaAprobada: Boolean,
    val requiereApoyo: Boolean = false,
    val alertaExtra: String? = null
) {
    fun calcularProgreso(): Float {
        return (calificacion / 10.0).toFloat().coerceIn(0f, 1f)
    }
}