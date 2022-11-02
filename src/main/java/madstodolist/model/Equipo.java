package madstodolist.model;

import javax.persistence.*;
import java.io.Serializable;
@Entity
@Table(name="equipos")
public class Equipo implements Serializable {
    private String nombre;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Equipo(){}
    public Equipo(String nombre){
        this.nombre=nombre;
    }
    public String getNombre(){
        return nombre;
    }
    public Long getId(){
        return id;
    }
}

