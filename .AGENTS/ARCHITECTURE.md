# Arquitectura Técnica - CanastaList

El proyecto sigue el patrón de diseño **MVVM (Model-View-ViewModel)**, que es el estándar recomendado por Google para aplicaciones Android robustas y testables.

## Capas de la Aplicación

### 1. Capa de Datos (Data Layer)
- **Room Database**: Actúa como la "Single Source of Truth". Todos los datos se leen y escriben primero en la base de datos local (SQLite).
- **Firebase Firestore**: Actúa como el espejo en la nube. Provee sincronización bidireccional y persistencia remota.
- **Data Access Object (DAO)**: Define las consultas SQL y los flujos de datos reactivos (Flow/StateFlow).

### 2. Capa de Lógica (ViewModel Layer)
- **ShoppingViewModel**: El cerebro de la app. Gestiona el estado de la UI y coordina la sincronización.
    - Inicia una escucha activa (`SnapshotListener`) a Firebase al seleccionar un grupo.
    - Maneja la lógica de generación de códigos de grupo.
    - Programa alarmas en el sistema Android.

### 3. Capa de Interfaz (UI Layer)
- **Jetpack Compose**: Declarativa y moderna.
- **Scaffold & Material 3**: Provee la estructura visual (barras superiores, menús, botones flotantes).
- **LazyColumn**: Optimización de listas largas con animaciones de ítems.

## Flujo de Sincronización (Offline-First)
1. El usuario realiza una acción (ej: añade "Leche").
2. El ViewModel guarda el cambio en **Room** inmediatamente (La UI se actualiza al instante).
3. Simultáneamente, se lanza una corrutina para subir el cambio a **Firebase Firestore**.
4. Si no hay internet, Firebase guarda el cambio en su caché interna y lo sube automáticamente al recuperar conexión.
5. Los otros dispositivos escuchan el cambio de Firestore y actualizan su Room local.
