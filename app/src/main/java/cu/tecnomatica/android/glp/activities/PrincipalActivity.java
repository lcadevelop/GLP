package cu.tecnomatica.android.glp.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import android.view.View;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import org.greenrobot.greendao.database.Database;
import java.util.List;
import cu.tecnomatica.android.glp.R;
import cu.tecnomatica.android.glp.activities.ayuda.AyudaActivity;
import cu.tecnomatica.android.glp.activities.cliente.NuevoClienteActivity;
import cu.tecnomatica.android.glp.activities.compras.CompraActivity;
import cu.tecnomatica.android.glp.activities.configuracion.ConfiguracionActivity;
import cu.tecnomatica.android.glp.activities.informaciones.InformacionesActivity;
import cu.tecnomatica.android.glp.activities.localizacion.MapaActivity;
import cu.tecnomatica.android.glp.activities.servicios.ServiciosActivity;
import cu.tecnomatica.android.glp.activities.tramites.TramitesActivity;
import cu.tecnomatica.android.glp.database.greendao.Cliente;
import cu.tecnomatica.android.glp.database.greendao.DaoMaster;
import cu.tecnomatica.android.glp.database.greendao.DaoSession;
import cu.tecnomatica.android.glp.database.greendao.Provincia;

public class PrincipalActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
    private List<Provincia> provincias;
    private List<Cliente> listaclientes;
    private List<Cliente> listarecorrer;
    private Spinner spinnerclientes;
    private TextView texto_contrato_cliente;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ImageView principal_tramites = findViewById(R.id.id_principal_tramites);
        ImageView principal_servicios = findViewById(R.id.id_principal_servicios);
        ImageView principal_informaciones = findViewById(R.id.id_principal_informaciones);
        ImageView principal_mapa = findViewById(R.id.id_principal_localizacion);

        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "daoglp.db");
        Database database = helper.getWritableDb();
        final DaoSession daoSession = new DaoMaster(database).newSession();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View hView = navigationView.getHeaderView(0);

        listaclientes = daoSession.getClienteDao().loadAll();
        listarecorrer = daoSession.getClienteDao().loadAll();
        listarecorrer.clear();
        spinnerclientes = (Spinner) hView.findViewById(R.id.id_spinner_clientes);
        texto_contrato_cliente = (TextView) hView.findViewById(R.id.id_texto_contrato_cliente);

        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item);

        Cliente clientetemporal = new Cliente();

        for (int i = 0; i < listaclientes.size(); i++)
        {
            if (listaclientes.get(i).getActivo())
            {
                clientetemporal = listaclientes.get(i);
                listaclientes.remove(clientetemporal);
            }
        }

        listarecorrer.add(clientetemporal);

        if (listaclientes.size() > 0)
        {
            for (int i = 0; i < listaclientes.size(); i++)
            {
                listarecorrer.add(listaclientes.get(i));
            }
        }

        for (int i = 0; i < listarecorrer.size(); i++)
        {
            adapter.add(listarecorrer.get(i).getTitular());
        }

        adapter.add("Agregar Cliente");
        spinnerclientes.setAdapter(adapter);

        spinnerclientes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                if (parent.getItemAtPosition(position).equals("Agregar Cliente"))
                {
                    Intent intent = new Intent(PrincipalActivity.this, NuevoClienteActivity.class);
                    startActivity(intent);
                    finish();
                }
                else
                {
                    Cliente activo = listarecorrer.get(position);
                    activo.setActivo(true);
                    texto_contrato_cliente.setText(activo.getNumerocontrato());
                    daoSession.insertOrReplace(activo);
                    for (int i = 0; i < listarecorrer.size(); i++)
                    {
                        if (i != position)
                        {
                            listarecorrer.get(i).setActivo(false);
                            daoSession.insertOrReplace(listarecorrer.get(i));
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        navigationView.setNavigationItemSelectedListener(this);

        provincias = daoSession.getProvinciaDao().loadAll();

        principal_tramites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(PrincipalActivity.this, TramitesActivity.class);
                startActivity(intent);
            }
        });

        principal_servicios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(PrincipalActivity.this, ServiciosActivity.class);
                startActivity(intent);
            }
        });

        principal_informaciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(PrincipalActivity.this, InformacionesActivity.class);
                startActivity(intent);
            }
        });

        principal_mapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(PrincipalActivity.this, MapaActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed()
    {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        }
        else
        {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.principal, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            Intent intent = new Intent(PrincipalActivity.this, ConfiguracionActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        /*if (id == R.id.id_menu_agregar_cliente)
        {
            Intent intent = new Intent(PrincipalActivity.this, NuevoClienteActivity.class);
            startActivity(intent);
        }
        else*/ if (id == R.id.id_menu_gestionar_compra)
        {
            DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "daoglp.db");
            Database database = helper.getWritableDb();
            final DaoSession daoSession = new DaoMaster(database).newSession();

            List<Cliente> clienteList = daoSession.getClienteDao().loadAll();
            Cliente clienteactivo = new Cliente();
            for (int i = 0; i < clienteList.size(); i++)
            {
                if (clienteList.get(i).getActivo())
                {
                    clienteactivo = clienteList.get(i);
                }
            }

            if (clienteList.size() == 1)
            {
                Toast toast = Toast.makeText(this, "Agregue un Cliente para Gestionar Compras", Toast.LENGTH_LONG);
                toast.show();

            }
            else if (clienteactivo.getTitular().equals("Invitado"))
            {
                Toast toast = Toast.makeText(this, "Seleccione un Cliente para Gestionar Compras", Toast.LENGTH_LONG);
                toast.show();
            }
            else
                {
                Intent intent = new Intent(PrincipalActivity.this, CompraActivity.class);
                startActivity(intent);
            }

        }
        else if (id == R.id.id_menu_informaciones)
        {
            Intent intent = new Intent(PrincipalActivity.this, InformacionesActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.id_menu_tramites)
        {
            Intent intent = new Intent(PrincipalActivity.this, TramitesActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.id_menu_localizacion)
        {
            Intent intent = new Intent(PrincipalActivity.this, MapaActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.id_menu_servicioes)
        {
            Intent intent = new Intent(PrincipalActivity.this, ServiciosActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.id_menu_provincia)
        {
            DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "daoglp.db");
            Database database = helper.getWritableDb();
            final DaoSession daoSession = new DaoMaster(database).newSession();

            AlertDialog.Builder listaprovincias = new AlertDialog.Builder(this);
            listaprovincias.setTitle("Seleccione la Provincia");

            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item);

            for (int i = 0; i < provincias.size(); i++)
            {
                arrayAdapter.add(provincias.get(i).getNombre());
            }

            listaprovincias.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    String provinciaseleccionada = arrayAdapter.getItem(which);

                    for (int i = 0; i < provincias.size(); i++)
                    {
                        Provincia provinciaTemporal = provincias.get(i);
                        if (provinciaTemporal.getNombre().equals(provinciaseleccionada))
                        {
                            provinciaTemporal.setActiva(true);
                            daoSession.insertOrReplace(provinciaTemporal);
                        }
                        else
                        {
                            provinciaTemporal.setActiva(false);
                            daoSession.insertOrReplace(provinciaTemporal);
                        }
                    }

                    Toast toast = Toast.makeText(getApplicationContext(), "Provincia Seleccionada: " + provinciaseleccionada, Toast.LENGTH_SHORT);
                    toast.show();
                    dialog.dismiss();
                }
            });

            listaprovincias.show();
        }
        /*else if (id == R.id.id_menu_configuracion)
        {
            Intent intent = new Intent(PrincipalActivity.this, ConfiguracionActivity.class);
            startActivity(intent);
        }*/
        else if (id == R.id.id_menu_ayuda)
        {
            Intent intent = new Intent(PrincipalActivity.this, AyudaActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.id_menu_salir)
        {
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
