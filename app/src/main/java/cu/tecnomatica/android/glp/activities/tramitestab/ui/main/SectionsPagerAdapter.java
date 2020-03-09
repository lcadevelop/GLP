package cu.tecnomatica.android.glp.activities.tramitestab.ui.main;

import android.content.Context;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import cu.tecnomatica.android.glp.R;
import cu.tecnomatica.android.glp.activities.tramites.AltasBajasFragment;
import cu.tecnomatica.android.glp.activities.tramites.ContratoFragment;
import cu.tecnomatica.android.glp.activities.tramites.TitularFragment;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.gestionar_contrato_titulo, R.string.cambio_titular_titulo, R.string.altas_bajas_titulo};
    private final Context mContext;
    private int numOfTabs;

    public SectionsPagerAdapter(Context context, FragmentManager fm, int NumOfTabs)
    {
        super(fm);
        mContext = context;
        this.numOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position)
    {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        //return PlaceholderFragment.newInstance(position + 1);
        switch (position)
        {
            case 0:
                ContratoFragment contratoFragment = new ContratoFragment();
                return contratoFragment;
            case 1:
                AltasBajasFragment altasBajasFragment = new AltasBajasFragment();
                return altasBajasFragment;
            case 2:
                TitularFragment titularFragment = new TitularFragment();
                return titularFragment;
             default:
                 return null;
        }
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position)
    {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount()
    {
        return numOfTabs;
    }
}