# Guía de Funciones y Módulos - CanastaList

Este documento detalla las funciones técnicas ("Funciones m") implementadas en el código.

## Módulo de Compras (`ShoppingViewModel.kt`)
- `addItem(name: String)`: Inyecta el `userId`, `authorName` y `groupId` actual para crear un registro completo tanto en Room como en Firestore.
- `toggleItem(item: ShoppingItem)`: Cambia el estado `isChecked`. Utiliza actualizaciones atómicas en la nube para evitar conflictos.
- `removeItem(item: ShoppingItem)`: Elimina el registro físicamente de la base de datos local y remota.

## Módulo de Grupos y Colaboración
- `createGroup(name: String)`: Genera una clave alfanumérica de 8 caracteres. Crea una colección en Firestore para albergar los productos de ese grupo.
- `joinGroup(code: String)`: Valida la existencia de un código en el servidor y descarga el historial de productos de ese grupo a la base local del nuevo miembro.
- `switchGroup(groupId: String?)`: Cambia el filtrado de la base de datos para mostrar solo los productos vinculados a ese ID.

## Módulo de Notificaciones y Recordatorios
- `setReminder(context, item, timestamp)`:
    1. Guarda el tiempo en la base de datos.
    2. Usa `AlarmManager` con `setExactAndAllowWhileIdle` para garantizar que la notificación suene incluso en modo de ahorro de energía.
- `ReminderReceiver.kt`: Un `BroadcastReceiver` que construye la notificación usando un `NotificationChannel` de alta importancia y le asigna el logo personalizado del proyecto.

## Módulo de Perfil
- `setUserName(name: String)`: Persiste el nombre del usuario en la tabla de preferencias, asegurando que todas las acciones futuras lleven su firma.
