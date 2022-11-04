package madstodolist;

import madstodolist.authentication.ManagerUserSession;
import madstodolist.model.Equipo;
import madstodolist.model.EquipoRepository;
import madstodolist.model.Usuario;
import madstodolist.model.UsuarioRepository;
import madstodolist.service.EquipoService;
import madstodolist.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts="/clean-db.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
//
// A diferencia de los tests web de tarea, donde usábamos los datos
// de prueba de la base de datos, aquí vamos a practicar otro enfoque:
// moquear el usuarioService.
public class EquipoWebTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    @MockBean
    private ManagerUserSession managerUserSession;

    @Autowired
    UsuarioRepository usuarioRepository;
    @Autowired
    EquipoRepository equipoRepository;
    @MockBean
    private UsuarioService usuarioService;

    @MockBean
    private EquipoService equipoService;

    @Test
    public void ListadoUsuariosTest() throws Exception{
        Usuario user = new Usuario("prueba@ua");
        user.setPassword("123");
        usuarioService.registrar(user);

        Equipo equipo=new Equipo("equipo");
        equipoRepository.save(equipo);

        this.mockMvc.perform(get("/equipos"))
                .andExpect((content().string(allOf(
                        containsString("Nombre"),
                        containsString(equipo.getNombre())
                ))));
    }
}
