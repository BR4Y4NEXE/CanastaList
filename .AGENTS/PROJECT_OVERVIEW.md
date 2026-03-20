# CanastaList - Visión General del Proyecto

## ¿Qué es CanastaList?
**CanastaList** es una aplicación móvil nativa para Android diseñada como una herramienta colaborativa de lista de compras. Su propósito principal es facilitar la gestión de suministros del hogar entre múltiples usuarios en tiempo real, manteniendo una estética minimalista y una funcionalidad fluida.

## Objetivos del Proyecto
- **Colaboración Eficiente**: Permitir que familias o grupos compartan una lista común.
- **Experiencia Offline-First**: La app funciona instantáneamente sin internet y se sincroniza cuando hay conexión.
- **Interactividad Moderna**: Uso de gestos, animaciones y notificaciones del sistema.
- **Portafolio Senior**: Demostrar el uso de las tecnologías más demandadas en el ecosistema Android actual.

## Características Principales
1. **Gestión de Ítems**: Añadir, tachar y eliminar productos con gestos de deslizamiento (Swipe-to-dismiss).
2. **Grupos Familiares**: Creación de grupos con códigos únicos aleatorios (ej: AB12-CD34) para invitar a otros miembros.
3. **Sincronización en Tiempo Real**: Los cambios realizados en un dispositivo se reflejan en todos los demás miembros del grupo instantáneamente mediante Firebase Firestore.
4. **Recordatorios Inteligentes**: Programación de fecha y hora para productos, con notificaciones locales mediante AlarmManager.
5. **Identidad de Usuario**: Cada ítem muestra quién lo añadió, permitiendo una trazabilidad clara en grupos grandes.
6. **Diseño Minimalista**: Interfaz limpia basada en Material 3 con soporte para responsividad y scroll infinito.
