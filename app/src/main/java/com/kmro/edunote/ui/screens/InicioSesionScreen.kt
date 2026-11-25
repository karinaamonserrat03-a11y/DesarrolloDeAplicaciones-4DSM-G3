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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.kmro.edunote.R
import com.kmro.edunote.ui.EduNoteViewModel

/**
 * Pantalla de inicio de sesión de EduNote.
 *
 * Permite al estudiante autenticarse ingresando su correo electrónico y contraseña, valida el
 * formato del correo, muestra los mensajes de autenticación emitidos por [EduNoteViewModel] y
 * navega directamente a la pantalla principal cuando el acceso es válido.
 */
@Composable
fun InicioSesionScreen(
    navController: NavController,
    onCrearCuentaClick: () -> Unit = { navController.navigate("registro") },
    viewModel: EduNoteViewModel = hiltViewModel()
) {
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
                text = stringResource(R.string.login_subtitulo),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Campo de correo electrónico.
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

            // Campo de contraseña con alternador de visibilidad.
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

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterEnd
            ) {
                TextButton(onClick = { }) {
                    Text(stringResource(R.string.btn_olvido_contrasena))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Botón de iniciar sesión.
            Button(
                onClick = {
                    viewModel.iniciarSesion(
                        correo = correo,
                        contrasena = contrasena,
                        onExito = {
                            navController.navigate("inicio") {
                                popUpTo("inicio_sesion") { inclusive = true }
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
                    Text(stringResource(R.string.btn_iniciar_sesion))
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(imageVector = Icons.Filled.ArrowForward, contentDescription = "Ir")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            HorizontalDivider(modifier = Modifier.fillMaxWidth())

            Spacer(modifier = Modifier.height(16.dp))

            // Botón para navegar hacia el registro de una nueva cuenta.
            OutlinedButton(
                onClick = onCrearCuentaClick,
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
                    Text(stringResource(R.string.btn_crear_cuenta))
                }
            }
        }
    }
}