package cu.tecnomatica.android.glp.activities.servicios;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import org.greenrobot.greendao.database.Database;

import java.io.File;
import java.util.List;
import cu.tecnomatica.android.glp.R;
import cu.tecnomatica.android.glp.activities.localizacion.MapaActivity;
import cu.tecnomatica.android.glp.database.greendao.Atencionclientes;
import cu.tecnomatica.android.glp.database.greendao.AtencionclientesDao;
import cu.tecnomatica.android.glp.database.greendao.DaoMaster;
import cu.tecnomatica.android.glp.database.greendao.DaoSession;
import cu.tecnomatica.android.glp.database.greendao.Provincia;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AtencionClientesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AtencionClientesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AtencionClientesFragment extends Fragment {

    private static final String DB_FILE = "/GLP/daoglp.db";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private TextView textonombre;
    private TextView textodireccion;
    private TextView textohorario;
    private TextView textotelefono;
    private TextView mapatexto;

    public AtencionClientesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AtencionClientesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AtencionClientesFragment newInstance(String param1, String param2) {
        AtencionClientesFragment fragment = new AtencionClientesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_atencion_clientes, container, false);

        textonombre = (TextView)view.findViewById(R.id.id_nombre_atencion_clientes);
        textodireccion = (TextView)view.findViewById(R.id.id_direccion_atencion_clientes);
        textohorario = (TextView)view.findViewById(R.id.id_horario_atencion_clientes);
        textotelefono = (TextView)view.findViewById(R.id.id_telefono_atencion_clientes);
        //mapatexto = (TextView)view.findViewById(R.id.id_mapa__atencion_clientes);

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

        final List<Atencionclientes>listaatencionclientes = daoSession.getAtencionclientesDao().queryBuilder().where(AtencionclientesDao.Properties.Idprovincia.like(provinciaactiva.getIdprovincia().toString())).list();
        final Atencionclientes atencionclientes = listaatencionclientes.get(0);

        textonombre.setText(atencionclientes.getNombre());
        textodireccion.setText(atencionclientes.getDireccion());
        textohorario.setText(atencionclientes.getHorario());
        textotelefono.setText(atencionclientes.getTelefono());

        /*mapatexto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getContext(), MapaActivity.class);
                Bundle bundle = new Bundle();

                bundle.putString("Latitud", atencionclientes.getLatitud());
                bundle.putString("Longitud", atencionclientes.getLongitud());
                intent.putExtras(bundle);

                startActivity(intent);
            }
        });*/

        return view;
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_atencion_clientes, container, false);
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
    public void onDetach() {
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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
