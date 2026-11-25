package com.kmro.edunote.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.kmro.edunote.R
import com.kmro.edunote.ui.EduNoteViewModel
import com.kmro.edunote.util.SessionManager

/**
 * Pantalla de Registro de Cuenta.
 *
 * Permite al estudiante crear una cuenta nueva ingresando su nombre, correo electrónico y
 * contraseña, conserva el estado ante rotaciones de pantalla y envía a la pantalla de Términos y
 * Privacidad cuando la cuenta aún no ha aceptado las condiciones.

 */
@Composable
fun RegistroScreen(
    navController: NavController,
    onIniciarSesionClick: () -> Unit = { navController.popBackStack() },
    viewModel: EduNoteViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }

    var nombreUsuario by rememberSaveable { mutableStateOf("") }
    var correo by rememberSaveable { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    val mensajeAuth by viewModel.mensajeAuth.collectAsState()
    val esCorreoValido = android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Filled.School,
                contentDescription = "Logo EduNote",
                modifier = Modifier
                    .size(72.dp)
                    .padding(bottom = 8.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Crea tu cuenta de estudiante",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Campo de nombre de usuario
            OutlinedTextField(
                value = nombreUsuario,
                onValueChange = {
                    nombreUsuario = it
                    viewModel.limpiarMensaje()
                },
                label = { Text("Nombre de usuario") },
                singleLine = true,
                leadingIcon = {
                    Icon(imageVector = Icons.Filled.Person, contentDescription = "Icono usuario")
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo de correo electrónico
            OutlinedTextField(
                value = correo,
                onValueChange = {
                    correo = it
                    viewModel.limpiarMensaje()
                },
                label = { Text(stringResource(R.string.label_correo)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                leadingIcon = {
                    Icon(imageVector = Icons.Filled.Email, contentDescription = "Icono correo")
                },
                trailingIcon = {
                    if (esCorreoValido) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = "Correo válido",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                isError = correo.isNotBlank() && !esCorreoValido,
                modifier = Modifier.fillMaxWidth()
            )

            if (correo.isNotBlank() && !esCorreoValido) {
                Text(
                    text = stringResource(R.string.error_formato_correo),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Campo de contraseña
            OutlinedTextField(
                value = contrasena,
                onValueChange = {
                    contrasena = it
                    viewModel.limpiarMensaje()
                },
                label = { Text(stringResource(R.string.label_contrasena)) },
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                leadingIcon = {
                    Icon(imageVector = Icons.Filled.Lock, contentDescription = "Icono candado")
                },
                trailingIcon = {
                    val imagen = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = imagen, contentDescription = "Mostrar/Ocultar contraseña")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            if (mensajeAuth != null) {
                Text(
                    text = mensajeAuth!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Botón de Crear cuenta: registra y dirige a Términos y Privacidad si aún no acepta
            Button(
                onClick = {
                    viewModel.registrarUsuario(
                        nombreUsuario = nombreUsuario,
                        correo = correo,
                        contrasena = contrasena,
                        onExito = {
                            val correoRegistrado = sessionManager.obtenerCorreoActivo() ?: correo.trim()
                            val destino = if (sessionManager.haAceptadoTerminos(correoRegistrado)) {
                                "inicio"
                            } else {
                                "terminos"
                            }
                            navController.navigate(destino) {
                                popUpTo("registro") { inclusive = true }
                            }
                        }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(imageVector = Icons.Filled.PersonAdd, contentDescription = "Crear cuenta")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.btn_crear_cuenta),
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            HorizontalDivider(modifier = Modifier.fillMaxWidth())

            Spacer(modifier = Modifier.height(16.dp))

            // Botón para volver a iniciar sesión si ya tiene cuenta
            OutlinedButton(
                onClick = onIniciarSesionClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(imageVector = Icons.Filled.ArrowForward, contentDescription = "Iniciar sesión")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.btn_iniciar_sesion))
                }
            }
        }
    }
}