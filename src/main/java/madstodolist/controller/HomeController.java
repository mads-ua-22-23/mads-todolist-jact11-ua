package madstodolist.controller;

import madstodolist.authentication.ManagerUserSession;
import madstodolist.controller.exception.UsuarioNoLogeadoException;
import madstodolist.model.Usuario;
import madstodolist.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;
import java.util.List;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    UsuarioService usuarioService;

    @GetMapping("/registrados")
    public String usuarios(Model model){
        List<Usuario> allUsuarios = usuarioService.listarUsuario();
        model.addAttribute("usuarios", allUsuarios);
        return "listaUsuarios";
    }

    @GetMapping("/about")
    public String about(Model model) {
        return "about";
    }
}
