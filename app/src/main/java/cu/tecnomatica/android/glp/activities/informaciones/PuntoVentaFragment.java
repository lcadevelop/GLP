package cu.tecnomatica.android.glp.activities.informaciones;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import org.greenrobot.greendao.database.Database;
import java.util.List;
import cu.tecnomatica.android.glp.R;
import cu.tecnomatica.android.glp.database.greendao.DaoMaster;
import cu.tecnomatica.android.glp.database.greendao.DaoSession;
import cu.tecnomatica.android.glp.database.greendao.Municipio;
import cu.tecnomatica.android.glp.database.greendao.MunicipioDao;
import cu.tecnomatica.android.glp.database.greendao.Provincia;
import cu.tecnomatica.android.glp.database.greendao.Puntoventa;
import cu.tecnomatica.android.glp.database.greendao.PuntoventaDao;
import cu.tecnomatica.android.glp.activities.localizacion.MapaActivity;
import cu.tecnomatica.android.glp.helps.puntoventa.PuntoVentaHelp;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PuntoVentaFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PuntoVentaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PuntoVentaFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private Spinner combo_municipios_ptos;
    private ListView lista_putos;
    private PuntoVentaHelp[] puntoVentaHelps;

    private String municipioAMostrar = "Cotorro";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public PuntoVentaFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     //* @param param1 Parameter 1.
     //* @param param2 Parameter 2.
     * @return A new instance of fragment PuntoVentaFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PuntoVentaFragment newInstance() {
        PuntoVentaFragment fragment = new PuntoVentaFragment();
        Bundle args = new Bundle();
        //args.putString(ARG_PARAM1, param1);
        //args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,   Bundle savedInstanceState)
    {

        View view = inflater.inflate(R.layout.fragment_punto_venta, container, false);
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(getActivity(), "daoglp.db");
        Database database = helper.getWritableDb();
        final DaoSession daoSession = new DaoMaster(database).newSession();

        final List<Provincia> listaprovincias = daoSession.getProvinciaDao().loadAll();
        Provincia provinciaactiva = listaprovincias.get(0);

        for (int i = 0; i < listaprovincias.size(); i++)
        {
            if (listaprovincias.get(i).getActiva())
            {
                provinciaactiva = listaprovincias.get(i);
            }
        }

        final List<Municipio> municipios = daoSession.getMunicipioDao().queryBuilder().where(MunicipioDao.Properties.Idprovincia.like(provinciaactiva.getIdprovincia().toString())).list();
        for (int i = 0; i < municipios.size(); i++)
        {
            if (municipios.get(i).getActivo() == true)
            {
                municipioAMostrar = municipios.get(i).getNombre();
            }
        }
        int total = municipios.size();

        List<Municipio> listmunicipio = daoSession.getMunicipioDao().queryBuilder().where(MunicipioDao.Properties.Nombre.like(municipioAMostrar)).list();
        Municipio objmunicipio = listmunicipio.get(0);
        municipios.remove(objmunicipio);
        municipios.add(0, objmunicipio);

        String[] pasar = new String[total];

        for (int i = 0; i < municipios.size(); i++)
        {
            pasar[i] = municipios.get(i).getNombre();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, pasar);

        lista_putos = (ListView)view.findViewById(R.id.id_lista_puntoventa);
        combo_municipios_ptos = (Spinner) view.findViewById(R.id.id_combo_municipios_ptos);
        combo_municipios_ptos.setAdapter(adapter);

        combo_municipios_ptos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {

                String municipio = parent.getItemAtPosition(position).toString();

                List<Municipio> listmunicipio = daoSession.getMunicipioDao().queryBuilder().where(MunicipioDao.Properties.Nombre.like(municipio)).list();
                Municipio objmunicipio = listmunicipio.get(0);

                for (int i = 0; i< municipios.size(); i++)
                {
                    Municipio municipioapagar = municipios.get(i);
                    municipioapagar.setActivo(false);
                    daoSession.insertOrReplace(municipioapagar);
                }
                objmunicipio.setActivo(true);
                daoSession.insertOrReplace(objmunicipio);

                List<Puntoventa> puntoventas = daoSession.getPuntoventaDao().queryBuilder().where(PuntoventaDao.Properties.Idmunicipio.like(objmunicipio.getIdmunicipio().toString())).list();
                puntoVentaHelps = new PuntoVentaHelp[puntoventas.size()];

                for (int i = 0; i < puntoventas.size(); i++)
                {
                    PuntoVentaHelp puntoVentaHelp = new PuntoVentaHelp(puntoventas.get(i).getDireccion(), puntoventas.get(i).getHorario(), puntoventas.get(i).getTelefono(), puntoventas.get(i).getLatitud(), puntoventas.get(i).getLongitud());
                    puntoVentaHelps[i] = puntoVentaHelp;
                }
                AdaptadorPuntoVenta adaptadorPuntoVenta = new AdaptadorPuntoVenta(getContext(), puntoVentaHelps);
                lista_putos.setAdapter(adaptadorPuntoVenta);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    /*@Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }*/

    @Override
    public void onDetach()
    {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener
    {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    class AdaptadorPuntoVenta extends ArrayAdapter<PuntoVentaHelp>
    {
        AdaptadorPuntoVenta(Context context, PuntoVentaHelp[] puntoVentaHelps)
        {
            super(context, R.layout.list_item_punto_venta, puntoVentaHelps);
        }

        public View getView(final int position, View convertView, ViewGroup parent)
        {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            View item = inflater.inflate(R.layout.list_item_punto_venta, null);

            TextView direccion = (TextView)item.findViewById(R.id.id_direccion_texto_pt);
            direccion.setText(puntoVentaHelps[position].getDireccion());

            TextView horario = (TextView)item.findViewById(R.id.id_horario_texto_pt);
            horario.setText(puntoVentaHelps[position].getHorario());

            TextView telefono = (TextView)item.findViewById(R.id.id_telefono_texto_pt);
            telefono.setText(puntoVentaHelps[position].getTelefono());

            TextView textmapapv = (TextView)item.findViewById(R.id.id_mapa_texto_pv);
            textmapapv.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Intent intent = new Intent(getContext(), MapaActivity.class);
                    Bundle bundle = new Bundle();

                    bundle.putString("Latitud", puntoVentaHelps[position].getLatitud());
                    bundle.putString("Longitud", puntoVentaHelps[position].getLongitud());
                    intent.putExtras(bundle);

                    startActivity(intent);
                }
            });

            return item;
        }
    }
}