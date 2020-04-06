package cu.tecnomatica.android.glp.activities.servicios;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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
import cu.tecnomatica.android.glp.database.greendao.DaoMaster;
import cu.tecnomatica.android.glp.database.greendao.DaoSession;
import cu.tecnomatica.android.glp.database.greendao.Provincia;
import cu.tecnomatica.android.glp.database.greendao.Serviciosmecanicos;
import cu.tecnomatica.android.glp.database.greendao.ServiciosmecanicosDao;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ServiciosMecanicosFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ServiciosMecanicosFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ServiciosMecanicosFragment extends Fragment {

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
    private TextView llamar;

    public ServiciosMecanicosFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ServiciosMecanicosFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ServiciosMecanicosFragment newInstance(String param1, String param2) {
        ServiciosMecanicosFragment fragment = new ServiciosMecanicosFragment();
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
        View view = inflater.inflate(R.layout.fragment_servicios_mecanicos, container, false);

        PedirPermiso();

        textonombre = (TextView)view.findViewById(R.id.id_nombre_servicios_mecanicos);
        textodireccion = (TextView)view.findViewById(R.id.id_direccion_servicios_mecanicos);
        textohorario = (TextView)view.findViewById(R.id.id_horario_servicios_mecanicos);
        textotelefono = (TextView)view.findViewById(R.id.id_telefono_servicios_mecanicos);
        mapatexto = (TextView)view.findViewById(R.id.id_mapa_servicios_mecanicos);
        llamar = (TextView)view.findViewById(R.id.id_texto_llamar);

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

        final List<Serviciosmecanicos>listaatencionclientes = daoSession.getServiciosmecanicosDao().queryBuilder().where(ServiciosmecanicosDao.Properties.Idprovincia.like(provinciaactiva.getIdprovincia().toString())).list();
        final Serviciosmecanicos serviciosmecanicos = listaatencionclientes.get(0);

        textonombre.setText(serviciosmecanicos.getNombre());
        textodireccion.setText(serviciosmecanicos.getDireccion());
        textohorario.setText(serviciosmecanicos.getHorario());
        textotelefono.setText(serviciosmecanicos.getTelefono());

        llamar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                try
                {
                    String[] numeros = serviciosmecanicos.getTelefono().split(" ");
                    String[] llamar = numeros[0].split(",");
                    startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + llamar[0])));
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });

        mapatexto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getContext(), MapaActivity.class);
                Bundle bundle = new Bundle();

                bundle.putString("Latitud", serviciosmecanicos.getLatitud());
                bundle.putString("Longitud", serviciosmecanicos.getLongitud());

                bundle.putString("Bandera", "ServiciosMecanicos");

                bundle.putString("Nombre", serviciosmecanicos.getNombre());
                bundle.putString("Direccion", serviciosmecanicos.getDireccion());
                bundle.putString("Horario", serviciosmecanicos.getHorario());
                bundle.putString("Telefono", serviciosmecanicos.getTelefono());
                intent.putExtras(bundle);

                startActivity(intent);
            }
        });

        return view;
    }

    public void PedirPermiso()
    {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, 2);
        }
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
