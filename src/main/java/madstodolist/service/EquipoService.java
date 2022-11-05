package madstodolist.service;

import madstodolist.model.Equipo;
import madstodolist.model.EquipoRepository;
import madstodolist.model.Usuario;
import madstodolist.model.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.transaction.TransactionScoped;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public class EquipoService {
    Logger logger = LoggerFactory.getLogger(EquipoService.class);

    @Autowired
    EquipoRepository equipoRepository;
    @Autowired
    UsuarioRepository usuarioRepository;

    @Transactional
    public Equipo crearEquipo(String nombre) {
        Equipo equipo = new Equipo(nombre);
        equipoRepository.save(equipo);
        return equipo;
    }

    @Transactional(readOnly = true)
    public Equipo recuperarEquipo(Long id) {
        return equipoRepository.findById(id).orElse(null);
    }

    @Transactional(readOnly = true)
    public List<Equipo>findAllOrderedByName(){
        List<Equipo> list_equipo=equipoRepository.findAll();
        Collections.sort(list_equipo, Comparator.comparing(Equipo::getNombre));
        return list_equipo;
    }

    @Transactional
    public void addUsuarioEquipo(Long idUsuario, Long idEquipo) {
        Usuario usuario=usuarioRepository.findById(idUsuario).orElse(null);
        Equipo equipo=equipoRepository.findById(idEquipo).orElse(null);
        equipo.addUsuario(usuario);
    }

    @Transactional(readOnly = true)
    public List<Usuario>usuariosEquipo(Long id){
        Equipo equipo=equipoRepository.findById(id).orElse(null);
        List<Usuario>usuarios= new ArrayList<>(equipo.getUsuarios());
        return usuarios;
    }

    @Transactional(readOnly = true)
    public Equipo findById(Long equipoId) {
        return equipoRepository.findById(equipoId).orElse(null);
    }
}