package cu.tecnomatica.android.glp.database.modelos;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = {@ForeignKey(entity = Cliente.class, parentColumns = "idcliente", childColumns = "idcliente"), @ForeignKey(entity = Puntoventa.class, parentColumns = "idpuntoventa", childColumns = "idpuntoventa")})
public class Compra
{
    @PrimaryKey(autoGenerate = true)
    public Integer idcompra;

    @ColumnInfo(name = "fecha")
    public String fecha;
    @ColumnInfo(name = "idcliente")
    public Integer cliente;
    @ColumnInfo(name = "idpuntoventa")
    public Integer puntoventa;

    public Compra(){}

    public Compra(String fecha, Integer cliente, Integer puntoventa) {
        this.fecha = fecha;
        this.cliente = cliente;
        this.puntoventa = puntoventa;
    }

    public Integer getIdcompra() {
        return idcompra;
    }

    public void setIdcompra(Integer idcompra) {
        this.idcompra = idcompra;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public Integer getCliente() {
        return cliente;
    }

    public void setCliente(Integer cliente) {
        this.cliente = cliente;
    }

    public Integer getPuntoventa() {
        return puntoventa;
    }

    public void setPuntoventa(Integer puntoventa) {
        this.puntoventa = puntoventa;
    }
}
