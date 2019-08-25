package cu.tecnomatica.android.glp.database.modelos;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = @ForeignKey(entity = Municipio.class, parentColumns = "idmunicipio", childColumns = "idmunicipio"))
public class Casacomercial
{
    @PrimaryKey(autoGenerate = true)
    public Integer  idcasacomercial;

    @ColumnInfo(name = "codigo")
    public String codigo;
    @ColumnInfo(name = "nombre")
    public String nombre;
    @ColumnInfo(name = "direccion")
    public String direccion;
    @ColumnInfo(name = "telefono")
    public String telefono;
    @ColumnInfo(name = "idmunicipio")
    public Integer idmunicipio;

    public Casacomercial(){}

    public Casacomercial(String codigo, String nombre, String direccion, String telefono, Integer idmunicipio) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.direccion = direccion;
        this.telefono = telefono;
        this.idmunicipio = idmunicipio;
    }

    public Integer getIdcasacomercial() {
        return idcasacomercial;
    }

    public void setIdcasacomercial(Integer idcasacomercial) {
        this.idcasacomercial = idcasacomercial;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public Integer getMunicipio() {
        return idmunicipio;
    }

    public void setMunicipio(Integer idmunicipio) {
        this.idmunicipio = idmunicipio;
    }
}
