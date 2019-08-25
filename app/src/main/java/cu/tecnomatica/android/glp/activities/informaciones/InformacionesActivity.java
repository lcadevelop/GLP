package cu.tecnomatica.android.glp.activities.informaciones;

import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MenuItem;
import cu.tecnomatica.android.glp.R;

public class InformacionesActivity extends AppCompatActivity
{
    private FragmentManager fragmentManager = getSupportFragmentManager();
    private Fragment fragmentEmpresa = new EmpresaComercializadoraFragment();
    private Fragment fragmentCasa = new CasaComercialFragment();
    private Fragment fragmentPunto = new PuntoVentaFragment();

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener()
    {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item)
        {
            switch (item.getItemId())
            {
                case R.id.id_navigation_informacion_0:
                    fragmentManager = getSupportFragmentManager();
                    fragmentEmpresa = new EmpresaComercializadoraFragment();
                    fragmentManager.beginTransaction().replace(R.id.id_fragment_contenedor, fragmentEmpresa).commit();
                    return true;
                case R.id.id_navigation_informacion_1:
                    fragmentManager = getSupportFragmentManager();
                    fragmentCasa = new CasaComercialFragment();
                    fragmentManager.beginTransaction().replace(R.id.id_fragment_contenedor, fragmentCasa).commit();
                    return true;
                case R.id.id_navigation_informacion_2:
                    fragmentManager = getSupportFragmentManager();
                    fragmentPunto = new PuntoVentaFragment();
                    fragmentManager.beginTransaction().replace(R.id.id_fragment_contenedor, fragmentPunto).commit();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informaciones);

        fragmentManager.beginTransaction().replace(R.id.id_fragment_contenedor, fragmentEmpresa).commit();
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.id_navigation_informaciones);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }
}