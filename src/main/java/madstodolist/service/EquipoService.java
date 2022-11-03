package madstodolist.service;

import madstodolist.model.Equipo;
import madstodolist.model.EquipoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EquipoService {
    Logger logger = LoggerFactory.getLogger(EquipoService.class);

    @Autowired
    EquipoRepository equipoRepository;

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
}
