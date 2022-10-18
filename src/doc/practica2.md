# Documentación de la aplicación ToDoList

## Links:
* Github: https://github.com/mads-ua-22-23/mads-todolist-jact11-ua
* DockerHub: https://hub.docker.com/repository/docker/jact11/mads-todolist

## Funcionalidad y código añadido.

### Barra de navegación.

Uno de los objetivos de la práctica era el de implementar una barra de navegación común a todas las páginas desde la cual pudieramos desplazarnos entre las mismas.

Además, para el caso específico de la página 'Acerca de', esta Navbar debía variar dependiendo de si el usuario estaba logeado o no.


**Controlador**

src/main/java/madstodolist/controller/HomeController.java

```Java
public class HomeController {

    @Autowired
    private ManagerUserSession managerUserSession;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/about")
    public String about(Model model) {
        Long usuarioId = 0L;
        usuarioId = managerUserSession.usuarioLogeado();
        if (usuarioId == null) return "about";
        else {
            Usuario usuario = usuarioService.findById(usuarioId);
            model.addAttribute("usuario", usuario);
            return "aboutLog";
        }
    }
}
```
Para implementar esta funcionalidad debíamos comprobar si el usuario estaba logeado con el método ```usuarioLogeado()```. Si este devolvía un ID de usuario, significaría que el usuario estaba logeado y viceversa.
Una vez hecha esta comprobación lo único restante era crear las dos barras de navegación mediante un Navbar de Bootstrap.

**Template**
Las dos barras de navegación se forman a partir del siguiente codigo html.

- La primera de ellas será aquella que se mostrará cuando el usuario no está logeado:

```html
<!-- Navbar -->
<div class="container-fluid">
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark">
        <div class="collapse navbar-collapse" id="navbarNavDropdown">
            <ul class="navbar-nav mr-auto">
                <li class="nav-item active">
                    <a class="nav-link" href="/about">ToDoList <span class="sr-only">(current)</span></a>
                </li>
                <li class="nav-item active">
                    <a class="nav-link" th:href="@{/login}">Login</a>
                </li>
                <li class="nav-item active">
                    <a class="nav-link" th:href="@{/registro}">Registro</a>
                </li>
            </ul>
        </div>
    </nav>
</div>
<!-- Navbar -->
```
La barra de navegación se forma mediante una lista desordenada de elementos. Esta en concreto contiene tres enlaces hacia diferentes páginas de la aplicación.
El primero de ellos te redirige a la propia página ```Acerca de ```, el segundo a la página de ```Login``` y el tercero a la de ```Registro```.

- La segunda de ellas se mostrará cuando el usuario se encuentra logeado en la aplicación.

```html
<!-- Navbar -->
<div class="container-fluid">
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark">
        <div class="collapse navbar-collapse" id="navbarNavDropdown">
            <ul class="navbar-nav mr-auto">
                <li class="nav-item active">
                    <a class="nav-link" href="/about">ToDoList <span class="sr-only">(current)</span></a>
                </li>
                <li class="nav-item active">
                    <a class="nav-link" th:href="@{/usuarios/{id}/tareas(id=${usuario.id})}">Tareas</a>
                </li>
                <li class="nav-item dropdown">
                    <a class="nav-link dropdown-toggle" href="#" id="navbarDropdownMenuLink" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false"
                       th:text="${usuario.nombre}">
                    </a>
                    <div class="dropdown-menu" aria-labelledby="navbarDropdownMenuLink">
                        <a class="dropdown-item" href="#">Cuenta</a>
                        <a class="dropdown-item" href="/logout" th:text="'Cerrar sesión ' + ${usuario.nombre}"></a>
                    </div>
                </li>
            </ul>
        </div>
    </nav>
</div>
<!-- Navbar -->
```
Esta segunda barra también tiene tres elementos: el primero te redirige a la página ``Acerca de`` y el segundo a la página que contiene el ``Listado de Tareas`` del usuario logeado.
El tercer elemento se trata de un item dropdown y recibe el nombre del usuario. Cuando se hace click encima muestra dos opciones extra: ``Cuenta`` (página aún sin implementar) y ``Cerrar sesión `` la cual como su nombre indica, cierra la sesión del usuario actual y te redirige a la página de ``Login``.

**Tests de prueba**
Para comprobar el correcto funcionamiento de las barras de navegación y de la página ``Acerca de`` en general he implementado los siguientes tests:

```Java
public class AcercaDeWebTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ManagerUserSession managerUserSession;

    @Autowired
    UsuarioRepository usuarioRepository;

    @MockBean
    private UsuarioService usuarioService;
    @Test
    public void getAboutDevuelveNombreAplicacion() throws Exception {
        this.mockMvc.perform(get("/about"))
                .andExpect(content().string(containsString("ToDoList")));
    }

    @Test
    public void AboutMuestraNavbarsinLog() throws Exception {
        this.mockMvc.perform(get("/about"))
                .andExpect(content().string(containsString("Registro")));
    }
}
```

