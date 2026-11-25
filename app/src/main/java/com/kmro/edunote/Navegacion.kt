package com.kmro.edunote

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.kmro.edunote.ui.screens.InicioScreen
import com.kmro.edunote.ui.screens.InicioSesionScreen
import com.kmro.edunote.ui.screens.NotasScreen
import com.kmro.edunote.ui.screens.NuevTareas
import com.kmro.edunote.ui.screens.PerfilScreen
import com.kmro.edunote.ui.screens.RegistroScreen
import com.kmro.edunote.ui.screens.TareasScreen
import com.kmro.edunote.ui.screens.TerminosPrivacidadScreen
import com.kmro.edunote.util.SessionManager

@Composable
fun EduNoteNavegacion() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }

    // Si hay sesión guardada entra directo a inicio, si no, al login
    val destinoInicial = if (sessionManager.obtenerCorreoActivo() != null) {
        "inicio"
    } else {
        "inicio_sesion"
    }

    NavHost(navController = navController, startDestination = destinoInicial) {
        // Inicio de sesión
        composable("inicio_sesion") {
            InicioSesionScreen(navController)
        }
        // Crear cuenta
        composable("registro") {
            RegistroScreen(navController)
        }
        // Inicio
        composable("inicio") {
            InicioScreen(navController)
        }
        // Mis tareas
        composable("tareas") {
            TareasScreen(navController)
        }
        // Nueva tarea
        composable("nueva_tarea") {
            NuevTareas(navController)
        }
        // Notas
        composable("notas") {
            NotasScreen(navController)
        }
        // Perfil
        composable("perfil") {
            PerfilScreen(navController)
        }

        composable("terminos") {
            TerminosPrivacidadScreen(navController = navController, modoSoloLectura = false)
        }
        composable("terminos_consulta") {
            TerminosPrivacidadScreen(navController = navController, modoSoloLectura = true)
        }

    }
}