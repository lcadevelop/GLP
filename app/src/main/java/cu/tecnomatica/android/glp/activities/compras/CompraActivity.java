package cu.tecnomatica.android.glp.activities.compras;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import org.greenrobot.greendao.database.Database;
import java.util.Calendar;
import java.util.List;
import cu.tecnomatica.android.glp.R;
import cu.tecnomatica.android.glp.database.greendao.Cliente;
import cu.tecnomatica.android.glp.database.greendao.Compra;
import cu.tecnomatica.android.glp.database.greendao.CompraDao;
import cu.tecnomatica.android.glp.database.greendao.DaoMaster;
import cu.tecnomatica.android.glp.database.greendao.DaoSession;
import cu.tecnomatica.android.glp.helps.compras.ComprasHelp;

public class CompraActivity extends AppCompatActivity
{
    private ComprasHelp[] comprasHelps;
    private List<Compra> compras;
    private ListView listacompras;
    private List<Cliente> listaclientes;
    private Cliente clienteactivo;
    private TextView textocliente;
    private TextView tipocontratocliente;
    private TextView numerocontratocliente;
    private TextView municipiocliente;
    private TextView puntodeventacliente;
    private TextView proximaventa;
    private ImageView imagen_calendario;
    private RelativeLayout layoutcalendario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compra);

        listacompras = (ListView)findViewById(R.id.idlistacompras);

        FloatingActionButton boton_comprar = (FloatingActionButton) findViewById(R.id.boton_comprar);
        boton_comprar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(CompraActivity.this, RealizarCompraActivity.class);
                startActivity(intent);
                finish();
            }
        });

        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "daoglp.db");
        Database database = helper.getWritableDb();
        DaoSession daoSession = new DaoMaster(database).newSession();

        listaclientes = daoSession.getClienteDao().loadAll();
        clienteactivo = new Cliente();

        for (int i =0; i < listaclientes.size(); i++)
        {
            if (listaclientes.get(i).getActivo() == true)
            {
                clienteactivo = listaclientes.get(i);
            }
        }

        textocliente = (TextView)findViewById(R.id.idtextocliente);
        textocliente.setText("Titular: " + clienteactivo.getTitular());

        tipocontratocliente = (TextView)findViewById(R.id.idtipocontratocliente);
        tipocontratocliente.setText("Tipo de Contrato: " + clienteactivo.getContrato().getNombre());

        numerocontratocliente = (TextView)findViewById(R.id.idnumerocontratocliente);
        numerocontratocliente.setText("Número de Contrato: " + clienteactivo.getNumerocontrato());

        municipiocliente = (TextView)findViewById(R.id.idmunicipioocliente);
        municipiocliente.setText("Municipio: " + clienteactivo.getMunicipio().getNombre());

        puntodeventacliente = (TextView)findViewById(R.id.idpuntoventaocliente);
        puntodeventacliente.setText("Punto de Venta: " + clienteactivo.getPuntoventa().getNombre());

        layoutcalendario = (RelativeLayout)findViewById(R.id.id_layout_calendario);
        proximaventa = (TextView)findViewById(R.id.idproximaventa);
        imagen_calendario = (ImageView)findViewById(R.id.idimagencalendario);

        compras = daoSession.getCompraDao().queryBuilder().where(CompraDao.Properties.Idcliente.like(clienteactivo.getIdcliente().toString())).list();
        comprasHelps = new ComprasHelp[compras.size()];

        int j = 0;
        for (int i = compras.size()-1; i >= 0; i--)
        {
            ComprasHelp comprasHelp = new ComprasHelp(compras.get(i).getCliente().getTitular(), compras.get(i).getFecha());
            comprasHelps[j] = comprasHelp;
            j++;
        }

        if (compras.size() == 0 || !clienteactivo.getContrato().getNombre().equals("Normado"))
        {
            layoutcalendario.setVisibility(View.GONE);
        }
        else
        {
            layoutcalendario.setVisibility(View.VISIBLE);
            Compra comprafinal = compras.get(compras.size() - 1);
            proximaventa.setText("Próxima Venta: " + comprafinal.getProximaventa());
        }

        AdaptadaroCompras adaptadaroCompras = new AdaptadaroCompras(this, comprasHelps);
        listacompras.setAdapter(adaptadaroCompras);

        imagen_calendario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                AlertDialog.Builder alertcalendario = new AlertDialog.Builder(CompraActivity.this);
                alertcalendario.setTitle("Agregar Cita para Compra del Gas al Calendario");
                //alertcalendario.setMessage("Agregar Compra del Gas al Calendario");
                alertcalendario.setCancelable(false);
                alertcalendario.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                    }
                });
                alertcalendario.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        int dia = compras.get(compras.size()-1).getUltimodia();
                        int mes = compras.get(compras.size()-1).getUltimomes();
                        int anno = compras.get(compras.size()-1).getUltimoanno();

                        Calendar calendar = Calendar.getInstance();
                        calendar.set(anno, mes, dia);
                        calendar.set(Calendar.HOUR_OF_DAY, 10);

                        Intent intent = new Intent(Intent.ACTION_EDIT);
                        intent.setType("vnd.android.cursor.item/event");
                        intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, calendar.getTimeInMillis());
                        intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, calendar.getTimeInMillis()+60*60*1000);
                        intent.putExtra(CalendarContract.Events.ALL_DAY, false);
                        intent.putExtra(CalendarContract.Events.RRULE, "FREQ=DAILY");
                        intent.putExtra(CalendarContract.Events.TITLE, "Comprar Gas Licuado");
                        intent.putExtra(CalendarContract.Events.DESCRIPTION, "Comprar Gas Licuado");

                        startActivity(intent);

                        Toast toast = Toast.makeText(getApplicationContext(), "Cita Agregada al Calendario", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                });

                alertcalendario.show();
            }
        });
    }

    class AdaptadaroCompras extends ArrayAdapter<ComprasHelp>
    {
        AdaptadaroCompras(Context context, ComprasHelp[] comprasHelps)
        {
            super(context, R.layout.list_item_compras, comprasHelps);
        }

        public View getView(final int position, View convertView, ViewGroup parent)
        {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            View item = inflater.inflate(R.layout.list_item_compras, null);

            TextView titular = (TextView)item.findViewById(R.id.id_titular_texto_cp);
            titular.setText(comprasHelps[position].getTitular());

            TextView fecha = (TextView)item.findViewById(R.id.id_fecha_texto_cp);
            fecha.setText(comprasHelps[position].getFecha());

            return item;
        }
    }
}