package madstodolist.controller;

import madstodolist.authentication.ManagerUserSession;
import madstodolist.model.Equipo;
import madstodolist.model.Usuario;
import madstodolist.service.EquipoService;
import madstodolist.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class EquipoController {

    @Autowired
    EquipoService equipoService;
    @Autowired
    UsuarioService usuarioService;
    @Autowired
    ManagerUserSession managerUserSession;

    @GetMapping("/equipos")
    public String listadoEquipos(Model model){
        Long usuarioId=0L;
        usuarioId=managerUserSession.usuarioLogeado();
        Usuario usuario=usuarioService.findById(usuarioId);
        model.addAttribute("usuario", usuario);
        List<Equipo>allEquipos=equipoService.findAllOrderedByName();
        model.addAttribute("equipos", allEquipos);
        return "listaEquipos";
    }

    @GetMapping("/equipos/{id}")
    public String miembrosEquipos(@PathVariable(value="id") Long equipoId, Model model){
        Long usuarioId=0L;
        usuarioId=managerUserSession.usuarioLogeado();
        Usuario usuario=usuarioService.findById(usuarioId);
        model.addAttribute("usuario", usuario);
        Equipo equipo=equipoService.findById(equipoId);
        List<Usuario>allUsuarios=equipoService.usuariosEquipo(equipoId);
        model.addAttribute("equipo", equipo);
        model.addAttribute("usuarios", allUsuarios);
        return "integrantesEquipo";
    }

    @GetMapping("/equipos/nuevo")
    public String formNuevoEquipo(@ModelAttribute EquipoData equipoData, Model model,
                                 HttpSession session) {

        Long usuarioId=managerUserSession.usuarioLogeado();
        Usuario usuario=usuarioService.findById(usuarioId);
        model.addAttribute("usuario", usuario);
        return "formCrearEquipo";
    }

    @PostMapping("/equipos/nuevo")
    public String nuevaTarea(@ModelAttribute EquipoData equipoData, Model model, RedirectAttributes flash,
                             HttpSession session) {
        Long usuarioId=managerUserSession.usuarioLogeado();
        Usuario usuario=usuarioService.findById(usuarioId);
        model.addAttribute("usuario", usuario);
        equipoService.crearEquipo(equipoData.getTitulo());
        return "redirect:/equipos";
    }
}
