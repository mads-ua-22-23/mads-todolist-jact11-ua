package madstodolist.controller;

import madstodolist.controller.exception.UsuarioNoLogeadoException;
import madstodolist.model.Usuario;
import madstodolist.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class UsuarioController {

    @Autowired
    UsuarioService usuarioService;

    @GetMapping("/registrados")
    public String usuarios(Model model){
        List<Usuario> allUsuarios = usuarioService.listarUsuario();
        model.addAttribute("usuarios", allUsuarios);
        return "listaUsuarios";
    }

    @GetMapping("/registrados/{id}")
    public String descripcionUsuarios(@PathVariable(value="id") Long usuarioId, Model model){
        Usuario usuario = usuarioService.findById(usuarioId);
        model.addAttribute("usuarios", usuario);
        return "descripcionUsuarios";
    }
}