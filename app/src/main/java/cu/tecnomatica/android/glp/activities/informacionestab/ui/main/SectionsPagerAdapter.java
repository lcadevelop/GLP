package cu.tecnomatica.android.glp.activities.informacionestab.ui.main;

import android.content.Context;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import cu.tecnomatica.android.glp.R;
import cu.tecnomatica.android.glp.activities.informaciones.CicloVentaFragment;
import cu.tecnomatica.android.glp.activities.informaciones.MedidasFragment;
import cu.tecnomatica.android.glp.activities.servicios.CasaComercialFragment;
import cu.tecnomatica.android.glp.activities.servicios.EmpresaComercializadoraFragment;
import cu.tecnomatica.android.glp.activities.servicios.PuntoVentaFragment;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.ciclo_venta, R.string.casas_comerciales, R.string.puntos_venta};
    private final Context mContext;
    private int numOfTabs;

    public SectionsPagerAdapter(Context context, FragmentManager fm, int numOfTabs)
    {
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
                CicloVentaFragment cicloVentaFragment = new CicloVentaFragment();
                return cicloVentaFragment;
            case 1:
                MedidasFragment medidasFragment = new MedidasFragment();
                return medidasFragment;
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