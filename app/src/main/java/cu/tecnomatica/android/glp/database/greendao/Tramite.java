package cu.tecnomatica.android.glp.database.greendao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class Tramite
{
    @Id(autoincrement = true)
    private Long idtramite;

    private String nombre;
    private String descripcion;
    @Generated(hash = 1726091034)
    public Tramite(Long idtramite, String nombre, String descripcion) {
        this.idtramite = idtramite;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }
    @Generated(hash = 1988047204)
    public Tramite() {
    }
    public Long getIdtramite() {
        return this.idtramite;
    }
    public void setIdtramite(Long idtramite) {
        this.idtramite = idtramite;
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
