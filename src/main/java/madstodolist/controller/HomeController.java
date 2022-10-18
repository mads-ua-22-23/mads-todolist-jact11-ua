package madstodolist.controller;


import madstodolist.authentication.ManagerUserSession;
import madstodolist.model.Usuario;
import madstodolist.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.sound.midi.SysexMessage;

@Controller
public class HomeController {

    @Autowired
    private ManagerUserSession managerUserSession;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping ("/about")
    public String about(Model model) {
        Long usuarioId = 0L;
        usuarioId = managerUserSession.usuarioLogeado();
        if (usuarioId == null) return "about";
        else{
            Usuario usuario = usuarioService.findById(usuarioId);
            model.addAttribute("usuario", usuario);
            return "aboutLog";
        }
    }

}
