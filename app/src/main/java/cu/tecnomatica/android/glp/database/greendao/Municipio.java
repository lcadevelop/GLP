package cu.tecnomatica.android.glp.database.greendao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.ToOne;
import org.greenrobot.greendao.DaoException;

@Entity
public class Municipio
{
    @Id(autoincrement = true)
    private Long idmunicipio;
    private Long idprovincia;

    private String nombre;

    private boolean activo;

    @ToOne(joinProperty = "idprovincia")
    private Provincia provincia;
    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /** Used for active entity operations. */
    @Generated(hash = 2144533908)
    private transient MunicipioDao myDao;
    @Generated(hash = 2051001964)
    private transient Long provincia__resolvedKey;

    @Generated(hash = 266036735)
    public Municipio(Long idmunicipio, Long idprovincia, String nombre, boolean activo) {
        this.idmunicipio = idmunicipio;
        this.idprovincia = idprovincia;
        this.nombre = nombre;
        this.activo = activo;
    }

    @Generated(hash = 2081067433)
    public Municipio() {
    }

    public Long getIdmunicipio() {
        return this.idmunicipio;
    }

    public void setIdmunicipio(Long idmunicipio) {
        this.idmunicipio = idmunicipio;
    }

    public String getNombre() {
        return this.nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Long getIdprovincia() {
        return this.idprovincia;
    }

    public void setIdprovincia(Long idprovincia) {
        this.idprovincia = idprovincia;
    }

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

    public boolean getActivo() {
        return this.activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1208019941)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getMunicipioDao() : null;
    }

}
