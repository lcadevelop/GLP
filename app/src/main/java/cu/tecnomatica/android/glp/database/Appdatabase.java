package cu.tecnomatica.android.glp.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import cu.tecnomatica.android.glp.database.modelos.Casacomercial;
import cu.tecnomatica.android.glp.database.modelos.CasacomercialDao;
import cu.tecnomatica.android.glp.database.modelos.Cliente;
import cu.tecnomatica.android.glp.database.modelos.ClienteDao;
import cu.tecnomatica.android.glp.database.modelos.Compra;
import cu.tecnomatica.android.glp.database.modelos.CompraDao;
import cu.tecnomatica.android.glp.database.modelos.Contrato;
import cu.tecnomatica.android.glp.database.modelos.ContratoDao;
import cu.tecnomatica.android.glp.database.modelos.Municipio;
import cu.tecnomatica.android.glp.database.modelos.MunicipioDao;
import cu.tecnomatica.android.glp.database.modelos.Puntoventa;
import cu.tecnomatica.android.glp.database.modelos.PuntoventaDao;
import cu.tecnomatica.android.glp.database.modelos.Servicio;
import cu.tecnomatica.android.glp.database.modelos.ServicioDao;
import cu.tecnomatica.android.glp.database.modelos.Tramite;
import cu.tecnomatica.android.glp.database.modelos.TramiteDao;

@Database(entities = {Casacomercial.class, Cliente.class, Compra.class, Contrato.class,Municipio.class, Puntoventa.class, Servicio.class, Tramite.class}, version = 3)
public abstract class Appdatabase extends RoomDatabase
{
    public abstract CasacomercialDao casacomercialDao();
    public abstract ClienteDao clienteDao();
    public abstract CompraDao compraDao();
    public abstract ContratoDao contratoDao();
    public abstract MunicipioDao municipioDao();
    public abstract PuntoventaDao puntoventaDao();
    public abstract ServicioDao servicioDao();
    public abstract TramiteDao tramiteDao();
}
