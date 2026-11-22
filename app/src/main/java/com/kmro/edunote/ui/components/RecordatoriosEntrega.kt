package com.kmro.edunote.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kmro.edunote.domain.model.Tarea as TareaDominio

// Componente visual que muestra un aviso destacado para las tareas pendientes cuya fecha límite
// * está dentro de las próximas 24 horas (o ya venció sin completarse)
// * Filtra las actividades que no están marcadas como completadas
@Composable
fun TarjetaRecordatoriosEntrega(
    tareas: List<TareaDominio>,
    modifier: Modifier = Modifier
) {
    val ahoraMillis = System.currentTimeMillis()
    val veinticuatroHorasMillis = 24L * 60L * 60L * 1000L

    val tareasPorVencer = tareas.filter { tarea ->
        val noCompletada = !tarea.estado.equals("Completada", ignoreCase = true)
        val tieneFecha = tarea.fechaLimiteMillis > 0L
        val diferencia = tarea.fechaLimiteMillis - ahoraMillis
        noCompletada && tieneFecha && diferencia <= veinticuatroHorasMillis
    }

    if (tareasPorVencer.isNotEmpty()) {
        Card(
            modifier = modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.45f)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.NotificationsActive,
                        contentDescription = "Recordatorio",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Recordatorio de entregas (${tareasPorVencer.size})",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                tareasPorVencer.forEach { tarea ->
                    val textoEntrega = if (tarea.fechaLimite.isNotBlank()) {
                        "· ${tarea.titulo} (${tarea.materia}) — Entrega: ${tarea.fechaLimite}"
                    } else {
                        "· ${tarea.titulo} (${tarea.materia}) — ¡Entrega próxima!"
                    }
                    Text(
                        text = textoEntrega,
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}