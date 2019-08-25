package cu.tecnomatica.android.glp.helps.puntoventa;

public class PuntoVentaHelp
{
    private String direccion;
    private String horario;
    private String telefono;
    private String latitud;
    private String longitud;

    public PuntoVentaHelp(String direccion, String horario, String telefono, String latitud, String longitud)
    {
        this.direccion = direccion;
        this.horario = horario;
        this.telefono = telefono;
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getLatitud() {
        return latitud;
    }

    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    public String getLongitud() {
        return longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }
}
