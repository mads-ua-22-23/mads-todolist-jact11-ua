package madstodolist.controller;

import madstodolist.model.Equipo;
import madstodolist.model.Usuario;
import madstodolist.service.EquipoService;
import madstodolist.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class EquipoController {

    @Autowired
    EquipoService equipoService;

    @GetMapping("/equipos")
    public String listadoEquipos(Model model){
        List<Equipo>allEquipos=equipoService.findAllOrderedByName();
        model.addAttribute("equipos", allEquipos);
        return "listaEquipos";
    }
}
