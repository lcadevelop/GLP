package cu.tecnomatica.android.glp.database.modelos;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = {@ForeignKey(entity = Contrato.class, parentColumns = "idcontrato", childColumns = "idcontrato"), @ForeignKey(entity = Municipio.class, parentColumns = "idmunicipio", childColumns = "idmunicipio")})
public class Cliente
{
    @PrimaryKey(autoGenerate = true)
    public Integer idcliente;

    @ColumnInfo(name = "numerocontrato")
    public String numerocontrato;
    @ColumnInfo(name = "titular")
    public String titular;
    @ColumnInfo(name = "cantidadconsumidores")
    public int cantidadconsumidores;
    @ColumnInfo(name = "idmunicipio")
    public Integer idmunicipio;
    @ColumnInfo(name = "idcontrato")
    public Integer idcontrato;

    public Cliente(){}

    public Cliente(String numerocontrato, String titular, int cantidadconsumidores, Integer idmunicipio, Integer idcontrato) {
        this.numerocontrato = numerocontrato;
        this.titular = titular;
        this.cantidadconsumidores = cantidadconsumidores;
        this.idmunicipio = idmunicipio;
        this.idcontrato = idcontrato;
    }

    public Integer getIdcliente() {
        return idcliente;
    }

    public void setIdcliente(Integer idcliente) {
        this.idcliente = idcliente;
    }

    public String getNumerocontrato() {
        return numerocontrato;
    }

    public void setNumerocontrato(String numerocontrato) {
        this.numerocontrato = numerocontrato;
    }

    public String getTitular() {
        return titular;
    }

    public void setTitular(String titular) {
        this.titular = titular;
    }

    public int getCantidadconsumidores() {
        return cantidadconsumidores;
    }

    public void setCantidadconsumidores(int cantidadconsumidores) {
        this.cantidadconsumidores = cantidadconsumidores;
    }

    public Integer getMunicipio() {
        return idmunicipio;
    }

    public void setMunicipio(Integer idmunicipio) {
        this.idmunicipio = idmunicipio;
    }

    public Integer getContrato() {
        return idcontrato;
    }

    public void setTipocontrato(Integer idcontrato) {
        this.idcontrato = idcontrato;
    }
}
