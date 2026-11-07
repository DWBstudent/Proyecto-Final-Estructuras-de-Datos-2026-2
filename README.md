# Juego de cartas

Juego de cartas 2D inspirado en *Yu-Gi-Oh! Forbidden Memories* (1999), hecho en **Java 25 + JavaFX** para el proyecto final de **Estructuras de Datos** (Universidad El Bosque, 2026-2).

Todas las estructuras del modelo (pilas, colas, listas, tablas hash, árboles, montículos y grafos) son **propias**, con nodos y referencias. No se usan `java.util.LinkedList`, `Stack`, `Queue`, `HashMap` ni `Collections.sort`. El proyecto sigue la arquitectura **MVC**.

---

## Contenido

1. [Equipo](#1-equipo)
2. [Qué hay que instalar](#2-qué-hay-que-instalar)
3. [Clonar el repositorio](#3-clonar-el-repositorio)
4. [Abrir el proyecto en Eclipse](#4-abrir-el-proyecto-en-eclipse)
5. [Correr las pruebas](#5-correr-las-pruebas)
6. [Correr el juego](#6-correr-el-juego)
7. [Cómo está organizado el código](#7-cómo-está-organizado-el-código)
8. [Cómo trabajamos con Git (paso a paso)](#8-cómo-trabajamos-con-git-paso-a-paso)
9. [Reglas del equipo](#9-reglas-del-equipo)
10. [Hitos](#10-hitos)
11. [Problemas comunes y cómo resolverlos](#11-problemas-comunes-y-cómo-resolverlos)

---

## 1. Equipo

| Persona | Rol | A cargo de |
| --- | --- | --- |
| **Daniel** | Líder: GitHub, contratos, integración y revisión de pull requests | `Pila`, `Cola`, `Carta`, `Jugador`, catálogo de 100 cartas, contraseñas, guardado de partida, menú, navegación y música |
| **Brayan** | Colección de cartas | `ListaDoble`, `TablaHash`, `ArbolAVL`, Merge Sort, Insertion Sort, los 7 comparadores, `Chest`, `Deck`, deshacer/rehacer; pantallas de Chest, Deck, detalle de carta e inventario |
| **Santiago** | Duelo, campaña e inventario | `ColaCircular`, `MonticuloMax`, barajado, `Grafo` con BFS, `Inventario`, `Duelo`, IA de los 3 rivales, campaña, PvP; pantallas de selección, duelo y resultado |

El plan completo, con las tareas de cada día, está en el documento **"Juego de cartas — Ruta de desarrollo"** que compartió Daniel.

---

## 2. Qué hay que instalar

Necesitas **tres programas**. Si ya tienes alguno, revisa que la versión sea la correcta.

### 2.1 Git

Sirve para descargar el repositorio y subir tus cambios.

1. Descárgalo de **https://git-scm.com/downloads** (Windows).
2. Instálalo con las opciones por defecto (siguiente, siguiente…).
3. Comprueba que quedó instalado: abre **Git Bash** (búscalo en el menú de Windows) y escribe:
   ```bash
   git --version
   ```
   Debe responder algo como `git version 2.xx`.
4. Dile a Git quién eres (una sola vez por computador, usa el correo de tu cuenta de GitHub):
   ```bash
   git config --global user.name "Tu Nombre"
   git config --global user.email "tu-correo@ejemplo.com"
   ```
   > Si estás en un computador prestado, hazlo **sin** `--global` y **dentro** de la carpeta del repo, para no dejar tu nombre en ese computador.

### 2.2 JDK 25 (Java)

El proyecto **solo compila con Java 25**. Con Java 21 o 17 sale error.

1. Entra a **https://adoptium.net/temurin/releases**.
2. Elige: **Operating System** = Windows, **Architecture** = x64, **Package Type** = JDK, **Version** = **25 - LTS**.
3. Descarga el **.msi** y ejecútalo.
4. En la pantalla de opciones del instalador, activa **"Set JAVA_HOME variable"** y **"Add to PATH"** (clic en el ícono de cada opción → "Will be installed on local hard drive").
5. Cierra y vuelve a abrir la terminal, y comprueba:
   ```bash
   java -version
   ```
   Debe decir `openjdk version "25..."`. Si dice otra versión, revisa la sección [Problemas comunes](#11-problemas-comunes-y-cómo-resolverlos).

> **¿No tienes permisos de administrador?** Descarga el **.zip** en vez del .msi, descomprímelo en tu carpeta de usuario y en Eclipse apunta a esa carpeta (paso 4.1).

### 2.3 Eclipse IDE for Java Developers

1. Descárgalo de **https://www.eclipse.org/downloads/packages/** → **Eclipse IDE for Java Developers**, versión **2026-09** (o más reciente).
2. Descomprime o instala, y ábrelo.
3. Cuando pregunte por el *workspace*, elige una carpeta **distinta** a la del repositorio (por ejemplo `C:\Users\TuUsuario\eclipse-workspace`).

Eclipse ya trae todo lo demás que se necesita: **Maven** (descarga JavaFX y JUnit solo), soporte de **JUnit** y soporte de **Git**. **No hay que instalar JavaFX a mano.**

---

## 3. Clonar el repositorio

1. **Acepta la invitación** al repositorio. Te llegó por correo o la ves en **https://github.com/notifications**. Sin aceptarla no puedes subir cambios.
2. Abre **Git Bash** y ve a la carpeta donde quieres guardar el proyecto, por ejemplo:
   ```bash
   cd ~/Documents
   ```
3. Clona con **HTTPS**:
   ```bash
   git clone https://github.com/DWBstudent/Proyecto-Final-Estructuras-de-Datos-2026-2.git
   cd Proyecto-Final-Estructuras-de-Datos-2026-2
   ```
4. La primera vez que hagas `git push`, se abrirá el navegador para iniciar sesión en GitHub. Complétalo y vuelve a la terminal.

> Si te sale `Permission denied (publickey)`, usaste la dirección `git@github.com:...` (SSH). Usa la de **HTTPS** que está arriba.

---

## 4. Abrir el proyecto en Eclipse

### 4.1 Decirle a Eclipse que use Java 25 (una sola vez)

1. `Window → Preferences → Java → Installed JREs`.
2. Si en la lista **no** aparece un JDK 25: clic en **Add… → Standard VM → Next → Directory…** y elige la carpeta donde se instaló el JDK (normalmente `C:\Program Files\Eclipse Adoptium\jdk-25...`). **Finish**.
3. Marca la casilla del **JDK 25** para que sea el predeterminado. **Apply and Close**.

### 4.2 Importar el proyecto

1. `File → Import…`
2. Abre la carpeta **Maven** → elige **Existing Maven Projects** → **Next**.
3. En **Root Directory**, clic en **Browse…** y elige la carpeta del repositorio (la que tiene el archivo `pom.xml`).
4. Debe aparecer marcado `/pom.xml`. Clic en **Finish**.
5. Espera a que termine la barra de progreso de abajo a la derecha. **La primera vez descarga JavaFX y JUnit; puede tardar uno o dos minutos y necesita internet.**

El proyecto queda en el **Package Explorer** (panel de la izquierda) con el nombre `juego-de-cartas`. No debe tener una **X roja**. Si la tiene, revisa [Problemas comunes](#11-problemas-comunes-y-cómo-resolverlos).

---

## 5. Correr las pruebas

**Desde Eclipse:** clic derecho sobre el proyecto `juego-de-cartas` → **Run As → Maven test**.

En la consola de abajo, al final, debe salir:

```
Tests run: 33, Failures: 0, Errors: 0, Skipped: 0
...
BUILD SUCCESS
```

(El número de pruebas sube a medida que cada uno agrega las suyas.)

**Para correr solo una clase de pruebas** (más rápido mientras programas): abre el archivo, por ejemplo `PilaTest.java`, clic derecho dentro del código → **Run As → JUnit Test**. Se abre una pestaña JUnit con una barra **verde** si todo pasa o **roja** si algo falla.

**Desde la terminal** (solo si tienes Maven instalado aparte): `mvn test`.

---

## 6. Correr el juego

**Opción A (recomendada):**

1. Clic derecho sobre el proyecto → **Run As → Maven build…** (la que tiene los tres puntos).
2. En **Goals** escribe: `javafx:run`
3. En **Name** (arriba) escribe: `Correr juego`. Así queda guardado.
4. Clic en **Run**.

Debe abrirse una ventana que dice **"Juego de cartas"**. Las siguientes veces, usa el botón verde de *Run* (▶) de la barra superior → `Correr juego`.

**Opción B:** abre `src/main/java/co/edu/unbosque/Main.java`, clic derecho → **Run As → Java Application**. También funciona; si en la consola sale el aviso `Unsupported JavaFX configuration`, es normal y se puede ignorar.

---

## 7. Cómo está organizado el código

```
pom.xml                         ← configuración de Maven (Java 25, JavaFX, JUnit)
src/main/java/co/edu/unbosque/
├── Main.java                   ← solo arranca la aplicación
├── modelo/                     ← TODA la lógica del juego. Sin JavaFX y sin java.util
│   ├── estructuras/            ← Pila, Cola, ListaDoble, TablaHash, ArbolAVL, MonticuloMax, Grafo
│   ├── ordenamiento/           ← Comparador, MergeSort, InsertionSort y los 7 criterios
│   ├── entidades/              ← Carta, TipoCarta, Direccion, Jugador
│   └── juego/                  ← Chest, Deck, Catalogo, Inventario, Duelo, Campania, IA
├── controlador/                ← Navegador y controladores de cada pantalla
└── vista/                      ← JuegoApp y las pantallas (JavaFX)
src/main/resources/
└── cartas.csv                  ← catálogo de cartas (hoy 10 de prueba; el domingo, las 100)
src/test/java/co/edu/unbosque/  ← pruebas JUnit, con la misma estructura de paquetes
```

### La regla de MVC en una línea

**La vista solo muestra y captura clics; el controlador traduce; el modelo decide.** Si una regla del juego no se puede probar con JUnit sin abrir una ventana, está en el lugar equivocado.

### Contratos: lo que ya está hecho para que nadie espere a nadie

Algunas clases ya existen con sus métodos **declarados pero vacíos**. Al llamarlos lanzan `UnsupportedOperationException("Pendiente: ...")`. Sirven para que cada uno pueda escribir y compilar su código usando el del otro, aunque el otro todavía no lo haya terminado.

| Contrato | Lo implementa | Lo usa |
| --- | --- | --- |
| `Carta`, `TipoCarta`, `Direccion` | Listo | Todos |
| `PilaTAD` / `Pila`, `ColaTAD` / `Cola` | Listo (Daniel) | Todos |
| `Lista`, `Mapa`, `Comparador` (interfaces) | Brayan (`ListaDoble`, `TablaHash`, comparadores) | Todos |
| `Jugador.estaDescubierta(id)`, `Jugador.recibirCarta(carta)` | Listo (Daniel) | Brayan y Santiago |
| `Chest`, `Deck` (incluye `Deck.aArreglo()`) | Brayan | Santiago (el duelo usa las 40 cartas) |
| `Inventario.mover(direccion)`, `getCartaEnCursor()` | Santiago | Brayan (pantalla del inventario) |
| `Catalogo.buscarPorId(id)`, `idPorContrasena(texto)` | Daniel | Todos |
| `Pantalla` (interfaz) y `Navegador.ir()` / `volver()` | Listo (Daniel) | Todas las pantallas |

**Los contratos no se cambian sin avisar en el grupo y tener el visto bueno de los tres.** Puedes agregar métodos nuevos a tus clases; lo que no se cambia es lo que otro ya está usando.

---

## 8. Cómo trabajamos con Git (paso a paso)

**Nadie sube cambios directo a `main`.** Cada tarea se hace en una rama propia y entra a `main` por un *pull request* que revisa otra persona.

### 8.1 Antes de empezar una tarea

```bash
git checkout main              # vuelve a la rama principal
git pull                       # baja lo último que subieron los demás
git checkout -b brayan/lista-doble   # crea tu rama: tunombre/tema
```

Nombre de la rama: **`tunombre/tema`**, en minúsculas y con guiones. Ejemplos: `santiago/monticulo`, `daniel/catalogo`.

### 8.2 Mientras trabajas

Guarda tu avance con commits pequeños:

```bash
git status                     # mira qué archivos cambiaste
git add src                    # prepara los cambios
git commit -m "feat: ListaDoble con inserción y eliminación"
```

Los mensajes van **en español** y empiezan con un prefijo:

| Prefijo | Cuándo |
| --- | --- |
| `feat:` | Algo nuevo (una estructura, una pantalla, una regla) |
| `fix:` | Corregir un error |
| `test:` | Solo pruebas |
| `docs:` | Documentación, README, bitácora |

### 8.3 Subir tu rama y abrir el pull request

1. Antes de subir, **corre las pruebas** (sección 5). Todas deben pasar.
2. Sube tu rama:
   ```bash
   git push -u origin brayan/lista-doble
   ```
3. Entra al repositorio en GitHub. Aparece un aviso amarillo con el botón **Compare & pull request**. Clic ahí.
4. Se abre un formulario que ya trae la **lista de verificación** (definición de terminado). Llénala: escribe qué hiciste, pon `Cierra #número` del issue, y marca cada casilla que cumples.
5. A la derecha, en **Reviewers**, elige a un compañero.
6. Clic en **Create pull request**.
7. Avisa en el grupo que tienes un PR para revisar.

### 8.4 Revisar el pull request de otro

1. En GitHub, pestaña **Pull requests** → abre el del compañero → pestaña **Files changed**.
2. Lee el código. Si algo no se entiende o está mal, deja un comentario en la línea (clic en el **+** azul).
3. Cuando esté bien: **Review changes → Approve → Submit review**.
4. Daniel lo fusiona (**Merge pull request**).

Revisa el mismo día que te lo piden. Revisar no es un trámite: **en la sustentación cada uno debe poder explicar el código de los otros dos.**

### 8.5 Después de que fusionan tu PR

```bash
git checkout main
git pull                       # ya trae tu trabajo y el de los demás
git branch -d brayan/lista-doble   # borra tu rama local, ya no se necesita
```

Y empiezas la siguiente tarea desde el paso 8.1.

### 8.6 Si `git pull` dice que hay un conflicto

Pasa cuando dos personas cambiaron las mismas líneas. Git marca el archivo así:

```
<<<<<<< HEAD
tu versión
=======
la versión del otro
>>>>>>> main
```

Deja la versión correcta (o una mezcla), borra las líneas `<<<<<<<`, `=======` y `>>>>>>>`, guarda, y luego:

```bash
git add .
git commit -m "fix: resolver conflicto en Deck"
```

Si no sabes cuál versión dejar, **pregunta en el grupo antes de decidir**.

---

## 9. Reglas del equipo

### Definición de terminado (cada pull request la cumple)

- [ ] Compila y corre en Eclipse.
- [ ] Tiene pruebas JUnit de **caso normal, caso límite y caso de error**, y todas pasan.
- [ ] En el paquete `modelo` **no** hay `import java.util`, salvo `java.util.Random`.
- [ ] Cada método público tiene Javadoc con su complejidad, por ejemplo `Complejidad: O(log n)`.
- [ ] La vista no contiene reglas del juego.
- [ ] La bitácora del día está actualizada.

### Coordinación

- **Reunión diaria de 15 minutos a las 8:00 p.m.**: qué terminé, qué sigue, qué me bloquea.
- **Bloqueado más de 1 hora** → avisa en el grupo en ese momento, no esperes a la reunión.
- **Si un hito no se cumple**, esa noche se recorta alcance; las fechas no se corren.
- **Miércoles 11 a las 12:00 m. se congela el código**: después solo se corrigen errores.

### Decisiones técnicas

- Las pantallas se escriben **en código Java**, sin FXML ni Scene Builder.
- Las cartas se dibujan como **marcos de color por tipo** con su nombre. No usamos imágenes de Konami.
- Para lo aleatorio (barajar, recompensas) se recibe un `Random` desde afuera, así en las pruebas se usa `new Random(42)` y el resultado siempre es el mismo.

---

## 10. Hitos

| Hito | Fecha límite | Se cumple cuando… |
| --- | --- | --- |
| **H0 · Base lista** | Sáb 7 nov, 2:00 p.m. | Los 3 clonan, abren en Eclipse, las pruebas pasan y la ventana abre |
| **H1 · Estructuras** | Dom 8 nov, 6:00 p.m. | Las 8 estructuras, Merge Sort, Insertion Sort y los 7 comparadores están en `main` con pruebas |
| **H2 · Modelo completo** | Lun 9 nov, 2:00 p.m. | Pasan las pruebas de Chest, Deck, contraseñas, catálogo, guardado, inventario, duelo e IA; una prueba juega un duelo máquina contra máquina hasta que hay ganador |
| **H3 · Juego completo** | Mar 10 nov, 8:00 p.m. | Se juega de principio a fin: menú → Deck → 3 rivales → recompensas → guardar y cargar; también modo libre, PvP, contraseñas, inventario y música |
| **H4 · Entrega** | Mié 11 nov, 12:00 m. | Código congelado con la etiqueta `v1.0` y documentación completa |

---

## 11. Problemas comunes y cómo resolverlos

| Lo que ves | Por qué pasa | Cómo se arregla |
| --- | --- | --- |
| `release version 25 not supported` o `invalid target release: 25` | Eclipse está compilando con un Java más viejo | Haz el paso 4.1 (JDK 25 como predeterminado). Luego clic derecho en el proyecto → **Maven → Update Project…** (o `Alt+F5`) → **OK** |
| El proyecto tiene una X roja y en `pom.xml` dice `Missing artifact` | Maven no alcanzó a descargar JavaFX o JUnit (sin internet o descarga cortada) | Con internet: clic derecho en el proyecto → **Maven → Update Project…** → marca **Force Update of Snapshots/Releases** → **OK** |
| `java -version` dice una versión que no es 25 | Tienes varios Java y el viejo está primero en el PATH | Desinstala el viejo o reinstala el JDK 25 marcando "Add to PATH"; cierra y abre la terminal |
| `Permission denied (publickey)` al clonar | Usaste la dirección SSH | Clona con la dirección **HTTPS** (sección 3) |
| `remote: Permission to ... denied` al hacer `push` | No has aceptado la invitación al repositorio | Acéptala en https://github.com/notifications |
| `JavaFX runtime components are missing` | Se corrió `JuegoApp` directamente | Corre `Main.java` o usa `javafx:run` (sección 6) |
| `UnsupportedOperationException: Pendiente: ...` | Llamaste un método de contrato que su dueño todavía no implementa | Es normal antes del hito correspondiente. Avísale al dueño si lo necesitas antes |
| Avisos `LF will be replaced by CRLF` | Diferencia de fin de línea entre Windows y Linux | Ya lo maneja el archivo `.gitattributes`; se puede ignorar |
| La pestaña JUnit no encuentra pruebas | La clase de prueba no termina en `Test` o le falta `@Test` | Nombra las clases `AlgoTest` y marca cada método con `@Test` |

Si tu problema no está aquí, copia el mensaje de error **completo** y mándalo al grupo.
