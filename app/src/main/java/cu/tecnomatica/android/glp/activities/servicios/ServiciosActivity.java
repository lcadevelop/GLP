package cu.tecnomatica.android.glp.activities.servicios;

import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MenuItem;
import cu.tecnomatica.android.glp.R;

public class ServiciosActivity extends AppCompatActivity
{
    private FragmentManager fragmentManager = getSupportFragmentManager();
    private Fragment fragmentAveria = new AtencionClientesFragment();
    private Fragment fragmentPiezas = new ServiciosMecanicosFragment();

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener()
    {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item)
        {
            switch (item.getItemId())
            {
                case R.id.id_navigation_servicio_1:
                    fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.id_fragment_contenedor_servicios, fragmentAveria).commit();
                    return true;
                case R.id.id_navigation_servicio_2:
                    fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.id_fragment_contenedor_servicios, fragmentPiezas).commit();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_servicios);

        fragmentManager.beginTransaction().replace(R.id.id_fragment_contenedor_servicios, fragmentAveria).commit();
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.id_navigation_servicios);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }
}