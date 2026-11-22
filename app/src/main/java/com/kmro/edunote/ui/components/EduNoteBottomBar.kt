package com.kmro.edunote.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.kmro.edunote.R

/**
 * Barra de navegación inferior
 * Permite cambiar entre Inicio, Tareas y Notas
 */
@Composable
fun EduNoteBottomBar(navController: NavController) {
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surfaceVariant
    ) {
        // Inicio
        NavigationBarItem(
            selected = currentRoute == "inicio",
            onClick = {
                if (currentRoute != "inicio") {
                    navController.navigate("inicio") {
                        popUpTo("inicio") { inclusive = true }
                    }
                }
            },
            icon = { Icon(Icons.Filled.Home, contentDescription = stringResource(R.string.nav_inicio)) },
            label = { Text(text = stringResource(R.string.nav_inicio)) }
        )

        // Tareas
        NavigationBarItem(
            selected = currentRoute == "tareas",
            onClick = {
                if (currentRoute != "tareas") {
                    navController.navigate("tareas")
                }
            },
            icon = { Icon(Icons.Filled.List, contentDescription = stringResource(R.string.nav_tareas)) },
            label = { Text(text = stringResource(R.string.nav_tareas)) } )

        // Notas
        NavigationBarItem(
            selected = currentRoute == "notas",
            onClick = {
                if (currentRoute != "notas") {
                    navController.navigate("notas")
                }
            },
            icon = { Icon(Icons.Filled.BarChart, contentDescription = stringResource(R.string.nav_notas)) },
            label = { Text(text = stringResource(R.string.nav_notas)) }
        )
    }
}