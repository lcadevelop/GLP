package cu.tecnomatica.android.glp.activities.serviciostab.ui.main;

import android.content.Context;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import cu.tecnomatica.android.glp.R;
import cu.tecnomatica.android.glp.activities.servicios.CasaComercialFragment;
import cu.tecnomatica.android.glp.activities.servicios.PuntoVentaFragment;
import cu.tecnomatica.android.glp.activities.servicios.AtencionClientesFragment;
import cu.tecnomatica.android.glp.activities.servicios.EmpresaComercializadoraFragment;
import cu.tecnomatica.android.glp.activities.servicios.ServiciosMecanicosFragment;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.atencion_cliente_titulo, R.string.servicios_mecanicos_titulo};
    private final Context mContext;
    private int numOfTabs;

    public SectionsPagerAdapter(Context context, FragmentManager fm, int numOfTabs) {
        super(fm);
        mContext = context;
        this.numOfTabs = numOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        //return PlaceholderFragment.newInstance(position + 1);

        switch (position)
        {
            case 0:
                AtencionClientesFragment atencionClientesFragment = new AtencionClientesFragment();
                return atencionClientesFragment;
            case 1:
                ServiciosMecanicosFragment serviciosMecanicosFragment = new ServiciosMecanicosFragment();
                return serviciosMecanicosFragment;
            case 2:
                EmpresaComercializadoraFragment empresaComercializadoraFragment = new EmpresaComercializadoraFragment();
                return empresaComercializadoraFragment;
            case 3:
                CasaComercialFragment casaComercialFragment= new CasaComercialFragment();
                return casaComercialFragment;
            case 4:
                PuntoVentaFragment puntoVentaFragment = new PuntoVentaFragment();
                return puntoVentaFragment;
            default:
                return null;
        }
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount()
    {
        return numOfTabs;
    }
}