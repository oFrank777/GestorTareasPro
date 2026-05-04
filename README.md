# 🎯 Gestor de Tareas y Hábitos

![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin-0095D5?&style=for-the-badge&logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack_Compose-4285F4?style=for-the-badge&logo=android&logoColor=white)
![Room](https://img.shields.io/badge/Room_Database-003B57?style=for-the-badge)

Una aplicación Android nativa, robusta e interactiva para la gestión integral de rutinas, productividad y seguimiento de metas. Diseñada meticulosamente para mantener a los usuarios enfocados mediante un seguimiento detallado de tareas pendientes y la formación de hábitos a largo plazo utilizando mecánicas de recompensa (rachas) y una rica retroalimentación visual del progreso.

---

## 🎓 Objetivo del Proyecto

Este proyecto nace con un doble propósito:
1. **Aplicación Real y Escalable:** Proporcionar una herramienta verdaderamente útil, libre de distracciones y offline-first, que aplique los conceptos más avanzados de UX/UI en aplicaciones móviles.
2. **Propósito Académico:** Desarrollado como proyecto principal para el curso de **Desarrollo Avanzado en Nuevas Plataformas**, demostrando dominio técnico en Arquitectura de Software, Concurrencia, Manejo de Estado Reactivo y Persistencia Local bajo los más estrictos estándares recomendados por Google.

---

## 🚀 Características Principales

- **✅ Gestión Avanzada de Tareas:** Módulo dinámico para la creación, edición, filtrado y marcado de tareas diarias. Incorpora un indicador circular que calcula el porcentaje de productividad del día en tiempo real.
- **🔄 Seguimiento de Hábitos:** Subsistema enfocado en la persistencia a largo plazo. Permite crear hábitos categorizados con soporte para iconografía descriptiva.
- **🔥 Sistema Dinámico de Rachas (Streaks):** Algoritmo inteligente que evalúa la constancia ininterrumpida. Fomenta la retención del usuario celebrando tanto la "Racha Actual" como la "Mejor Racha Histórica".
- **📊 Calendario Interactivo:** Panel de control (Dashboard) mensual reactivo que colorea semánticamente los días dependiendo de la tasa de éxito general del usuario.
- **💾 Persistencia Robusta Local:** La aplicación es 100% independiente de conexiones a internet. Garantiza un acceso a datos ultrarrápido y seguro.
- **🔀 Navegación Declarativa Multivista:** Transiciones de interfaz ininterrumpidas gestionando correctamete el *Back Stack* y el ciclo de vida de los estados.
- **🎨 UI / UX de Vanguardia:** Interfaz modelada bajo los estándares de **Material Design 3**, adaptabilidad fluida al **Modo Oscuro**, soporte Edge-to-Edge y micro-animaciones inmersivas.

---

## 🏗️ Arquitectura del Sistema

La aplicación fue diseñada siguiendo el paradigma **Clean Architecture** estructurado mediante el patrón **MVVM (Model-View-ViewModel)**. 

### Separación de Capas
1. **Capa de Presentación (UI + ViewModel):** 
   - **UI (Jetpack Compose):** Funciones composables 100% declarativas. Sin referencias directas al contexto lógico.
   - **ViewModel:** Encargado de transformar los flujos de la capa de dominio en un estado inmutable (`StateFlow`) que la UI pueda consumir.
2. **Capa de Dominio:** Alberga los modelos puros (Entities lógicas como `Habito`, `Tarea`) y las interfaces de los repositorios. Es completamente agnóstica de frameworks de Android.
3. **Capa de Datos:** Implementaciones concretas (`Room`, `DataStore`). Realiza las operaciones transaccionales reales.

### Unidirectional Data Flow (UDF)
El estado fluye *hacia abajo* (del ViewModel a la UI) y los eventos fluyen *hacia arriba* (de la UI al ViewModel). La UI jamás modifica directamente un dato.

### 🔄 Flujo Completo de Datos
1. **Acción:** El usuario interactúa con la vista (ej. "Completar Hábito").
2. **Evento:** La vista delega la acción al `ViewModel`.
3. **Petición:** El `ViewModel` ejecuta una corrutina y llama al `Repository`.
4. **Mutación:** El `Repository` realiza un UPDATE/INSERT en la Base de Datos (`Room`).
5. **Reacción:** `Room` detecta el cambio e internamente emite un nuevo `Flow`.
6. **Transformación:** El `ViewModel` captura este flujo, recalcula métricas complejas y actualiza el `StateFlow` unificado.
7. **Renderizado:** Jetpack Compose, al estar suscrito al `StateFlow`, re-renderiza eficientemente únicamente los nodos de UI afectados.

---

## 📱 Flujo Completo de la Aplicación

1. **Pantalla Splash:** Actúa como punto de validación. Intercepta el flujo del `DataStore` para verificar asíncronamente si ya existe una sesión iniciada.
2. **Login:** Pantalla inicial para capturar la identidad del usuario y personalizar la experiencia. Solo se muestra si el Splash dicta la ausencia de datos.
3. **Home Dashboard (Pestañas Animadas):**
   - **Tab Tareas:** Lista de tareas efémeras (del día). Permite inserción rápida y feedback de progreso.
   - **Tab Hábitos:** Centro de operaciones de progreso a largo plazo. Integra un resumen gráfico de hoy y el Calendario Histórico General.
4. **Detalle de Hábito:** Accesible al pulsar sobre la información de un hábito. Aísla estadísticamente a una sola rutina, mostrando su consistencia individual mediante un calendario focalizado.
5. **Navegación Intuitiva:** El diseño del *Back Stack* permite que volver desde cualquier "Detalle" aterrice siempre lógicamente en la pestaña "Hábitos" sin reconstrucciones forzadas, respetando el modelo mental del usuario.

---

## 📅 Sistema de Hábitos al Detalle

El motor principal de la retención de la app.
- **Creación:** Se asocia un Hábito a una `fechaCreacion` (Timestamp en Unix).
- **Historial Transaccional:** Cada "Check" de un hábito no incrementa un contador genérico, sino que **inserta un registro en una tabla transaccional** relacionando `habitoId` + `fecha`.
- **Lógica Histórica por Fechas:** Esta arquitectura garantiza que al consultar días pasados, el sistema sepa exactamente con qué rutinas interactuó el usuario de manera inmutable.

### 🔥 Sistema Avanzado de Rachas (Streaks)

La algoritmia de rachas está diseñada a nivel producción para evitar desajustes:
- **Algoritmo de Rastreo:** El repositorio obtiene la lista cronológica descendente de fechas en las que un hábito específico fue completado. 
- **Punto de Anclaje:** Revisa "hoy". Si "hoy" está completo, ancla el contador aquí. Si "hoy" falta, verifica "ayer". Si "ayer" está completo, la racha sobrevive a la espera del usuario en el presente.
- **Prevención de Errores Históricos:** Ignora hábitos creados *después* de la fecha evaluada, impidiendo que la creación de metas en el presente contamine negativamente los porcentajes de éxito del pasado (falso negativo histórico).

---

## 📊 Calendario Interactivo

Un panel matricial generado íntegramente con Compose. Renderiza un grid reactivo del mes actual o pasados.
- **Indicadores Semánticos (Estados):**
  - 🟢 **Completo (Verde):** El usuario completó el 100% de sus compromisos vigentes ese día.
  - 🟡 **Parcial (Ámbar):** Hubo actividad, pero quedaron hábitos sin marcar.
  - 🔴 **Fallado (Rojo):** Inactividad absoluta frente a metas existentes.
  - ⚪ **Inactivo/Futuro (Gris/Transparente):** Sin historial evaluable.
- **Interacción Profunda:** Al tocar un día de la matriz, el sistema re-evalúa el `StateFlow` y despliega en tiempo real una tarjeta inferior resumiendo las victorias y derrotas exactas de ese día.

---

## 💾 Subsistemas de Persistencia

### Base de Datos Relacional (`Room`)
- **Estructura Normalizada:** 
  - `TareaEntity`: Mapeo simple de estados pendientes.
  - `HabitoEntidad`: Metadatos del hábito (icono, categoría, mejor racha absoluta).
  - `HistorialHabitoEntidad`: Tabla transaccional (Registro diario de éxito).
- **Reactividad Nativa:** Se utilizan interfaces `DAO` que retornan instancias nativas de `Flow`, conectando la capa SQLite al framework asíncrono de Kotlin sin intermediarios.

### Preferencias Clave-Valor (`DataStore`)
- Sustituto moderno de `SharedPreferences`. Empleado de manera asíncrona (libre de hilos principales bloqueados) para persistir el nombre del usuario, bandera booleana de `sesionIniciada` y el `temaOscuro`.

---

## 🔄 Navegación (Navigation Compose)

Enfoque Single-Activity donde las "pantallas" son funciones composables montadas y desmontadas dinámicamente.
- **Sealed Classes (`Rutas`):** Restringe las URLs internas en un contrato estricto en tiempo de compilación. Elimina errores por cadenas de texto sueltas.
- **Paso de parámetros:** Navegación hacia `Detalles` adjuntando y recuperando el `habitoId` nativamente desde la entrada (arguments) del `NavHost`.
- **Manejo de la Pila (Back Stack):** Se utilizan comandos como `popUpTo` con `inclusive = true` en el Login/Splash para evitar que el usuario vuelva a pantallas de inicio cerradas oprimiendo el botón atrás.

---

## 🎨 UI / UX 

- **Material Design 3:** Adopción de colores de esquema extraídos programáticamente (`colorScheme.primaryContainer`, `surfaceVariant`, etc.), garantizando legibilidad suprema.
- **Animaciones Declarativas:** Uso de `AnimatedContent` para realizar fundidos cruzados (*Crossfades*) al cambiar entre la pestaña Tareas/Hábitos o modificar filtros, otorgando una sensación inmensamente fluida.
- **Feedback Sensorial:** Componentes como `FilterChip` y `CircularProgressIndicator` se actualizan reactivamente, satisfaciendo interacciones instantáneas.

---

## 🧪 Tecnologías Utilizadas

| Categoría | Tecnología / Biblioteca |
| :--- | :--- |
| **Lenguaje Base** | Kotlin Moderno |
| **Interfaz UI** | Jetpack Compose (Material 3) |
| **Arquitectura** | Clean Architecture + MVVM |
| **Asincronía & Flujos**| Coroutines + Flow / StateFlow / flatMapLatest |
| **Persistencia SQL**| Room Database (`androidx.room`) |
| **Preferencias** | Preferences DataStore |
| **Navegación** | Jetpack Navigation Compose |
| **IDE** | Android Studio Ladybug / Gradle KTS |

---

## ⚙️ Guía de Instalación

1. Clona este repositorio en tu máquina local:
   ```bash
   git clone https://github.com/oFrank777/GestorTareasPro
   ```
2. Abre **Android Studio** y selecciona `Open` -> Busca la carpeta clonada.
3. Espera a que **Gradle** indexe el proyecto y descargue las dependencias requeridas (asegúrate de tener una conexión estable).
4. Configura un emulador con **API 26 o superior**, o conecta un dispositivo físico mediante depuración USB.
5. Ejecuta la aplicación desde el IDE presionando `Shift + F10` o el botón **Run**.

---

## 🧠 Decisiones Técnicas Relevantes

- **Elección de MVVM sobre MVI:** Dado que la aplicación mantiene ciclos de validación relativamente aislados, MVVM garantizó un balance perfecto entre código limpio (*Boilerplate* mínimo) e Inmutabilidad de estado, sin añadir la complejidad que exige MVI en el manejo de intenciones de evento masivo.
- **Elección de Room + Flow:** Rompe la necesidad tradicional de efectuar *Queries* manuales en los ciclos de vida (`onResume`). La base de datos es Reactiva; al inyectar un nuevo hábito el estado viaja automáticamente desde el almacenamiento binario hasta el último pixel de la UI.
- **Decisión de Migrar a Jetpack Compose:** Reducción del código de UI en un 60% al desprenderse del antiguo sistema de XML e inflado de vistas. Facilita la creación paramétrica y reutilizable de componentes atómicos como `BarraFiltros` o `Tarjetas`.

---

## 🚧 Roadmap y Mejoras Futuras

- [ ] **WorkManager para Tareas de Fondo:** Sincronizar un proceso en segundo plano que dispare Notificaciones Push nativas recordando realizar un hábito a horas programadas.
- [ ] **Respaldo en la Nube:** Migración de datos híbrida vinculando Room Database con `Firebase Cloud Firestore` o un servicio REST API, protegiendo al usuario ante desinstalaciones.
- [ ] **Widget Pantalla de Inicio (Glance):** Integración de `Jetpack Glance` para ofrecer visualización e interacción de tareas sin tener que abrir activamente la aplicación.
- [ ] **Métricas Visuales Avanzadas:** Soporte de gráficos de barras lineales evaluando patrones de rendimiento semanales o mensuales.

---

## 👨‍💻 Autor

**Ower Frank López Arela**  
*Estudiante de Ingeniería de Sistemas*  
*Curso: Desarrollo Avanzado en Nuevas Plataformas*  

Con pasión por el Software Clean, las Arquitecturas Reactivas y el Diseño Centrado en el Usuario (UX).  
[LinkedIn](https://www.linkedin.com/in/owerfrank-data/) • [GitHub](https://github.com/oFrank777)

---

## 📄 Licencia

Este proyecto es estrictamente para fines académicos y portafolio. Las libertades de uso de componentes y patrones recaen bajo las licencias de sus correspondientes ecosistemas Open Source (Apache 2.0).
