package madstodolist;

import madstodolist.model.Equipo;
import madstodolist.model.EquipoRepository;
import madstodolist.model.Usuario;
import madstodolist.service.EquipoService;
import madstodolist.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.params.shadow.com.univocity.parsers.conversions.Conversions.string;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest
@Sql(scripts = "/clean-db.sql", executionPhase = AFTER_TEST_METHOD)
@AutoConfigureMockMvc
public class EquipoWebTest {

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
                        containsString("1"),
                        containsString(equipo2.getNombre()),
                        containsString("2")
                ))));
    }
}
