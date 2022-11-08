# Documentación de la aplicación ToDoList

## Links:
* Github: https://github.com/mads-ua-22-23/mads-todolist-jact11-ua
* DockerHub: https://hub.docker.com/repository/docker/jact11/mads-todolist

## Funcionalidad y código añadido.

### Listado de equipos

Uno de los objetivos de la práctica era el de implementar un listado de
los equipos existentes y los participantes en cada uno de ellos para
poder consultar la estructura de la empresa y los proyectos en marcha.

**Servicio**
src/main/java/madstodolist/service/EquipoService.java

Para realizar esta funcionalidad he realizado las siguientes funciones:

```Java
@Transactional
    public Equipo crearEquipo(String nombre) {
        Equipo equipo = new Equipo(nombre);
        equipoRepository.save(equipo);
        return equipo;
    }
``` 
En esta función ```crearEquipo(String)``` simplemente creamos un nuevo equipo con el título que 
queramos y este se guarda con la función ```save()``` del repository.

```Java
    @Transactional(readOnly = true)
    public List<Equipo>findAllOrderedByName(){
        List<Equipo> list_equipo=equipoRepository.findAll();
        Collections.sort(list_equipo, Comparator.comparing(Equipo::getNombre));
        return list_equipo;
    }
```
A continuación tenemos la función ```findAllOrderedByName()``` que obtiene una lista con todos los equipos creados
hasta el momento y la ordena alfabéticamente usando la función ```sort()```.

```Java
@Transactional(readOnly = true)
    public List<Usuario>usuariosEquipo(Long id){
        Equipo equipo=equipoRepository.findById(id).orElse(null);
        List<Usuario>usuarios= new ArrayList<>(equipo.getUsuarios());
        return usuarios;
    }
```
Por último, esta función obtiene todos los usuarios que se encuentran participando en un equipo.
**Controlador**

src/main/java/madstodolist/controller/EquipoController.java

```Java
@GetMapping("/equipos")
    public String listadoEquipos(Model model){
        Long usuarioId=0L;
        usuarioId=managerUserSession.usuarioLogeado();
        Usuario usuario=usuarioService.findById(usuarioId);
        if(usuario==null)
            throw new UsuarioNotFoundException();
        model.addAttribute("usuario", usuario);
        List<Equipo>allEquipos=equipoService.findAllOrderedByName();
        model.addAttribute("equipos", allEquipos);
        return "listaEquipos";
    }
```
Para implementar el listado de equipos lo único que debemos hacer 
en el controller era llamar a la función ```findAllOrderedByName()```
y añadir la lista que obtiene como atributo para mostrarla en el template.
```Java
    @GetMapping("/equipos/{id}")
    public String miembrosEquipos(@PathVariable(value="id") Long equipoId, Model model){
        Long usuarioId=0L;
        usuarioId=managerUserSession.usuarioLogeado();
        Usuario usuario=usuarioService.findById(usuarioId);
        if(usuario==null)
            throw new UsuarioNotFoundException();
        model.addAttribute("usuario", usuario);
        Equipo equipo=equipoService.findById(equipoId);
        if(equipo==null)
            throw new EquipoNotFoundException();
        List<Usuario>allUsuarios=equipoService.usuariosEquipo(equipoId);
        model.addAttribute("equipo", equipo);
        model.addAttribute("usuarios", allUsuarios);
        return "integrantesEquipo";
``` 
Para implementar el listado de miembros llamamos a la función ```usuariosEquipo(Long)```
y añadimos la lista que obtiene como atributo para mostrarla en el template.

**Template**

src/main/resources/templates/listaEquipos.html

Para implementar la vista de esta historia de usuario, he utilizado dos ficheros html, uno para la lista de equipos
y otro para sus miembros.

