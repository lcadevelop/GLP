package cu.tecnomatica.android.glp.activities.informacionestab;

import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import cu.tecnomatica.android.glp.R;
import cu.tecnomatica.android.glp.activities.informacionestab.ui.main.SectionsPagerAdapter;

public class InformacionesTabActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informaciones_tab);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarinformaciones);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        TabLayout tabs = findViewById(R.id.tabs_informaciones);
        tabs.addTab(tabs.newTab().setText(R.string.empresa_comercializadora).setIcon(R.mipmap.ic_comercializadora_blanco_foreground));
        tabs.addTab(tabs.newTab().setText(R.string.casas_comerciales).setIcon(R.mipmap.ic_casascomerciales_blanco_foreground));
        tabs.addTab(tabs.newTab().setText(R.string.puntos_venta).setIcon(R.mipmap.ic_puntoventa_blanco_foreground));
        tabs.setTabGravity(TabLayout.GRAVITY_FILL);
        tabs.setTabMode(TabLayout.MODE_FIXED);
        tabs.setTabMode(TabLayout.MODE_SCROLLABLE);

        final SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager(), tabs.getTabCount());

        final ViewPager viewPager = findViewById(R.id.view_pager_informaciones);
        viewPager.setAdapter(sectionsPagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabs));

        tabs.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
}