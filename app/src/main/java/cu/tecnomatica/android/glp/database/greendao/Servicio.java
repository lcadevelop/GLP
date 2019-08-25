package cu.tecnomatica.android.glp.database.greendao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class Servicio
{
    @Id(autoincrement = true)
    private Long idservicio;

    private String nombre;
    private String descripcion;
    @Generated(hash = 1825408839)
    public Servicio(Long idservicio, String nombre, String descripcion) {
        this.idservicio = idservicio;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }
    @Generated(hash = 780542706)
    public Servicio() {
    }
    public Long getIdservicio() {
        return this.idservicio;
    }
    public void setIdservicio(Long idservicio) {
        this.idservicio = idservicio;
    }
    public String getNombre() {
        return this.nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public String getDescripcion() {
        return this.descripcion;
    }
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
