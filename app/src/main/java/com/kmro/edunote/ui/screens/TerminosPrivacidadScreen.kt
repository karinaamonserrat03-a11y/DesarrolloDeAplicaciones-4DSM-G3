package com.kmro.edunote.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.kmro.edunote.util.SessionManager

/**
 * Modelo interno que representa cada punto informativo dentro de [TerminosPrivacidadScreen].
 */
private data class ClausulaTerminos(
    val numero: String,
    val titulo: String,
    val descripcion: String
)

/**
 * Pantalla de Términos de Servicio y Política de Privacidad de EduNote.
 *
 * Presenta de forma transparente las condiciones de uso educativo, resguardo local de datos,
 * derechos del alumno y seguridad de la cuenta. Cuando [modoSoloLectura] es `false`, exige
 * marcar la casilla de consentimiento antes de habilitar el acceso a la pantalla principal; si
 * el usuario rechaza o retrocede sin aceptar, limpia la sesión pendiente y regresa al inicio de sesión.
 *
 * @param navController Controlador de navegación para avanzar a inicio o regresar.
 * @param modoSoloLectura `true` cuando se consulta desde el perfil; `false` durante el flujo de acceso.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TerminosPrivacidadScreen(
    navController: NavController,
    modoSoloLectura: Boolean = false
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val correoActivo = remember { sessionManager.obtenerCorreoActivo().orEmpty() }

    val yaAceptoPreviamente = remember(correoActivo) {
        correoActivo.isNotBlank() && sessionManager.haAceptadoTerminos(correoActivo)
    }

    var aceptoTerminos by rememberSaveable {
        mutableStateOf(modoSoloLectura || yaAceptoPreviamente)
    }
    var mostrarDialogoRechazo by rememberSaveable { mutableStateOf(false) }

    val clausulas = remember {
        listOf(
            ClausulaTerminos(
                numero = "1",
                titulo = "Uso estrictamente educativo",
                descripcion = "Tu cuenta es exclusivamente para tareas, calificaciones y seguimiento escolar colaborativo."
            ),
            ClausulaTerminos(
                numero = "2",
                titulo = "Protección y privacidad de tus datos",
                descripcion = "Tus notas y entregas no se venden ni se comparten con anunciantes ni entidades terceras."
            ),
            ClausulaTerminos(
                numero = "3",
                titulo = "Control y derechos del alumno y tutor",
                descripcion = "Puedes consultar, exportar o solicitar la eliminación de tu historial cuando lo desees."
            ),
            ClausulaTerminos(
                numero = "4",
                titulo = "Seguridad de tus entregas y cuenta",
                descripcion = "Resguardo local aislado por cuenta conforme a las normativas de protección de datos escolares."
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.School,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(26.dp)
                        )
                        Text(
                            text = "Términos y Privacidad",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (modoSoloLectura) {
                                navController.popBackStack()
                            } else {
                                mostrarDialogoRechazo = true
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Regresar",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Insignia circular superior con ícono de seguridad verificada.
            Surface(
                modifier = Modifier.size(64.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Filled.VerifiedUser,
                        contentDescription = "Seguridad y privacidad",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Términos y Privacidad",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Todo lo que necesitas saber de forma clara, transparente y sin letra pequeña.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Contenedor principal con las 4 tarjetas numeradas.
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    clausulas.forEach { clausula ->
                        TarjetaClausulaTerminos(clausula = clausula)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tarjeta de aceptación con Checkbox interactivo.
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                        .clickable(enabled = !modoSoloLectura) {
                            aceptoTerminos = !aceptoTerminos
                        },
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Checkbox(
                            checked = aceptoTerminos,
                            onCheckedChange = { marcado ->
                                if (!modoSoloLectura) {
                                    aceptoTerminos = marcado
                                }
                            }
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "He leído y acepto los Términos de Servicio y la Política de Privacidad",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Válido para alumnos, docentes y tutores acreditados.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Botón principal: habilita el acceso a la aplicación solo si aceptó los términos.
            Button(
                onClick = {
                    if (modoSoloLectura) {
                        navController.popBackStack()
                    } else if (aceptoTerminos) {
                        if (correoActivo.isNotBlank()) {
                            sessionManager.guardarAceptacionTerminos(correoActivo, true)
                        }
                        navController.navigate("inicio") {
                            popUpTo("terminos") { inclusive = true }
                        }
                    }
                },
                enabled = aceptoTerminos,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (modoSoloLectura) "Entendido" else "Aceptar y continuar",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                )
            }

            if (!modoSoloLectura) {
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(
                    onClick = { mostrarDialogoRechazo = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "No acepto los términos",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }

    // Diálogo de confirmación cuando el usuario intenta salir o rechazar los términos.
    if (mostrarDialogoRechazo) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoRechazo = false },
            title = {
                Text(
                    text = "Consentimiento requerido",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Text(
                    text = "Para utilizar EduNote y almacenar tus actividades académicas es necesario " +
                            "aceptar los Términos de Servicio y la Política de Privacidad. Si rechazas, " +
                            "se cerrará la sesión actual y volverás al inicio de sesión.",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        mostrarDialogoRechazo = false
                        sessionManager.cerrarSesion()
                        navController.navigate("inicio_sesion") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                ) {
                    Text(
                        text = "Salir al inicio de sesión",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoRechazo = false }) {
                    Text("Volver y revisar")
                }
            }
        )
    }
}

/**
 * Tarjeta blanca redondeada que muestra una cláusula numerada dentro de [TerminosPrivacidadScreen].
 *
 * @param clausula Datos de número, título y descripción de la cláusula.
 */
@Composable
private fun TarjetaClausulaTerminos(clausula: ClausulaTerminos) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                modifier = Modifier.size(32.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = clausula.numero,
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = clausula.titulo,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = clausula.descripcion,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}