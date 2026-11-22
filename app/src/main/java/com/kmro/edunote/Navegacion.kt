package com.kmro.edunote

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.kmro.edunote.ui.screens.InicioScreen
import com.kmro.edunote.ui.screens.InicioSesionScreen
import com.kmro.edunote.ui.screens.NotasScreen
import com.kmro.edunote.ui.screens.NuevTareas
import com.kmro.edunote.ui.screens.RegistroScreen
import com.kmro.edunote.ui.screens.TareasScreen

@Composable
fun EduNoteNavegacion() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "inicio_sesion") {
        // Inicio de Sesión
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
        // Mis Tareas
        composable("tareas") {
            TareasScreen(navController)
        }
        // Nueva Tarea
        composable("nueva_tarea") {
            NuevTareas(navController)
        }
        // Notas
        composable("notas") {
            NotasScreen(navController)
        }
    }
}