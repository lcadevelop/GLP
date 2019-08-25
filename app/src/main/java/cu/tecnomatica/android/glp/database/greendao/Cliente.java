package cu.tecnomatica.android.glp.database.greendao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.ToOne;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;

@Entity
public class Cliente
{
    @Id(autoincrement = true)
    private Long idcliente;
    private Long idmunicipio;
    private Long idcontrato;
    private Long idpuntoventa;
    private String numerocontrato;
    private String titular;
    private int cantidadconsumidores;
    private boolean activo;

    @ToOne(joinProperty = "idmunicipio")
    private Municipio municipio;
    @ToOne(joinProperty = "idcontrato")
    private Contrato contrato;
    @ToOne(joinProperty = "idpuntoventa")
    private Puntoventa puntoventa;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /** Used for active entity operations. */
    @Generated(hash = 356477667)
    private transient ClienteDao myDao;
    @Generated(hash = 1166872155)
    public Cliente(Long idcliente, Long idmunicipio, Long idcontrato, Long idpuntoventa,
            String numerocontrato, String titular, int cantidadconsumidores, boolean activo) {
        this.idcliente = idcliente;
        this.idmunicipio = idmunicipio;
        this.idcontrato = idcontrato;
        this.idpuntoventa = idpuntoventa;
        this.numerocontrato = numerocontrato;
        this.titular = titular;
        this.cantidadconsumidores = cantidadconsumidores;
        this.activo = activo;
    }
    @Generated(hash = 1805939709)
    public Cliente() {
    }
    public Long getIdcliente() {
        return this.idcliente;
    }
    public void setIdcliente(Long idcliente) {
        this.idcliente = idcliente;
    }
    public Long getIdmunicipio() {
        return this.idmunicipio;
    }
    public void setIdmunicipio(Long idmunicipio) {
        this.idmunicipio = idmunicipio;
    }
    public Long getIdcontrato() {
        return this.idcontrato;
    }
    public void setIdcontrato(Long idcontrato) {
        this.idcontrato = idcontrato;
    }
    public String getNumerocontrato() {
        return this.numerocontrato;
    }
    public void setNumerocontrato(String numerocontrato) {
        this.numerocontrato = numerocontrato;
    }
    public String getTitular() {
        return this.titular;
    }
    public void setTitular(String titular) {
        this.titular = titular;
    }
    public int getCantidadconsumidores() {
        return this.cantidadconsumidores;
    }
    public void setCantidadconsumidores(int cantidadconsumidores) {
        this.cantidadconsumidores = cantidadconsumidores;
    }
    public boolean getActivo() {
        return this.activo;
    }
    public void setActivo(boolean activo) {
        this.activo = activo;
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
    public void setMunicipio(Municipio municipio) {
        synchronized (this) {
            this.municipio = municipio;
            idmunicipio = municipio == null ? null : municipio.getIdmunicipio();
            municipio__resolvedKey = idmunicipio;
        }
    }
    @Generated(hash = 1127129734)
    private transient Long contrato__resolvedKey;
    @Generated(hash = 218843721)
    private transient Long puntoventa__resolvedKey;
    /** To-one relationship, resolved on first access. */
    @Generated(hash = 1298822447)
    public Contrato getContrato() {
        Long __key = this.idcontrato;
        if (contrato__resolvedKey == null || !contrato__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            ContratoDao targetDao = daoSession.getContratoDao();
            Contrato contratoNew = targetDao.load(__key);
            synchronized (this) {
                contrato = contratoNew;
                contrato__resolvedKey = __key;
            }
        }
        return contrato;
    }
    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1934525561)
    public void setContrato(Contrato contrato) {
        synchronized (this) {
            this.contrato = contrato;
            idcontrato = contrato == null ? null : contrato.getIdcontrato();
            contrato__resolvedKey = idcontrato;
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

    public Long getIdpuntoventa() {
        return this.idpuntoventa;
    }
    public void setIdpuntoventa(Long idpuntoventa) {
        this.idpuntoventa = idpuntoventa;
    }
    /** To-one relationship, resolved on first access. */
    @Generated(hash = 1664386818)
    public Puntoventa getPuntoventa() {
        Long __key = this.idpuntoventa;
        if (puntoventa__resolvedKey == null || !puntoventa__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            PuntoventaDao targetDao = daoSession.getPuntoventaDao();
            Puntoventa puntoventaNew = targetDao.load(__key);
            synchronized (this) {
                puntoventa = puntoventaNew;
                puntoventa__resolvedKey = __key;
            }
        }
        return puntoventa;
    }
    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 326142682)
    public void setPuntoventa(Puntoventa puntoventa) {
        synchronized (this) {
            this.puntoventa = puntoventa;
            idpuntoventa = puntoventa == null ? null : puntoventa.getIdpuntoventa();
            puntoventa__resolvedKey = idpuntoventa;
        }
    }
    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 48169481)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getClienteDao() : null;
    }
}