```html
<div class="container-fluid">
    <div class="row mt-3">
        <div class="col">
            <h2 th:text="'Listado de equipos'"></h2>
        </div>
    </div>

    <a class="btn btn-primary" href="/equipos/nuevo">Crear nuevo equipo</a>

    <div class="row mt-3">
        <div class="col">
            <table class="table table-striped">
                <thead>
                <tr>
                    <th>Id</th>
                    <th>Nombre</th>
                </tr>
                </thead>
                <tbody>
                <tr th:each="equipo: ${equipos}">
                    <td th:text="${equipo.id}"></td>
                    <td>
                        <a th:href="@{/equipos/{id}(id=${equipo.id})}"
                           th:text="${equipo.nombre}"></a>
                    </td>
                </tr>
                </tbody>
            </table>
        </div>
    </div>
</div>
```
Introducimos la información (nombre e identificador) del equipo en una tabla 
y establecemos el nombre de cada equipo como un enlace que nos lleva a la lista de miembros del mismo.

src/main/resources/templates/integrantesEquipo.html

```html
<div class="row mt-3">
    <div class="col">
      <table class="table table-striped">
        <thead>
        <tr>
          <th>Id</th>
          <th>Email</th>
          <th>Nombre</th>
        </tr>
        </thead>
        <tbody>
        <tr th:each="usuario: ${usuarios}">
          <td th:text="${usuario.id}"></td>
          <td th:text="${usuario.email}"></td>
          <td th:text="${usuario.nombre}"></td>
        </tr>
        </tbody>
      </table>
    </div>
  </div>
</div>
```
Utilizando una metodología similar a la de la página de registrados, introducimos la información de cada
usuario participante en una tabla donde cada columna es un atributo de usuario (identificador, nombre e email).

**Tests de prueba**

src/test/java/madstodolist/EquipoWebTest.java


Para comprobar el correcto funcionamiento del listado de equipos y miembros participantes se implementaron dos tests.

```Java
@Test
    public void ListadoEquiposTest() throws Exception{
        Usuario user = new Usuario("prueba@ua");
        user.setPassword("123");
        usuarioService.registrar(user);

        this.managerUserSession.logearUsuario(user.getId());
        when(managerUserSession.usuarioLogeado()).thenReturn(user.getId());

        Equipo equipo=equipoService.crearEquipo("equipoPrueba");
        equipoRepository.save(equipo);
        Equipo equipo2=equipoService.crearEquipo("pruebaEquipo");
        equipoRepository.save(equipo2);

        this.mockMvc.perform(get("/equipos"))
                .andExpect((content().string(allOf(
                        containsString("Nombre"),
                        containsString(equipo.getNombre()),
                        containsString(equipo2.getNombre()),
                        containsString("2")
                ))));
    }
``` 
En este test creamos una serie de equipos y comprobamos que se muestren correctamente en
el listado de equipos.

```Java
@Test
    public void ListadoUsuariosEquipo() throws Exception{
        Equipo equipo = equipoService.crearEquipo("Proyecto 1");
        Equipo equipo2 = equipoService.crearEquipo("Proyecto 2");

        Usuario usuario = new Usuario("user@ua");
        usuario.setNombre("NombrePrueba");
        usuario.setPassword("123");
        usuario = usuarioService.registrar(usuario);

        this.managerUserSession.logearUsuario(usuario.getId());

        when(managerUserSession.usuarioLogeado()).thenReturn(usuario.getId());

        equipoService.addUsuarioEquipo(usuario.getId(), equipo.getId());
        equipoService.addUsuarioEquipo(usuario.getId(), equipo2.getId());

        this.mockMvc.perform(get("/equipos/1"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("1")))
                .andExpect(content().string(containsString("NombrePrueba")))
                .andExpect(content().string(containsString("user@ua")));

        this.mockMvc.perform(get("/equipos/2"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("1")))
                .andExpect(content().string(containsString("NombrePrueba")))
                .andExpect(content().string(containsString("user@ua")));
    }
```
Por último, en este test creamos varios equipos y usuarios. Añadimos estos usuarios a los distinos equipos
y comprobamos que aparecen en el listado de miembros de cada uno.

### Gestión de pertenencia de equipos

Como usuario podré crear nuevos equipos y añadirme y eliminarme de cualquiera de ellos para poder participar y dejar de participar en ellos.

**Servicio**

src/main/java/madstodolist/service/EquipoService.java

