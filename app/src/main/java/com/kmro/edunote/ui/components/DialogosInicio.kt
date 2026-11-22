package com.kmro.edunote.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kmro.edunote.domain.model.Tarea as TareaDominio
import com.kmro.edunote.ui.data.RefuerzosData

// Diálogo modal que permite asignar o actualizar la calificación de una tarea académica
// * Valida que el valor ingresado corresponda a un número dentro del rango permitido 0 a 10
// * aceptando tanto punto como coma decimal antes de notificar al componente padre
@Composable
fun DialogoCalificarTarea(
    tarea: TareaDominio,
    onDismiss: () -> Unit,
    onGuardarCalificacion: (Double) -> Unit
) {
    var textoNota by remember {
        mutableStateOf(tarea.calificacion?.toString() ?: "")
    }
    var errorTexto by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Calificar tarea",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "${tarea.titulo} · ${tarea.materia}",
                    style = MaterialTheme.typography.bodyMedium
                )
                OutlinedTextField(
                    value = textoNota,
                    onValueChange = {
                        textoNota = it
                        errorTexto = null
                    },
                    label = { Text("Calificación (0 a 10)") },
                    singleLine = true,
                    isError = errorTexto != null
                )
                errorTexto?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val valor = textoNota.replace(",", ".").toDoubleOrNull()
                    if (valor == null || valor < 0.0 || valor > 10.0) {
                        errorTexto = "Ingresa un número válido entre 0 y 10"
                    } else {
                        onGuardarCalificacion(valor)
                    }
                }
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

// Recupera el material didáctico correspondiente desde RefuerzosData y muestra un resumen
// conceptual, un ejercicio resuelto de ejemplo, un enlace para abrir el video explicativo
// en el navegador o en la aplicación correspondiente y la opción de generar una tarea de estudio

@Composable
fun DialogoRefuerzoMateria(
    tarea: TareaDominio,
    onDismiss: () -> Unit,
    onCrearTareaEstudio: () -> Unit
) {
    val material = remember(tarea.materia) {
        RefuerzosData.obtenerPorMateria(tarea.materia)
    }
    val uriHandler = LocalUriHandler.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Refuerzo · ${material.materia}",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.error
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Resumen del tema:",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = material.resumen,
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = "Ejercicio resuelto:",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = material.ejercicioResuelto,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(8.dp)
                    )
                }

                TextButton(
                    onClick = { uriHandler.openUri(material.videoUrl) },
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.PlayArrow,
                        contentDescription = "Ver video",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = material.videoTitulo,
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = onCrearTareaEstudio) {
                Text("Crear tarea de estudio")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar")
            }
        }
    )
}