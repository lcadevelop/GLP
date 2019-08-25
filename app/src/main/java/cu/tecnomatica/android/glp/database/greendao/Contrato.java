package cu.tecnomatica.android.glp.database.greendao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class Contrato
{
    @Id(autoincrement = true)
    private Long idcontrato;

    private String nombre;
    private String descripcion;
    @Generated(hash = 1982827540)
    public Contrato(Long idcontrato, String nombre, String descripcion) {
        this.idcontrato = idcontrato;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }
    @Generated(hash = 1339756915)
    public Contrato() {
    }
    public Long getIdcontrato() {
        return this.idcontrato;
    }
    public void setIdcontrato(Long idcontrato) {
        this.idcontrato = idcontrato;
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
