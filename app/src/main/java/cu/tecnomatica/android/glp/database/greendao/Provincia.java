package cu.tecnomatica.android.glp.database.greendao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class Provincia
{
    @Id(autoincrement = true)
    private Long idprovincia;
    private String nombre;
    private String latitud;
    private String longitud;
    private boolean activa;

    @Generated(hash = 1190094091)
    public Provincia(Long idprovincia, String nombre, String latitud,
            String longitud, boolean activa) {
        this.idprovincia = idprovincia;
        this.nombre = nombre;
        this.latitud = latitud;
        this.longitud = longitud;
        this.activa = activa;
    }

    @Generated(hash = 1282290553)
    public Provincia() {
    }

    public Long getIdprovincia() {
        return this.idprovincia;
    }

    public void setIdprovincia(Long idprovincia) {
        this.idprovincia = idprovincia;
    }

    public String getNombre() {
        return this.nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public boolean getActiva() {
        return this.activa;
    }

    public void setActiva(boolean activa) {
        this.activa = activa;
    }

    public String getLatitud() {
        return this.latitud;
    }

    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    public String getLongitud() {
        return this.longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }
}
