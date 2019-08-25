package cu.tecnomatica.android.glp.database.greendao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.ToOne;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;

@Entity
public class Compra
{
    @Id(autoincrement = true)
    private Long idcompra;
    private Long idcliente;
    private Long idpuntoventa;

    private String fecha;
    private String proximaventa;
    private Integer ultimodia;
    private Integer ultimomes;
    private Integer ultimoanno;

    @ToOne(joinProperty = "idcliente")
    private Cliente cliente;
    @ToOne(joinProperty = "idpuntoventa")
    private Puntoventa puntoventa;
    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /** Used for active entity operations. */
    @Generated(hash = 259798488)
    private transient CompraDao myDao;
    @Generated(hash = 293333520)
    public Compra(Long idcompra, Long idcliente, Long idpuntoventa, String fecha,
            String proximaventa, Integer ultimodia, Integer ultimomes,
            Integer ultimoanno) {
        this.idcompra = idcompra;
        this.idcliente = idcliente;
        this.idpuntoventa = idpuntoventa;
        this.fecha = fecha;
        this.proximaventa = proximaventa;
        this.ultimodia = ultimodia;
        this.ultimomes = ultimomes;
        this.ultimoanno = ultimoanno;
    }
    @Generated(hash = 1056029622)
    public Compra() {
    }
    public Long getIdcompra() {
        return this.idcompra;
    }
    public void setIdcompra(Long idcompra) {
        this.idcompra = idcompra;
    }
    public Long getIdcliente() {
        return this.idcliente;
    }
    public void setIdcliente(Long idcliente) {
        this.idcliente = idcliente;
    }
    public Long getIdpuntoventa() {
        return this.idpuntoventa;
    }
    public void setIdpuntoventa(Long idpuntoventa) {
        this.idpuntoventa = idpuntoventa;
    }
    public String getFecha() {
        return this.fecha;
    }
    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
    public String getProximaventa() {
        return this.proximaventa;
    }
    public void setProximaventa(String proximaventa) {
        this.proximaventa = proximaventa;
    }
    public Integer getUltimodia() {
        return this.ultimodia;
    }
    public void setUltimodia(Integer ultimodia) {
        this.ultimodia = ultimodia;
    }
    public Integer getUltimomes() {
        return this.ultimomes;
    }
    public void setUltimomes(Integer ultimomes) {
        this.ultimomes = ultimomes;
    }
    public Integer getUltimoanno() {
        return this.ultimoanno;
    }
    public void setUltimoanno(Integer ultimoanno) {
        this.ultimoanno = ultimoanno;
    }
    @Generated(hash = 1668724671)
    private transient Long cliente__resolvedKey;
    /** To-one relationship, resolved on first access. */
    @Generated(hash = 895772864)
    public Cliente getCliente() {
        Long __key = this.idcliente;
        if (cliente__resolvedKey == null || !cliente__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            ClienteDao targetDao = daoSession.getClienteDao();
            Cliente clienteNew = targetDao.load(__key);
            synchronized (this) {
                cliente = clienteNew;
                cliente__resolvedKey = __key;
            }
        }
        return cliente;
    }
    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 703308034)
    public void setCliente(Cliente cliente) {
        synchronized (this) {
            this.cliente = cliente;
            idcliente = cliente == null ? null : cliente.getIdcliente();
            cliente__resolvedKey = idcliente;
        }
    }
    @Generated(hash = 218843721)
    private transient Long puntoventa__resolvedKey;
    /** To-one relationship, resolved on first access. */
    @Generated(hash = 1664386818)
    public Puntoventa getPuntoventa() {
        Long __key = this.idpuntoventa;
        if (puntoventa__resolvedKey == null
                || !puntoventa__resolvedKey.equals(__key)) {
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
    @Generated(hash = 1993970361)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getCompraDao() : null;
    }



}
