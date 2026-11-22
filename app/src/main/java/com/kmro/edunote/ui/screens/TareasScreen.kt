package com.kmro.edunote.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
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
import com.kmro.edunote.domain.model.Tarea
import com.kmro.edunote.ui.EduNoteViewModel
import com.kmro.edunote.ui.components.EduNoteBottomBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TareasScreen(
    navController: NavController,
    viewModel: EduNoteViewModel = hiltViewModel()
) {
    var filtroSeleccionado by remember { mutableStateOf(0) }
    val tareas by viewModel.tareas.collectAsState()

    val categoriaTodas = stringResource(R.string.filtro_todas)
    val categoriaPendientes = stringResource(R.string.filtro_pendientes)
    val categoriaHechas = stringResource(R.string.filtro_hechas)

    val tareasPendientes = tareas.filter { !it.estado.equals("Completada", ignoreCase = true) }
    val tareasHechas = tareas.filter { it.estado.equals("Completada", ignoreCase = true) }

    val categorias = listOf(
        "$categoriaTodas (${tareas.size})",
        "$categoriaPendientes (${tareasPendientes.size})",
        "$categoriaHechas (${tareasHechas.size})"
    )

    val tareasFiltradas = when (filtroSeleccionado) {
        1 -> tareasPendientes
        2 -> tareasHechas
        else -> tareas
    }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.fillMaxWidth()) {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(R.string.tareas_titulo),
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    },
                    actions = {
                        IconButton(onClick = {}) {
                            Icon(
                                imageVector = Icons.Filled.AccountCircle,
                                contentDescription = "Perfil",
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categorias.forEachIndexed { index, texto ->
                        FilterChip(
                            selected = filtroSeleccionado == index,
                            onClick = { filtroSeleccionado = index },
                            label = { Text(text = texto) },
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("nueva_tarea") },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = "Agregar tarea")
            }
        },
        bottomBar = {
            EduNoteBottomBar(navController = navController)
        }
    ) { paddingValues ->
        if (tareasFiltradas.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No hay tareas en ${categorias[filtroSeleccionado]}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(tareasFiltradas, key = { it.idTarea }) { tarea ->
                    TareaCardItem(
                        tarea = tarea,
                        onCheckedChange = { completada ->
                            viewModel.cambiarEstadoTarea(tarea, completada)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun TareaCardItem(
    tarea: Tarea,
    onCheckedChange: (Boolean) -> Unit
) {
    val estaHecha = tarea.estado.equals("Completada", ignoreCase = true)
    val esAlta = tarea.prioridad.equals("Alta", ignoreCase = true)
    val badgeBg = if (esAlta) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.surfaceVariant
    val badgeFg = if (esAlta) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant

    val detalle = listOf(tarea.materia, tarea.fechaLimite)
        .filter { it.isNotBlank() }
        .joinToString(" · ")

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = estaHecha,
                onCheckedChange = onCheckedChange
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = tarea.titulo,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        textDecoration = if (estaHecha) TextDecoration.LineThrough else TextDecoration.None
                    ),
                    color = if (estaHecha) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                )
                if (detalle.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = detalle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (tarea.notas.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = tarea.notas,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .padding(end = 6.dp)
                    .clip(RoundedCornerShape(50))
                    .background(badgeBg)
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text(
                    text = tarea.prioridad,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = badgeFg
                )
            }
        }
    }
}