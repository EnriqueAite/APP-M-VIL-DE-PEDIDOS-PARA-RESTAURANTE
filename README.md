# 🍽️ El Buen Sabor – Sistema de pedidos para restaurante

Sistema de pedidos para un restaurante formado por **tres partes que trabajan juntas**: una app móvil para los clientes, un portal web para el administrador y una base de datos MySQL. Proyecto académico desarrollado en **Cibertec**.

## ¿Qué hace el sistema?

**Cliente (app Android)**
- Registrarse e iniciar sesión.
- Ver la carta del restaurante por categorías.
- Agregar platos al carrito y realizar pedidos.
- Consultar sus pedidos y el estado de cada uno.
- Ver y actualizar su perfil.

**Administrador (portal web)**
- Ver el panel principal (dashboard) con el resumen de la actividad.
- Cambiar el estado de los pedidos (pendiente, preparando, enviado, entregado o cancelado).
- Editar el inventario: agregar, modificar y eliminar productos de la carta en línea, con sus imágenes.
- Consultar la lista de clientes.

Los cambios que hace el administrador se reflejan en la app, porque ambos consumen la misma API y la misma base de datos.

## Tecnologías

| Parte | Tecnología |
|---|---|
| App móvil | **Kotlin**, Android Studio, Retrofit (consumo de la API) |
| API REST | **C# (.NET)**, ASP.NET Core Web API, Entity Framework Core |
| Portal del administrador | ASP.NET Core, HTML, CSS y JavaScript |
| Base de datos | **MySQL 8**, MySQL Workbench 8.0 CE |
| Herramientas | Visual Studio 2022, Android Studio, Git y GitHub |

## Estructura del repositorio

```
├── README.md
├── android-app/     → App móvil para clientes (Kotlin, Android Studio)
├── api-portal/      → Solución de Visual Studio (ElBuenSaborAPI.sln)
│   ├── ElBuenSaborAPI/      → API REST (C#, .NET)
│   └── ElBuenSaborPortal/   → Portal web del administrador
└── database/        → Script SQL de la base de datos (ElBuenSabor.sql)
```

## Requisitos

- MySQL Server 8 y MySQL Workbench 8.0 CE
- Visual Studio 2022 con la carga de trabajo **ASP.NET y desarrollo web**
- Android Studio con un emulador configurado (o un celular Android)

## Cómo ejecutarlo

Sigue los pasos **en este orden**, porque cada parte depende de la anterior.

### 1. Cargar la base de datos (MySQL Workbench)

1. Abre MySQL Workbench y conéctate a tu servidor local.
2. Abre el archivo `database/ElBuenSabor.sql`.
3. Ejecútalo completo (ícono del rayo) para crear la base de datos, las tablas y los datos de prueba.

### 2. Ejecutar la API y el portal (Visual Studio 2022)

1. Abre el archivo `api-portal/ElBuenSaborAPI.sln` con Visual Studio 2022.
2. En `ElBuenSaborAPI/appsettings.json`, cambia la cadena de conexión con los datos de tu MySQL (servidor, base de datos, usuario y contraseña).
3. Configura la solución para iniciar los dos proyectos a la vez (clic derecho en la solución → *Configurar proyectos de inicio* → *Varios proyectos de inicio*, y marca **ElBuenSaborAPI** y **ElBuenSaborPortal** en *Iniciar*).
4. Presiona **F5** o el botón de ejecutar. Se abrirán la API y el portal del administrador en el navegador.

### 3. Ejecutar la app (Android Studio)

1. Abre Android Studio y elige **Open** → selecciona la carpeta `android-app`.
2. Espera a que termine la sincronización de Gradle.
3. Abre `data/RetrofitClient.kt` y comprueba que la dirección de la API apunte a tu equipo:
   - En el **emulador de Android**, usa `http://10.0.2.2:<puerto de la API>/`. La dirección `10.0.2.2` es el acceso del emulador a tu computadora.
   - En un **celular físico**, usa la IP local de tu PC, por ejemplo `http://192.168.x.x:<puerto>/`.
4. Ejecuta la app en el emulador (botón **Run**).

### 4. Probar el flujo completo

1. En la app, regístrate como cliente, agrega platos al carrito y realiza un pedido.
2. En el portal del administrador, abre la sección **Pedidos** y cambia el estado de ese pedido.
3. Vuelve a la app y comprueba que el estado se actualizó.
4. En la sección **Productos** del portal, edita un plato y verifica el cambio en la carta de la app.

## Notas

- Las contraseñas y datos de conexión del repositorio son de ejemplo. Cámbialos por los de tu entorno antes de ejecutar.
- Los datos del script SQL son solo de prueba.

## Autor

**Diego Enrique** – Técnico en Computación e Informática, Cibertec.
LinkedIn: _(agrega aquí el enlace a tu perfil)_
