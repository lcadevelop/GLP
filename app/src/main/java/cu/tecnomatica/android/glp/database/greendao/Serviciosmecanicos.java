package cu.tecnomatica.android.glp.database.greendao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.ToOne;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;

@Entity
public class Serviciosmecanicos
{
    @Id
    private Long idserviciosmecanicos;
    private Long idprovincia;

    private String nombre;
    private String direccion;
    private String telefono;
    private String horario;
    private String latitud;
    private String longitud;

    @ToOne(joinProperty = "idprovincia")
    private Provincia provincia;
    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /** Used for active entity operations. */
    @Generated(hash = 1203836326)
    private transient ServiciosmecanicosDao myDao;

    @Generated(hash = 247502955)
    public Serviciosmecanicos(Long idserviciosmecanicos, Long idprovincia,
            String nombre, String direccion, String telefono, String horario,
            String latitud, String longitud) {
        this.idserviciosmecanicos = idserviciosmecanicos;
        this.idprovincia = idprovincia;
        this.nombre = nombre;
        this.direccion = direccion;
        this.telefono = telefono;
        this.horario = horario;
        this.latitud = latitud;
        this.longitud = longitud;
    }

    @Generated(hash = 1663119382)
    public Serviciosmecanicos() {
    }

    public Long getIdserviciosmecanicos() {
        return this.idserviciosmecanicos;
    }

    public void setIdserviciosmecanicos(Long idserviciosmecanicos) {
        this.idserviciosmecanicos = idserviciosmecanicos;
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

    public String getDireccion() {
        return this.direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return this.telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getHorario() {
        return this.horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
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

    @Generated(hash = 2051001964)
    private transient Long provincia__resolvedKey;

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 234473121)
    public Provincia getProvincia() {
        Long __key = this.idprovincia;
        if (provincia__resolvedKey == null
                || !provincia__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            ProvinciaDao targetDao = daoSession.getProvinciaDao();
            Provincia provinciaNew = targetDao.load(__key);
            synchronized (this) {
                provincia = provinciaNew;
                provincia__resolvedKey = __key;
            }
        }
        return provincia;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1140887961)
    public void setProvincia(Provincia provincia) {
        synchronized (this) {
            this.provincia = provincia;
            idprovincia = provincia == null ? null : provincia.getIdprovincia();
            provincia__resolvedKey = idprovincia;
        }
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 2143257555)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getServiciosmecanicosDao() : null;
    }
}
