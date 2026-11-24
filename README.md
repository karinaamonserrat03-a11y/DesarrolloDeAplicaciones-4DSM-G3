# EduNote

**EduNote** es una aplicación móvil nativa para Android desarrollada en **Kotlin** y **Jetpack Compose**, diseñada para ayudar a los estudiantes a organizar sus tareas académicas, monitorear sus calificaciones por materia, recibir alertas tempranas de bajo rendimiento y generar planes de estudio personalizados

---

## Características Principales

* **Gestión Multiusuario con Aislamiento de Datos:** Registro e inicio de sesión local con persistencia de sesión reactiva (`SessionManager` + `StateFlow`), garantizando que cada estudiante visualice exclusivamente sus propias tareas, calificaciones y perfil.
* **Panel Principal (Inicio):** Saludo personalizado, recordatorios de próximas entregas, resumen del promedio general, barra de porcentaje de avance y alertas inteligentes cuando se detecta una calificación o promedio por debajo del mínimo esperado (`7.0`).
* **Control de Tareas:** Creación, consulta y filtrado dinámico de actividades (`Todas`, `Pendientes` y `Completadas`) con niveles de prioridad (`Alta`, `Media`, `Baja`) y actualización de estado en tiempo real.
* **Seguimiento de Notas y Refuerzos:** Cálculo automático de promedios por asignatura, identificación de materias aprobadas o en riesgo (`< 6.0`) y generación automática de tareas de estudio con material de apoyo recomendado.
* **Perfil del Estudiante:** Vista dedicada con información de la cuenta activa y cierre de sesión seguro con limpieza del historial de navegación.

---

## Arquitectura y Tecnologías

El proyecto sigue los principios de **Clean Architecture** y el patrón **MVVM (Model-View-ViewModel)**:

* **Lenguaje:** Kotlin
* **Interfaz de Usuario:** Jetpack Compose + Material Design 3
* **Inyección de Dependencias:** Dagger Hilt
* **Persistencia Local:** Room Database (v4 con migración explícita `MIGRATION_3_4`) & SharedPreferences
* **Asincronía y Reactividad:** Kotlin Coroutines, `StateFlow` y operadores reactivos (`flatMapLatest`)
* **Navegación:** Jetpack Navigation Compose

---

## Estructura del Proyecto

```text
com.kmro.edunote
├── data
│   ├── database
│   │   ├── dao          # EduNoteDao, CalificacionDao, RefuerzoDao
│   │   ├── entity       # TareaEntity, UsuarioEntity, CalificacionEntity, RefuerzoEntity
│   │   └── EduNoteDatabase.kt & MigracionesEduNote.kt
│   ├── mapper           # Conversión entre entidades de Room y modelos de dominio
│   └── repository       # EduNoteRepositoryImpl
├── domain
│   ├── model            # Modelos puros de negocio (Tarea, Usuario)
│   └── repository       # Interfaces de repositorio
├── ui
│   ├── components       # Tarjetas de recordatorios, diálogos y barra de navegación
│   ├── data             # Recursos estáticos de apoyo académico (RefuerzosData)
│   ├── model            # Modelos de presentación (Tarea, ResumenAcademico, MateriaCalificacion)
│   ├── screens          # InicioScreen, TareasScreen, NotasScreen, PerfilScreen
│   └── EduNoteViewModel.kt
└── util                 # SessionManager, NotificadorRecordatorios
