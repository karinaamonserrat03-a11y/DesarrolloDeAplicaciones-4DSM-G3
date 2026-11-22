package com.kmro.edunote.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.PriorityHigh
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.kmro.edunote.R
import com.kmro.edunote.domain.model.Tarea as TareaDominio
import com.kmro.edunote.ui.EduNoteViewModel
import com.kmro.edunote.ui.components.DialogoRefuerzoMateria
import com.kmro.edunote.ui.components.EduNoteBottomBar
import com.kmro.edunote.ui.components.TarjetaRecordatoriosEntrega
import com.kmro.edunote.ui.data.RefuerzosData
import com.kmro.edunote.ui.model.Tarea as TareaUi
import kotlin.math.round

@Composable
fun InicioScreen(
    navController: NavController,
    viewModel: EduNoteViewModel = hiltViewModel()
) {
    val usuarioActual by viewModel.usuarioActual.collectAsState()
    val tareasRoom by viewModel.tareas.collectAsState()
    val nombreUsuario = usuarioActual?.nombreUsuario.orEmpty()

    val tareasEntregas = remember(tareasRoom) {
        tareasRoom.filter { tarea ->
            !tarea.titulo.startsWith("Estudiar ", ignoreCase = true) &&
                    !tarea.notas.orEmpty().contains("generada por refuerzo", ignoreCase = true) &&
                    !tarea.estado.equals("Completada", ignoreCase = true)
        }
    }

    val totalTareas = tareasRoom.size
    val tareasHechas = tareasRoom.count { it.estado.equals("Completada", ignoreCase = true) }
    val progresoTareas = if (totalTareas > 0) tareasHechas.toFloat() / totalTareas.toFloat() else 0f
    val porcentajeTareas = (progresoTareas * 100).toInt()

    val tareasConCalificacion = remember(tareasRoom) {
        tareasRoom.filter { it.calificacion != null }
    }
    val calificaciones = remember(tareasConCalificacion) {
        tareasConCalificacion.mapNotNull { it.calificacion?.toDouble() }
    }
    val promedioGeneral = remember(calificaciones) {
        if (calificaciones.isNotEmpty()) calificaciones.average() else null
    }

    val tareaEnRiesgo = remember(tareasConCalificacion) {
        tareasConCalificacion
            .filter { (it.calificacion?.toDouble() ?: 10.0) < 7.0 }
            .minByOrNull { it.calificacion?.toDouble() ?: 10.0 }
    }

    val mostrarTarjetaRefuerzo = tareaEnRiesgo != null || (promedioGeneral != null && promedioGeneral < 7.0)

    var tareaParaRefuerzo by remember { mutableStateOf<TareaDominio?>(null) }

    Scaffold(
        topBar = { EduNoteTopBar() },
        bottomBar = { EduNoteBottomBar(navController = navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top
        ) {
            EncabezadoBienvenida(nombre = nombreUsuario)
            Spacer(modifier = Modifier.height(16.dp))
            TarjetaRecordatoriosEntrega(tareas = tareasRoom)
            Spacer(modifier = Modifier.height(16.dp))
            TarjetaPromedioGeneral(
                progreso = progresoTareas,
                porcentaje = porcentajeTareas,
                hechas = tareasHechas,
                total = totalTareas,
                promedio = promedioGeneral
            )

            if (mostrarTarjetaRefuerzo) {
                Spacer(modifier = Modifier.height(16.dp))
                val materiaReferencia = tareaEnRiesgo?.materia
                    ?.takeIf { it.isNotBlank() }
                    ?: "Matemáticas"
                val calificacionReferencia = tareaEnRiesgo?.calificacion?.toDouble()
                    ?: promedioGeneral
                    ?: 0.0
                val calificacionRedondeada = round(calificacionReferencia * 10.0) / 10.0
                val tareaObjetivo = tareaEnRiesgo ?: tareasConCalificacion.firstOrNull {
                    it.calificacion?.toDouble() == promedioGeneral
                } ?: tareasRoom.firstOrNull()

                TarjetaRefuerzoRecomendado(
                    materia = materiaReferencia,
                    calificacion = calificacionRedondeada,
                    onVerRefuerzoClick = {
                        tareaParaRefuerzo = tareaObjetivo
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            SeccionProximasEntregas(
                tareas = tareasEntregas,
                onToggleCompletada = { tarea, completada ->
                    viewModel.cambiarEstadoTarea(tarea, completada)
                }
            )
        }
    }

    tareaParaRefuerzo?.let { tarea ->
        DialogoRefuerzoMateria(
            tarea = tarea,
            onDismiss = { tareaParaRefuerzo = null },
            onCrearTareaEstudio = {
                val material = RefuerzosData.obtenerPorMateria(tarea.materia)
                viewModel.guardarTarea(
                    titulo = "Estudiar ${material.materia}: ${material.videoTitulo}",
                    materia = material.materia,
                    fechaLimite = tarea.fechaLimite,
                    prioridad = "Alta",
                    notas = "Tarea de estudio generada por refuerzo",
                    onExito = { tareaParaRefuerzo = null }
                )
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EduNoteTopBar() {
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.School,
                    contentDescription = "Logo",
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Column {
                    Text(
                        text = stringResource(R.string.app_name),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = stringResource(R.string.home_titulo),
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        },
        actions = {
            IconButton(onClick = { }) {
                Icon(
                    imageVector = Icons.Filled.AccountCircle,
                    contentDescription = "Perfil",
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.onSecondaryFixedVariant
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
    )
}

@Composable
fun EncabezadoBienvenida(nombre: String) {
    val saludo = if (nombre.isNotBlank()) "Hola, $nombre" else "Hola"
    Column {
        Text(
            text = saludo,
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = stringResource(R.string.home_subtitulo),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun TarjetaPromedioGeneral(
    progreso: Float,
    porcentaje: Int,
    hechas: Int,
    total: Int,
    promedio: Double?
) {
    val promedioGrande = promedio?.let {
        val redondeado = round(it * 10.0) / 10.0
        "$redondeado / 10"
    } ?: "-- / 10"

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(R.string.home_promedio_titulo),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = promedioGrande,
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(10.dp))

            LinearProgressIndicator(
                progress = { progreso },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Completadas: $hechas / $total",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "$porcentaje% completado",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun TarjetaRefuerzoRecomendado(
    materia: String,
    calificacion: Double,
    onVerRefuerzoClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.45f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    modifier = Modifier.size(40.dp),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.error.copy(alpha = 0.16f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Filled.PriorityHigh,
                            contentDescription = "Alerta de refuerzo",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Refuerzo recomendado",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.error
                    )
                    Text(
                        text = "$materia · calificación $calificacion",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.error
                    )
                    Text(
                        text = "Lecciones interactivas para subir tu puntaje.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Button(
                onClick = onVerRefuerzoClick,
                modifier = Modifier.align(Alignment.End),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Ver refuerzo",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    imageVector = Icons.Filled.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun SeccionProximasEntregas(
    tareas: List<TareaDominio>,
    onToggleCompletada: (TareaDominio, Boolean) -> Unit
) {
    Column {
        Text(
            text = stringResource(R.string.home_seccion_entregas),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(12.dp))
        if (tareas.isEmpty()) {
            Text(
                text = "No hay tareas pendientes guardadas. ¡Todo al día!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            tareas.forEach { tareaDominio ->
                val estaHecha = tareaDominio.estado.equals("Completada", ignoreCase = true)
                val detalleTexto = listOf(tareaDominio.materia, tareaDominio.fechaLimite)
                    .filter { it.isNotBlank() }
                    .joinToString(" · ")
                val tareaUi = TareaUi(
                    titulo = tareaDominio.titulo,
                    detalle = detalleTexto,
                    prioridad = tareaDominio.prioridad
                )

                ItemEntrega(
                    tarea = tareaUi,
                    checked = estaHecha,
                    onCheckedChange = { completada ->
                        onToggleCompletada(tareaDominio, completada)
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun ItemEntrega(
    tarea: TareaUi,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val colorPrioridad = when (tarea.prioridad) {
        "Alta" -> MaterialTheme.colorScheme.errorContainer
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    val textoPrioridadColor = when (tarea.prioridad) {
        "Alta" -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = checked,
                onCheckedChange = onCheckedChange
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = tarea.titulo,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium,
                        textDecoration = if (checked) TextDecoration.LineThrough else TextDecoration.None
                    ),
                    color = if (checked) {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )
                if (tarea.detalle.isNotBlank()) {
                    Text(
                        text = tarea.detalle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(colorPrioridad)
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text(
                    text = tarea.prioridad,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = textoPrioridadColor
                )
            }
        }
    }
}