# Notification Restorer App

Una aplicación Android moderna desarrollada en Kotlin con Material Design 3 que permite guardar y restaurar notificaciones persistentes.

## Características

✨ **Material Design 3**: Interfaz moderna con soporte para tema dinámico
🔔 **Captura de notificaciones**: Guarda automáticamente todas las notificaciones que recibes
🔄 **Restaurar notificaciones**: Vuelve a abrir notificaciones que cerraste accidentalmente
🔍 **Búsqueda y filtrado**: Encuentra fácilmente notificaciones específicas
📊 **Dos pestañas**: Separación clara entre notificaciones activas y cerradas
🎨 **Ordenamiento**: Ordena por tiempo (reciente/antiguo) o por nombre de app
🗑️ **Gestión**: Elimina notificaciones individuales o todas a la vez

## Requisitos

- Android 8.0 (API 26) o superior
- Kotlin 1.9.22
- Android Studio Hedgehog o superior

## Instalación

### Opción 1: Compilar con GitHub Actions (Sin PC) ⭐ RECOMENDADO

**¡Compila el APK gratis sin instalar nada!**

Si no tienes PC para compilar, usa GitHub Actions:

1. **Lee las instrucciones detalladas**: Abre el archivo `INSTRUCCIONES_GITHUB.md`
2. **Sube el proyecto a GitHub**: Crea un repositorio y sube los archivos
3. **GitHub compila automáticamente**: Espera 5-10 minutos
4. **Descarga el APK**: Ve a Actions → Artifacts → Descarga el APK
5. **Instala en tu Android**: Abre el APK descargado

✅ **100% Gratis**  
✅ **Sin instalar software**  
✅ **Funciona desde tu teléfono**  
✅ **Compilación automática en la nube**

**[Ver instrucciones completas en INSTRUCCIONES_GITHUB.md](INSTRUCCIONES_GITHUB.md)**

---

### Opción 2: Compilar localmente (Con PC)

1. Clona o descarga este proyecto
2. Abre el proyecto en Android Studio
3. Sincroniza los archivos Gradle
4. Ejecuta la aplicación en un emulador o dispositivo físico

## Configuración inicial

Al abrir la app por primera vez, necesitarás conceder el permiso de "Acceso a notificaciones":

1. La app mostrará una tarjeta de advertencia
2. Toca "Conceder permiso"
3. Serás redirigido a la configuración del sistema
4. Busca "Notification Restorer" en la lista
5. Activa el interruptor para permitir el acceso a notificaciones
6. Regresa a la app

## Uso

### Ver notificaciones

- **Pestaña "Activas"**: Muestra todas las notificaciones que actualmente están en tu panel de notificaciones
- **Pestaña "Cerradas"**: Muestra las notificaciones que has cerrado pero que se han guardado

### Restaurar una notificación

1. Ve a la pestaña "Cerradas"
2. Toca una notificación para abrirla directamente
3. O expande la notificación y toca el botón "Restaurar"

### Buscar notificaciones

1. Toca el icono de búsqueda en la barra superior
2. Escribe el nombre de la app, título o contenido de la notificación
3. Los resultados se filtrarán en tiempo real

### Ordenar notificaciones

1. Toca el icono de ordenamiento (↕️) en la barra superior
2. Selecciona:
   - **Más reciente**: Notificaciones nuevas primero
   - **Más antiguo**: Notificaciones antiguas primero
   - **Por app**: Ordenadas alfabéticamente por nombre de aplicación

### Eliminar notificaciones

- **Individual**: Expande una notificación y toca el icono de papelera
- **Todas**: Toca el icono de papelera en la barra superior y confirma

## Arquitectura

La aplicación sigue las mejores prácticas de Android:

- **MVVM** (Model-View-ViewModel)
- **Jetpack Compose** para la UI
- **Material 3** para el diseño
- **Kotlin Coroutines** para operaciones asíncronas
- **StateFlow** para la gestión de estado
- **NotificationListenerService** para capturar notificaciones

## Estructura del proyecto

```
app/
├── src/main/java/com/example/notificationrestorer/
│   ├── MainActivity.kt                 # Actividad principal
│   ├── NotificationRestorerApp.kt      # Clase Application
│   ├── model/
│   │   └── SavedNotification.kt        # Modelo de datos
│   ├── repository/
│   │   └── NotificationRepository.kt   # Gestión de datos
│   ├── service/
│   │   └── NotificationListenerService.kt  # Servicio de escucha
│   ├── ui/
│   │   ├── screen/
│   │   │   ├── MainScreen.kt           # Pantalla principal
│   │   │   └── NotificationList.kt     # Lista de notificaciones
│   │   ├── theme/
│   │   │   ├── Theme.kt                # Tema Material 3
│   │   │   └── Type.kt                 # Tipografía
│   │   └── viewmodel/
│   │       └── MainViewModel.kt        # ViewModel principal
```

## Permisos

La aplicación requiere los siguientes permisos:

- `POST_NOTIFICATIONS`: Para mostrar notificaciones (Android 13+)
- `FOREGROUND_SERVICE`: Para el servicio de escucha
- `BIND_NOTIFICATION_LISTENER_SERVICE`: Para acceder a las notificaciones del sistema

## Limitaciones

- La app guarda hasta 100 notificaciones por defecto
- No se guardan notificaciones del propio sistema Android
- Algunas apps pueden bloquear la restauración de notificaciones
- Los iconos de notificación pueden no estar disponibles para todas las apps

## Personalización

### Cambiar el límite de notificaciones guardadas

En `NotificationRepository.kt`, modifica:
```kotlin
private val maxNotifications = 100  // Cambia este valor
```

### Ignorar apps específicas

En `NotificationListenerService.kt`, en el método `shouldIgnoreNotification()`, añade:
```kotlin
if (sbn.packageName == "com.example.app") {
    return true
}
```

## Solución de problemas

### Las notificaciones no se guardan

1. Verifica que el permiso de acceso a notificaciones esté activado
2. Reinicia la app
3. Ve a Configuración → Apps → Notification Restorer → Permisos y verifica

### El servicio se detiene

- El sistema Android puede detener servicios en segundo plano para ahorrar batería
- Desactiva las optimizaciones de batería para esta app en la configuración del sistema

## Mejoras futuras

- [ ] Persistencia de datos con Room Database
- [ ] Notificaciones por categoría
- [ ] Exportar/importar notificaciones
- [ ] Estadísticas de uso
- [ ] Widget para acceso rápido
- [ ] Modo oscuro personalizable
- [ ] Soporte para acciones de notificación

## Licencia

Este proyecto es de código abierto y está disponible para uso educativo y personal.

## Contribuciones

Las contribuciones son bienvenidas. Por favor, abre un issue o pull request para sugerencias o mejoras.

## Autor

Desarrollado con ❤️ usando Kotlin y Jetpack Compose

