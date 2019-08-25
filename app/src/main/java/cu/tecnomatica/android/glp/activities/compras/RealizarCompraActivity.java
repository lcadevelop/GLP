package cu.tecnomatica.android.glp.activities.compras;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import org.greenrobot.greendao.database.Database;
import java.util.Calendar;
import java.util.List;
import cu.tecnomatica.android.glp.R;
import cu.tecnomatica.android.glp.database.greendao.Cliente;
import cu.tecnomatica.android.glp.database.greendao.Compra;
import cu.tecnomatica.android.glp.database.greendao.DaoMaster;
import cu.tecnomatica.android.glp.database.greendao.DaoSession;

public class RealizarCompraActivity extends AppCompatActivity
{
    private DatePicker datePicker;
    private Button boton;
    private Cliente clienteactivo;
    private List<Cliente> clienteList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_realizar_compra);

        final int[] ciclo = new int[]{44,26,24,21,17,14,12,10,10,8};

        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "daoglp.db");
        Database database = helper.getWritableDb();
        final DaoSession daoSession = new DaoMaster(database).newSession();

        datePicker = (DatePicker)findViewById(R.id.id_fecha_compra);

        clienteList = daoSession.getClienteDao().loadAll();

        for (int i = 0; i < clienteList.size(); i++)
        {
            if (clienteList.get(i).getActivo())
            {
                clienteactivo = clienteList.get(i);
            }
        }


        boton = (Button)findViewById(R.id.id_boton_comprar);

        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                int dia = datePicker.getDayOfMonth();
                int mes = datePicker.getMonth() + 1;
                int anno = datePicker.getYear();

                Compra compra = new Compra();
                compra.setFecha(Integer.toString(dia) + "/" + Integer.toString(mes) + "/" + Integer.toString(anno));
                compra.setCliente(clienteactivo);
                compra.setIdcliente(clienteactivo.getIdcliente());

                if (clienteactivo.getContrato().getNombre().equals("Normado"))
                {
                    int diasasumar = 0;

                    for (int i = 1; i < 11; i++)
                    {
                        if (clienteactivo.getCantidadconsumidores() == i)
                        {
                            diasasumar = ciclo[i-1];
                        }
                    }

                    Calendar calendar = Calendar.getInstance();
                    calendar.set(anno, mes, dia);
                    calendar.add(5, diasasumar);
                    compra.setUltimodia(calendar.get(5));
                    compra.setUltimomes(calendar.get(2)-1);
                    compra.setUltimoanno(calendar.get(1));
                    compra.setProximaventa(calendar.get(5) + "/" + calendar.get(2) + "/" + calendar.get(1));
                }

                daoSession.insert(compra);
                Intent intent = new Intent(RealizarCompraActivity.this, CompraActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
