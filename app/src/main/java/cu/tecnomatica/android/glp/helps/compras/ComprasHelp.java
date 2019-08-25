package cu.tecnomatica.android.glp.helps.compras;

import java.util.Date;

public class ComprasHelp
{
    private String titular;
    private String fecha;

    public ComprasHelp(String titular, String fecha) {
        this.titular = titular;
        this.fecha = fecha;
    }

    public String getTitular() {
        return titular;
    }

    public void setTitular(String titular) {
        this.titular = titular;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
}
