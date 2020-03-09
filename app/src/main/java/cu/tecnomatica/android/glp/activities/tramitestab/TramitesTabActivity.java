package cu.tecnomatica.android.glp.activities.tramitestab;
import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import cu.tecnomatica.android.glp.R;
import cu.tecnomatica.android.glp.activities.tramitestab.ui.main.SectionsPagerAdapter;

public class TramitesTabActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tramites_tab);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbartramites);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        TabLayout tabLayout = (TabLayout)findViewById(R.id.tabs_tramites);

        tabLayout.addTab(tabLayout.newTab().setText(R.string.gestionar_contrato_titulo).setIcon(R.mipmap.ic_contrato_blanco_foreground));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.altas_bajas_titulo).setIcon(R.mipmap.ic_altasbajas_blanco_foreground));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.cambio_titular_titulo).setIcon(R.mipmap.ic_titular_blanco_foreground));

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager(), tabLayout.getTabCount());

        final ViewPager viewPager = findViewById(R.id.view_pager_tramites);
        viewPager.setAdapter(sectionsPagerAdapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab)
            {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab)
            {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab)
            {
            }
        });

    }

}