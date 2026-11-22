package com.kmro.edunote.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

/**
 * Pantalla para crear una cuenta
 */
@Composable
fun RegistroScreen(
    navController: NavController,
    viewModel: EduNoteViewModel = hiltViewModel()
) {
    var nombreUsuario by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var confirmarContrasena by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmarPasswordVisible by remember { mutableStateOf(false) }
    var mensajeError by remember { mutableStateOf<String?>(null) }

    val mensajeAuth by viewModel.mensajeAuth.collectAsState()
    val errorMostrado = mensajeError ?: mensajeAuth

    val esCorreoValido =
        correo.isBlank() || android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()

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
                imageVector = Icons.Filled.PersonAdd,
                contentDescription = "Crear cuenta",
                modifier = Modifier
                    .size(64.dp)
                    .padding(bottom = 8.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "Crear cuenta",
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Regístrate en ${stringResource(R.string.app_name)} para organizar tus tareas y notas",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Nombre de usuario
            OutlinedTextField(
                value = nombreUsuario,
                onValueChange = {
                    nombreUsuario = it
                    mensajeError = null
                    viewModel.limpiarMensaje()
                },
                label = { Text("Nombre de usuario") },
                singleLine = true,
                leadingIcon = {
                    Icon(imageVector = Icons.Filled.Person, contentDescription = "Nombre de usuario")
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Correo electrónico
            OutlinedTextField(
                value = correo,
                onValueChange = {
                    correo = it
                    mensajeError = null
                    viewModel.limpiarMensaje()
                },
                label = { Text(stringResource(R.string.label_correo)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                leadingIcon = {
                    Icon(imageVector = Icons.Filled.Email, contentDescription = "Correo")
                },
                trailingIcon = {
                    if (correo.isNotBlank() && android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = "Correo válido",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                isError = !esCorreoValido,
                modifier = Modifier.fillMaxWidth()
            )

            if (!esCorreoValido) {
                Text(
                    text = stringResource(R.string.error_formato_correo),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Contraseña
            OutlinedTextField(
                value = contrasena,
                onValueChange = {
                    contrasena = it
                    mensajeError = null
                    viewModel.limpiarMensaje()
                },
                label = { Text(stringResource(R.string.label_contrasena)) },
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                leadingIcon = {
                    Icon(imageVector = Icons.Filled.Lock, contentDescription = "Contraseña")
                },
                trailingIcon = {
                    val icono = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = icono, contentDescription = "Mostrar u ocultar contraseña")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Confirmar contraseña
            OutlinedTextField(
                value = confirmarContrasena,
                onValueChange = {
                    confirmarContrasena = it
                    mensajeError = null
                    viewModel.limpiarMensaje()
                },
                label = { Text("Confirmar contraseña") },
                singleLine = true,
                visualTransformation = if (confirmarPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                leadingIcon = {
                    Icon(imageVector = Icons.Filled.Lock, contentDescription = "Confirmar contraseña")
                },
                trailingIcon = {
                    val icono = if (confirmarPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    IconButton(onClick = { confirmarPasswordVisible = !confirmarPasswordVisible }) {
                        Icon(imageVector = icono, contentDescription = "Mostrar u ocultar confirmación")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            if (errorMostrado != null) {
                Text(
                    text = errorMostrado,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    when {
                        nombreUsuario.isBlank() || correo.isBlank() || contrasena.isBlank() -> {
                            mensajeError = "Completa todos los campos."
                        }
                        !android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches() -> {
                            mensajeError = "Ingresa un correo electrónico válido."
                        }
                        contrasena != confirmarContrasena -> {
                            mensajeError = "Las contraseñas no coinciden."
                        }
                        else -> {
                            viewModel.registrarUsuario(
                                nombreUsuario = nombreUsuario,
                                correo = correo,
                                contrasena = contrasena,
                                onExito = {
                                    navController.navigate("inicio") {
                                        popUpTo("inicio_sesion") { inclusive = true }
                                    }
                                }
                            )
                        }
                    }
                },
                shape = RoundedCornerShape(25.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(
                    text = "Crear cuenta",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            TextButton(onClick = { navController.popBackStack() }) {
                Text("Ya tengo cuenta · Iniciar sesión")
            }
        }
    }
}