```Java
@Transactional
    public void addUsuarioEquipo(Long idUsuario, Long idEquipo) {
        Usuario usuario=usuarioRepository.findById(idUsuario).orElse(null);
        Equipo equipo=equipoRepository.findById(idEquipo).orElse(null);
        equipo.addUsuario(usuario);
    }

    @Transactional
    public void deleteUsuarioEquipo(Long idUsuario, Long idEquipo){
        Usuario usuario=usuarioRepository.findById(idUsuario).orElse(null);
        Equipo equipo=equipoRepository.findById(idEquipo).orElse(null);
        equipo.deleteUsuario(usuario);
    }
```
Obtenemos tanto el usuario y el equipo que coincide con los identificadores que se pasan
como parámetro y añadimos o eliminamos dicho usuario del equipo

**Controlador**

src/main/java/madstodolist/controller/EquipoController.java

```Java
@GetMapping("/equipos/nuevo")
    public String formNuevoEquipo(@ModelAttribute EquipoData equipoData, Model model){
        Long usuarioId=managerUserSession.usuarioLogeado();
        Usuario usuario=usuarioService.findById(usuarioId);
        if(usuario==null)
            throw new UsuarioNotFoundException();
        model.addAttribute("usuario", usuario);
        return "formCrearEquipo";
    }

    @PostMapping("/equipos/nuevo")
    public String nuevoEquipo(@ModelAttribute EquipoData equipoData, Model model) {
        Long usuarioId=managerUserSession.usuarioLogeado();
        Usuario usuario=usuarioService.findById(usuarioId);
        if(usuario==null)
            throw new UsuarioNotFoundException();
        model.addAttribute("usuario", usuario);
        equipoService.crearEquipo(equipoData.getTitulo());
        return "redirect:/equipos";
    }
```
Para crear un nuevo equipo utilizamos un form con el que establecemos el título del equipo y creamos el equipo

```Java
@PostMapping("/equipos/{id}")
    public String añadirUsuarioEquipo(@PathVariable(value="id") Long idEquipo, @ModelAttribute EquipoData equipoData){
        Long usuarioId=managerUserSession.usuarioLogeado();
        equipoService.addUsuarioEquipo(usuarioId, idEquipo);
        return "redirect:/equipos/" + idEquipo;
    }

    @DeleteMapping("/equipos/{id}")
    @ResponseBody
    public String borrarUsuarioEquipo(@PathVariable(value="id") Long idEquipo, @ModelAttribute EquipoData equipoData){
        Long usuarioId=managerUserSession.usuarioLogeado();
        equipoService.deleteUsuarioEquipo(usuarioId, idEquipo);
        return "";
    }
```
Llamamos a las funciones ``addUsuarioEquipo(Long, Long)`` y ``deleteUsuarioEquipo(Long, Long)`` para iniciar o finalizar la participación
en un equipo.

**Template**

src/main/resources/templates/formCrearEquipo.html

```html
<div class="container-fluid">
    <form method="post" th:action="@{/equipos/nuevo}" th:object="${equipoData}">
        <div class="col-6">
            <div class="form-group">
                <label for="titulo">Título del equipo:</label>
                <input class="form-control" id="titulo" name="titulo" required th:field="*{titulo}" type="text"/>
            </div>
            <button class="btn btn-primary" type="submit">Crear equipo</button>
            <a class="btn btn-link" th:href="@{/equipos}">Cancelar</a>
        </div>
    </form>
</div>
```
Añadimos un formulario donde solicitamos el título del equipo a crear, un botón para cancelar que nos redirecciona al listado de equipos
y un botón para confirmar la creación. Una vez se ha creado el equipo, recargamos la página.

src/main/resources/templates/integrantesEquipo.html

```html
<div class="container-fluid">
  <div class="row mt-3">
    <div class="col">
      <h2 th:text="'Listado de integrantes de ' + ${equipo.nombre}"></h2>
    </div>
  </div>

  <form method="post" th:action="@{/equipos/{id}(id=${equipo.id})}" th:object="${equipoData}">
    <div class="col-6">
      <button class="btn btn-primary" type="submit">Participar en el equipo</button>
    </div>
  </form>
  <br>
    <div>
      <button class="btn btn-danger btn-xs" onmouseover="" style="cursor: pointer;"
              th:onclick="'del(\'/equipos/' + ${equipo.id} + '\')'">Dejar de participar en el equipo</button>
    </div>

(...)
```
Para añadir el usuario logeado al equipo en el que nos encontramos situado, usaremos el botón "Participar en el equipo".
Una vez añadido, se recargará la página y el usuario aparecerá como integrante.

