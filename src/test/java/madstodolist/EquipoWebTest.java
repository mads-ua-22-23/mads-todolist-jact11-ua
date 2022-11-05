package madstodolist;

import madstodolist.authentication.ManagerUserSession;
import madstodolist.model.Equipo;
import madstodolist.model.EquipoRepository;
import madstodolist.model.Usuario;
import madstodolist.service.EquipoService;
import madstodolist.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.params.shadow.com.univocity.parsers.conversions.Conversions.string;
import static org.mockito.Mockito.when;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Sql(scripts = "/clean-db.sql", executionPhase = AFTER_TEST_METHOD)
@AutoConfigureMockMvc
public class EquipoWebTest {

    @Autowired
    @MockBean
    private ManagerUserSession managerUserSession;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EquipoRepository equipoRepository;

    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private EquipoService equipoService;

    @Test
    public void ListadoEquiposTest() throws Exception{
        Usuario user = new Usuario("prueba@ua");
        user.setPassword("123");
        usuarioService.registrar(user);

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

    @Test
    public void ListadoUsuariosEquipo() throws Exception{
        Equipo equipo = equipoService.crearEquipo("Proyecto 1");
        Equipo equipo2 = equipoService.crearEquipo("Proyecto 2");

        Usuario usuario = new Usuario("user@ua");
        usuario.setNombre("NombrePrueba");
        usuario.setPassword("123");
        usuario = usuarioService.registrar(usuario);

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
}
