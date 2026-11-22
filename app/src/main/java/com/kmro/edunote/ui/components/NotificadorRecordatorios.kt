package com.kmro.edunote.ui.components

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.kmro.edunote.MainActivity

// Utilidad centralizada para la gestión y emisión de notificaciones locales de recordatorio
object NotificadorRecordatorios {

    // Identificador único del canal de notificaciones destinado a los recordatorios de entregas

    const val CANAL_ID = "edunote_recordatorios"

    // Crea el canal de notificaciones para recordatorios de entregas en dispositivos con  Android 8.0 (API 26) o superior.
    fun crearCanal(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nombre = "Recordatorios de entregas"
            val descripcion = "Avisos un día antes de la fecha de entrega"
            val importancia = NotificationManager.IMPORTANCE_DEFAULT
            val canal = NotificationChannel(CANAL_ID, nombre, importancia).apply {
                description = descripcion
            }
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(canal)
        }
    }

    // * Verifica si la aplicación cuenta con permiso para publicar notificaciones.
    fun tienePermiso(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    // * Construye y muestra una notificación local con la información del recordatorio de una tarea
    fun mostrarNotificacion(
        context: Context,
        tituloTarea: String,
        horaTexto: String,
        notificationId: Int = 1001
    ) {
        crearCanal(context)
        if (!tienePermiso(context)) return

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val actividad = tituloTarea.trim().ifBlank { "Actividad sin título" }

        val builder = NotificationCompat.Builder(context, CANAL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Actividad: $actividad")
            .setContentText("Recordatorio un día antes a las $horaTexto")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("Actividad: $actividad\nRecordatorio un día antes a las $horaTexto")
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        try {
            NotificationManagerCompat.from(context).notify(notificationId, builder.build())
        } catch (_: SecurityException) {
            // Evita cierres si el permiso no está activo
        }
    }
}