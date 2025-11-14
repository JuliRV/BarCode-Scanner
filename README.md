# Barcode Scanner App 📱

[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.0-purple.svg)](https://kotlinlang.org)
[![Android](https://img.shields.io/badge/Android-API%2024+-green.svg)](https://developer.android.com)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

> **Proyecto de prácticas para Minsait** - Aplicación Android de escaneo de códigos de barras desarrollada como parte del programa de formación en tecnologías móviles.

## 📋 Descripción

Esta aplicación de escaneo de códigos de barras fue desarrollada como proyecto de prácticas para **Minsait**, con el objetivo de aprender y aplicar las mejores prácticas en desarrollo Android moderno. El proyecto abarca desde conceptos fundamentales de arquitectura hasta integración de librerías avanzadas y testing completo.

### ✨ Características principales

- 📸 **Escaneo en tiempo real** con ML Kit de Google
- 🎯 **Detección visual** con cuadros de seguimiento en verde
- 💾 **Historial persistente** de códigos escaneados
- 🔄 **Dos modos de escaneo**: Simple y Avanzado
- ⚡ **WorkManager** para tareas en segundo plano (limpieza automática, backups, sincronización)
- 🧪 **Cobertura de testing** completa con informes JaCoCo
- 🏗️ **Arquitectura modular** con separación de responsabilidades

---

## 🏗️ Arquitectura y Patrones

Este proyecto fue desarrollado siguiendo las directrices de prácticas de Minsait, implementando:

### Arquitectura MVVM + Clean Architecture

```
app/
├── core/               # Navegación y configuración base
├── features/
│   └── barcodehistory/
│       ├── data/       # Capa de datos (Room, DAOs, Entities)
│       ├── domain/     # Lógica de negocio (UseCases, Repository)
│       └── presentation/ # UI (ViewModels, Composables)
|        ...
└── di/                # Inyección de dependencias
...

scanner/               # Módulo independiente de escaneo
├── data/
├── domain/
└── ui/
...
```

### 🔧 Tecnologías implementadas

#### Inyección de Dependencias
- **Dagger Hilt** - Gestión de dependencias siguiendo el patrón DI

#### Persistencia de Datos
- **Room Database** - Base de datos local SQLite
- **TypeConverters** para objetos complejos (Date)
- **Flow** para observación reactiva de datos

#### Escaneo de Códigos
- **ML Kit Barcode Scanning** - Detección y lectura de códigos
- **CameraX** - Gestión moderna de la cámara
- **Canvas Drawing** - Dibujado de cuadros delimitadores en tiempo real

#### Tareas en Segundo Plano
- **WorkManager** - Tareas persistentes en segundo plano
- **Hilt Worker** - Inyección de dependencias en Workers
- **Constraints API** - Optimización de batería y red

#### Testing
- **JUnit 4** - Tests unitarios
- **MockK** - Mocking para pruebas
- **Robolectric** - Testing de Workers con contexto Android
- **JaCoCo** - Informes de cobertura de código
- **Espresso** - Tests de UI instrumentados
- **Compose Testing** - Testing de componentes Jetpack Compose

---

## 📱 Funcionalidades

### 🏠 Pantalla Principal
Menú de navegación con acceso a:
- Scanner de códigos
- Historial de escaneos

### 📸 Scanner (Modo Simple)
- Lista en tiempo real de códigos detectados
- Guardado automático en base de datos
- Visualización de formato del código

### 🎯 Scanner (Modo Avanzado)
- Detección individual con enfoque
- Cuadro verde de seguimiento visual
- Botón de eliminación manual
- Delay de 1.5s tras eliminar para evitar re-escaneos

### 📋 Historial
- Lista completa de códigos escaneados
- Fecha y hora de cada escaneo
- Eliminación individual o masiva
- Estado vacío con mensaje informativo

### ⚡ WorkManager - Tareas en Segundo Plano

La app implementa WorkManager para gestionar tareas persistentes en segundo plano:

#### 🧹 Limpieza Automática (Periódica - 24h)
- Elimina códigos con más de 30 días de antigüedad
- Se ejecuta solo cuando el dispositivo está cargando
- Requiere conexión a red y batería no baja
- Optimiza el almacenamiento automáticamente

#### 💾 Backup Automático (Periódico - 7 días)
- Exporta el historial completo a JSON
- Solo se ejecuta con WiFi disponible
- Mantiene los últimos 5 backups
- Guardado en almacenamiento interno de la app

#### 🔄 Sincronización con Servidor (Manual/On-demand)
- Sincroniza códigos locales con servidor remoto
- Implementación mock preparada para backend real
- Reporta progreso y resultados en tiempo real
- Requiere conexión WiFi

**📖 Documentación completa**: Ver `WORKMANAGER_GUIDE.md` para detalles de implementación y uso.

---

## 🧪 Testing y Calidad

### Cobertura de Tests

El proyecto incluye testing en múltiples niveles según los requisitos de las prácticas:

#### Tests Unitarios
```kotlin
// ViewModels
- BarcodeHistoryViewModelTest
- ScannerViewModelTest
- AppScannerViewModelTest

// UseCases
- GetBarcodesUseCaseTest
- SaveBarcodeUseCaseTest
- DeleteBarcodeUseCaseTest
- ClearHistoryUseCaseTest

// Repository
- BarcodeRepositoryTest

// Workers (WorkManager)
- CleanupOldBarcodesWorkerTest
- BackupBarcodesWorkerTest
- SyncBarcodesToServerWorkerTest

...
```

#### Tests Instrumentados
```kotlin
// UI Tests
- ScannerScreenTest
- BarcodeHistoryScreenTest
- NavigationTest
...
```

### 📊 JaCoCo - Informes de Cobertura

Para generar el informe de cobertura:

```bash
./gradlew jacocoTestReport
```

Los informes HTML se generan en:
```
build/reports/jacoco/jacocoTestReport/html/index.html
```

---

## 🚀 Instalación y Ejecución

### Prerrequisitos

- Android Studio Narwhal | 2025.1.1 o superior
- JDK 17
- Android SDK API 24+
- Gradle 8.7

### Configuración

1. **Clonar el repositorio**
```bash
git clone https://github.com/JuliRV/BarCode-Scanner.git
cd BarCode-Scanner
```

2. **Abrir en Android Studio**
```
File > Open > Seleccionar carpeta del proyecto
```

3. **Sincronizar Gradle**
```
Sync Now cuando aparezca el banner
```

4. **Ejecutar la aplicación**
```
Run > Run 'app'
```

### Ejecutar Tests

```bash
# Tests unitarios
./gradlew test

# Tests instrumentados
./gradlew connectedAndroidTest

# Cobertura completa
./gradlew jacocoTestReport
```

---

## 📦 Dependencias Principales

```kotlin
// Jetpack Compose
implementation("androidx.compose.ui:ui")
implementation("androidx.compose.material3:material3")

// Hilt
implementation("com.google.dagger:hilt-android")
kapt("com.google.dagger:hilt-compiler")

// Room
implementation("androidx.room:room-runtime")
implementation("androidx.room:room-ktx")
kapt("androidx.room:room-compiler")

// ML Kit
implementation("com.google.mlkit:barcode-scanning")

// CameraX
implementation("androidx.camera:camera-camera2")
implementation("androidx.camera:camera-lifecycle")
implementation("androidx.camera:camera-view")

// WorkManager
implementation("androidx.work:work-runtime-ktx")
implementation("androidx.hilt:hilt-work")

// Testing
testImplementation("junit:junit")
testImplementation("io.mockk:mockk")
testImplementation("org.robolectric:robolectric")
testImplementation("androidx.work:work-testing")
androidTestImplementation("androidx.test.espresso:espresso-core")
androidTestImplementation("androidx.compose.ui:ui-test-junit4")
```

---

## 📚 Aprendizajes del Proyecto

Este proyecto de prácticas permitió trabajar con:

### 🎯 Conceptos Fundamentales
- ✅ MVVM (Model-View-ViewModel)
- ✅ Clean Architecture
- ✅ Inyección de Dependencias con Dagger Hilt
- ✅ Persistencia con Room Database
- ✅ Coroutines y Flow para programación asíncrona

### 🔬 ML Kit y Cámara
- ✅ Integración de ML Kit Barcode Scanning
- ✅ Gestión de permisos de cámara
- ✅ CameraX para preview y análisis
- ✅ Dibujado de bounding boxes con Canvas

### 🧪 Testing Avanzado
- ✅ Tests unitarios con JUnit y MockK
- ✅ Tests instrumentados con Espresso
- ✅ Compose Testing Library
- ✅ JaCoCo para métricas de cobertura
- ✅ Estrategias de mocking y testing de ViewModels

### 🏗️ Arquitectura Modular
- ✅ Separación en módulos (`app` y `scanner`)
- ✅ Patrón Repository
- ✅ Use Cases para lógica de negocio
- ✅ Jetpack Compose para UI declarativa

### ⚡ Tareas en Segundo Plano
- ✅ WorkManager para tareas persistentes
- ✅ Workers periódicos y únicos (OneTime)
- ✅ Constraints para optimización de batería y red
- ✅ Integración de HiltWorkerFactory
- ✅ Testing de Workers con WorkManagerTestInitHelper

---

## 👨‍💻 Autor

**Julián Regueira Vilas**
- GitHub: [@JuliRV](https://github.com/JuliRV)
- Proyecto desarrollado durante prácticas en **Minsait**

---

## 📄 Licencia

Este proyecto fue desarrollado con fines educativos como parte del programa de prácticas de Minsait.

---

## 🙏 Agradecimientos

- **Minsait** por la oportunidad de prácticas y la guía en el desarrollo y sobretodo agradecer al equipo que tuve formado por Angel y Jose
- **Google ML Kit** por las herramientas de escaneo de códigos
- Comunidad de Android por la documentación y recursos

---

## 📝 Notas de Desarrollo

### Ramas
- `main` - Versión estable de producción
- `develop` - Rama de desarrollo activa

### Estado del Proyecto
🟢 **Activo** - En desarrollo como parte de las prácticas

---

**¿Preguntas o sugerencias?** Abre un issue en el repositorio.