Estos primeros comprueban que se muestra correctamente el contenido de la página y la barra de navegación respectivamente cuando el usuario NO está logeado.

```Java
@Test
    public void AboutMuestraNavbarconLog() throws Exception{

        Usuario user = new Usuario("prueba@ua");
        user.setId(9L);
        user.setNombre("Prueba User");

        this.managerUserSession.logearUsuario(8L);

        when(managerUserSession.usuarioLogeado()).thenReturn(8L);
        when(usuarioService.findById(8L)).thenReturn(user);

        this.mockMvc.perform(get("/about"))
                .andExpect(content().string(containsString("Prueba User")))
                .andExpect(content().string(containsString("Tareas")));

        this.managerUserSession.logout();

    }
```
Este último comprueba que se muestra correctamente el contenido de la barra de navegación cuando el usuario está logeado.
Para ello creamos un usuario de prueba, lo logeamos y comprobamos que las  ``substring`` que contienen la palabra 'Tareas' y el nombre de usuario (los cuales se encuentran en la barra de navegación) aparecen en la página.

### Listado de usuarios
Otro de los objetivos era crear una página que contenga una lista con todos los usuarios registrados hasta el momento, mostrando tanto su ``id`` como su  ``email``.

**Servicio**

src/main/java/madstodolist/service/UsuarioService.java

En el fichero ``UsuarioService.java`` añadí un método llamado  ``listarUsuarios()`` que devolvía un ArrayList con todos los usuarios registrados hasta el momento, que luego sería usado en el controller.
````Java
    public List<Usuario> listarUsuario(){
        List<Usuario>usuarios = new ArrayList<>();
        for(Usuario usuario : usuarioRepository.findAll())
        usuarios.add(usuario);
        return usuarios;
   }
````

**Controller**

src/main/java/madstodolist/controller/UsuarioController.java

```Java
@GetMapping("/registrados")
    public String usuarios(Model model){
        List<Usuario> allUsuarios = usuarioService.listarUsuario();
        model.addAttribute("usuarios", allUsuarios);
        return "listaUsuarios";
    }
```
Simplemente llamamos al método ``listarUsuario()`` para obtener todos los usuarios registrados y poder mostrarlos en la página.

**Template**

src/main/resources/templates/listaUsuarios.html

```html
<div class="row mt-3">
        <div class="col">
            <table class="table table-striped">
                <thead>
                <tr>
                    <th>Id</th>
                    <th>Correo Electrónico</th>
                </tr>
                </thead>
                <tbody>
                <tr th:each="usuario: ${usuarios}">
                    <td th:text="${usuario.id}"></td>
                    <td th:text="${usuario.email}"></td>
                    <td>
                        <a class="btn btn-primary" th:href="@{/registrados/{id}(id=${usuario.id})}">Descripcion </a>
                    </td>
                </tr>

                </tbody>
            </table>
        </div>
    </div>
```
Para crear la página con el listado de usuarios seguimos una estructura muy parecida
a la del listado de tareas. Tenemos una tabla con dos columnas (una para el ``id`` y otra para el ``email``)
y en cada fila mostramos la información de cada usuario.

**Tests de prueba**

src/test/java/madstodolist/UsuarioWebTest.java

Para comprobar el correcto funcionamiento del listado de usuarios, he implementado los siguientes tests:

```Java
@Test
    public void ListadoUsuariosTest() throws Exception{
        Usuario user = new Usuario("prueba@ua");
        user.setId(8L);
        user.setNombre("Prueba User");

        this.managerUserSession.logearUsuario(8L);

        when(managerUserSession.usuarioLogeado()).thenReturn(8L);
        when(usuarioService.findById(8L)).thenReturn(user);

        this.mockMvc.perform(get("/registrados"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("8")));
    }
```

Creamos un usuario de prueba, lo logeamos y comprobamos que en la página 
``/registrados`` aparece correctamente el ``id`` del usuario que acabamos de logear.

### Descripción de usuarios
Otro de los objetivos era crear una página que contenga información de cada usuario, mostrando su ``id``, su ``nombre``, ``email`` y ``fecha de nacimiento``.

**Controller**

src/main/java/madstodolist/controller/UsuarioController.java

```Java
@GetMapping("/registrados/{id}")
    public String descripcionUsuarios(@PathVariable(value="id") Long usuarioId, Model model){
        Usuario usuario = usuarioService.findById(usuarioId);
        model.addAttribute("usuarios", usuario);
        return "descripcionUsuarios";
    }
```

Obtenemos el id del usuario a partir de la url para poder mostrarlo en la página.

