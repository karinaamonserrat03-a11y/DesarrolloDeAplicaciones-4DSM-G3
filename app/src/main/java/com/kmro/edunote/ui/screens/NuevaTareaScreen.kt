package com.kmro.edunote.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.kmro.edunote.R
import com.kmro.edunote.ui.EduNoteViewModel
import com.kmro.edunote.ui.components.EduNoteBottomBar
import com.kmro.edunote.ui.components.TarjetaRecordatorioUnDiaAntes
import com.kmro.edunote.ui.data.MateriasData
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun NuevTareas(
    navController: NavController,
    viewModel: EduNoteViewModel = hiltViewModel()
) {
    var tituloTarea by remember { mutableStateOf("") }
    var materiaSeleccionada by remember { mutableStateOf(MateriasData.listaMaterias[0]) }
    var prioridadSeleccionada by remember { mutableStateOf("Media") }
    var notasTarea by remember { mutableStateOf("") }
    var recordatorioActivado by remember { mutableStateOf(false) }
    var mostrarDialogoFecha by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState()
    val fechaFormateada = remember(datePickerState.selectedDateMillis) {
        datePickerState.selectedDateMillis?.let { millis ->
            val formatter = SimpleDateFormat("dd / MMM / yyyy", Locale("es", "ES"))
            formatter.format(Date(millis))
        } ?: "Selecciona una fecha"
    }

    Scaffold(
        topBar = { NuevaTareaTopBar(navController) },
        bottomBar = { EduNoteBottomBar(navController = navController) },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        FormularioNuevaTarea(
            modifier = Modifier.padding(paddingValues),
            tituloTarea = tituloTarea,
            onTituloChanged = { tituloTarea = it },
            materiaSeleccionada = materiaSeleccionada,
            onMateriaSelected = { materiaSeleccionada = it },
            prioridadSeleccionada = prioridadSeleccionada,
            onPrioridadSelected = { prioridadSeleccionada = it },
            notasTarea = notasTarea,
            onNotasChanged = { notasTarea = it },
            recordatorioActivado = recordatorioActivado,
            onRecordatorioChanged = { recordatorioActivado = it },
            fechaFormateada = fechaFormateada,
            onAbrirCalendario = { mostrarDialogoFecha = true },
            onGuardarClick = {
                if (tituloTarea.isNotBlank()) {
                    val fechaParaGuardar =
                        if (fechaFormateada == "Selecciona una fecha") "" else fechaFormateada
                    val millisSeleccionados = datePickerState.selectedDateMillis ?: 0L
                    viewModel.guardarTarea(
                        titulo = tituloTarea,
                        materia = materiaSeleccionada,
                        fechaLimite = fechaParaGuardar,
                        prioridad = prioridadSeleccionada,
                        notas = notasTarea,
                        fechaLimiteMillis = millisSeleccionados,
                        onExito = {
                            navController.navigate("tareas") {
                                popUpTo("tareas") { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    )
                }
            },
            onCancelarClick = {
                navController.popBackStack()
            }
        )
    }

    if (mostrarDialogoFecha) {
        CalendarioDialogoM3(
            datePickerState = datePickerState,
            onDismiss = { mostrarDialogoFecha = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevaTareaTopBar(navController: NavController) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.nueva_tarea_titulo),
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        },
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Regresar",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface
        )
    )
}

@Composable
fun FormularioNuevaTarea(
    modifier: Modifier = Modifier,
    tituloTarea: String,
    onTituloChanged: (String) -> Unit,
    materiaSeleccionada: String,
    onMateriaSelected: (String) -> Unit,
    prioridadSeleccionada: String,
    onPrioridadSelected: (String) -> Unit,
    notasTarea: String,
    onNotasChanged: (String) -> Unit,
    recordatorioActivado: Boolean,
    onRecordatorioChanged: (Boolean) -> Unit,
    fechaFormateada: String,
    onAbrirCalendario: () -> Unit,
    onGuardarClick: () -> Unit,
    onCancelarClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CampoTitulo(titulo = tituloTarea, onValueChange = onTituloChanged)
        CampoMateria(
            materiaSeleccionada = materiaSeleccionada,
            onMateriaSelected = onMateriaSelected
        )
        CampoFechaLimite(fechaFormateada = fechaFormateada, onClick = onAbrirCalendario)
        CampoPrioridad(
            prioridadSeleccionada = prioridadSeleccionada,
            onPrioridadSelected = onPrioridadSelected
        )
        CampoNotas(notas = notasTarea, onValueChange = onNotasChanged)

        TarjetaRecordatorioUnDiaAntes(
            activado = recordatorioActivado,
            onActivadoChange = onRecordatorioChanged,
            tituloTareaActual = tituloTarea
        )

        Spacer(modifier = Modifier.height(8.dp))

        BotonesAccion(onGuardarClick = onGuardarClick, onCancelarClick = onCancelarClick)
    }
}

@Composable
fun CampoTitulo(titulo: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = titulo,
        onValueChange = onValueChange,
        label = { Text(stringResource(R.string.label_titulo_tarea)) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampoMateria(materiaSeleccionada: String, onMateriaSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = materiaSeleccionada,
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(R.string.label_materia)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            MateriasData.listaMaterias.forEach { materia ->
                DropdownMenuItem(
                    text = { Text(text = materia, style = MaterialTheme.typography.bodyLarge) },
                    onClick = {
                        onMateriaSelected(materia)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun CampoFechaLimite(fechaFormateada: String, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${stringResource(R.string.label_fecha_limite)}: $fechaFormateada",
                style = MaterialTheme.typography.bodyLarge
            )
            Icon(
                imageVector = Icons.Filled.DateRange,
                contentDescription = "Calendario",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun CampoPrioridad(prioridadSeleccionada: String, onPrioridadSelected: (String) -> Unit) {
    val options = listOf("Baja", "Media", "Alta")

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = "Prioridad",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        SingleChoiceSegmentedButtonRow(
            modifier = Modifier.fillMaxWidth()
        ) {
            options.forEachIndexed { index, label ->
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
                    onClick = { onPrioridadSelected(label) },
                    selected = prioridadSeleccionada == label,
                    label = { Text(label, style = MaterialTheme.typography.bodyMedium) }
                )
            }
        }
    }
}

@Composable
fun CampoNotas(notas: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = notas,
        onValueChange = onValueChange,
        label = { Text("Notas (opcional)") },
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp),
        shape = RoundedCornerShape(12.dp),
        maxLines = 4
    )
}

@Composable
fun BotonesAccion(onGuardarClick: () -> Unit, onCancelarClick: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Button(
            onClick = onGuardarClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(26.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Save,
                    contentDescription = "Guardar",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.btn_guardar),
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                )
            }
        }

        TextButton(
            onClick = onCancelarClick,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(
                text = "Cancelar",
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun CalendarioDialogoM3(
    datePickerState: DatePickerState,
    onDismiss: () -> Unit
) {
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Aceptar", color = MaterialTheme.colorScheme.primary)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        },
        colors = DatePickerDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        DatePicker(state = datePickerState)
    }
}