```html
<script type="text/javascript">
  function del(urlBorrar) {
    if (confirm('¿Estás seguro/a de que quieres dejar de participar en el equipo?')) {
      fetch(urlBorrar, {
        method: 'DELETE'
      }).then((res) => location.reload());
    }
  }
</script>
```
Para borrar el usuario usaremos el botón "Dejar de participar en el equipo". Cuando hagamos click en dicho botón se 
ejecutará un script de Javascript que pedirá la confirmación, borrará la pertenencia al equipo y recargará la página.

**Tests de prueba**

src/test/java/madstodolist/EquipoWebTest.java

```Java
@Test
    public void postNuevoEquipoDevuelveRedirectYAñadeEquipo() throws Exception {
        Usuario usuario = new Usuario("user@ua");
        usuario.setNombre("NombrePrueba");
        usuario.setPassword("123");
        usuario = usuarioService.registrar(usuario);

        this.managerUserSession.logearUsuario(usuario.getId());
        when(managerUserSession.usuarioLogeado()).thenReturn(usuario.getId());

        String urlPost = "/equipos/nuevo";
        String urlRedirect = "/equipos";

        this.mockMvc.perform(post(urlPost)
                        .param("titulo", "Grupo MADS"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(urlRedirect));

        this.mockMvc.perform(get(urlRedirect))
                .andExpect((content().string(containsString("Grupo MADS"))));
    }
```
En esta prueba, creamos un nuevo grupo con el nombre "Grupo MADS" y comprobamos
que después de añadirse, este aparece en el listado de equipos.

```Java
@Test
    public void postAñadirUsuarioEquipoDevuelveRedirectYAñadeUsuario() throws Exception{
        Usuario usuario = new Usuario("user@ua");
        usuario.setNombre("NombrePrueba");
        usuario.setPassword("123");
        usuario = usuarioService.registrar(usuario);
        Equipo equipo=equipoService.crearEquipo("Grupo MADS");

        this.managerUserSession.logearUsuario(usuario.getId());
        when(managerUserSession.usuarioLogeado()).thenReturn(usuario.getId());

        this.mockMvc.perform(post("/equipos/" + equipo.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/equipos/" + equipo.getId()));

        this.mockMvc.perform(get("/equipos/" + equipo.getId()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(usuario.getNombre())))
                .andExpect(content().string(containsString(usuario.getEmail())));
    }
```
A continuación nos logeamos con un usuario, creamos un nuev equipo y añadimos al usuario como participante.
Una vez hecho esto, comprobamos que la información del usuario aparece en el listado de miembros del equipo.

```Java
@Test
    public void deleteUsuarioEquipo() throws Exception {
        Usuario usuario = new Usuario("user@ua");
        usuario.setNombre("NombrePrueba");
        usuario.setPassword("123");
        usuario = usuarioService.registrar(usuario);
        Equipo equipo=equipoService.crearEquipo("Grupo MADS");

        this.managerUserSession.logearUsuario(usuario.getId());
        when(managerUserSession.usuarioLogeado()).thenReturn(usuario.getId());

        this.mockMvc.perform(post("/equipos/" + equipo.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/equipos/" + equipo.getId()));

        this.mockMvc.perform(delete("/equipos/" + equipo.getId()))
                .andExpect(status().isOk());

        this.mockMvc.perform(get("/equipos" + equipo.getId()))
                .andExpect(content().string(
                        allOf(not(containsString(usuario.getNombre())),
                                not(containsString(usuario.getEmail())))));
    }
```
Por último, en este test nos logeamos con un usuario, creamos un equipo y marcamos dicho usuario como participante.
Seguidamente, dejamos de ser participante con ese mismo usuario y comprobamos que la información del mismo **NO** aparece en el listado de miembros del equipo.
