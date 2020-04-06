package cu.tecnomatica.android.glp.activities.servicios;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import org.greenrobot.greendao.database.Database;
import java.io.File;
import java.util.ArrayList;
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
import cu.tecnomatica.android.glp.helps.puntoventa.AdaptadorPuntoVenta;
import cu.tecnomatica.android.glp.helps.puntoventa.PuntoVentaHelp;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PuntoVentaFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PuntoVentaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PuntoVentaFragment extends Fragment
{
    private static final String DB_FILE = "/GLP/daoglp.db";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private Spinner combo_municipios_ptos;

    private RecyclerView recyclerView;
    ArrayList<PuntoVentaHelp> arrayListPuntosVenta;

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

        String dbPath = new File(Environment.getExternalStorageDirectory().getPath() + DB_FILE).getAbsolutePath();
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(getActivity(), dbPath);
        //DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(getActivity(), "daoglp.db");
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


        recyclerView = (RecyclerView)view.findViewById(R.id.id_lista_puntoventa);
        //recyclerView.setHasFixedSize(true);
        combo_municipios_ptos = (Spinner) view.findViewById(R.id.id_combo_municipios_ptos);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        combo_municipios_ptos.setAdapter(adapter);

        combo_municipios_ptos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {

                String municipio = parent.getItemAtPosition(position).toString();

                arrayListPuntosVenta = new ArrayList<PuntoVentaHelp>();

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


                for (int i = 0; i < puntoventas.size(); i++)
                {
                    PuntoVentaHelp puntoVentaHelp = new PuntoVentaHelp(puntoventas.get(i).getDireccion(), puntoventas.get(i).getHorario(), puntoventas.get(i).getTelefono(), puntoventas.get(i).getLatitud(), puntoventas.get(i).getLongitud());
                    arrayListPuntosVenta.add(puntoVentaHelp);
                }
                final AdaptadorPuntoVenta adaptadorPuntoVenta = new AdaptadorPuntoVenta(arrayListPuntosVenta);

                adaptadorPuntoVenta.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        Intent intent = new Intent(getContext(), MapaActivity.class);
                        Bundle bundle = new Bundle();

                        bundle.putString("Latitud", arrayListPuntosVenta.get(recyclerView.getChildPosition(v)).getLatitud());
                        bundle.putString("Longitud", arrayListPuntosVenta.get(recyclerView.getChildPosition(v)).getLongitud());

                        bundle.putString("Bandera", "PuntoVenta");

                        bundle.putString("Nombre", "Nombre");
                        bundle.putString("Direccion", arrayListPuntosVenta.get(recyclerView.getChildPosition(v)).getDireccion());
                        bundle.putString("Horario", arrayListPuntosVenta.get(recyclerView.getChildPosition(v)).getHorario());
                        bundle.putString("Telefono", arrayListPuntosVenta.get(recyclerView.getChildPosition(v)).getTelefono());
                        intent.putExtras(bundle);

                        startActivity(intent);
                    }
                });
                recyclerView.setAdapter(adaptadorPuntoVenta);
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                //recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
                //recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.HORIZONTAL));
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
}