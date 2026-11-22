package com.kmro.edunote.ui.data

import android.net.Uri


// Componente el cual son los refuerzos de cada materia, incluye ejercicio resuelto, video
data class MaterialRefuerzo(
    val materia: String,
    val resumen: String,
    val ejercicioResuelto: String,
    val videoTitulo: String,
    val videoUrl: String
)

object RefuerzosData {

    private val materiales = listOf(
        MaterialRefuerzo(
            materia = "Español",
            resumen = "Repasa ortografía, signos de puntuación, estructura de textos e ideas principales.",
            ejercicioResuelto = "Ejemplo: Identifica tesis, argumentos y conclusión en un párrafo antes de resumirlo.",
            videoTitulo = "Lengua castellana y literatura",
            videoUrl = "https://youtube.com/playlist?list=PLgXFNZYKAlPbkfgZO0AF69kxBzgwuEHtz&si=uC0BalgDtrZlBhG2"
        ),
        MaterialRefuerzo(
            materia = "Matemáticas",
            resumen = "Revisa jerarquía de operaciones, despejes y resolución paso a paso de ecuaciones.",
            ejercicioResuelto = "Ejemplo: 2x + 6 = 14 → 2x = 8 → x = 4.",
            videoTitulo = "Matemáticas desde cero | Antonio Díaz Profe",
            videoUrl = "https://youtube.com/playlist?list=PLaiS5O_PbRfkQsp7uEtQ1zOyJI28HcbPn&si=l3c0jAAoiO6jXVmb"
        ),
        MaterialRefuerzo(
            materia = "Ciencias Naturales",
            resumen = "Refuerza el método científico, observación de fenómenos y cuidado del entorno.",
            ejercicioResuelto = "Ejemplo: Observación → Hipótesis → Experimento → Conclusión.",
            videoTitulo = "Clases de Ciencias Naturales - 1° año de Bachillerato",
            videoUrl = "https://youtube.com/playlist?list=PLof1wzs2RVVsnre2cnOLZw5bQKzMt4TU6&si=Nx0l8GEg39H9RL3B"
        ),
        MaterialRefuerzo(
            materia = "Física",
            resumen = "Revisa unidades, conversión de medidas y fórmulas básicas antes de sustituir datos.",
            ejercicioResuelto = "Ejemplo: v = d / t → Si d = 100 m y t = 20 s, entonces v = 5 m/s.",
            videoTitulo = "Curso de fisica desde cero",
            videoUrl = "https://youtube.com/playlist?list=PLu5yFXEO_4L9RLE4_RtWgCZ-3cUbHCPLH&si=sNXIGQtYfe_ePdlR"
        ),
        MaterialRefuerzo(
            materia = "Química",
            resumen = "Repasa tabla periódica, enlaces químicos y balanceo de ecuaciones.",
            ejercicioResuelto = "Ejemplo: 2H₂ + O₂ → 2H₂O (balanceo de átomos en reactivos y productos).",
            videoTitulo = "Química General I",
            videoUrl = "https://youtube.com/playlist?list=PL5eXNtcO-SQtGBrtzMoUWpcrD9vLx-Fgu&si=hgEAU-pbrfyQes9i"
        ),
        MaterialRefuerzo(
            materia = "Historia",
            resumen = "Ubica líneas del tiempo, causas, desarrollo y consecuencias de los hechos históricos.",
            ejercicioResuelto = "Ejemplo: Relaciona cada fecha clave con su causa política o social y su efecto.",
            videoTitulo = "La Historia completa del Mundo",
            videoUrl = "https://youtu.be/dIUhOTF8HTM?si=gqrad03scg3ARNcy"
        ),
        MaterialRefuerzo(
            materia = "Geografía",
            resumen = "Repasa coordenadas, relieve, climas y relación entre espacio geográfico y población.",
            ejercicioResuelto = "Ejemplo: Distingue latitud y longitud para ubicar un punto en el mapa.",
            videoTitulo = "Geografía Guía UNAM 2026",
            videoUrl = "https://youtube.com/playlist?list=PLAsP0-mz6YS_rrPxWZiLKc5REDwornLWM&si=QAOePqGCC2ykxm-4"
        ),
        MaterialRefuerzo(
            materia = "Formación Cívica y Ética",
            resumen = "Revisa derechos humanos, toma de decisiones éticas, convivencia y participación ciudadana.",
            ejercicioResuelto = "Ejemplo: Analiza un dilema identificando derechos involucrados y consecuencias.",
            videoTitulo = "Formación Cívica y Ética",
            videoUrl = "https://youtube.com/playlist?list=PL24snM8sp90hfTr09xYMW-tSturEuB-Jz&si=IChP2E9W_N2yaBmj"
        ),
        MaterialRefuerzo(
            materia = "Inglés",
            resumen = "Practica tiempos verbales básicos (Presente, Pasado, Futuro) y vocabulario cotidiano.",
            ejercicioResuelto = "Ejemplo: She studied (Past Simple) for the exam yesterday.",
            videoTitulo = "Curso inglés desde cero - Completo",
            videoUrl = "https://youtube.com/playlist?list=PLgrNDDl9MxYmUmf19zPiljdg8FKIRmP78&si=AjXIU00BhvFCqKgo"
        ),
        MaterialRefuerzo(
            materia = "Artes",
            resumen = "Explora elementos visuales o expresivos (color, forma, ritmo, composición) y técnicas.",
            ejercicioResuelto = "Ejemplo: Combina colores complementarios para generar contraste en un boceto.",
            videoTitulo = "Curso para aprender técnicas de arte",
            videoUrl = "https://youtube.com/playlist?list=PLleEMqpsrxEu5kZW1zMLO2ApQpQpAg1J_&si=H4sNRYw6WC_et1BF"
        ),
        MaterialRefuerzo(
            materia = "Tecnología",
            resumen = "Repasa procesos técnicos, resolución de problemas con herramientas digitales y buenas prácticas.",
            ejercicioResuelto = "Ejemplo: Define problema → Diseña solución → Implementa → Evalúa resultado.",
            videoTitulo = "Tecnologías y Herramientas Digitales para la Educación",
            videoUrl = "https://youtube.com/playlist?list=PLL_5XVOFV8HjqneOu1kVaCgMpL1x1LFWc&si=Wx6xhuHIr_Z-4Sru"
        ),
        MaterialRefuerzo(
            materia = "Medicina",
            resumen = "Refuerza anatomía básica, sistemas del cuerpo humano, terminología y prevención.",
            ejercicioResuelto = "Ejemplo: Asocia cada sistema (circulatorio, respiratorio, digestivo) con su función principal.",
            videoTitulo = "Semestre Filtro Medicina 2026",
            videoUrl = "https://youtube.com/playlist?list=PLEuqo0T6p5mE&si=uA42rYzo80YF3KkV"
        ),
        MaterialRefuerzo(
            materia = "Sociología",
            resumen = "Repasa grupos sociales, cultura, normas, instituciones y dinámicas de la sociedad.",
            ejercicioResuelto = "Ejemplo: Identifica cómo una norma social influye en la conducta de un grupo.",
            videoTitulo = "Curso de Sociología",
            videoUrl = "https://youtube.com/playlist?list=PLMra4G0-Z7pMnBecUK6b8VpwDceBI7xcE&si=6F-wBm8foOuhUCgd"
        )
    )

    fun obtenerPorMateria(materia: String): MaterialRefuerzo {
        val limpia = materia.trim().ifBlank { "General" }
        val encontrada = materiales.find {
            it.materia.equals(limpia, ignoreCase = true)
        }
        if (encontrada != null) {
            return encontrada
        }

        val queryEncoded = Uri.encode("repaso $limpia ejercicios resueltos")
        return MaterialRefuerzo(
            materia = limpia,
            resumen = "Repasa los conceptos clave de $limpia y resume las ideas principales del tema.",
            ejercicioResuelto = "Ejercicio guiado de $limpia: Anota 3 puntos clave y explícalos con tus palabras.",
            videoTitulo = "Video de repaso: $limpia",
            videoUrl = "https://www.youtube.com/results?search_query=$queryEncoded"
        )
    }
}