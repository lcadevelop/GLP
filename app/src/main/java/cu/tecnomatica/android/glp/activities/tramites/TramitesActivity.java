package cu.tecnomatica.android.glp.activities.tramites;

import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MenuItem;
import cu.tecnomatica.android.glp.R;

public class TramitesActivity extends AppCompatActivity {

    private FragmentManager fragmentManager = getSupportFragmentManager();
    private Fragment fragmentTitular = new TitularFragment();
    private Fragment fragmentContrato = new ContratoFragment();
    private Fragment fragmentAltasBajas = new AltasBajasFragment();

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId())
            {
                case R.id.id_navigation_tramite_1:
                    fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.id_fragment_contenedor_tramites, fragmentTitular).commit();
                    return true;
                case R.id.id_navigation_tramite_2:
                    fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.id_fragment_contenedor_tramites, fragmentAltasBajas).commit();
                    return true;
                case R.id.id_navigation_tramite_3:
                    fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.id_fragment_contenedor_tramites, fragmentContrato).commit();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tramites);

        fragmentManager.beginTransaction().replace(R.id.id_fragment_contenedor_tramites, fragmentTitular).commit();

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.id_navigation_tramites);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

}