**Template**
```html
<div class="row mt-3">
        <div class="col">
            <table class="table table-striped">
                <thead>
                <tr>
                    <th>Id</th>
                    <th>Correo Electrónico</th>
                </tr>
                </thead>
                <tbody>
                <tr th:each="usuario: ${usuarios}">
                    <td th:text="${usuario.id}"></td>
                    <td th:text="${usuario.email}"></td>
                    <td>
                        <a class="btn btn-primary" th:href="@{/registrados/{id}(id=${usuario.id})}">Descripcion </a>
                    </td>
                </tr>

                </tbody>
            </table>
        </div>
    </div>
```
Simplemente debemos añadir a la página del listado de usuarios un botón que nos redirija a la página de la descripción de usuario a partir de la ``id`` de este.

**Tests de prueba**

src/test/java/madstodolist/UsuarioWebTest.java

Para comprobar el correcto funcionamiento de ls descripción de usuario, he implementado los siguientes tests:

```Java
@Test
    public void DescripcionUsuarioTest() throws Exception{
        Usuario user = new Usuario("prueba@ua");
        user.setId(9L);
        user.setNombre("Prueba User");

        this.managerUserSession.logearUsuario(9L);

        when(managerUserSession.usuarioLogeado()).thenReturn(9L);
        when(usuarioService.findById(9L)).thenReturn(user);

        this.mockMvc.perform(get("/registrados/9"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("9")))
                .andExpect(content().string(containsString("Prueba User")))
                .andExpect(content().string(containsString("prueba@ua")));
    }
```

Creamos un usuario de prueba, lo logeamos y comprobamos que en la página
``/registrados/{id}`` aparece correctamente la información del usuario en cuestión.

### Usuario administrador

Para realizar esta funcionalidad añadí una nueva columna booleana  ``admin`` a la base de datos.

```Java
private boolean admin;

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }
```

Esta columna, será la encargada de guardar si un usuario registrado tiene privilegios de administrador o no.

**Controller**

src/main/java/madstodolist/controller/LoginController.java

```Java
@PostMapping("/registro")
   public String registroSubmit(@Valid RegistroData registroData, BindingResult result, Model model) {

        if (result.hasErrors()) {
            return "formRegistro";
        }

        if (usuarioService.findByEmail(registroData.geteMail()) != null) {
            model.addAttribute("registroData", registroData);
            model.addAttribute("error", "El usuario " + registroData.geteMail() + " ya existe");
            return "formRegistro";
        }

        Usuario usuario = new Usuario(registroData.geteMail());
        usuario.setPassword(registroData.getPassword());
        usuario.setFechaNacimiento(registroData.getFechaNacimiento());
        usuario.setNombre(registroData.getNombre());
        usuario.setAdmin(registroData.isAdmin());

        usuarioService.registrar(usuario);
        if(usuario.isAdmin())
            return "redirect:/registrados";
        else
            return "redirect:/login";
   }
```

Cuando un usuario se registra como administrador, tiene que ser redirigido a la página ``/registrados``.
Para realizar esto correctamente modifiqué un método ya existente donde compruebo si el usuario es administrador o no y redirijo en consecuencia.

**Template**

src/main/resources/templates/formRegistro.html

En la página de registro debemos añadir un checkbox para asignar al usuario que vamos a registrar como usuario o no
y en el caso de que lo sea, debemos esconder el checkbox para evitar que exista más de un administrador a la vez.

```html
<div class="form-group" id="divCB">
    <label for="cb"> Usuario Administrador </label>
    <input type="checkbox" id="cb" class="form-control" name="cb"
           th:field="*{admin}" Usuario Administrador/>
</div>
<script>
    function hidecb() {
        const div = document.getElementById("divCB");
        const cb = document.getElementById("cb");
        if(cb.checked) {
            div.style.display = "none";
        }
    }
</script>
<button onclick="hidecb()" type="submit" id="boton" class="btn btn-primary">Registro</button>
```
Utilizo un script de Javascript para esconder el chekbox si registramos a un usuario cuando está marcado como ``checked``.

**Tests de prueba**

src/test/java/madstodolist/UsuarioTest.java

```Java
@Test
    public void crearUsuario() throws Exception {

        // GIVEN
        // Creado un nuevo usuario,
        Usuario usuario = new Usuario("juan.gutierrez@gmail.com");

        // WHEN
        // actualizamos sus propiedades usando los setters,

        usuario.setNombre("Juan Gutiérrez");
        usuario.setPassword("12345678");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        usuario.setFechaNacimiento(sdf.parse("1997-02-20"));
        usuario.setAdmin(true);

        // THEN
        // los valores actualizados quedan guardados en el usuario y se
        // pueden recuperar con los getters.

        assertThat(usuario.getEmail()).isEqualTo("juan.gutierrez@gmail.com");
        assertThat(usuario.getNombre()).isEqualTo("Juan Gutiérrez");
        assertThat(usuario.getPassword()).isEqualTo("12345678");
        assertThat(usuario.getFechaNacimiento()).isEqualTo(sdf.parse("1997-02-20"));
        assertThat(usuario.isAdmin()).isEqualTo(true);
    }
```

Modificamos este test ya existente para comprobar que el atributo ``admin`` se guarda correctamente en la base de datos.

