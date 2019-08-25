package cu.tecnomatica.android.glp.database.modelos;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Tramite
{
    @PrimaryKey(autoGenerate = true)
    public Integer idtramite;

    @ColumnInfo(name = "nombre")
    public String nombre;
    @ColumnInfo(name = "descripcion")
    public String descripcion;

    public Tramite(){}

    public Tramite(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public Integer getIdtramite() {
        return idtramite;
    }

    public void setIdtramite(Integer idtramite) {
        this.idtramite = idtramite;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
