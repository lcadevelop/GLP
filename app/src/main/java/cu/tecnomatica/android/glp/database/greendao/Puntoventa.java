package cu.tecnomatica.android.glp.database.greendao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.ToOne;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;

@Entity
public class Puntoventa
{
    @Id(autoincrement = true)
    private Long idpuntoventa;
    private Long idmunicipio;
    private String codigo;
    private String nombre;
    private String direccion;
    private String telefono;
    private String horario;
    private String latitud;
    private String longitud;

    @ToOne(joinProperty = "idmunicipio")
    private Municipio municipio;
    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /** Used for active entity operations. */
    @Generated(hash = 528104442)
    private transient PuntoventaDao myDao;

    @Generated(hash = 943729914)
    public Puntoventa(Long idpuntoventa, Long idmunicipio, String codigo, String nombre,
            String direccion, String telefono, String horario, String latitud,
            String longitud)
    {
        this.idpuntoventa = idpuntoventa;
        this.idmunicipio = idmunicipio;
        this.codigo = codigo;
        this.nombre = nombre;
        this.direccion = direccion;
        this.telefono = telefono;
        this.horario = horario;
        this.latitud = latitud;
        this.longitud = longitud;
    }

    @Generated(hash = 693855814)
    public Puntoventa() {
    }

    public Long getIdpuntoventa() {
        return this.idpuntoventa;
    }

    public void setIdpuntoventa(Long idpuntoventa) {
        this.idpuntoventa = idpuntoventa;
    }

    public Long getIdmunicipio() {
        return this.idmunicipio;
    }

    public void setIdmunicipio(Long idmunicipio) {
        this.idmunicipio = idmunicipio;
    }

    public String getCodigo() {
        return this.codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
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

    @Generated(hash = 1751975971)
    private transient Long municipio__resolvedKey;

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 99177501)
    public Municipio getMunicipio() {
        Long __key = this.idmunicipio;
        if (municipio__resolvedKey == null
                || !municipio__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            MunicipioDao targetDao = daoSession.getMunicipioDao();
            Municipio municipioNew = targetDao.load(__key);
            synchronized (this) {
                municipio = municipioNew;
                municipio__resolvedKey = __key;
            }
        }
        return municipio;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 465681750)
    public void setMunicipio(Municipio municipio)
    {
        synchronized (this)
        {
            this.municipio = municipio;
            idmunicipio = municipio == null ? null : municipio.getIdmunicipio();
            municipio__resolvedKey = idmunicipio;
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
    public void update()
    {
        if (myDao == null)
        {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 834856344)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getPuntoventaDao() : null;
    }
}
