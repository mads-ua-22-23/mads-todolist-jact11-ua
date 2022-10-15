package madstodolist;

import madstodolist.authentication.ManagerUserSession;
import madstodolist.model.Usuario;
import madstodolist.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts="/clean-db.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class AcercaDeWebTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    @Autowired
    private ManagerUserSession managerUserSession;

    @Autowired
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
                .andExpect(content().string(containsString("Registro")))
                .andExpect(content().string(containsString("Login")));
    }

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

    }
}
