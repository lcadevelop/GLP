package cu.tecnomatica.android.glp.database.modelos;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Municipio
{
    @PrimaryKey(autoGenerate = true)
    public Integer idmunicipio;

    @ColumnInfo(name = "nombre")
    public String nombre;

    public Municipio(){}

    public Municipio(String nombre) {
        this.nombre = nombre;
    }

    public Integer getIdmunicipio() {
        return idmunicipio;
    }

    public void setIdmunicipio(Integer idmunicipio) {
        this.idmunicipio = idmunicipio;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
