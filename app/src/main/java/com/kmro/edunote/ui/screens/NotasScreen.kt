package com.kmro.edunote.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.kmro.edunote.domain.model.Tarea as TareaDominio
import com.kmro.edunote.ui.EduNoteViewModel
import com.kmro.edunote.ui.components.DialogoCalificarTarea
import com.kmro.edunote.ui.components.DialogoRefuerzoMateria
import com.kmro.edunote.ui.components.EduNoteBottomBar
import com.kmro.edunote.ui.data.RefuerzosData
import com.kmro.edunote.ui.model.MateriaCalificacion
import com.kmro.edunote.ui.model.ResumenAcademico
import kotlin.math.round

/*
 * Pantalla de notas y seguimiento académico de EduNote.
 */
@Composable
fun NotasScreen(
    navController: NavController,
    viewModel: EduNoteViewModel = hiltViewModel()
) {
    val tareasRoom by viewModel.tareas.collectAsState()
    val aprobatoriaMinima = 6.0
    val tareasPorMateria = remember(tareasRoom) {
        tareasRoom
            .filter { it.materia.isNotBlank() && it.calificacion != null }
            .groupBy { it.materia.trim() }
    }

    val listaMaterias: List<MateriaCalificacion> = remember(tareasPorMateria) {
        tareasPorMateria
            .map { (materia, tareasDeMateria) ->
                val promedioMateria = tareasDeMateria
                    .mapNotNull { it.calificacion?.toDouble() }
                    .average()
                val redondeada = round(promedioMateria * 10.0) / 10.0
                val aprobada = redondeada >= aprobatoriaMinima

                MateriaCalificacion(
                    nombre = materia,
                    calificacion = redondeada,
                    estaAprobada = aprobada,
                    requiereApoyo = !aprobada,
                    alertaExtra = if (!aprobada) "Calificación menor a $aprobatoriaMinima" else null
                )
            }
            .sortedBy { it.nombre }
    }

    val resumen: ResumenAcademico = remember(tareasRoom, listaMaterias) {
        val todasLasNotas = tareasRoom.mapNotNull { it.calificacion?.toDouble() }
        val promedio = if (todasLasNotas.isNotEmpty()) {
            round(todasLasNotas.average() * 10.0) / 10.0
        } else {
            0.0
        }
        ResumenAcademico(
            promedioGeneral = promedio,
            aprobatoriaMinima = aprobatoriaMinima,
            aprobadas = listaMaterias.count { it.estaAprobada },
            enRiesgo = listaMaterias.count { !it.estaAprobada }
        )
    }

    var mostrarSelectorActividad by remember { mutableStateOf(false) }
    var tareaParaCalificar by remember { mutableStateOf<TareaDominio?>(null) }
    var tareaParaRefuerzo by remember { mutableStateOf<TareaDominio?>(null) }

    Scaffold(
        topBar = {
            NotasTopBar(
                onPerfilClick = { navController.navigate("perfil") }
            )
        },
        bottomBar = { EduNoteBottomBar(navController = navController) },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TarjetaPromedioGeneralNotas(
                resumen = resumen,
                hayCalificaciones = listaMaterias.isNotEmpty()
            )
            EncabezadoSeccionMaterias(totalAsignaturas = listaMaterias.size)

            if (listaMaterias.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                    )
                ) {
                    Text(
                        text = "Aún no hay calificaciones registradas. Toca '+ Registrar calificación' para calificar una actividad.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } else {
                listaMaterias.forEach { materia ->
                    if (materia.estaAprobada) {
                        ItemMateriaNormal(materia = materia)
                    } else {
                        ItemMateriaAlerta(
                            materia = materia,
                            onVerRefuerzoClick = {
                                val tareaMenor = tareasPorMateria[materia.nombre]
                                    ?.minByOrNull { it.calificacion?.toDouble() ?: 10.0 }
                                tareaParaRefuerzo = tareaMenor
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            BotonRegistrarCalificacion(
                onClick = { mostrarSelectorActividad = true }
            )
        }
    }

    if (mostrarSelectorActividad) {
        AlertDialog(
            onDismissRequest = { mostrarSelectorActividad = false },
            title = {
                Text(
                    text = "Selecciona una actividad",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                val actividadesDisponibles = tareasRoom.filter { tarea ->
                    !tarea.titulo.startsWith("Estudiar ", ignoreCase = true) &&
                            !tarea.notas.orEmpty().contains("generada por refuerzo", ignoreCase = true)
                }
                if (actividadesDisponibles.isEmpty()) {
                    Text(
                        text = "No tienes actividades disponibles para calificar.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        actividadesDisponibles.forEach { tarea ->
                            val estadoCal = tarea.calificacion?.let {
                                " · Cal: ${round(it * 10.0) / 10.0}"
                            } ?: " · Sin calificar"

                            Surface(
                                onClick = {
                                    mostrarSelectorActividad = false
                                    tareaParaCalificar = tarea
                                },
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = tarea.titulo,
                                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "${tarea.materia}$estadoCal",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { mostrarSelectorActividad = false }) {
                    Text("Cerrar")
                }
            }
        )
    }

    tareaParaCalificar?.let { tarea ->
        DialogoCalificarTarea(
            tarea = tarea,
            onDismiss = { tareaParaCalificar = null },
            onGuardarCalificacion = { nota ->
                viewModel.calificarTarea(tarea.idTarea, nota)
                tareaParaCalificar = null
            }
        )
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

/*
 * Barra superior de la pantalla de notas con el título de sección y acceso de perfil.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotasTopBar(
    onPerfilClick: () -> Unit = {}
) {
    TopAppBar(
        title = {
            Text(
                text = "Notas",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        },
        actions = {
            IconButton(onClick = onPerfilClick) {
                Icon(
                    imageVector = Icons.Filled.AccountCircle,
                    contentDescription = "Perfil",
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface
        )
    )
}

// Tarjeta que resume el promedio general, la calificación aprobatoria mínima, la barra de avance y el conteo de asignaturas aprobadas frente a las que se encuentran en riesgo.

@Composable
fun TarjetaPromedioGeneralNotas(
    resumen: ResumenAcademico,
    hayCalificaciones: Boolean = true
) {
    val textoPromedio = if (hayCalificaciones) "${resumen.promedioGeneral}" else "--"

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Promedio general",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Surface(
                    shape = RoundedCornerShape(50),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "Aprobatoria: ${resumen.aprobatoriaMinima}",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = textoPromedio,
                    style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "/ 10",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
            }

            LinearProgressIndicator(
                progress = { resumen.calcularProgreso() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${resumen.aprobadas} aprobadas",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${resumen.enRiesgo} en riesgo",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

//Encabezado de la sección de materias que muestra el título y el total de asignaturas evaluadas

@Composable
fun EncabezadoSeccionMaterias(totalAsignaturas: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Materias del periodo",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "$totalAsignaturas asignaturas",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// Tarjeta de asignatura aprobada que muestra su nombre, calificación promedio y barra de progreso

@Composable
fun ItemMateriaNormal(materia: MateriaCalificacion) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                        modifier = Modifier.size(38.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Filled.MenuBook,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    Text(
                        text = materia.nombre,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Text(
                    text = "${materia.calificacion}",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            LinearProgressIndicator(
                progress = { materia.calcularProgreso() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }
    }
}

// Tarjeta de asignatura en riesgo o reprobada que resalta la alerta y permite abrir el material de refuerzo correspondiente
@Composable
fun ItemMateriaAlerta(
    materia: MateriaCalificacion,
    onVerRefuerzoClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.error.copy(alpha = 0.12f),
                        modifier = Modifier.size(38.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = materia.nombre.take(1).uppercase(),
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = materia.nombre,
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = MaterialTheme.colorScheme.error.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "Reprobada",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }
                        if (materia.requiereApoyo) {
                            Text(
                                text = "Requiere apoyo",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }

                Text(
                    text = "${materia.calificacion}",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.error
                )
            }

            LinearProgressIndicator(
                progress = { materia.calcularProgreso() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = MaterialTheme.colorScheme.error,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            if (materia.alertaExtra != null) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Warning,
                                contentDescription = "Alerta",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = materia.alertaExtra,
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Button(
                            onClick = onVerRefuerzoClick,
                            shape = RoundedCornerShape(50),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                            ),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text(
                                text = "Ver refuerzo →",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }
            }
        }
    }
}

// Botón de acción inferior que abre el selector de actividades para registrar una calificación

@Composable
fun BotonRegistrarCalificacion(onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(25.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Registrar",
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Registrar calificación",
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
}