package cu.tecnomatica.android.glp.activities.servicios;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import org.greenrobot.greendao.database.Database;
import java.io.File;
import java.util.List;
import cu.tecnomatica.android.glp.R;
import cu.tecnomatica.android.glp.database.greendao.Provincia;
import cu.tecnomatica.android.glp.helps.casacomercial.CasaComercialHelp;
import cu.tecnomatica.android.glp.database.greendao.Casacomercial;
import cu.tecnomatica.android.glp.database.greendao.CasacomercialDao;
import cu.tecnomatica.android.glp.database.greendao.DaoMaster;
import cu.tecnomatica.android.glp.database.greendao.DaoSession;
import cu.tecnomatica.android.glp.database.greendao.Municipio;
import cu.tecnomatica.android.glp.database.greendao.MunicipioDao;
import cu.tecnomatica.android.glp.activities.localizacion.MapaActivity;

public class CasaComercialFragment extends Fragment
{
    private static final String DB_FILE = "/GLP/daoglp.db";

    private Spinner combo_municipios;
    private CasaComercialHelp casaComercialHelps;

    private TextView direccion;
    private TextView horario;
    private TextView telefono;
    private TextView textmapa;
    private ImageView imagen;

    String municipioSeleccionado;

    private String municipioAMostrar = "Cotorro";

    public CasaComercialFragment()
    {
    }

    public static CasaComercialFragment newInstance() {
        CasaComercialFragment fragment = new CasaComercialFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_casa_comercial, container, false);

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

        combo_municipios = (Spinner) view.findViewById(R.id.id_combo_municipios);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        combo_municipios.setAdapter(adapter);

        direccion = (TextView)view.findViewById(R.id.id_direccion_texto);
        horario = (TextView)view.findViewById(R.id.id_horario_texto);
        telefono = (TextView)view.findViewById(R.id.id_telefono_texto);
        textmapa = (TextView)view.findViewById(R.id.id_mapa_texto);

        combo_municipios.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                municipioSeleccionado = parent.getItemAtPosition(position).toString();

                List<Municipio> listmunicipio = daoSession.getMunicipioDao().queryBuilder().where(MunicipioDao.Properties.Nombre.like(municipioSeleccionado)).list();
                Municipio objmunicipio = listmunicipio.get(0);

                for (int i = 0; i< municipios.size(); i++)
                {
                    Municipio municipioapagar = municipios.get(i);
                    municipioapagar.setActivo(false);
                    daoSession.insertOrReplace(municipioapagar);
                }
                objmunicipio.setActivo(true);
                daoSession.insertOrReplace(objmunicipio);

                List<Casacomercial> casacomercials = daoSession.getCasacomercialDao().queryBuilder().where(CasacomercialDao.Properties.Idmunicipio.like(objmunicipio.getIdmunicipio().toString())).list();
                casaComercialHelps = new CasaComercialHelp(casacomercials.get(0).getHorario(), casacomercials.get(0).getDireccion(), casacomercials.get(0).getTelefono(), casacomercials.get(0).getLatitud(), casacomercials.get(0).getLongitud());


                direccion.setText(casaComercialHelps.getDireccion());
                horario.setText(casaComercialHelps.getHorario());
                telefono.setText(casaComercialHelps.getTelefono());
                textmapa.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        Intent intent = new Intent(getContext(), MapaActivity.class);
                        Bundle bundle = new Bundle();

                        bundle.putString("Latitud", casaComercialHelps.getLatitud());
                        bundle.putString("Longitud", casaComercialHelps.getLongitud());
                        intent.putExtras(bundle);

                        startActivity(intent);
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
            }
        });
        return view;
    }

    public String getMunicipioSeleccionado() {
        return municipioSeleccionado;
    }

    public void setMunicipioSeleccionado(String municipioSeleccionado) {
        this.municipioSeleccionado = municipioSeleccionado;
    }
}