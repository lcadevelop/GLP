package cu.tecnomatica.android.glp.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.os.Environment;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import org.greenrobot.greendao.database.Database;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import cu.tecnomatica.android.glp.R;
import cu.tecnomatica.android.glp.database.greendao.Atencionclientes;
import cu.tecnomatica.android.glp.database.greendao.Cliente;
import cu.tecnomatica.android.glp.database.greendao.DaoMaster;
import cu.tecnomatica.android.glp.database.greendao.DaoSession;
import cu.tecnomatica.android.glp.database.greendao.Empresacomercializadora;
import cu.tecnomatica.android.glp.database.greendao.Provincia;
import cu.tecnomatica.android.glp.database.greendao.Serviciosmecanicos;


public class PortadaActivity extends AppCompatActivity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 0;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 0;
    private final Handler mHideHandler = new Handler();
    //private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            /*mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);*/
        }
    };
    //private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            //mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    //String version = "";
    private final int duracion = 2000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_portada);
        //mContentView = findViewById(R.id.id_texto_portada);
        //version = BuildConfig.VERSION_NAME;
        //TextView texto_vesion = (TextView)findViewById(R.id.id_texto_version);
        //texto_vesion.setText("Versión: " + version);

        /*//ROOM
        Appdatabase appdatabase = Room.databaseBuilder(getApplicationContext(), Appdatabase.class, "glp.db").allowMainThreadQueries().build();
        cargarDatos(appdatabase);*/

        AssetManager assetManager = getAssets();

        try
        {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            {
            }
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE))
            {
            }
            else
            {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
            File dir = new File(Environment.getExternalStorageDirectory() + "/GLP/");

            if (!dir.exists())
            {
                dir.mkdir();
            }

            String filename = "cuba.map";
            String filedbname = "daoglp.db";

            File destinationfile = new File(Environment.getExternalStorageDirectory() + "/GLP/" + filename);
            File destinationdbfile = new File(Environment.getExternalStorageDirectory() + "/GLP/" + filedbname);

            if (!destinationfile.exists())
            {
                InputStream inputStream = assetManager.open("cuba.map");
                FileOutputStream fileOutputStream = new FileOutputStream(destinationfile);
                byte[] buffer = new byte[1024];

                int len1 = 0;

                while ((len1 = inputStream.read(buffer)) > 0)
                {
                    fileOutputStream.write(buffer, 0, len1);
                }
                fileOutputStream.close();
            }

            if (!destinationdbfile.exists())
            {
                InputStream inputStreamdb = assetManager.open("daoglp.db");
                FileOutputStream fileOutputStreamdb = new FileOutputStream(destinationdbfile);
                byte[] bufferdb = new byte[1024];

                int len1 = 0;

                while ((len1 = inputStreamdb.read(bufferdb)) > 0)
                {
                    fileOutputStreamdb.write(bufferdb, 0, len1);
                }
                fileOutputStreamdb.close();
            }
        }
        catch (Exception exc)
        {
            Log.e("Error de Copia", exc.getMessage());
        }

        try
        {
            DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "daoglp.db");
            Database database = helper.getWritableDb();
            DaoSession daoSession = new DaoMaster(database).newSession();
            cargarDatos(daoSession);
        }
        catch (Exception e)
        {
            Log.e("Error", e.getMessage());
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(PortadaActivity.this, PrincipalActivity.class);
                startActivity(intent);
                finish();
            };
        }, duracion);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        //mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
               // | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        //mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis)
    {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    public void cargarDatos(DaoSession daoSession)
    {
        try {
            //CONTRATOS
            if (daoSession.getMunicipioDao().loadAll().size() == 0)
            {
                cu.tecnomatica.android.glp.database.greendao.Contrato contrato_normado = new cu.tecnomatica.android.glp.database.greendao.Contrato();
                contrato_normado.setNombre("Normado");
                contrato_normado.setDescripcion("Descripción del Contrato Normado");
                daoSession.insertOrReplace(contrato_normado);

                cu.tecnomatica.android.glp.database.greendao.Contrato contrato_liberado = new cu.tecnomatica.android.glp.database.greendao.Contrato();
                contrato_liberado.setNombre("Liberado");
                contrato_liberado.setDescripcion("Descripción del Contrato Liberado");
                daoSession.insertOrReplace(contrato_liberado);

                cu.tecnomatica.android.glp.database.greendao.Contrato contrato_social = new cu.tecnomatica.android.glp.database.greendao.Contrato();
                contrato_social.setNombre("Pacto Social");
                contrato_social.setDescripcion("Descripción del Contrato Pacto Social");
                daoSession.insertOrReplace(contrato_social);

                //PROVINCIAS

                //PINAR DEL RÍO PROVINCIA
                Provincia provinciapinar = new Provincia();
                provinciapinar.setNombre("Pinar del Río");
                provinciapinar.setLatitud("22.416780");
                provinciapinar.setLongitud("-83.691051");
                provinciapinar.setActiva(false);
                daoSession.insertOrReplace(provinciapinar);

                Empresacomercializadora empresacomercializadorapinar = new Empresacomercializadora();
                empresacomercializadorapinar.setNombre("División Territorial de Comercialización de Combustible Pinar del Río");
                empresacomercializadorapinar.setProvincia(provinciapinar);
                empresacomercializadorapinar.setIdprovincia(provinciapinar.getIdprovincia());
                empresacomercializadorapinar.setDireccion("Calle C No 52 / Pasaje 50 y Línea Férrea, Loma del Ganso. Pinar del Río.");
                empresacomercializadorapinar.setTelefono("48773051, 48754822 Y 48750125");
                empresacomercializadorapinar.setHorario("Lunes - Jueves 7:30 - 5:00 Viernes 7:30 - 4:00");
                empresacomercializadorapinar.setLatitud("22.350835");
                empresacomercializadorapinar.setLongitud("-83.732329");
                daoSession.insertOrReplace(empresacomercializadorapinar);

                Serviciosmecanicos serviciosmecanicospinar = new Serviciosmecanicos();
                serviciosmecanicospinar.setNombre("Servicios Mecánicos Pinar del Río");
                serviciosmecanicospinar.setProvincia(provinciapinar);
                serviciosmecanicospinar.setIdprovincia(provinciapinar.getIdprovincia());
                serviciosmecanicospinar.setDireccion("Calle D final No 2 e/ D y E  Rpto Hnos Cruz Pinar del Río");
                serviciosmecanicospinar.setTelefono("48767715");
                serviciosmecanicospinar.setHorario("Lunes - Jueves 7:30 - 5:00   Viernes 7:30 - 4:00");
                serviciosmecanicospinar.setLatitud("22.429478");
                serviciosmecanicospinar.setLongitud("-83.681842");
                daoSession.insertOrReplace(serviciosmecanicospinar);

                Atencionclientes atencionclientespinar = new Atencionclientes();
                atencionclientespinar.setNombre("Oficina de Atención al Cliente Pinar del Río");
                atencionclientespinar.setProvincia(provinciapinar);
                atencionclientespinar.setIdprovincia(provinciapinar.getIdprovincia());
                atencionclientespinar.setDireccion("Calle D final No 2 e/ D y E  Rpto Hnos Cruz Pinar del Río");
                atencionclientespinar.setTelefono("48767715");
                atencionclientespinar.setHorario("Lunes - Jueves 7:30 - 5:00   Viernes 7:30 - 4:00");
                atencionclientespinar.setLatitud("22.429478");
                atencionclientespinar.setLongitud("-83.681842");
                daoSession.insertOrReplace(atencionclientespinar);

                //PINAR DEL RÍO MUNICIPIOS
                cu.tecnomatica.android.glp.database.greendao.Municipio municipiopinardelrio = new cu.tecnomatica.android.glp.database.greendao.Municipio();
                municipiopinardelrio.setNombre("Pinar del Río");
                municipiopinardelrio.setProvincia(provinciapinar);
                municipiopinardelrio.setActivo(true);
                daoSession.insertOrReplace(municipiopinardelrio);

                cu.tecnomatica.android.glp.database.greendao.Municipio municipiolospalacios = new cu.tecnomatica.android.glp.database.greendao.Municipio();
                municipiolospalacios.setNombre("Los Palacios");
                municipiolospalacios.setProvincia(provinciapinar);
                municipiolospalacios.setActivo(false);
                daoSession.insertOrReplace(municipiolospalacios);

                cu.tecnomatica.android.glp.database.greendao.Municipio municipioconsolacion = new cu.tecnomatica.android.glp.database.greendao.Municipio();
                municipioconsolacion.setNombre("Consolación del Sur");
                municipioconsolacion.setProvincia(provinciapinar);
                municipioconsolacion.setActivo(false);
                daoSession.insertOrReplace(municipioconsolacion);

                //PINAR DEL RÍO CASAS COMERCIALES
                cu.tecnomatica.android.glp.database.greendao.Casacomercial casacomercialpinar = new cu.tecnomatica.android.glp.database.greendao.Casacomercial();

                casacomercialpinar.setIdmunicipio(municipiopinardelrio.getIdmunicipio());
                casacomercialpinar.setMunicipio(municipiopinardelrio);
                casacomercialpinar.setDireccion("Calle D final No 2 e/ D y E  Rpto Hermanos Cruz. Pinar del Río");
                casacomercialpinar.setTelefono("48752313, 48752279, 48767708");
                casacomercialpinar.setHorario("Lunes - Jueves 7:30AM - 5:00PM Viernes 7:30AM - 4:00PM");
                casacomercialpinar.setNombre("CC Pinar del Río");
                casacomercialpinar.setLatitud("22.429478");
                casacomercialpinar.setLongitud("-83.681842");
                daoSession.insertOrReplace(casacomercialpinar);

                cu.tecnomatica.android.glp.database.greendao.Casacomercial casacomercialpinarpalacios = new cu.tecnomatica.android.glp.database.greendao.Casacomercial();

                casacomercialpinar.setIdmunicipio(municipiolospalacios.getIdmunicipio());
                casacomercialpinarpalacios.setMunicipio(municipiolospalacios);
                casacomercialpinarpalacios.setDireccion("Calle D final No 2 e/ D y E  Rpto Hermanos Cruz. Pinar del Río");
                casacomercialpinarpalacios.setTelefono("48752313, 48752279, 48767708");
                casacomercialpinarpalacios.setHorario("Lunes - Jueves 7:30AM - 5:00PM Viernes 7:30AM - 4:00PM");
                casacomercialpinarpalacios.setNombre("CC Pinar del Río");
                casacomercialpinarpalacios.setLatitud("22.429478");
                casacomercialpinarpalacios.setLongitud("-83.681842");
                daoSession.insertOrReplace(casacomercialpinarpalacios);

                cu.tecnomatica.android.glp.database.greendao.Casacomercial casacomercialpinarconsolacion = new cu.tecnomatica.android.glp.database.greendao.Casacomercial();

                casacomercialpinarconsolacion.setIdmunicipio(municipioconsolacion.getIdmunicipio());
                casacomercialpinarconsolacion.setMunicipio(municipioconsolacion);
                casacomercialpinarconsolacion.setDireccion("Calle D final No 2 e/ D y E  Rpto Hermanos Cruz. Pinar del Río");
                casacomercialpinarconsolacion.setTelefono("48752313, 48752279, 48767708");
                casacomercialpinarconsolacion.setHorario("Lunes - Jueves 7:30AM - 5:00PM Viernes 7:30AM - 4:00PM");
                casacomercialpinarconsolacion.setNombre("CC Pinar del Río");
                casacomercialpinarconsolacion.setLatitud("22.429478");
                casacomercialpinarconsolacion.setLongitud("-83.681842");
                daoSession.insertOrReplace(casacomercialpinarconsolacion);

                //PINAR DEL RÍO PUNTOS DE VENTA

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventapinar1 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventapinar1.setCodigo("Pinar1");
                puntoventapinar1.setMunicipio(municipiopinardelrio);
                puntoventapinar1.setIdmunicipio(municipiopinardelrio.getIdmunicipio());
                puntoventapinar1.setDireccion("C-1 Pasaje A #1 e/ 1ra y Álvaro Barba Microdistrito 3, Rpto Hnos Cruz");
                puntoventapinar1.setLatitud("22.42658");
                puntoventapinar1.setLongitud("-83.674942");
                puntoventapinar1.setHorario("Lunes - Viernes 8:00 am - 12:00 m y 1:00 pm -4.0 pm Sábados 8:00 - 12:00m");
                puntoventapinar1.setTelefono("48786010");
                puntoventapinar1.setNombre("Hermanos Cruz 1");
                daoSession.insertOrReplace(puntoventapinar1);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventapinar2 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventapinar2.setCodigo("Pinar2");
                puntoventapinar2.setMunicipio(municipiopinardelrio);
                puntoventapinar2.setIdmunicipio(municipiopinardelrio.getIdmunicipio());
                puntoventapinar2.setDireccion("Avenida 56 e/ Aven. Alvaro Barba Microdistrito 3 Rpto.Hnos Cruz");
                puntoventapinar2.setLatitud("22.425075");
                puntoventapinar2.setLongitud("-83.679316");
                puntoventapinar2.setHorario("Lunes - Viernes 8:00 am - 12:00 m y 1:00 pm -4.0 pm Sábados 8:00 - 12:00m");
                puntoventapinar2.setTelefono("48762210");
                puntoventapinar2.setNombre("Hermanos Cruz 2");
                daoSession.insertOrReplace(puntoventapinar2);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventapinar3 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventapinar3.setCodigo("Pinar3");
                puntoventapinar3.setMunicipio(municipiopinardelrio);
                puntoventapinar3.setIdmunicipio(municipiopinardelrio.getIdmunicipio());
                puntoventapinar3.setDireccion("Calle 5ta No 101 e/ Los Pinos y 2da Microdistrito 2.Rpto Hnos Cruz");
                puntoventapinar3.setLatitud("22.433486");
                puntoventapinar3.setLongitud("-83.678566");
                puntoventapinar3.setHorario("Lunes - Viernes 8:00 am - 12:00 m y 1:00 pm -4.0 pm Sábados 8:00 - 12:00m");
                puntoventapinar3.setTelefono("48765547");
                puntoventapinar3.setNombre("Hermanos Cruz 3");
                daoSession.insertOrReplace(puntoventapinar3);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventapinar4 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventapinar4.setCodigo("Pinar4");
                puntoventapinar4.setMunicipio(municipiopinardelrio);
                puntoventapinar4.setIdmunicipio(municipiopinardelrio.getIdmunicipio());
                puntoventapinar4.setDireccion("Calle Marina Azcuy e/ Pedro Tellez y Ormany Arenado");
                puntoventapinar4.setLatitud("22.420119");
                puntoventapinar4.setLongitud("-83.693046");
                puntoventapinar4.setHorario("Lunes - Viernes 8:00 am - 12:00 m y 1:00 pm -4.0 pm Sábados 8:00 - 12:00m");
                puntoventapinar4.setTelefono("48777557");
                puntoventapinar4.setNombre("Villamil");
                daoSession.insertOrReplace(puntoventapinar4);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventapinar5 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventapinar5.setCodigo("Pinar5");
                puntoventapinar5.setMunicipio(municipiopinardelrio);
                puntoventapinar5.setIdmunicipio(municipiopinardelrio.getIdmunicipio());
                puntoventapinar5.setDireccion("Calle Geraldo Medina No 29 e/ 2da y6ta Rpto Raúl Sánchez");
                puntoventapinar5.setLatitud("22.409225");
                puntoventapinar5.setLongitud("-83.697567");
                puntoventapinar5.setHorario("Lunes - Viernes 8:00 am - 12:00 m y 1:00 pm -4.0 pm Sábados 8:00 - 12:00m");
                puntoventapinar5.setTelefono("48777805");
                puntoventapinar5.setNombre("Raúl Sánchez");
                daoSession.insertOrReplace(puntoventapinar5);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventapinar6 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventapinar6.setCodigo("Pinar6");
                puntoventapinar6.setMunicipio(municipiopinardelrio);
                puntoventapinar6.setIdmunicipio(municipiopinardelrio.getIdmunicipio());
                puntoventapinar6.setDireccion("Calle Gónzalez Alcorta, Reparto Carlos Manuel");
                puntoventapinar6.setLatitud("22.415324");
                puntoventapinar6.setLongitud("-83.686972");
                puntoventapinar6.setHorario("Lunes - Viernes 8:00 am - 12:00 m y 1:00 pm -4.0 pm Sábados 8:00 - 12:00m");
                puntoventapinar6.setTelefono("48777553");
                puntoventapinar6.setNombre("Carlos Manuel");
                daoSession.insertOrReplace(puntoventapinar6);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventapinar7 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventapinar7.setCodigo("Pinar7");
                puntoventapinar7.setMunicipio(municipiopinardelrio);
                puntoventapinar7.setIdmunicipio(municipiopinardelrio.getIdmunicipio());
                puntoventapinar7.setDireccion("Calle Frank País e/ Rafael Morales y Antonio Tarafa,Reparto Hermanos Barcón.");
                puntoventapinar7.setLatitud("22.413579");
                puntoventapinar7.setLongitud("-83.700891");
                puntoventapinar7.setHorario("Lunes - Viernes 8:00 am - 12:00 m y 1:00 pm -4.0 pm Sábados 8:00 - 12:00m");
                puntoventapinar7.setTelefono("481847");
                puntoventapinar7.setNombre("Hermanos Barcón");
                daoSession.insertOrReplace(puntoventapinar7);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventapinar8 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventapinar8.setCodigo("Pinar8");
                puntoventapinar8.setMunicipio(municipiopinardelrio);
                puntoventapinar8.setIdmunicipio(municipiopinardelrio.getIdmunicipio());
                puntoventapinar8.setDireccion("Calle Isidro Barredo e/ Grabiel Lache y Alameda, Rpto C. Maragoto");
                puntoventapinar8.setLatitud("22.422509");
                puntoventapinar8.setLongitud("-83.70589");
                puntoventapinar8.setHorario("Lunes - Viernes 8:00 am - 12:00 m y 1:00 pm -4.0 pm Sábados 8:00 - 12:00m");
                puntoventapinar8.setTelefono("48777818");
                puntoventapinar8.setNombre("Celso Maragoto");
                daoSession.insertOrReplace(puntoventapinar8);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventapinar9 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventapinar9.setCodigo("Pinar9");
                puntoventapinar9.setMunicipio(municipiopinardelrio);
                puntoventapinar9.setIdmunicipio(municipiopinardelrio.getIdmunicipio());
                puntoventapinar9.setDireccion("Calle E e/ G. y Areparto Pepe Chepe");
                puntoventapinar9.setLatitud("22.435267");
                puntoventapinar9.setLongitud("-83.689756");
                puntoventapinar9.setHorario("Lunes - Viernes 8:00 am - 12:00 m y 1:00 pm -4.0 pm Sábados 8:00 - 12:00m");
                puntoventapinar9.setTelefono("48766253");
                puntoventapinar9.setNombre("Guanajera");
                daoSession.insertOrReplace(puntoventapinar9);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventapinar10 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventapinar10.setCodigo("Pinar10");
                puntoventapinar10.setMunicipio(municipiolospalacios);
                puntoventapinar10.setIdmunicipio(municipiolospalacios.getIdmunicipio());
                puntoventapinar10.setDireccion("Calle 23 A no.3403 e/ 34 y 34 A Los Palacios");
                puntoventapinar10.setLatitud("22.583224");
                puntoventapinar10.setLongitud("-83.25482");
                puntoventapinar10.setHorario("Lunes - Viernes 8:00 am - 12:00 m y 1:00 pm -4.0 pm Sábados 8:00 - 12:00m");
                puntoventapinar10.setTelefono("No Tiene");
                puntoventapinar10.setNombre("Los Palacios");
                daoSession.insertOrReplace(puntoventapinar10);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventapinar11 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventapinar11.setCodigo("Pinar11");
                puntoventapinar11.setMunicipio(municipioconsolacion);
                puntoventapinar11.setIdmunicipio(municipioconsolacion.getIdmunicipio());
                puntoventapinar11.setDireccion("Calle 55  e/ 48 y Final. Consolación del Sur");
                puntoventapinar11.setLatitud("22.507239");
                puntoventapinar11.setLongitud("-83.506746");
                puntoventapinar11.setHorario("Lunes - Viernes 8:00 am - 12:00 m y 1:00 pm -4.0 pm Sábados 8:00 - 12:00m");
                puntoventapinar11.setTelefono("No Tiene");
                puntoventapinar11.setNombre("Consolación del Sur");
                daoSession.insertOrReplace(puntoventapinar11);

                /*//ARTEMISA PROVINCIA
                Provincia provinciaartemisa = new Provincia();
                provinciaartemisa.setNombre("Artemisa");
                provinciaartemisa.setActiva(false);
                daoSession.insertOrReplace(provinciaartemisa);*/

                //LA HABANA PROVINCIA
                Provincia provinciahabana = new Provincia();
                provinciahabana.setNombre("La Habana");
                provinciahabana.setLatitud("23.123592");
                provinciahabana.setLongitud("-82.366592");
                provinciahabana.setActiva(true);
                daoSession.insertOrReplace(provinciahabana);

                Empresacomercializadora empresacomercializadorahabana = new Empresacomercializadora();
                empresacomercializadorahabana.setNombre("Empresa Gas Licuado");
                empresacomercializadorahabana.setProvincia(provinciahabana);
                empresacomercializadorahabana.setIdprovincia(provinciahabana.getIdprovincia());
                empresacomercializadorahabana.setDireccion("Calle 21 # 2104 e/ 94 y Vía Blanca. Rpto. A. Guiteras");
                empresacomercializadorahabana.setTelefono("77674254");
                empresacomercializadorahabana.setHorario("De lunes a viernes, de 6:50 am a 4:10 pm");
                empresacomercializadorahabana.setLatitud("23.14012");
                empresacomercializadorahabana.setLongitud("-82.30491");
                daoSession.insertOrReplace(empresacomercializadorahabana);

                Serviciosmecanicos serviciosmecanicoshabana = new Serviciosmecanicos();
                serviciosmecanicoshabana.setNombre("UEB Mantenimiento y Servicios Mecánicos");
                serviciosmecanicoshabana.setProvincia(provinciahabana);
                serviciosmecanicoshabana.setIdprovincia(provinciahabana.getIdprovincia());
                serviciosmecanicoshabana.setDireccion("San Luís # 9602 e/ 4ta y Final. Reparto Azotea. Guanabacoa");
                serviciosmecanicoshabana.setTelefono("77976888");
                serviciosmecanicoshabana.setHorario("De lunes a viernes, de 8:30 am a 4.00 pm y sábados, de: 8:30 am a 3:00 pm");
                serviciosmecanicoshabana.setLatitud("23.11336");
                serviciosmecanicoshabana.setLongitud("-82.31521");
                daoSession.insertOrReplace(serviciosmecanicoshabana);

                Atencionclientes atencionclienteshabana = new Atencionclientes();
                atencionclienteshabana.setNombre("Grupo Atención al Cliente");
                atencionclienteshabana.setProvincia(provinciahabana);
                atencionclienteshabana.setIdprovincia(provinciahabana.getIdprovincia());
                atencionclienteshabana.setDireccion("Calle Águila # 711 e/ Monte y Estrella. Centro Habana");
                atencionclienteshabana.setTelefono("78643289 Y 78635444");
                atencionclienteshabana.setHorario("De lunes a viernes: De 8:00 am a 5:00 pm. Sábados alternos: De 8:00 am a 1:00 pm");
                atencionclienteshabana.setLatitud("23.13138");
                atencionclienteshabana.setLongitud("-82.36166");
                daoSession.insertOrReplace(atencionclienteshabana);

                //MUNICIPIOS LA HABANA
                cu.tecnomatica.android.glp.database.greendao.Municipio municipioarroyo = new cu.tecnomatica.android.glp.database.greendao.Municipio();
                municipioarroyo.setNombre("Arroyo Naranjo");
                municipioarroyo.setProvincia(provinciahabana);
                municipioarroyo.setActivo(true);
                daoSession.insertOrReplace(municipioarroyo);

                cu.tecnomatica.android.glp.database.greendao.Municipio municipioboyeros = new cu.tecnomatica.android.glp.database.greendao.Municipio();
                municipioboyeros.setNombre("Boyeros");
                municipioboyeros.setProvincia(provinciahabana);
                municipioboyeros.setActivo(false);
                daoSession.insertOrReplace(municipioboyeros);

                cu.tecnomatica.android.glp.database.greendao.Municipio municipiocentrohabana = new cu.tecnomatica.android.glp.database.greendao.Municipio();
                municipiocentrohabana.setNombre("Centro Habana");
                municipiocentrohabana.setProvincia(provinciahabana);
                municipiocentrohabana.setActivo(false);
                daoSession.insertOrReplace(municipiocentrohabana);

                cu.tecnomatica.android.glp.database.greendao.Municipio municipiocerro = new cu.tecnomatica.android.glp.database.greendao.Municipio();
                municipiocerro.setNombre("Cerro");
                municipiocerro.setProvincia(provinciahabana);
                municipiocerro.setActivo(false);
                daoSession.insertOrReplace(municipiocerro);

                cu.tecnomatica.android.glp.database.greendao.Municipio municipiocotorro = new cu.tecnomatica.android.glp.database.greendao.Municipio();
                municipiocotorro.setNombre("Cotorro");
                municipiocotorro.setProvincia(provinciahabana);
                municipiocotorro.setActivo(false);
                daoSession.insertOrReplace(municipiocotorro);

                cu.tecnomatica.android.glp.database.greendao.Municipio municipiodiezdeoctubre = new cu.tecnomatica.android.glp.database.greendao.Municipio();
                municipiodiezdeoctubre.setNombre("Diez de Octubre");
                municipiodiezdeoctubre.setProvincia(provinciahabana);
                municipiodiezdeoctubre.setActivo(false);
                daoSession.insertOrReplace(municipiodiezdeoctubre);

                cu.tecnomatica.android.glp.database.greendao.Municipio municipioguanabacoa = new cu.tecnomatica.android.glp.database.greendao.Municipio();
                municipioguanabacoa.setNombre("Guanabacoa");
                municipioguanabacoa.setProvincia(provinciahabana);
                municipioguanabacoa.setActivo(false);
                daoSession.insertOrReplace(municipioguanabacoa);

                cu.tecnomatica.android.glp.database.greendao.Municipio municipiohabanadeleste = new cu.tecnomatica.android.glp.database.greendao.Municipio();
                municipiohabanadeleste.setNombre("Habana del Este");
                municipiohabanadeleste.setProvincia(provinciahabana);
                municipiohabanadeleste.setActivo(false);
                daoSession.insertOrReplace(municipiohabanadeleste);

                cu.tecnomatica.android.glp.database.greendao.Municipio municipiohabanavieja = new cu.tecnomatica.android.glp.database.greendao.Municipio();
                municipiohabanavieja.setNombre("Habana Vieja");
                municipiohabanavieja.setProvincia(provinciahabana);
                municipiohabanavieja.setActivo(false);
                daoSession.insertOrReplace(municipiohabanavieja);

                cu.tecnomatica.android.glp.database.greendao.Municipio municipioliza = new cu.tecnomatica.android.glp.database.greendao.Municipio();
                municipioliza.setNombre("La Lisa");
                municipioliza.setProvincia(provinciahabana);
                municipioliza.setActivo(false);
                daoSession.insertOrReplace(municipioliza);

                cu.tecnomatica.android.glp.database.greendao.Municipio municipiomarianao = new cu.tecnomatica.android.glp.database.greendao.Municipio();
                municipiomarianao.setNombre("Marianao");
                municipiomarianao.setProvincia(provinciahabana);
                municipiomarianao.setActivo(false);
                daoSession.insertOrReplace(municipiomarianao);

                cu.tecnomatica.android.glp.database.greendao.Municipio municipioplaya = new cu.tecnomatica.android.glp.database.greendao.Municipio();
                municipioplaya.setNombre("Playa");
                municipioplaya.setProvincia(provinciahabana);
                municipioplaya.setActivo(false);
                daoSession.insertOrReplace(municipioplaya);

                cu.tecnomatica.android.glp.database.greendao.Municipio municipioplaza = new cu.tecnomatica.android.glp.database.greendao.Municipio();
                municipioplaza.setNombre("Plaza de la Revolución");
                municipioplaza.setProvincia(provinciahabana);
                municipioplaza.setActivo(false);
                daoSession.insertOrReplace(municipioplaza);

                cu.tecnomatica.android.glp.database.greendao.Municipio municipioregla = new cu.tecnomatica.android.glp.database.greendao.Municipio();
                municipioregla.setNombre("Regla");
                municipioregla.setProvincia(provinciahabana);
                municipioregla.setActivo(false);
                daoSession.insertOrReplace(municipioregla);

                cu.tecnomatica.android.glp.database.greendao.Municipio municipiosanmiguel = new cu.tecnomatica.android.glp.database.greendao.Municipio();
                municipiosanmiguel.setNombre("San Miguel del Padrón");
                municipiosanmiguel.setProvincia(provinciahabana);
                municipiosanmiguel.setActivo(false);
                daoSession.insertOrReplace(municipiosanmiguel);

                //CASAS COMERCIALES LA HABANA
                cu.tecnomatica.android.glp.database.greendao.Casacomercial casacomercialarroyo = new cu.tecnomatica.android.glp.database.greendao.Casacomercial();

                casacomercialarroyo.setIdmunicipio(municipioarroyo.getIdmunicipio());
                casacomercialarroyo.setMunicipio(municipioarroyo);
                casacomercialarroyo.setDireccion("Calzada de 10 de Octubre y Sta Beatriz");
                casacomercialarroyo.setTelefono("76434080 Y 76438096");
                casacomercialarroyo.setHorario("8:00AM - 7:30PM");
                casacomercialarroyo.setNombre("CC Arroyo Naranjo");
                casacomercialarroyo.setLatitud("23.082025");
                casacomercialarroyo.setLongitud("-82.364211");
                daoSession.insertOrReplace(casacomercialarroyo);

                cu.tecnomatica.android.glp.database.greendao.Casacomercial casacomercialboyeros = new cu.tecnomatica.android.glp.database.greendao.Casacomercial();

                casacomercialboyeros.setIdmunicipio(municipioboyeros.getIdmunicipio());
                casacomercialboyeros.setMunicipio(municipioboyeros);
                casacomercialboyeros.setDireccion("Calle 2 esq. 9 Santiago de las Vegas");
                casacomercialboyeros.setLatitud("22.9705222");
                casacomercialboyeros.setLongitud("-82.3868027");
                casacomercialboyeros.setTelefono("76839179 ext 611");
                casacomercialboyeros.setHorario("8:00AM - 7:30PM");
                casacomercialboyeros.setNombre("CC Boyeros");
                daoSession.insertOrReplace(casacomercialboyeros);

                cu.tecnomatica.android.glp.database.greendao.Casacomercial casacomercialcerro = new cu.tecnomatica.android.glp.database.greendao.Casacomercial();

                casacomercialcerro.setIdmunicipio(municipiocerro.getIdmunicipio());
                casacomercialcerro.setMunicipio(municipiocerro);
                casacomercialcerro.setDireccion("Calzada de Luyano y Armenteros");
                casacomercialcerro.setLatitud("23.1060417");
                casacomercialcerro.setLongitud("-82.348727");
                casacomercialcerro.setTelefono("76968465 Y 76968467");
                casacomercialcerro.setHorario("8:00AM - 7:30PM");
                casacomercialcerro.setNombre("CC 10 de Octubre");
                daoSession.insertOrReplace(casacomercialcerro);

                cu.tecnomatica.android.glp.database.greendao.Casacomercial casacomercialcentrohabana = new cu.tecnomatica.android.glp.database.greendao.Casacomercial();

                casacomercialcentrohabana.setIdmunicipio(municipiocentrohabana.getIdmunicipio());
                casacomercialcentrohabana.setMunicipio(municipiocentrohabana);
                casacomercialcentrohabana.setDireccion("Calzada de Luyano y Armenteros");
                casacomercialcentrohabana.setLatitud("23.1060417");
                casacomercialcentrohabana.setLongitud("-82.348727");
                casacomercialcentrohabana.setTelefono("76968465 Y 76968467");
                casacomercialcentrohabana.setHorario("8:00AM - 7:30PM");
                casacomercialcentrohabana.setNombre("CC 10 de Octubre");
                daoSession.insertOrReplace(casacomercialcentrohabana);

                cu.tecnomatica.android.glp.database.greendao.Casacomercial casacomercialcotorro = new cu.tecnomatica.android.glp.database.greendao.Casacomercial();

                casacomercialcotorro.setIdmunicipio(municipiocotorro.getIdmunicipio());
                casacomercialcotorro.setMunicipio(municipiocotorro);
                casacomercialcotorro.setDireccion("Calle 101 esq 32 Magdalena. Cotorro");
                casacomercialcotorro.setLatitud("23.0399583");
                casacomercialcotorro.setLongitud("-82.2629916");
                casacomercialcotorro.setTelefono("76829643 Y 76823473");
                casacomercialcotorro.setHorario("8:00AM - 7:30PM");
                casacomercialcotorro.setNombre("CC Cotorro");
                daoSession.insertOrReplace(casacomercialcotorro);

                cu.tecnomatica.android.glp.database.greendao.Casacomercial casacomercialdiezoctubre = new cu.tecnomatica.android.glp.database.greendao.Casacomercial();

                casacomercialdiezoctubre.setIdmunicipio(municipiodiezdeoctubre.getIdmunicipio());
                casacomercialdiezoctubre.setMunicipio(municipiodiezdeoctubre);
                casacomercialdiezoctubre.setDireccion("Calzada de Luyano y Armenteros");
                casacomercialdiezoctubre.setLatitud("23.1060417");
                casacomercialdiezoctubre.setLongitud("-82.348727");
                casacomercialdiezoctubre.setTelefono("76968465 Y 76968467");
                casacomercialdiezoctubre.setHorario("8:00AM - 7:30PM");
                casacomercialdiezoctubre.setNombre("CC 10 de Octubre");
                daoSession.insertOrReplace(casacomercialdiezoctubre);

                cu.tecnomatica.android.glp.database.greendao.Casacomercial casacomercialguanabacoa = new cu.tecnomatica.android.glp.database.greendao.Casacomercial();

                casacomercialguanabacoa.setIdmunicipio(municipioguanabacoa.getIdmunicipio());
                casacomercialguanabacoa.setMunicipio(municipioguanabacoa);
                casacomercialguanabacoa.setDireccion("Independencia e/ Morales y Calle A");
                casacomercialguanabacoa.setLatitud("23.1200056");
                casacomercialguanabacoa.setLongitud("-82.2861416");
                casacomercialguanabacoa.setTelefono("77970413 Y 77972213");
                casacomercialguanabacoa.setHorario("8:00AM - 7:30PM");
                casacomercialguanabacoa.setNombre("CC Guanabacoa");
                daoSession.insertOrReplace(casacomercialguanabacoa);

                cu.tecnomatica.android.glp.database.greendao.Casacomercial casacomercialhabanadeleste = new cu.tecnomatica.android.glp.database.greendao.Casacomercial();

                casacomercialhabanadeleste.setIdmunicipio(municipiohabanadeleste.getIdmunicipio());
                casacomercialhabanadeleste.setMunicipio(municipiohabanadeleste);
                casacomercialhabanadeleste.setDireccion("Calle 162D e/e 3rac y 3raD Zona 1 Alamar");
                casacomercialhabanadeleste.setLatitud("23.1670556");
                casacomercialhabanadeleste.setLongitud("-82.2801277");
                casacomercialhabanadeleste.setTelefono("7623438 Y 7623437");
                casacomercialhabanadeleste.setHorario("8:00AM - 7:30PM");
                casacomercialhabanadeleste.setNombre("CC Habana del Este");
                daoSession.insertOrReplace(casacomercialhabanadeleste);

                cu.tecnomatica.android.glp.database.greendao.Casacomercial casacomercialhavanavieja = new cu.tecnomatica.android.glp.database.greendao.Casacomercial();

                casacomercialhavanavieja.setIdmunicipio(municipiohabanavieja.getIdmunicipio());
                casacomercialhavanavieja.setMunicipio(municipiohabanavieja);
                casacomercialhavanavieja.setDireccion("Calzada de Luyano y Armenteros");
                casacomercialhavanavieja.setLatitud("23.1060417");
                casacomercialhavanavieja.setLongitud("-82.348727");
                casacomercialhavanavieja.setTelefono("76968465 Y 76968467");
                casacomercialhavanavieja.setHorario("8:00AM - 7:30PM");
                casacomercialhavanavieja.setNombre("CC 10 de Octubre");
                daoSession.insertOrReplace(casacomercialhavanavieja);

                cu.tecnomatica.android.glp.database.greendao.Casacomercial casacomercialliza = new cu.tecnomatica.android.glp.database.greendao.Casacomercial();

                casacomercialliza.setIdmunicipio(municipioliza.getIdmunicipio());
                casacomercialliza.setMunicipio(municipioliza);
                casacomercialliza.setDireccion("Calle 190 esq. 69");
                casacomercialliza.setLatitud("23.06970");
                casacomercialliza.setLongitud("-82.44890");
                casacomercialliza.setTelefono("72601516 Y 72601125");
                casacomercialliza.setHorario("8:00AM - 7:30PM");
                casacomercialliza.setNombre("CC La Liza");
                daoSession.insertOrReplace(casacomercialliza);

                cu.tecnomatica.android.glp.database.greendao.Casacomercial casacomercialmarianao = new cu.tecnomatica.android.glp.database.greendao.Casacomercial();

                casacomercialmarianao.setIdmunicipio(municipiomarianao.getIdmunicipio());
                casacomercialmarianao.setMunicipio(municipiomarianao);
                casacomercialmarianao.setDireccion("Calle 134 esq. 57");
                casacomercialmarianao.setLatitud("23.07016");
                casacomercialmarianao.setLongitud("-82.43493");
                casacomercialmarianao.setTelefono("72605618 Y 72605647");
                casacomercialmarianao.setHorario("8:00AM - 7:30PM");
                casacomercialmarianao.setNombre("CC Marianao");
                daoSession.insertOrReplace(casacomercialmarianao);

                cu.tecnomatica.android.glp.database.greendao.Casacomercial casacomercialplaya = new cu.tecnomatica.android.glp.database.greendao.Casacomercial();

                casacomercialplaya.setIdmunicipio(municipioplaya.getIdmunicipio());
                casacomercialplaya.setMunicipio(municipioplaya);
                casacomercialplaya.setDireccion("Calle 3ra A y Calle 88");
                casacomercialplaya.setLatitud("23.10465");
                casacomercialplaya.setLongitud("-82.44572");
                casacomercialplaya.setTelefono("72035653 Y 72035877");
                casacomercialplaya.setHorario("8:00AM - 5:00PM");
                casacomercialplaya.setNombre("CC Playa");
                daoSession.insertOrReplace(casacomercialplaya);

                cu.tecnomatica.android.glp.database.greendao.Casacomercial casacomercialplaza = new cu.tecnomatica.android.glp.database.greendao.Casacomercial();

                casacomercialplaza.setIdmunicipio(municipioplaza.getIdmunicipio());
                casacomercialplaza.setMunicipio(municipioplaza);
                casacomercialplaza.setDireccion("Calle 3ra A y Calle 88");
                casacomercialplaza.setLatitud("23.10465");
                casacomercialplaza.setLongitud("-82.44572");
                casacomercialplaza.setTelefono("72035653 Y 72035877");
                casacomercialplaza.setHorario("8:00AM - 5:00PM");
                casacomercialplaza.setNombre("CC Playa");
                daoSession.insertOrReplace(casacomercialplaza);

                cu.tecnomatica.android.glp.database.greendao.Casacomercial casacomercialregla = new cu.tecnomatica.android.glp.database.greendao.Casacomercial();

                casacomercialregla.setIdmunicipio(municipioregla.getIdmunicipio());
                casacomercialregla.setMunicipio(municipioregla);
                casacomercialregla.setDireccion("Calzada de Regla esq. 10 de Octubre");
                casacomercialregla.setLatitud("23.1215278");
                casacomercialregla.setLongitud("-82.328722");
                casacomercialregla.setTelefono("77970415");
                casacomercialregla.setHorario("8:00AM - 7:30PM");
                casacomercialregla.setNombre("CC Regla");
                daoSession.insertOrReplace(casacomercialregla);

                cu.tecnomatica.android.glp.database.greendao.Casacomercial casacomercialsanmiguel = new cu.tecnomatica.android.glp.database.greendao.Casacomercial();

                casacomercialsanmiguel.setIdmunicipio(municipiosanmiguel.getIdmunicipio());
                casacomercialsanmiguel.setMunicipio(municipiosanmiguel);
                casacomercialsanmiguel.setDireccion("Avenida 1ra #1058 e/ Pepe Prieto y Miranda");
                casacomercialsanmiguel.setLatitud("23.0972833");
                casacomercialsanmiguel.setLongitud("-82.32743055");
                casacomercialsanmiguel.setTelefono("76938550 Y 76938551");
                casacomercialsanmiguel.setHorario("8:00AM - 5:00PM");
                casacomercialsanmiguel.setNombre("CC San Miguel del Padrón");
                daoSession.insertOrReplace(casacomercialsanmiguel);

                //PUNTOS DE VENTA LA HABANA

                //PUNTOS DE VENTA GUANABACOA (OK)

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa30001 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa30001.setCodigo("30001");
                puntoventa30001.setMunicipio(municipioguanabacoa);
                puntoventa30001.setIdmunicipio(municipioguanabacoa.getIdmunicipio());
                puntoventa30001.setDireccion("6ta e/ Central y Vigario Rpto Nalon");
                puntoventa30001.setLatitud("23.1149111");
                puntoventa30001.setLongitud("-82.2919805555555");
                puntoventa30001.setHorario("11:00AM - 7:30PM");
                puntoventa30001.setTelefono("No Tiene");
                puntoventa30001.setNombre("Nalón");
                daoSession.insertOrReplace(puntoventa30001);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa30002 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa30002.setCodigo("30002");
                puntoventa30002.setMunicipio(municipioguanabacoa);
                puntoventa30002.setIdmunicipio(municipioguanabacoa.getIdmunicipio());
                puntoventa30002.setDireccion("A esquina 14 Roble");
                puntoventa30002.setLatitud("23.1314472");
                puntoventa30002.setLongitud("-82.29460555");
                puntoventa30002.setHorario("11:00AM - 7:30PM");
                puntoventa30002.setTelefono("No Tiene");
                puntoventa30002.setNombre("Roble");
                daoSession.insertOrReplace(puntoventa30002);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa30003 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa30003.setCodigo("30003");
                puntoventa30003.setMunicipio(municipioguanabacoa);
                puntoventa30003.setIdmunicipio(municipioguanabacoa.getIdmunicipio());
                puntoventa30003.setDireccion("1ra e/ 7ma y Ave Guanabacoa");
                puntoventa30003.setLatitud("23.1173806");
                puntoventa30003.setLongitud("-82.30954444");
                puntoventa30003.setHorario("11:00AM - 7:30PM");
                puntoventa30003.setTelefono("No Tiene");
                puntoventa30003.setNombre("Chivás");
                daoSession.insertOrReplace(puntoventa30003);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa30004 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa30004.setCodigo("30004");
                puntoventa30004.setMunicipio(municipioguanabacoa);
                puntoventa30004.setIdmunicipio(municipioguanabacoa.getIdmunicipio());
                puntoventa30004.setDireccion("Cristobal de la Guardia e/ Nazareno y Cdte Beche");
                puntoventa30004.setLatitud("23.1165833");
                puntoventa30004.setLongitud("-82.301466666");
                puntoventa30004.setHorario("11:00AM - 7:30PM");
                puntoventa30004.setTelefono("No Tiene");
                puntoventa30004.setNombre("D´Beche");
                daoSession.insertOrReplace(puntoventa30004);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa30005 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa30005.setCodigo("30005");
                puntoventa30005.setMunicipio(municipioguanabacoa);
                puntoventa30005.setIdmunicipio(municipioguanabacoa.getIdmunicipio());
                puntoventa30005.setDireccion("Calle Real Santa Fe");
                puntoventa30005.setLatitud("23.1287806");
                puntoventa30005.setLongitud("-82.2616916666");
                puntoventa30005.setHorario("11:00AM - 7:30PM");
                puntoventa30005.setTelefono("No Tiene");
                puntoventa30005.setNombre("Calle Real");
                daoSession.insertOrReplace(puntoventa30005);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa30007 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa30007.setCodigo("30007");
                puntoventa30007.setMunicipio(municipioguanabacoa);
                puntoventa30007.setIdmunicipio(municipioguanabacoa.getIdmunicipio());
                puntoventa30007.setDireccion("Bertematy e/ Maceo y Luz");
                puntoventa30007.setLatitud("23.1234667");
                puntoventa30007.setLongitud("-82.302144444");
                puntoventa30007.setHorario("11:00AM - 7:30PM");
                puntoventa30007.setTelefono("No Tiene");
                puntoventa30007.setNombre("Bertematy");
                daoSession.insertOrReplace(puntoventa30007);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa30008 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa30008.setCodigo("30008");
                puntoventa30008.setMunicipio(municipioguanabacoa);
                puntoventa30008.setIdmunicipio(municipioguanabacoa.getIdmunicipio());
                puntoventa30008.setDireccion("Calle 2da e/ C y D Rpto H.Nueva");
                puntoventa30008.setLatitud("23.1186444");
                puntoventa30008.setLongitud("-82.322558333");
                puntoventa30008.setHorario("11:00AM - 7:30PM");
                puntoventa30008.setTelefono("No Tiene");
                puntoventa30008.setNombre("Habana Nueva");
                daoSession.insertOrReplace(puntoventa30008);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa30009 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa30009.setCodigo("30009");
                puntoventa30009.setMunicipio(municipioguanabacoa);
                puntoventa30009.setIdmunicipio(municipioguanabacoa.getIdmunicipio());
                puntoventa30009.setDireccion("Versalles y Calixto Garcias");
                puntoventa30009.setLatitud("23.1282806");
                puntoventa30009.setLongitud("-82.307122222");
                puntoventa30009.setHorario("11:00AM - 7:30PM");
                puntoventa30009.setTelefono("No Tiene");
                puntoventa30009.setNombre("Versalles");
                daoSession.insertOrReplace(puntoventa30009);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa30010 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa30010.setCodigo("30010");
                puntoventa30010.setMunicipio(municipioguanabacoa);
                puntoventa30010.setIdmunicipio(municipioguanabacoa.getIdmunicipio());
                puntoventa30010.setDireccion("D Mora esquina Nazareno");
                puntoventa30010.setLatitud("23.1266139");
                puntoventa30010.setLongitud("-82.3038861111");
                puntoventa30010.setHorario("11:00AM - 7:30PM");
                puntoventa30010.setTelefono("No Tiene");
                puntoventa30010.setNombre("Dr Mora");
                daoSession.insertOrReplace(puntoventa30010);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa30011 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa30011.setCodigo("30011");
                puntoventa30011.setMunicipio(municipioguanabacoa);
                puntoventa30011.setIdmunicipio(municipioguanabacoa.getIdmunicipio());
                puntoventa30011.setDireccion("Independencia e/ A y Morales");
                puntoventa30011.setLatitud("23.1173806");
                puntoventa30011.setLongitud("-82.30954444");
                puntoventa30011.setHorario("11:00AM - 7:30PM");
                puntoventa30011.setTelefono("No Tiene");
                puntoventa30011.setNombre("La Previsora");
                daoSession.insertOrReplace(puntoventa30011);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa30012 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa30012.setCodigo("30012");
                puntoventa30012.setMunicipio(municipioguanabacoa);
                puntoventa30012.setIdmunicipio(municipioguanabacoa.getIdmunicipio());
                puntoventa30012.setDireccion("Calle 6ta esq 1ra Rto Mañana");
                puntoventa30012.setLatitud("23.10855");
                puntoventa30012.setLongitud("-82.322475");
                puntoventa30012.setHorario("11:00AM - 7:30PM");
                puntoventa30012.setTelefono("No Tiene");
                puntoventa30012.setNombre("Mañana");
                daoSession.insertOrReplace(puntoventa30012);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa30013 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa30013.setCodigo("30013");
                puntoventa30013.setMunicipio(municipioguanabacoa);
                puntoventa30013.setIdmunicipio(municipioguanabacoa.getIdmunicipio());
                puntoventa30013.setDireccion("Maceo e/ Sta Maria y Apodaca");
                puntoventa30013.setLatitud("23.1252083");
                puntoventa30013.setLongitud("-82.2974111111111");
                puntoventa30013.setHorario("11:00AM - 7:30PM");
                puntoventa30013.setTelefono("No Tiene");
                puntoventa30013.setNombre("Santa María");
                daoSession.insertOrReplace(puntoventa30013);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa30014 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa30014.setCodigo("30014");
                puntoventa30014.setMunicipio(municipioguanabacoa);
                puntoventa30014.setIdmunicipio(municipioguanabacoa.getIdmunicipio());
                puntoventa30014.setDireccion("Barreto esquina padilla");
                puntoventa30014.setLatitud("23.1206361");
                puntoventa30014.setLongitud("-82.3052444444444");
                puntoventa30014.setHorario("11:00AM - 7:30PM");
                puntoventa30014.setTelefono("No Tiene");
                puntoventa30014.setNombre("Barreto");
                daoSession.insertOrReplace(puntoventa30014);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa30015 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa30015.setCodigo("30015");
                puntoventa30015.setMunicipio(municipioguanabacoa);
                puntoventa30015.setIdmunicipio(municipioguanabacoa.getIdmunicipio());
                puntoventa30015.setDireccion("Apodaca e/ Corrales y Aparicion");
                puntoventa30015.setLatitud("23.1209583");
                puntoventa30015.setLongitud("-82.2956666666666");
                puntoventa30015.setHorario("11:00AM - 7:30PM");
                puntoventa30015.setTelefono("No Tiene");
                puntoventa30015.setNombre("Apodaca");
                daoSession.insertOrReplace(puntoventa30015);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa30016 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa30016.setCodigo("30016");
                puntoventa30016.setMunicipio(municipioguanabacoa);
                puntoventa30016.setIdmunicipio(municipioguanabacoa.getIdmunicipio());
                puntoventa30016.setDireccion("Calixto Garcias e/ Versalles y Santo Domingo");
                puntoventa30016.setLatitud("23.1288528");
                puntoventa30016.setLongitud("-82.3022833333333");
                puntoventa30016.setHorario("11:00AM - 7:30PM");
                puntoventa30016.setTelefono("No Tiene");
                puntoventa30016.setNombre("Calixto García");
                daoSession.insertOrReplace(puntoventa30016);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa30017 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa30017.setCodigo("30017");
                puntoventa30017.setMunicipio(municipioguanabacoa);
                puntoventa30017.setIdmunicipio(municipioguanabacoa.getIdmunicipio());
                puntoventa30017.setDireccion("Calle Real Gallega");
                puntoventa30017.setLatitud("23.1309556");
                puntoventa30017.setLongitud("-82.2149111111111");
                puntoventa30017.setHorario("11:00AM - 7:30PM");
                puntoventa30017.setTelefono("No Tiene");
                puntoventa30017.setNombre("La Gallega");
                daoSession.insertOrReplace(puntoventa30017);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa30018 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa30018.setCodigo("30018");
                puntoventa30018.setMunicipio(municipioguanabacoa);
                puntoventa30018.setIdmunicipio(municipioguanabacoa.getIdmunicipio());
                puntoventa30018.setDireccion("San Luis esquina 4ta Rpto Azotea");
                puntoventa30018.setLatitud("23.1149722");
                puntoventa30018.setLongitud("-82.3139833333333");
                puntoventa30018.setHorario("11:00AM - 7:30PM");
                puntoventa30018.setTelefono("No Tiene");
                puntoventa30018.setNombre("La Azotea");
                daoSession.insertOrReplace(puntoventa30018);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa30020 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa30020.setCodigo("30020");
                puntoventa30020.setMunicipio(municipioguanabacoa);
                puntoventa30020.setIdmunicipio(municipioguanabacoa.getIdmunicipio());
                puntoventa30020.setDireccion("Altura de Villa Maria e/ B y Nominar");
                puntoventa30020.setLatitud("23.091925");
                puntoventa30020.setLongitud("-82.2823805555555");
                puntoventa30020.setHorario("11:00AM - 7:30PM");
                puntoventa30020.setTelefono("No Tiene");
                puntoventa30020.setNombre("Cubana de Bronce");
                daoSession.insertOrReplace(puntoventa30020);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa30021 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa30021.setCodigo("30021");
                puntoventa30021.setMunicipio(municipioguanabacoa);
                puntoventa30021.setIdmunicipio(municipioguanabacoa.getIdmunicipio());
                puntoventa30021.setDireccion("Ctra km 8 Santa María del Rosario");
                puntoventa30021.setLatitud("23.1013056");
                puntoventa30021.setLongitud("-82.2854333333333");
                puntoventa30021.setHorario("11:00AM - 7:30PM");
                puntoventa30021.setTelefono("No Tiene");
                puntoventa30021.setNombre("Villa María");
                daoSession.insertOrReplace(puntoventa30021);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa30022 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa30022.setCodigo("30022");
                puntoventa30022.setMunicipio(municipioguanabacoa);
                puntoventa30022.setIdmunicipio(municipioguanabacoa.getIdmunicipio());
                puntoventa30022.setDireccion("Ctra de Villa María km 6 1/2");
                puntoventa30022.setLatitud("23.08385");
                puntoventa30022.setLongitud("-82.2780777777777");
                puntoventa30022.setHorario("11:00AM - 7:30PM");
                puntoventa30022.setTelefono("No Tiene");
                puntoventa30022.setNombre("La Yuca");
                daoSession.insertOrReplace(puntoventa30022);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa30023 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa30023.setCodigo("30023");
                puntoventa30023.setMunicipio(municipioguanabacoa);
                puntoventa30023.setIdmunicipio(municipioguanabacoa.getIdmunicipio());
                puntoventa30023.setDireccion("Calle 6ta e/ Berroa y Final");
                puntoventa30023.setLatitud("23.0917639");
                puntoventa30023.setLongitud("-82.2261444444444");
                puntoventa30023.setHorario("11:00AM - 7:30PM");
                puntoventa30023.setTelefono("No Tiene");
                puntoventa30023.setNombre("Peñalver");
                daoSession.insertOrReplace(puntoventa30023);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa30024 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa30024.setCodigo("30024");
                puntoventa30024.setMunicipio(municipioguanabacoa);
                puntoventa30024.setIdmunicipio(municipioguanabacoa.getIdmunicipio());
                puntoventa30024.setDireccion("Ctra de Pañalver km 5 Via Monumental");
                puntoventa30024.setLatitud("23.0949472");
                puntoventa30024.setLongitud("-82.246875");
                puntoventa30024.setHorario("11:00AM - 7:30PM");
                puntoventa30024.setTelefono("No Tiene");
                puntoventa30024.setNombre("Los Mangos");
                daoSession.insertOrReplace(puntoventa30024);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa30025 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa30025.setCodigo("30025");
                puntoventa30025.setMunicipio(municipioguanabacoa);
                puntoventa30025.setIdmunicipio(municipioguanabacoa.getIdmunicipio());
                puntoventa30025.setDireccion("Calle Real s/n lado del Mercado");
                puntoventa30025.setLatitud("23.1303889");
                puntoventa30025.setLongitud("-82.2381638888888");
                puntoventa30025.setHorario("11:00AM - 7:30PM");
                puntoventa30025.setTelefono("No Tiene");
                puntoventa30025.setNombre("Bacuranao");
                daoSession.insertOrReplace(puntoventa30025);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa30026 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa30026.setCodigo("30026");
                puntoventa30026.setMunicipio(municipioguanabacoa);
                puntoventa30026.setIdmunicipio(municipioguanabacoa.getIdmunicipio());
                puntoventa30026.setDireccion("Ctra Peñalver km 2 e/ 1ra y Anillo");
                puntoventa30026.setLatitud("23.09885");
                puntoventa30026.setLongitud("-82.2658694444444");
                puntoventa30026.setHorario("11:00AM - 7:30PM");
                puntoventa30026.setTelefono("No Tiene");
                puntoventa30026.setNombre("Repollo");
                daoSession.insertOrReplace(puntoventa30026);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa30027 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa30027.setCodigo("30027");
                puntoventa30027.setMunicipio(municipioguanabacoa);
                puntoventa30027.setIdmunicipio(municipioguanabacoa.getIdmunicipio());
                puntoventa30027.setDireccion("Ctra Santa Maria del Rosario y Autopista Nacional");
                puntoventa30027.setLatitud("23.0888111");
                puntoventa30027.setLongitud("-82.269825");
                puntoventa30027.setHorario("11:00AM - 7:30PM");
                puntoventa30027.setTelefono("No Tiene");
                puntoventa30027.setNombre("Alecrín");
                daoSession.insertOrReplace(puntoventa30027);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa30028 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa30028.setCodigo("30028");
                puntoventa30028.setMunicipio(municipioguanabacoa);
                puntoventa30028.setIdmunicipio(municipioguanabacoa.getIdmunicipio());
                puntoventa30028.setDireccion("Calle 4ta e/ F y G Lima");
                puntoventa30028.setLatitud("23.1256667");
                puntoventa30028.setLongitud("-82.2852111111111");
                puntoventa30028.setHorario("11:00AM - 7:30PM");
                puntoventa30028.setTelefono("No Tiene");
                puntoventa30028.setNombre("La Lima");
                daoSession.insertOrReplace(puntoventa30028);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa30029 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa30029.setCodigo("30029");
                puntoventa30029.setMunicipio(municipioguanabacoa);
                puntoventa30029.setIdmunicipio(municipioguanabacoa.getIdmunicipio());
                puntoventa30029.setDireccion("Callejon Finca e/ Jesus Garay y 8 Escala");
                puntoventa30029.setLatitud("23.1198806");
                puntoventa30029.setLongitud("-82.280225");
                puntoventa30029.setHorario("11:00AM - 7:30PM");
                puntoventa30029.setTelefono("No Tiene");
                puntoventa30029.setNombre("Escala");
                daoSession.insertOrReplace(puntoventa30029);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa30030 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa30030.setCodigo("30030");
                puntoventa30030.setMunicipio(municipioguanabacoa);
                puntoventa30030.setIdmunicipio(municipioguanabacoa.getIdmunicipio());
                puntoventa30030.setDireccion("Independencia km 1 Ricabal");
                puntoventa30030.setLatitud("23.1238556");
                puntoventa30030.setLongitud("-82.2776861111111");
                puntoventa30030.setHorario("11:00AM - 7:30PM");
                puntoventa30030.setTelefono("No Tiene");
                puntoventa30030.setNombre("Ricabal");
                daoSession.insertOrReplace(puntoventa30030);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa30031 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa30031.setCodigo("30031");
                puntoventa30031.setMunicipio(municipioguanabacoa);
                puntoventa30031.setIdmunicipio(municipioguanabacoa.getIdmunicipio());
                puntoventa30031.setDireccion("Calle Real s/n Barrera");
                puntoventa30031.setLatitud("23.1463639");
                puntoventa30031.setLongitud("-82.215125");
                puntoventa30031.setHorario("11:00AM - 7:30PM");
                puntoventa30031.setTelefono("No Tiene");
                puntoventa30031.setNombre("Barrera");
                daoSession.insertOrReplace(puntoventa30031);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa30032 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa30032.setCodigo("30032");
                puntoventa30032.setMunicipio(municipioguanabacoa);
                puntoventa30032.setIdmunicipio(municipioguanabacoa.getIdmunicipio());
                puntoventa30032.setDireccion("Camino de Minas del Petroleo y km 2");
                puntoventa30032.setLatitud("23.1529639");
                puntoventa30032.setLongitud("-82.203425");
                puntoventa30032.setHorario("11:00AM - 7:30PM");
                puntoventa30032.setTelefono("No Tiene");
                puntoventa30032.setNombre("La Pelá");
                daoSession.insertOrReplace(puntoventa30032);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa30033 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa30033.setCodigo("30033");
                puntoventa30033.setMunicipio(municipioguanabacoa);
                puntoventa30033.setIdmunicipio(municipioguanabacoa.getIdmunicipio());
                puntoventa30033.setDireccion("Calle Real s/n al lado del sector");
                puntoventa30033.setLatitud("23.0820722");
                puntoventa30033.setLongitud("-82.1827388888888");
                puntoventa30033.setHorario("11:00AM - 7:30PM");
                puntoventa30033.setTelefono("No Tiene");
                puntoventa30033.setNombre("Arango");
                daoSession.insertOrReplace(puntoventa30033);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa30034 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa30034.setCodigo("30034");
                puntoventa30034.setMunicipio(municipioguanabacoa);
                puntoventa30034.setIdmunicipio(municipioguanabacoa.getIdmunicipio());
                puntoventa30034.setDireccion("Martí s/n detrás del bodegón");
                puntoventa30034.setLatitud("23.1281806");
                puntoventa30034.setLongitud("-82.1925166666666");
                puntoventa30034.setHorario("11:00AM - 7:30PM");
                puntoventa30034.setTelefono("No Tiene");
                puntoventa30034.setNombre("Minas");
                daoSession.insertOrReplace(puntoventa30034);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa30035 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa30035.setCodigo("30035");
                puntoventa30035.setMunicipio(municipioguanabacoa);
                puntoventa30035.setIdmunicipio(municipioguanabacoa.getIdmunicipio());
                puntoventa30035.setDireccion("Calle Real s/n al lado de la bodega");
                puntoventa30035.setLatitud("23.0649917");
                puntoventa30035.setLongitud("-82.1773833333333");
                puntoventa30035.setHorario("11:00AM - 7:30PM");
                puntoventa30035.setTelefono("No Tiene");
                puntoventa30035.setNombre("Sepultura");
                daoSession.insertOrReplace(puntoventa30035);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa30037 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa30037.setCodigo("30037");
                puntoventa30037.setMunicipio(municipioguanabacoa);
                puntoventa30037.setIdmunicipio(municipioguanabacoa.getIdmunicipio());
                puntoventa30037.setDireccion("Fuente esquina Obispo");
                puntoventa30037.setLatitud("23.1200667");
                puntoventa30037.setLongitud("-82.3127472222222");
                puntoventa30037.setHorario("11:00AM - 7:30PM");
                puntoventa30037.setTelefono("No Tiene");
                puntoventa30037.setNombre("Fuente y Obispo");
                daoSession.insertOrReplace(puntoventa30037);

                //PUNTOS DE VENTA REGLA (OK)

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa70001 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa70001.setCodigo("70001");
                puntoventa70001.setMunicipio(municipioregla);
                puntoventa70001.setIdmunicipio(municipioregla.getIdmunicipio());
                puntoventa70001.setDireccion("Calzada de Regla, esquina 10 de Octubre");
                puntoventa70001.setLatitud("23.1215278");
                puntoventa70001.setLongitud("-82.3287222222222");
                puntoventa70001.setHorario("11:00AM - 7:30PM");
                puntoventa70001.setTelefono("No Tiene");
                puntoventa70001.setNombre("");
                daoSession.insertOrReplace(puntoventa70001);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa70005 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa70005.setCodigo("70005");
                puntoventa70005.setMunicipio(municipioregla);
                puntoventa70005.setIdmunicipio(municipioregla.getIdmunicipio());
                puntoventa70005.setDireccion("Camino de la Virgen y Ciruela");
                puntoventa70005.setLatitud("23.1198333");
                puntoventa70005.setLongitud("-82.32633333");
                puntoventa70005.setHorario("11:00AM - 7:30PM");
                puntoventa70005.setTelefono("No Tiene");
                puntoventa70005.setNombre("La Ciruela");
                daoSession.insertOrReplace(puntoventa70005);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa70003 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa70003.setCodigo("70003");
                puntoventa70003.setMunicipio(municipioregla);
                puntoventa70003.setIdmunicipio(municipioregla.getIdmunicipio());
                puntoventa70003.setDireccion("4ta esq. C. Reparto El Modelo");
                puntoventa70003.setLatitud("23.1166944");
                puntoventa70003.setLongitud("-82.32961111");
                puntoventa70003.setHorario("11:00AM - 7:30PM");
                puntoventa70003.setTelefono("No Tiene");
                puntoventa70003.setNombre("El Modelo");
                daoSession.insertOrReplace(puntoventa70003);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa70010 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa70010.setCodigo("70010");
                puntoventa70010.setMunicipio(municipioregla);
                puntoventa70010.setIdmunicipio(municipioregla.getIdmunicipio());
                puntoventa70010.setDireccion("Recreo y Agramonte");
                puntoventa70010.setLatitud("23.1198333");
                puntoventa70010.setLongitud("-82.32633333");
                puntoventa70010.setHorario("11:00AM - 7:30PM");
                puntoventa70010.setTelefono("No Tiene");
                puntoventa70010.setNombre("El Recreo");
                daoSession.insertOrReplace(puntoventa70010);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa70004 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa70004.setCodigo("70004");
                puntoventa70004.setMunicipio(municipioregla);
                puntoventa70004.setIdmunicipio(municipioregla.getIdmunicipio());
                puntoventa70004.setDireccion("Diaz Benítez y Aranguren");
                puntoventa70004.setLatitud("23.1245556");
                puntoventa70004.setLongitud("-82.3353611111");
                puntoventa70004.setHorario("11:00AM - 7:30PM");
                puntoventa70004.setTelefono("No Tiene");
                puntoventa70004.setNombre("PV Diaz Benítez y Aranguren");
                daoSession.insertOrReplace(puntoventa70004);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa70016 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa70016.setCodigo("70016");
                puntoventa70016.setMunicipio(municipioregla);
                puntoventa70016.setIdmunicipio(municipioregla.getIdmunicipio());
                puntoventa70016.setDireccion("Calixto Garcia y Aranguren");
                puntoventa70016.setLatitud("23.127");
                puntoventa70016.setLongitud("-82.3318055555");
                puntoventa70016.setHorario("11:00AM - 7:30PM");
                puntoventa70016.setTelefono("No Tiene");
                puntoventa70016.setNombre("PV Calixto Garcia y Aranguren");
                daoSession.insertOrReplace(puntoventa70016);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa70015 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa70015.setCodigo("70015");
                puntoventa70015.setMunicipio(municipioregla);
                puntoventa70015.setIdmunicipio(municipioregla.getIdmunicipio());
                puntoventa70015.setDireccion("Pasaje Alemán");
                puntoventa70015.setLatitud("23.1259167");
                puntoventa70015.setLongitud("-82.330027777");
                puntoventa70015.setHorario("11:00AM - 7:30PM");
                puntoventa70015.setTelefono("No Tiene");
                puntoventa70015.setNombre("PV Pasaje Alemán");
                daoSession.insertOrReplace(puntoventa70015);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa70002 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa70002.setCodigo("70002");
                puntoventa70002.setMunicipio(municipioregla);
                puntoventa70002.setIdmunicipio(municipioregla.getIdmunicipio());
                puntoventa70002.setDireccion("Camilo Cienfuegos y Valdés Da Pena");
                puntoventa70002.setLatitud("23.1271944");
                puntoventa70002.setLongitud("-82.324694444");
                puntoventa70002.setHorario("11:00AM - 7:30PM");
                puntoventa70002.setTelefono("No Tiene");
                puntoventa70002.setNombre("PV Camilo Cienfuegos y Valdés Da Pena");
                daoSession.insertOrReplace(puntoventa70002);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa70011 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa70011.setCodigo("70011");
                puntoventa70011.setMunicipio(municipioregla);
                puntoventa70011.setIdmunicipio(municipioregla.getIdmunicipio());
                puntoventa70011.setDireccion("San Luís y C");
                puntoventa70011.setLatitud("23.1238333");
                puntoventa70011.setLongitud("-82.320944444");
                puntoventa70011.setHorario("11:00AM - 7:30PM");
                puntoventa70011.setTelefono("No Tiene");
                puntoventa70011.setNombre("PV San Luís y C");
                daoSession.insertOrReplace(puntoventa70011);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa70006 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa70006.setCodigo("70006");
                puntoventa70006.setMunicipio(municipioregla);
                puntoventa70006.setIdmunicipio(municipioregla.getIdmunicipio());
                puntoventa70006.setDireccion("Carretera de Casa Blanca e/ Dique y Planta Eléctrica");
                puntoventa70006.setLatitud("23.1460556");
                puntoventa70006.setLongitud("-82.33088888");
                puntoventa70006.setHorario("11:00AM - 7:30PM");
                puntoventa70006.setTelefono("No Tiene");
                puntoventa70006.setNombre("PV Casa Blanca");
                daoSession.insertOrReplace(puntoventa70006);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa70007 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa70007.setCodigo("70007");
                puntoventa70007.setMunicipio(municipioregla);
                puntoventa70007.setIdmunicipio(municipioregla.getIdmunicipio());
                puntoventa70007.setDireccion("Artex y Animas");
                puntoventa70007.setLatitud("23.1420556");
                puntoventa70007.setLongitud("-82.339472222");
                puntoventa70007.setHorario("11:00AM - 7:30PM");
                puntoventa70007.setTelefono("No Tiene");
                puntoventa70007.setNombre("PV Artex y Animas");
                daoSession.insertOrReplace(puntoventa70007);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa70008 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa70008.setCodigo("70008");
                puntoventa70008.setMunicipio(municipioregla);
                puntoventa70008.setIdmunicipio(municipioregla.getIdmunicipio());
                puntoventa70008.setDireccion("Carretera del Asilo");
                puntoventa70008.setLatitud("23.1490278");
                puntoventa70008.setLongitud("-82.336611111");
                puntoventa70008.setHorario("11:00AM - 7:30PM");
                puntoventa70008.setTelefono("No Tiene");
                puntoventa70008.setNombre("PV Carretera del Asilo");
                daoSession.insertOrReplace(puntoventa70008);


                //PUNTOS DE VENTA ARROYO NARANJO (OK)
                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa12001 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa12001.setCodigo("12001");
                puntoventa12001.setMunicipio(municipioarroyo);
                puntoventa12001.setIdmunicipio(municipioarroyo.getIdmunicipio());
                puntoventa12001.setDireccion("7ma esq. Rosales Electrico");
                puntoventa12001.setLatitud("23.0422361");
                puntoventa12001.setLongitud("-82.33508888");
                puntoventa12001.setHorario("11:00AM - 7:30PM");
                puntoventa12001.setTelefono("No Tiene");
                puntoventa12001.setNombre("Eléctrico");
                daoSession.insertOrReplace(puntoventa12001);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa12002 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa12002.setCodigo("12002");
                puntoventa12002.setMunicipio(municipioarroyo);
                puntoventa12002.setIdmunicipio(municipioarroyo.getIdmunicipio());
                puntoventa12002.setDireccion("Czda 10 de Octubre / San Lenardo y Santa Beatriz");
                puntoventa12002.setLatitud("23.0819611");
                puntoventa12002.setLongitud("-82.364174999");
                puntoventa12002.setHorario("11:00AM - 7:30PM");
                puntoventa12002.setTelefono("No Tiene");
                puntoventa12002.setNombre("Víbora Park");
                daoSession.insertOrReplace(puntoventa12002);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa12004 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa12004.setCodigo("12004");
                puntoventa12004.setMunicipio(municipioarroyo);
                puntoventa12004.setIdmunicipio(municipioarroyo.getIdmunicipio());
                puntoventa12004.setDireccion("Tunicu esq. Cuba Los Pinos");
                puntoventa12004.setLatitud("23.0582028");
                puntoventa12004.setLongitud("-82.3733833");
                puntoventa12004.setHorario("11:00AM - 7:30PM");
                puntoventa12004.setTelefono("No Tiene");
                puntoventa12004.setNombre("Los Pinos");
                daoSession.insertOrReplace(puntoventa12004);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa12005 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa12005.setCodigo("12005");
                puntoventa12005.setMunicipio(municipioarroyo);
                puntoventa12005.setIdmunicipio(municipioarroyo.getIdmunicipio());
                puntoventa12005.setDireccion("Don Tomas y Mirador Mantilla");
                puntoventa12005.setLatitud("23.0656389");
                puntoventa12005.setLongitud("-82.3446361111");
                puntoventa12005.setHorario("11:00AM - 7:30PM");
                puntoventa12005.setTelefono("No Tiene");
                puntoventa12005.setNombre("Don Tomas y Mirador Mantilla");
                daoSession.insertOrReplace(puntoventa12005);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa12006 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa12006.setCodigo("12006");
                puntoventa12006.setMunicipio(municipioarroyo);
                puntoventa12006.setIdmunicipio(municipioarroyo.getIdmunicipio());
                puntoventa12006.setDireccion("Parque / Calvario y Entrada Calvario");
                puntoventa12006.setLatitud("23.0568667");
                puntoventa12006.setLongitud("-82.331949999");
                puntoventa12006.setHorario("11:00AM - 7:30PM");
                puntoventa12006.setTelefono("No Tiene");
                puntoventa12006.setNombre("Parque y Entrada");
                daoSession.insertOrReplace(puntoventa12006);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa12008 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa12008.setCodigo("12008");
                puntoventa12008.setMunicipio(municipioarroyo);
                puntoventa12008.setIdmunicipio(municipioarroyo.getIdmunicipio());
                puntoventa12008.setDireccion("Estrella y Nueva Gerona");
                puntoventa12008.setLatitud("23.0625333");
                puntoventa12008.setLongitud("-82.3540638888");
                puntoventa12008.setHorario("11:00AM - 7:30PM");
                puntoventa12008.setTelefono("No Tiene");
                puntoventa12008.setNombre("Estrella y Nueva Geronas");
                daoSession.insertOrReplace(puntoventa12008);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa12010 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa12010.setCodigo("12010");
                puntoventa12010.setMunicipio(municipioarroyo);
                puntoventa12010.setIdmunicipio(municipioarroyo.getIdmunicipio());
                puntoventa12010.setDireccion("1ra / C y D Poey");
                puntoventa12010.setLatitud("23.0693667");
                puntoventa12010.setLongitud("-82.366194444");
                puntoventa12010.setHorario("11:00AM - 7:30PM");
                puntoventa12010.setTelefono("No Tiene");
                puntoventa12010.setNombre("Poey 1");
                daoSession.insertOrReplace(puntoventa12010);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa12011 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa12011.setCodigo("12011");
                puntoventa12011.setMunicipio(municipioarroyo);
                puntoventa12011.setIdmunicipio(municipioarroyo.getIdmunicipio());
                puntoventa12011.setDireccion("7ma / C y D Poey");
                puntoventa12011.setLatitud("23.06355");
                puntoventa12011.setLongitud("-82.367633333");
                puntoventa12011.setHorario("11:00AM - 7:30PM");
                puntoventa12011.setTelefono("No Tiene");
                puntoventa12011.setNombre("Poey 2");
                daoSession.insertOrReplace(puntoventa12011);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa12012 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa12012.setCodigo("12012");
                puntoventa12012.setMunicipio(municipioarroyo);
                puntoventa12012.setIdmunicipio(municipioarroyo.getIdmunicipio());
                puntoventa12012.setDireccion("Rodríguez de Armas / Delicias y Sta Julia");
                puntoventa12012.setLatitud("23.0765806");
                puntoventa12012.setLongitud("-82.3456472222");
                puntoventa12012.setHorario("11:00AM - 7:30PM");
                puntoventa12012.setTelefono("No Tiene");
                puntoventa12012.setNombre("Rodríguez de Armas");
                daoSession.insertOrReplace(puntoventa12012);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa12013 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa12013.setCodigo("12013");
                puntoventa12013.setMunicipio(municipioarroyo);
                puntoventa12013.setIdmunicipio(municipioarroyo.getIdmunicipio());
                puntoventa12013.setDireccion("1ra y Paseo Guásimas");
                puntoventa12013.setLatitud("23.0062861");
                puntoventa12013.setLongitud("-82.2975361111");
                puntoventa12013.setHorario("11:00AM - 7:30PM");
                puntoventa12013.setTelefono("No Tiene");
                puntoventa12013.setNombre("Guásimas");
                daoSession.insertOrReplace(puntoventa12013);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa12014 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa12014.setCodigo("12014");
                puntoventa12014.setMunicipio(municipioarroyo);
                puntoventa12014.setIdmunicipio(municipioarroyo.getIdmunicipio());
                puntoventa12014.setDireccion("Finca Lechuga");
                puntoventa12014.setLatitud("22.98065");
                puntoventa12014.setLongitud("-82.266075");
                puntoventa12014.setHorario("11:00AM - 7:30PM");
                puntoventa12014.setTelefono("No Tiene");
                puntoventa12014.setNombre("Finca Lechuga");
                daoSession.insertOrReplace(puntoventa12014);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa12015 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa12015.setCodigo("12015");
                puntoventa12015.setMunicipio(municipioarroyo);
                puntoventa12015.setIdmunicipio(municipioarroyo.getIdmunicipio());
                puntoventa12015.setDireccion("Seccion H");
                puntoventa12015.setLatitud("22.9714556");
                puntoventa12015.setLongitud("-82.3070247222");
                puntoventa12015.setHorario("11:00AM - 7:30PM");
                puntoventa12015.setTelefono("No Tiene");
                puntoventa12015.setNombre("Seccion H");
                daoSession.insertOrReplace(puntoventa12015);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa12017 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa12017.setCodigo("12017");
                puntoventa12017.setMunicipio(municipioarroyo);
                puntoventa12017.setIdmunicipio(municipioarroyo.getIdmunicipio());
                puntoventa12017.setDireccion("");
                puntoventa12017.setLatitud("22.9758917");
                puntoventa12017.setLongitud("-82.291033333");
                puntoventa12017.setHorario("11:00AM - 7:30PM");
                puntoventa12017.setTelefono("No Tiene");
                puntoventa12017.setNombre("Managua 1");
                daoSession.insertOrReplace(puntoventa12017);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa12018 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa12018.setCodigo("12018");
                puntoventa12018.setMunicipio(municipioarroyo);
                puntoventa12018.setIdmunicipio(municipioarroyo.getIdmunicipio());
                puntoventa12018.setDireccion("1ra / Paseo y Final ");
                puntoventa12018.setLatitud("22.9733611");
                puntoventa12018.setLongitud("-82.291291666");
                puntoventa12018.setHorario("11:00AM - 7:30PM");
                puntoventa12018.setTelefono("No Tiene");
                puntoventa12018.setNombre("Managua 2");
                daoSession.insertOrReplace(puntoventa12018);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa12019 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa12019.setCodigo("12019");
                puntoventa12019.setMunicipio(municipioarroyo);
                puntoventa12019.setIdmunicipio(municipioarroyo.getIdmunicipio());
                puntoventa12019.setDireccion("6ta esq. 2da Mantilla");
                puntoventa12019.setLatitud("23.0722861");
                puntoventa12019.setLongitud("-82.3370416666666");
                puntoventa12019.setHorario("11:00AM - 7:30PM");
                puntoventa12019.setTelefono("No Tiene");
                puntoventa12019.setNombre("6ta y 2da");
                daoSession.insertOrReplace(puntoventa12019);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa12020 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa12020.setCodigo("12020");
                puntoventa12020.setMunicipio(municipioarroyo);
                puntoventa12020.setIdmunicipio(municipioarroyo.getIdmunicipio());
                puntoventa12020.setDireccion("Mantilla");
                puntoventa12020.setLatitud("23.07825");
                puntoventa12020.setLongitud("-82.3333222222222");
                puntoventa12020.setHorario("11:00AM - 7:30PM");
                puntoventa12020.setTelefono("No Tiene");
                puntoventa12020.setNombre("La Solita");
                daoSession.insertOrReplace(puntoventa12020);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa12021 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa12021.setCodigo("12021");
                puntoventa12021.setMunicipio(municipioarroyo);
                puntoventa12021.setIdmunicipio(municipioarroyo.getIdmunicipio());
                puntoventa12021.setDireccion("Las Lajas");
                puntoventa12021.setLatitud("23.0695833");
                puntoventa12021.setLongitud("-82.3248722222222");
                puntoventa12021.setHorario("11:00AM - 7:30PM");
                puntoventa12021.setTelefono("No Tiene");
                puntoventa12021.setNombre("Las Lajas");
                daoSession.insertOrReplace(puntoventa12021);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa12022 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa12022.setCodigo("12022");
                puntoventa12022.setMunicipio(municipioarroyo);
                puntoventa12022.setIdmunicipio(municipioarroyo.getIdmunicipio());
                puntoventa12022.setDireccion("9na y 4ta");
                puntoventa12022.setLatitud("23.0432417");
                puntoventa12022.setLongitud("-82.3228722222222");
                puntoventa12022.setHorario("11:00AM - 7:30PM");
                puntoventa12022.setTelefono("No Tiene");
                puntoventa12022.setNombre("9na y 4ta");
                daoSession.insertOrReplace(puntoventa12022);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa12023 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa12023.setCodigo("12023");
                puntoventa12023.setMunicipio(municipioarroyo);
                puntoventa12023.setIdmunicipio(municipioarroyo.getIdmunicipio());
                puntoventa12023.setDireccion("Luis F. Almeida y Marti");
                puntoventa12023.setLatitud("23.0499917");
                puntoventa12023.setLongitud("-82.3295694444444");
                puntoventa12023.setHorario("11:00AM - 7:30PM");
                puntoventa12018.setTelefono("No Tiene");
                puntoventa12018.setNombre("Luis F. Almeida y Marti");
                daoSession.insertOrReplace(puntoventa12018);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa12025 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa12025.setCodigo("12025");
                puntoventa12025.setMunicipio(municipioarroyo);
                puntoventa12025.setIdmunicipio(municipioarroyo.getIdmunicipio());
                puntoventa12025.setDireccion("Finca Marrero");
                puntoventa12025.setLatitud("22.95545");
                puntoventa12025.setLongitud("-82.2969472222222");
                puntoventa12025.setHorario("11:00AM - 7:30PM");
                puntoventa12025.setTelefono("No Tiene");
                puntoventa12025.setNombre("Marrero");
                daoSession.insertOrReplace(puntoventa12025);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa12027 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa12027.setCodigo("12027");
                puntoventa12027.setMunicipio(municipioarroyo);
                puntoventa12027.setIdmunicipio(municipioarroyo.getIdmunicipio());
                puntoventa12027.setDireccion("Remedios / Lourdes y Alegria Vibora Park");
                puntoventa12027.setLatitud("23.0767278");
                puntoventa12027.setLongitud("-82.3532277777777");
                puntoventa12027.setHorario("11:00AM - 7:30PM");
                puntoventa12027.setTelefono("No Tiene");
                puntoventa12027.setNombre("Remedios");
                daoSession.insertOrReplace(puntoventa12027);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa12030 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa12030.setCodigo("12030");
                puntoventa12030.setMunicipio(municipioarroyo);
                puntoventa12030.setIdmunicipio(municipioarroyo.getIdmunicipio());
                puntoventa12030.setDireccion("Alfredo López / Gutembert y B ");
                puntoventa12030.setLatitud("23.0485");
                puntoventa12030.setLongitud("-82.3648944444444");
                puntoventa12030.setHorario("11:00AM - 7:30PM");
                puntoventa12030.setTelefono("No Tiene");
                puntoventa12030.setNombre("Alfredo López");
                daoSession.insertOrReplace(puntoventa12030);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa12031 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa12031.setCodigo("12031");
                puntoventa12031.setMunicipio(municipioarroyo);
                puntoventa12031.setIdmunicipio(municipioarroyo.getIdmunicipio());
                puntoventa12031.setDireccion("F / 3ra y Guinera. Guinera");
                puntoventa12031.setLatitud("23.048375");
                puntoventa12031.setLongitud("-82.3579666666666");
                puntoventa12031.setHorario("11:00AM - 7:30PM");
                puntoventa12031.setTelefono("No Tiene");
                puntoventa12031.setNombre("F y 3ra");
                daoSession.insertOrReplace(puntoventa12031);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa12032 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa12032.setCodigo("12032");
                puntoventa12032.setMunicipio(municipioarroyo);
                puntoventa12032.setIdmunicipio(municipioarroyo.getIdmunicipio());
                puntoventa12032.setDireccion("Meireles / Rosembell y San jose Guinera");
                puntoventa12032.setLatitud("23.0391667");
                puntoventa12032.setLongitud("-82.3661555555555");
                puntoventa12032.setHorario("11:00AM - 7:30PM");
                puntoventa12032.setTelefono("No Tiene");
                puntoventa12032.setNombre("Ponce");
                daoSession.insertOrReplace(puntoventa12032);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa12033 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa12033.setCodigo("12033");
                puntoventa12033.setMunicipio(municipioarroyo);
                puntoventa12033.setIdmunicipio(municipioarroyo.getIdmunicipio());
                puntoventa12033.setDireccion("Callejon del Muerto y Guinera");
                puntoventa12033.setLatitud("23.0469694");
                puntoventa12033.setLongitud("-82.3517888888888");
                puntoventa12033.setHorario("11:00AM - 7:30PM");
                puntoventa12033.setTelefono("No Tiene");
                puntoventa12033.setNombre("Callejon del Muerto");
                daoSession.insertOrReplace(puntoventa12033);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa12035 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa12035.setCodigo("12035");
                puntoventa12035.setMunicipio(municipioarroyo);
                puntoventa12035.setIdmunicipio(municipioarroyo.getIdmunicipio());
                puntoventa12035.setDireccion("Anita e/ A y B");
                puntoventa12035.setLatitud("23.0772194");
                puntoventa12035.setLongitud("-82.3763999999999");
                puntoventa12035.setHorario("11:00AM - 7:30PM");
                puntoventa12035.setTelefono("No Tiene");
                puntoventa12035.setNombre("Anita e/ A y B");
                daoSession.insertOrReplace(puntoventa12035);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa12038 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa12038.setCodigo("12038");
                puntoventa12038.setMunicipio(municipioarroyo);
                puntoventa12038.setIdmunicipio(municipioarroyo.getIdmunicipio());
                puntoventa12038.setDireccion("Mayía Rodríguez");
                puntoventa12038.setLatitud("23.0717611");
                puntoventa12038.setLongitud("-82.3743833333333");
                puntoventa12038.setHorario("11:00AM - 7:30PM");
                puntoventa12038.setTelefono("No Tiene");
                puntoventa12038.setNombre("Mayía Rodríguez");
                daoSession.insertOrReplace(puntoventa12038);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa12039 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa12039.setCodigo("12039");
                puntoventa12039.setMunicipio(municipioarroyo);
                puntoventa12039.setIdmunicipio(municipioarroyo.getIdmunicipio());
                puntoventa12039.setDireccion("Constancia / Pasaje Herrera y Paz Callejas");
                puntoventa12039.setLatitud("23.0662");
                puntoventa12039.setLongitud("-82.3521444444444");
                puntoventa12039.setHorario("11:00AM - 7:30PM");
                puntoventa12039.setTelefono("No Tiene");
                puntoventa12039.setNombre("Pasaje Herrera");
                daoSession.insertOrReplace(puntoventa12039);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa12040 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa12040.setCodigo("12040");
                puntoventa12040.setMunicipio(municipioarroyo);
                puntoventa12040.setIdmunicipio(municipioarroyo.getIdmunicipio());
                puntoventa12040.setDireccion("Hatuey / Guasimal y Nueva Gerona Parraga");
                puntoventa12040.setLatitud("23.0602493359115");
                puntoventa12040.setLongitud("-82.3528382552738");
                puntoventa12040.setHorario("11:00AM - 7:30PM");
                puntoventa12040.setTelefono("No Tiene");
                puntoventa12040.setNombre("Hatuey y Guasimal");
                daoSession.insertOrReplace(puntoventa12040);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa12041 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa12041.setCodigo("12041");
                puntoventa12041.setMunicipio(municipioarroyo);
                puntoventa12041.setIdmunicipio(municipioarroyo.getIdmunicipio());
                puntoventa12041.setDireccion("Maria Luisa y Enrique");
                puntoventa12041.setLatitud("23.0565278");
                puntoventa12041.setLongitud("-82.3499027777777");
                puntoventa12041.setHorario("11:00AM - 7:30PM");
                puntoventa12041.setTelefono("No Tiene");
                puntoventa12041.setNombre("Maria Luisa y Enrique");
                daoSession.insertOrReplace(puntoventa12041);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa12042 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa12042.setCodigo("12042");
                puntoventa12042.setMunicipio(municipioarroyo);
                puntoventa12042.setIdmunicipio(municipioarroyo.getIdmunicipio());
                puntoventa12042.setDireccion("Matanzas y Camilo Sierra");
                puntoventa12042.setLatitud("23.0615833");
                puntoventa12042.setLongitud("-82.3622");
                puntoventa12042.setHorario("11:00AM - 7:30PM");
                puntoventa12042.setTelefono("No Tiene");
                puntoventa12042.setNombre("Matanzas y Camilo Sierra");
                daoSession.insertOrReplace(puntoventa12042);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa12043 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa12043.setCodigo("12043");
                puntoventa12043.setMunicipio(municipioarroyo);
                puntoventa12043.setIdmunicipio(municipioarroyo.getIdmunicipio());
                puntoventa12043.setDireccion("Esperanza esq. Franklin Callejas");
                puntoventa12043.setLatitud("23.0671972");
                puntoventa12043.setLongitud("-82.3593722222222");
                puntoventa12043.setHorario("11:00AM - 7:30PM");
                puntoventa12043.setTelefono("No Tiene");
                puntoventa12043.setNombre("Esperanza Y Franklin Callejas");
                daoSession.insertOrReplace(puntoventa12043);

                //PUNTOS DE VENTA SAN MIGUEL (OK)
                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa14001 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa14001.setCodigo("14001");
                puntoventa14001.setMunicipio(municipiosanmiguel);
                puntoventa14001.setIdmunicipio(municipiosanmiguel.getIdmunicipio());
                puntoventa14001.setDireccion("3ra / 2da y A, Veracruz");
                puntoventa14001.setLatitud("23.0758194");
                puntoventa14001.setLongitud("-82.31879444");
                puntoventa14001.setHorario("11:00AM - 7:30PM");
                puntoventa14001.setTelefono("No Tiene");
                puntoventa14001.setNombre("Veracruz");
                daoSession.insertOrReplace(puntoventa14001);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa14002 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa14002.setCodigo("14002");
                puntoventa14002.setMunicipio(municipiosanmiguel);
                puntoventa14002.setIdmunicipio(municipiosanmiguel.getIdmunicipio());
                puntoventa14002.setDireccion("Cda S'Miguel y Hnos Ameijeiras, Diezmero");
                puntoventa14002.setLatitud("23.0798917");
                puntoventa14002.setLongitud("-82.31012222");
                puntoventa14002.setHorario("11:00AM - 7:30PM");
                puntoventa14002.setTelefono("No Tiene");
                puntoventa14002.setNombre("Diezmero");
                daoSession.insertOrReplace(puntoventa14002);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa14003 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa14003.setCodigo("14003");
                puntoventa14003.setMunicipio(municipiosanmiguel);
                puntoventa14003.setIdmunicipio(municipiosanmiguel.getIdmunicipio());
                puntoventa14003.setDireccion("Circunvalacion /1ro. Mayo y 24 de Febrero, B. Obrero");
                puntoventa14003.setLatitud("23.1052333");
                puntoventa14003.setLongitud("-82.3315888");
                puntoventa14003.setHorario("11:00AM - 7:30PM");
                puntoventa14003.setTelefono("No Tiene");
                puntoventa14003.setNombre("Barrio Obrero");
                daoSession.insertOrReplace(puntoventa14003);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa14004 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa14004.setCodigo("14004");
                puntoventa14004.setMunicipio(municipiosanmiguel);
                puntoventa14004.setIdmunicipio(municipiosanmiguel.getIdmunicipio());
                puntoventa14004.setDireccion("2da / A y B, San Juan de los Pinos");
                puntoventa14004.setLatitud("23.0925556");
                puntoventa14004.setLongitud("-82.313697222");
                puntoventa14004.setHorario("11:00AM - 7:30PM");
                puntoventa14004.setTelefono("No Tiene");
                puntoventa14004.setNombre("San Juan de los Pinos");
                daoSession.insertOrReplace(puntoventa14004);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa14005 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa14005.setCodigo("14005");
                puntoventa14005.setMunicipio(municipiosanmiguel);
                puntoventa14005.setIdmunicipio(municipiosanmiguel.getIdmunicipio());
                puntoventa14005.setDireccion("San Ramón y R. Flores, Jacomino");
                puntoventa14005.setLatitud("23.0965361");
                puntoventa14005.setLongitud("-82.3306166666");
                puntoventa14005.setHorario("11:00AM - 7:30PM");
                puntoventa14005.setTelefono("No Tiene");
                puntoventa14005.setNombre("Jacomino");
                daoSession.insertOrReplace(puntoventa14005);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa14006 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa14006.setCodigo("14006");
                puntoventa14006.setMunicipio(municipiosanmiguel);
                puntoventa14006.setIdmunicipio(municipiosanmiguel.getIdmunicipio());
                puntoventa14006.setDireccion("2da / G y H, Monterrey");
                puntoventa14006.setLatitud("23.0974972");
                puntoventa14006.setLongitud("-82.310291666");
                puntoventa14006.setHorario("11:00AM - 7:30PM");
                puntoventa14006.setTelefono("No Tiene");
                puntoventa14006.setNombre("Monterrey");
                daoSession.insertOrReplace(puntoventa14006);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa14007 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa14007.setCodigo("14007");
                puntoventa14007.setMunicipio(municipiosanmiguel);
                puntoventa14007.setIdmunicipio(municipiosanmiguel.getIdmunicipio());
                puntoventa14007.setDireccion("3ra y Pepe Prieto");
                puntoventa14007.setLatitud("23.1004444");
                puntoventa14007.setLongitud("-82.338483333");
                puntoventa14007.setHorario("11:00AM - 7:30PM");
                puntoventa14007.setTelefono("No Tiene");
                puntoventa14007.setNombre("Rosalía");
                daoSession.insertOrReplace(puntoventa14007);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa14008 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa14008.setCodigo("14008");
                puntoventa14008.setMunicipio(municipiosanmiguel);
                puntoventa14008.setIdmunicipio(municipiosanmiguel.getIdmunicipio());
                puntoventa14008.setDireccion("2da / F y E, Luyanó Moderno");
                puntoventa14008.setLatitud("23.0953917");
                puntoventa14008.setLongitud("-82.317658333");
                puntoventa14008.setHorario("11:00AM - 7:30PM");
                puntoventa14008.setTelefono("No Tiene");
                puntoventa14008.setNombre("Luyanó Moderno");
                daoSession.insertOrReplace(puntoventa14008);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa14009 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa14009.setCodigo("14009");
                puntoventa14009.setMunicipio(municipiosanmiguel);
                puntoventa14009.setIdmunicipio(municipiosanmiguel.getIdmunicipio());
                puntoventa14009.setDireccion("8va y 5ta, Mirador");
                puntoventa14009.setLatitud("23.0806806");
                puntoventa14009.setLongitud("-82.3006888888");
                puntoventa14009.setHorario("11:00AM - 7:30PM");
                puntoventa14009.setTelefono("No Tiene");
                puntoventa14009.setNombre("Mirador");
                daoSession.insertOrReplace(puntoventa14009);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa14010 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa14010.setCodigo("14010");
                puntoventa14010.setMunicipio(municipiosanmiguel);
                puntoventa14010.setIdmunicipio(municipiosanmiguel.getIdmunicipio());
                puntoventa14010.setDireccion("Madrid esq. A. Rico, Ciudamar");
                puntoventa14010.setLatitud("23.1004083");
                puntoventa14010.setLongitud("-82.3277833333");
                puntoventa14010.setHorario("11:00AM - 7:30PM");
                puntoventa14010.setTelefono("No Tiene");
                puntoventa14010.setNombre("Ciudamar");
                daoSession.insertOrReplace(puntoventa14010);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa14011 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa14011.setCodigo("14011");
                puntoventa14011.setMunicipio(municipiosanmiguel);
                puntoventa14011.setIdmunicipio(municipiosanmiguel.getIdmunicipio());
                puntoventa14011.setDireccion("8 Vías y 11, Martín Pérez");
                puntoventa14011.setLatitud("23.1031944");
                puntoventa14011.setLongitud("-82.3259416666666");
                puntoventa14011.setHorario("11:00AM - 7:30PM");
                puntoventa14011.setTelefono("No Tiene");
                puntoventa14011.setNombre("8 Vías");
                daoSession.insertOrReplace(puntoventa14011);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa14012 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa14012.setCodigo("14012");
                puntoventa14012.setMunicipio(municipiosanmiguel);
                puntoventa14012.setIdmunicipio(municipiosanmiguel.getIdmunicipio());
                puntoventa14012.setDireccion("2da e- R. Cárdenas y Álamos, 2da Ampliación");
                puntoventa14012.setLatitud("23.0963222");
                puntoventa14012.setLongitud("-82.3221888888888");
                puntoventa14012.setHorario("11:00AM - 7:30PM");
                puntoventa14012.setTelefono("No Tiene");
                puntoventa14012.setNombre("2da Ampliación");
                daoSession.insertOrReplace(puntoventa14012);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa14013 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa14013.setCodigo("14013");
                puntoventa14013.setMunicipio(municipiosanmiguel);
                puntoventa14013.setIdmunicipio(municipiosanmiguel.getIdmunicipio());
                puntoventa14013.setDireccion("7ma e/ Cda San Miguel y Ave. 1ra, Afán");
                puntoventa14013.setLatitud("23.0932056");
                puntoventa14013.setLongitud("-82.3194833333333");
                puntoventa14013.setHorario("11:00AM - 7:30PM");
                puntoventa14013.setTelefono("No Tiene");
                puntoventa14013.setNombre("Afán");
                daoSession.insertOrReplace(puntoventa14013);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa14014 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa14014.setCodigo("14014");
                puntoventa14014.setMunicipio(municipiosanmiguel);
                puntoventa14014.setIdmunicipio(municipiosanmiguel.getIdmunicipio());
                puntoventa14014.setDireccion("Longa y San Mariano, Rocafort");
                puntoventa14014.setLatitud("23.1010611");
                puntoventa14014.setLongitud("-82.3316694444444");
                puntoventa14014.setHorario("11:00AM - 7:30PM");
                puntoventa14014.setTelefono("No Tiene");
                puntoventa14014.setNombre("Longa");
                daoSession.insertOrReplace(puntoventa14014);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa14015 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa14015.setCodigo("14015");
                puntoventa14015.setMunicipio(municipiosanmiguel);
                puntoventa14015.setIdmunicipio(municipiosanmiguel.getIdmunicipio());
                puntoventa14015.setDireccion("Marapico y Línea, Merceditas");
                puntoventa14015.setLatitud("23.05955");
                puntoventa14015.setLongitud("-82.2960333333333");
                puntoventa14015.setHorario("11:00AM - 7:30PM");
                puntoventa14015.setTelefono("No Tiene");
                puntoventa14015.setNombre("Merceditas");
                daoSession.insertOrReplace(puntoventa14015);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa14016 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa14016.setCodigo("14016");
                puntoventa14016.setMunicipio(municipiosanmiguel);
                puntoventa14016.setIdmunicipio(municipiosanmiguel.getIdmunicipio());
                puntoventa14016.setDireccion("Mayor e/ Suáres y Linea del Ferrocarril, Juanelo");
                puntoventa14016.setLatitud("23.099075");
                puntoventa14016.setLongitud("-82.3435472222222");
                puntoventa14016.setHorario("11:00AM - 7:30PM");
                puntoventa14016.setTelefono("No Tiene");
                puntoventa14016.setNombre("Mayor");
                daoSession.insertOrReplace(puntoventa14016);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa14017 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa14017.setCodigo("14017");
                puntoventa14017.setMunicipio(municipiosanmiguel);
                puntoventa14017.setIdmunicipio(municipiosanmiguel.getIdmunicipio());
                puntoventa14017.setDireccion("Guadalupe e/ B. Ramos y Psje. Cerruda, Juanelo");
                puntoventa14017.setLatitud("23.0942861");
                puntoventa14017.setLongitud("-82.3388027777777");
                puntoventa14017.setHorario("11:00AM - 7:30PM");
                puntoventa14017.setTelefono("No Tiene");
                puntoventa14017.setNombre("Guadalupe");
                daoSession.insertOrReplace(puntoventa14017);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa14018 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa14018.setCodigo("14018");
                puntoventa14018.setMunicipio(municipiosanmiguel);
                puntoventa14018.setIdmunicipio(municipiosanmiguel.getIdmunicipio());
                puntoventa14018.setDireccion("3ra e/ B y C, California");
                puntoventa14018.setLatitud("23.094025");
                puntoventa14018.setLongitud("-82.3359055555555");
                puntoventa14018.setHorario("11:00AM - 7:30PM");
                puntoventa14018.setTelefono("No Tiene");
                puntoventa14018.setNombre("California");
                daoSession.insertOrReplace(puntoventa14018);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa14019 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa14019.setCodigo("14019");
                puntoventa14019.setMunicipio(municipiosanmiguel);
                puntoventa14019.setIdmunicipio(municipiosanmiguel.getIdmunicipio());
                puntoventa14019.setDireccion("Ave. Narcisa esq. 4ta, María Cristina");
                puntoventa14019.setLatitud("23.0935139");
                puntoventa14019.setLongitud("-82.3292611111111");
                puntoventa14019.setHorario("11:00AM - 7:30PM");
                puntoventa14019.setTelefono("No Tiene");
                puntoventa14019.setNombre("María Cristina");
                daoSession.insertOrReplace(puntoventa14019);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa14020 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa14020.setCodigo("14020");
                puntoventa14020.setMunicipio(municipiosanmiguel);
                puntoventa14020.setIdmunicipio(municipiosanmiguel.getIdmunicipio());
                puntoventa14020.setDireccion("Pasaje Troya, Los Ángeles");
                puntoventa14020.setLatitud("23.1046");
                puntoventa14020.setLongitud("-82.3372972222222");
                puntoventa14020.setHorario("11:00AM - 7:30PM");
                puntoventa14020.setTelefono("No Tiene");
                puntoventa14020.setNombre("Los Ángeles");
                daoSession.insertOrReplace(puntoventa14020);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa14021 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa14021.setCodigo("14021");
                puntoventa14021.setMunicipio(municipiosanmiguel);
                puntoventa14021.setIdmunicipio(municipiosanmiguel.getIdmunicipio());
                puntoventa14021.setDireccion("A e/ 2da y 3ra Dolores");
                puntoventa14021.setLatitud("23.082275");
                puntoventa14021.setLongitud("-82.3248916666666");
                puntoventa14021.setHorario("11:00AM - 7:30PM");
                puntoventa14021.setTelefono("No Tiene");
                puntoventa14021.setNombre("Dolores");
                daoSession.insertOrReplace(puntoventa14021);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa14022 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa14022.setCodigo("14022");
                puntoventa14022.setMunicipio(municipiosanmiguel);
                puntoventa14022.setIdmunicipio(municipiosanmiguel.getIdmunicipio());
                puntoventa14022.setDireccion("255 e/ 90 y 86, Cumbre");
                puntoventa14022.setLatitud("23.0817528");
                puntoventa14022.setLongitud("-82.3215222222222");
                puntoventa14022.setHorario("11:00AM - 7:30PM");
                puntoventa14022.setTelefono("No Tiene");
                puntoventa14022.setNombre("Cumbre");
                daoSession.insertOrReplace(puntoventa14022);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa14023 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa14023.setCodigo("14023");
                puntoventa14023.setMunicipio(municipiosanmiguel);
                puntoventa14023.setIdmunicipio(municipiosanmiguel.getIdmunicipio());
                puntoventa14023.setDireccion("Romanech e/ Línea y Sol");
                puntoventa14023.setLatitud("23.0640694");
                puntoventa14023.setLongitud("-82.2989055555555");
                puntoventa14023.setHorario("11:00AM - 7:30PM");
                puntoventa14023.setTelefono("No Tiene");
                puntoventa14023.setNombre("Romanech");
                daoSession.insertOrReplace(puntoventa14023);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa14024 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa14024.setCodigo("14024");
                puntoventa14024.setMunicipio(municipiosanmiguel);
                puntoventa14024.setIdmunicipio(municipiosanmiguel.getIdmunicipio());
                puntoventa14024.setDireccion("Siboney y C");
                puntoventa14024.setLatitud("23.06335");
                puntoventa14024.setLongitud("-82.2887972222222");
                puntoventa14024.setHorario("11:00AM - 7:30PM");
                puntoventa14024.setTelefono("No Tiene");
                puntoventa14024.setNombre("Siboney");
                daoSession.insertOrReplace(puntoventa14024);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa14025 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa14025.setCodigo("14025");
                puntoventa14025.setMunicipio(municipiosanmiguel);
                puntoventa14025.setIdmunicipio(municipiosanmiguel.getIdmunicipio());
                puntoventa14025.setDireccion("Caraballo y Reynaldo Pérez, S´Fco. Paula");
                puntoventa14025.setLatitud("23.0652083333333");
                puntoventa14025.setLongitud("-82.2941916666667");
                puntoventa14025.setHorario("11:00AM - 7:30PM");
                puntoventa14025.setTelefono("No Tiene");
                puntoventa14025.setNombre("Caraballo");
                daoSession.insertOrReplace(puntoventa14025);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa14026 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa14026.setCodigo("14026");
                puntoventa14026.setMunicipio(municipiosanmiguel);
                puntoventa14026.setIdmunicipio(municipiosanmiguel.getIdmunicipio());
                puntoventa14026.setDireccion("Villalobos y Czda. San Miguel, Alturas de SMP");
                puntoventa14026.setLatitud("23.0903333333333");
                puntoventa14026.setLongitud("-82.3081472222222");
                puntoventa14026.setHorario("11:00AM - 7:30PM");
                puntoventa14026.setTelefono("No Tiene");
                puntoventa14026.setNombre("Villalobos");
                daoSession.insertOrReplace(puntoventa14026);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa14027 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa14027.setCodigo("14027");
                puntoventa14027.setMunicipio(municipiosanmiguel);
                puntoventa14027.setIdmunicipio(municipiosanmiguel.getIdmunicipio());
                puntoventa14027.setDireccion("D y Delicias, San Francisco, Las Piedras");
                puntoventa14027.setLatitud("23.0710055555556");
                puntoventa14027.setLongitud("-82.2985472222222");
                puntoventa14027.setHorario("11:00AM - 7:30PM");
                puntoventa14027.setTelefono("No Tiene");
                puntoventa14027.setNombre("Las Piedras");
                daoSession.insertOrReplace(puntoventa14027);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa14028 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa14028.setCodigo("14028");
                puntoventa14028.setMunicipio(municipiosanmiguel);
                puntoventa14028.setIdmunicipio(municipiosanmiguel.getIdmunicipio());
                puntoventa14028.setDireccion("2da / 1ro Mayo y Guarina, B. Obrero");
                puntoventa14028.setLatitud("23.1074527777778");
                puntoventa14028.setLongitud("-82.3317027777778");
                puntoventa14028.setHorario("11:00AM - 7:30PM");
                puntoventa14028.setTelefono("No Tiene");
                puntoventa14028.setNombre("2da y Guarina");
                daoSession.insertOrReplace(puntoventa14028);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa14029 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa14029.setCodigo("14029");
                puntoventa14029.setMunicipio(municipiosanmiguel);
                puntoventa14029.setIdmunicipio(municipiosanmiguel.getIdmunicipio());
                puntoventa14029.setDireccion("B e/ 3ra y 4ta, Reboledo, San Fco Paula");
                puntoventa14029.setLatitud("23.06555");
                puntoventa14029.setLongitud("-82.2918416666667");
                puntoventa14029.setHorario("11:00AM - 7:30PM");
                puntoventa14029.setTelefono("No Tiene");
                puntoventa14029.setNombre("Reboledo");
                daoSession.insertOrReplace(puntoventa14029);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa14030 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa14030.setCodigo("14030");
                puntoventa14030.setMunicipio(municipiosanmiguel);
                puntoventa14030.setIdmunicipio(municipiosanmiguel.getIdmunicipio());
                puntoventa14030.setDireccion("9na y 6ta, San Matías");
                puntoventa14030.setLatitud("23.0751138888889");
                puntoventa14030.setLongitud("-82.3237111111111");
                puntoventa14030.setHorario("11:00AM - 7:30PM");
                puntoventa14030.setTelefono("No Tiene");
                puntoventa14030.setNombre("San Matías");
                daoSession.insertOrReplace(puntoventa14030);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa14031 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa14031.setCodigo("14031");
                puntoventa14031.setMunicipio(municipiosanmiguel);
                puntoventa14031.setIdmunicipio(municipiosanmiguel.getIdmunicipio());
                puntoventa14031.setDireccion("Vivian y Blanco, Las Granjas");
                puntoventa14031.setLatitud("23.0896833333333");
                puntoventa14031.setLongitud("-82.3003527777778");
                puntoventa14031.setHorario("11:00AM - 7:30PM");
                puntoventa14031.setTelefono("No Tiene");
                puntoventa14031.setNombre("Las Granjas");
                daoSession.insertOrReplace(puntoventa14031);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa14032 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa14032.setCodigo("14032");
                puntoventa14032.setMunicipio(municipiosanmiguel);
                puntoventa14032.setIdmunicipio(municipiosanmiguel.getIdmunicipio());
                puntoventa14032.setDireccion("Carretera Sta. María del Rosario, Cambute");
                puntoventa14032.setLatitud("23.0743805555556");
                puntoventa14032.setLongitud("-82.2775472222222");
                puntoventa14032.setHorario("11:00AM - 7:30PM");
                puntoventa14032.setTelefono("No Tiene");
                puntoventa14032.setNombre("Cambute");
                daoSession.insertOrReplace(puntoventa14032);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa14033 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa14033.setCodigo("14033");
                puntoventa14033.setMunicipio(municipiosanmiguel);
                puntoventa14033.setIdmunicipio(municipiosanmiguel.getIdmunicipio());
                puntoventa14033.setDireccion("99 e/ 108 y 110, Rosita");
                puntoventa14033.setLatitud("23.07165");
                puntoventa14033.setLongitud("-82.3070694444444");
                puntoventa14033.setHorario("11:00AM - 7:30PM");
                puntoventa14033.setTelefono("No Tiene");
                puntoventa14033.setNombre("La Rosita");
                daoSession.insertOrReplace(puntoventa14033);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa14036 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa14036.setCodigo("14036");
                puntoventa14036.setMunicipio(municipiosanmiguel);
                puntoventa14036.setIdmunicipio(municipiosanmiguel.getIdmunicipio());
                puntoventa14036.setDireccion("Cda S'Miguel esq. José Martí, Diezmero");
                puntoventa14036.setLatitud("23.0771111111111");
                puntoventa14036.setLongitud("-82.3131944444444");
                puntoventa14036.setHorario("11:00AM - 7:30PM");
                puntoventa14036.setTelefono("No Tiene");
                puntoventa14036.setNombre("Diezmero 2");
                daoSession.insertOrReplace(puntoventa14036);


                //PUNTOS DE VENTA 10 DE OCTUBRE (OK)
                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa16001 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa16001.setCodigo("16001");
                puntoventa16001.setMunicipio(municipiodiezdeoctubre);
                puntoventa16001.setIdmunicipio(municipiodiezdeoctubre.getIdmunicipio());
                puntoventa16001.setDireccion("Gertrudis y Destrampes Sevillano");
                puntoventa16001.setLatitud("23.083925");
                puntoventa16001.setLongitud("-82.373630555");
                puntoventa16001.setHorario("11:00AM - 7:30PM");
                puntoventa16001.setTelefono("No Tiene");
                puntoventa16001.setNombre("Sevillano 1");
                daoSession.insertOrReplace(puntoventa16001);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa16002 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa16002.setCodigo("16002");
                puntoventa16002.setMunicipio(municipiodiezdeoctubre);
                puntoventa16002.setIdmunicipio(municipiodiezdeoctubre.getIdmunicipio());
                puntoventa16002.setDireccion("Freide Andrade y Goicuria Sevillano");
                puntoventa16002.setLatitud("23.087041");
                puntoventa16002.setLongitud("-82.37676111");
                puntoventa16002.setHorario("11:00AM - 7:30PM");
                puntoventa16002.setTelefono("No Tiene");
                puntoventa16002.setNombre("Sevillano 2");
                daoSession.insertOrReplace(puntoventa16002);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa16003 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa16003.setCodigo("16003");
                puntoventa16003.setMunicipio(municipiodiezdeoctubre);
                puntoventa16003.setIdmunicipio(municipiodiezdeoctubre.getIdmunicipio());
                puntoventa16003.setDireccion("A e/ 16 y 17 Lawton");
                puntoventa16003.setLatitud("23.091130555");
                puntoventa16003.setLongitud("-82.3448");
                puntoventa16003.setHorario("11:00AM - 7:30PM");
                puntoventa16003.setTelefono("No Tiene");
                puntoventa16003.setNombre("Lawton 1");
                daoSession.insertOrReplace(puntoventa16003);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa16004 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa16004.setCodigo("16004");
                puntoventa16004.setMunicipio(municipiodiezdeoctubre);
                puntoventa16004.setIdmunicipio(municipiodiezdeoctubre.getIdmunicipio());
                puntoventa16004.setDireccion("Gertrudis y Finlay. Sevillano");
                puntoventa16004.setLatitud("23.0822861111");
                puntoventa16004.setLongitud("-82.37892222");
                puntoventa16004.setHorario("11:00AM - 7:30PM");
                puntoventa16004.setTelefono("No Tiene");
                puntoventa16004.setNombre("Sevillano 3");
                daoSession.insertOrReplace(puntoventa16004);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa16005 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa16005.setCodigo("16005");
                puntoventa16005.setMunicipio(municipiodiezdeoctubre);
                puntoventa16005.setIdmunicipio(municipiodiezdeoctubre.getIdmunicipio());
                puntoventa16005.setDireccion("15 y Eduardo Lores Lawton");
                puntoventa16005.setLatitud("23.0862833");
                puntoventa16005.setLongitud("-82.34983888");
                puntoventa16005.setHorario("11:00AM - 7:30PM");
                puntoventa16005.setTelefono("No Tiene");
                puntoventa16005.setNombre("Lawton 2");
                daoSession.insertOrReplace(puntoventa16005);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa16006 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa16006.setCodigo("16006");
                puntoventa16006.setMunicipio(municipiodiezdeoctubre);
                puntoventa16006.setIdmunicipio(municipiodiezdeoctubre.getIdmunicipio());
                puntoventa16006.setDireccion("23 y Comezañas Lawton");
                puntoventa16006.setLatitud("23.08283333");
                puntoventa16006.setLongitud("-82.34213333");
                puntoventa16006.setHorario("11:00AM - 7:30PM");
                puntoventa16006.setTelefono("No Tiene");
                puntoventa16006.setNombre("Lawton 3");
                daoSession.insertOrReplace(puntoventa16006);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa16007 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa16007.setCodigo("16007");
                puntoventa16007.setMunicipio(municipiodiezdeoctubre);
                puntoventa16007.setIdmunicipio(municipiodiezdeoctubre.getIdmunicipio());
                puntoventa16007.setDireccion("9na e/ D y E Lawton");
                puntoventa16007.setLatitud("23.098094444");
                puntoventa16007.setLongitud("-82.349797222");
                puntoventa16007.setHorario("11:00AM - 7:30PM");
                puntoventa16007.setTelefono("No Tiene");
                puntoventa16007.setNombre("Lawton 4");
                daoSession.insertOrReplace(puntoventa16007);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa16008 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa16008.setCodigo("16008");
                puntoventa16008.setMunicipio(municipiodiezdeoctubre);
                puntoventa16008.setIdmunicipio(municipiodiezdeoctubre.getIdmunicipio());
                puntoventa16008.setDireccion("9na y Lagueruela Lawton");
                puntoventa16008.setLatitud("23.0892027777");
                puntoventa16008.setLongitud("-82.355455555");
                puntoventa16008.setHorario("11:00AM - 7:30PM");
                puntoventa16008.setTelefono("No Tiene");
                puntoventa16008.setNombre("Lawton 5");
                daoSession.insertOrReplace(puntoventa16008);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa16009 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa16009.setCodigo("16009");
                puntoventa16009.setMunicipio(municipiodiezdeoctubre);
                puntoventa16009.setIdmunicipio(municipiodiezdeoctubre.getIdmunicipio());
                puntoventa16009.setDireccion("Aguilera y Lugareño Lawton");
                puntoventa16009.setLatitud("23.1028916666");
                puntoventa16009.setLongitud("-82.3495888888");
                puntoventa16009.setHorario("11:00AM - 7:30PM");
                puntoventa16009.setTelefono("No Tiene");
                puntoventa16009.setNombre("Lawton 6");
                daoSession.insertOrReplace(puntoventa16009);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa16010 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa16010.setCodigo("16010");
                puntoventa16010.setMunicipio(municipiodiezdeoctubre);
                puntoventa16010.setIdmunicipio(municipiodiezdeoctubre.getIdmunicipio());
                puntoventa16010.setDireccion("B y Martinez Lawton");
                puntoventa16010.setLatitud("23.097527777");
                puntoventa16010.setLongitud("-82.353916666");
                puntoventa16010.setHorario("11:00AM - 7:30PM");
                puntoventa16010.setTelefono("No Tiene");
                puntoventa16010.setNombre("Lawton 7");
                daoSession.insertOrReplace(puntoventa16010);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa16011 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa16011.setCodigo("16011");
                puntoventa16011.setMunicipio(municipiodiezdeoctubre);
                puntoventa16011.setIdmunicipio(municipiodiezdeoctubre.getIdmunicipio());
                puntoventa16011.setDireccion("11 e/ A y B Lawton");
                puntoventa16011.setLatitud("23.09423333");
                puntoventa16011.setLongitud("-82.350213888");
                puntoventa16011.setHorario("11:00AM - 7:30PM");
                puntoventa16011.setTelefono("No Tiene");
                puntoventa16011.setNombre("Lawton 8");
                daoSession.insertOrReplace(puntoventa16011);

                //PUNTOS DE VENTA CERRO (OK)

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa17004 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa17004.setCodigo("17004");
                puntoventa17004.setMunicipio(municipiocerro);
                puntoventa17004.setIdmunicipio(municipiocerro.getIdmunicipio());
                puntoventa17004.setDireccion("5ta e/ 3ra y 4ta Casino Deportivo");
                puntoventa17004.setLatitud("23.0784278");
                puntoventa17004.setLongitud("-82.383894444");
                puntoventa17004.setHorario("11:00AM - 7:30PM");
                puntoventa17004.setTelefono("No Tiene");
                puntoventa17004.setNombre("Casino 1");
                daoSession.insertOrReplace(puntoventa17004);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa17005 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa17005.setCodigo("17005");
                puntoventa17005.setMunicipio(municipiocerro);
                puntoventa17005.setIdmunicipio(municipiocerro.getIdmunicipio());
                puntoventa17005.setDireccion("Ave Este e/ Av Norte Casino Deportivo");
                puntoventa17005.setLatitud("23.0929444");
                puntoventa17005.setLongitud("-82.385588888");
                puntoventa17005.setHorario("11:00AM - 7:30PM");
                puntoventa17005.setTelefono("No Tiene");
                puntoventa17005.setNombre("Casino 2");
                daoSession.insertOrReplace(puntoventa17005);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa17006 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa17006.setCodigo("17006");
                puntoventa17006.setMunicipio(municipiocerro);
                puntoventa17006.setIdmunicipio(municipiocerro.getIdmunicipio());
                puntoventa17006.setDireccion("Ave Sur y 1. Casino Deportivo");
                puntoventa17006.setLatitud("23.0948639");
                puntoventa17006.setLongitud("-82.392544444");
                puntoventa17006.setHorario("11:00AM - 7:30PM");
                puntoventa17006.setTelefono("No Tiene");
                puntoventa17006.setNombre("Casino 3");
                daoSession.insertOrReplace(puntoventa17006);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa17007 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa17007.setCodigo("17007");
                puntoventa17007.setMunicipio(municipiocerro);
                puntoventa17007.setIdmunicipio(municipiocerro.getIdmunicipio());
                puntoventa17007.setDireccion("12 y Canal. Martí");
                puntoventa17007.setLatitud("23.0840944");
                puntoventa17007.setLongitud("-82.390372222");
                puntoventa17007.setHorario("11:00AM - 7:30PM");
                puntoventa17007.setTelefono("No Tiene");
                puntoventa17007.setNombre("Martí 1");
                daoSession.insertOrReplace(puntoventa17007);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa17009 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa17009.setCodigo("17009");
                puntoventa17009.setMunicipio(municipiocerro);
                puntoventa17009.setIdmunicipio(municipiocerro.getIdmunicipio());
                puntoventa17009.setDireccion("Ayestaran y dominguez, Cerro");
                puntoventa17009.setLatitud("23.1175194");
                puntoventa17009.setLongitud("-82.3844638888");
                puntoventa17009.setHorario("11:00AM - 7:30PM");
                puntoventa17009.setTelefono("No Tiene");
                puntoventa17009.setNombre("Ayestaran y Domínguez");
                daoSession.insertOrReplace(puntoventa17009);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa17011 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa17011.setCodigo("17011");
                puntoventa17011.setMunicipio(municipiocerro);
                puntoventa17011.setIdmunicipio(municipiocerro.getIdmunicipio());
                puntoventa17011.setDireccion("Independencia y Marti, R.Marti Cerro");
                puntoventa17011.setLatitud("23.0878306");
                puntoventa17011.setLongitud("-82.3916916666");
                puntoventa17011.setHorario("11:00AM - 7:30PM");
                puntoventa17011.setTelefono("No Tiene");
                puntoventa17011.setNombre("Martí 2");
                daoSession.insertOrReplace(puntoventa17011);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa17012 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa17012.setCodigo("17012");
                puntoventa17012.setMunicipio(municipiocerro);
                puntoventa17012.setIdmunicipio(municipiocerro.getIdmunicipio());
                puntoventa17012.setDireccion("Carmen y Ayentamiento Las Cañas, Cerro");
                puntoventa17012.setLatitud("23.0784278");
                puntoventa17012.setLongitud("-82.383894444");
                puntoventa17012.setHorario("11:00AM - 7:30PM");
                puntoventa17012.setTelefono("No Tiene");
                puntoventa17012.setNombre("Las Cañas");
                daoSession.insertOrReplace(puntoventa17012);

                //PUNTOS DE VENTA HABANA VIEJA (OK)

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa17014 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa17014.setCodigo("17014");
                puntoventa17014.setMunicipio(municipiohabanavieja);
                puntoventa17014.setIdmunicipio(municipiohabanavieja.getIdmunicipio());
                puntoventa17014.setDireccion("Villanueva y Aspuro. Habana Vieja");
                puntoventa17014.setLatitud("23.1154167");
                puntoventa17014.setLongitud("-82.36189999");
                puntoventa17014.setHorario("11:00AM - 7:30PM");
                puntoventa17014.setTelefono("No Tiene");
                puntoventa17014.setNombre("Villanueva y Aspuro");
                daoSession.insertOrReplace(puntoventa17014);

                //PUNTOS DE VENTA COTORRO (OK)
                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa20001 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa20001.setCodigo("20001");
                puntoventa20001.setMunicipio(municipiocotorro);
                puntoventa20001.setIdmunicipio(municipiocotorro.getIdmunicipio());
                puntoventa20001.setDireccion("99A entre 52 y 54 Loteria");
                puntoventa20001.setLatitud("23.035730555");
                puntoventa20001.setLongitud("-82.250602777");
                puntoventa20001.setHorario("11:00AM - 7:30PM");
                puntoventa20001.setTelefono("No Tiene");
                puntoventa20001.setNombre("Alberro 1");
                daoSession.insertOrReplace(puntoventa20001);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa20002 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa20002.setCodigo("20002");
                puntoventa20002.setMunicipio(municipiocotorro);
                puntoventa20002.setIdmunicipio(municipiocotorro.getIdmunicipio());
                puntoventa20002.setDireccion("101 entre l04 y 106");
                puntoventa20002.setLatitud("23.005797222");
                puntoventa20002.setLongitud("-82.214825");
                puntoventa20002.setHorario("11:00AM - 7:30PM");
                puntoventa20002.setTelefono("No Tiene");
                puntoventa20002.setNombre("Cuatro Caminos");
                daoSession.insertOrReplace(puntoventa20002);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa20003 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa20003.setCodigo("20003");
                puntoventa20003.setMunicipio(municipiocotorro);
                puntoventa20003.setIdmunicipio(municipiocotorro.getIdmunicipio());
                puntoventa20003.setDireccion("20B Esq. 31");
                puntoventa20003.setLatitud("23.0615361111");
                puntoventa20003.setLongitud("-82.258697222");
                puntoventa20003.setHorario("11:00AM - 7:30PM");
                puntoventa20003.setTelefono("No Tiene");
                puntoventa20003.setNombre("Alberro 1");
                daoSession.insertOrReplace(puntoventa20003);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa20004 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa20004.setCodigo("20004");
                puntoventa20004.setMunicipio(municipiocotorro);
                puntoventa20004.setIdmunicipio(municipiocotorro.getIdmunicipio());
                puntoventa20004.setDireccion("22 entre 63 y 65 América");
                puntoventa20004.setLatitud("23.0495055555");
                puntoventa20004.setLongitud("-82.2659805555");
                puntoventa20004.setHorario("11:00AM - 7:30PM");
                puntoventa20004.setTelefono("No Tiene");
                puntoventa20004.setNombre("América");
                daoSession.insertOrReplace(puntoventa20004);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa20005 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa20005.setCodigo("20005");
                puntoventa20005.setMunicipio(municipiocotorro);
                puntoventa20005.setIdmunicipio(municipiocotorro.getIdmunicipio());
                puntoventa20005.setDireccion("S.Pedro entre cuarta y línea San Pedro");
                puntoventa20005.setLatitud("23.054875");
                puntoventa20005.setLongitud("-82.289263888");
                puntoventa20005.setHorario("11:00AM - 7:30PM");
                puntoventa20005.setTelefono("No Tiene");
                puntoventa20005.setNombre("San Pedro");
                daoSession.insertOrReplace(puntoventa20005);


                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa20006 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa20006.setCodigo("20006");
                puntoventa20006.setMunicipio(municipiocotorro);
                puntoventa20006.setIdmunicipio(municipiocotorro.getIdmunicipio());
                puntoventa20006.setDireccion("1ra entre 217 y 218 Cruz Verde");
                puntoventa20006.setLatitud("23.05315555");
                puntoventa20006.setLongitud("-82.277763888");
                puntoventa20006.setHorario("11:00AM - 7:30PM");
                puntoventa20006.setTelefono("No Tiene");
                puntoventa20006.setNombre("Cruz Verde");
                daoSession.insertOrReplace(puntoventa20006);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa20008 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa20008.setCodigo("20008");
                puntoventa20008.setMunicipio(municipiocotorro);
                puntoventa20008.setIdmunicipio(municipiocotorro.getIdmunicipio());
                puntoventa20008.setDireccion("Calle 16 entre Parque y 3ra");
                puntoventa20008.setLatitud("23.0476694444");
                puntoventa20008.setLongitud("-82.27854444");
                puntoventa20008.setHorario("11:00AM - 7:30PM");
                puntoventa20008.setTelefono("No Tiene");
                puntoventa20008.setNombre("Paraíso");
                daoSession.insertOrReplace(puntoventa20008);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa20009 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa20009.setCodigo("20009");
                puntoventa20009.setMunicipio(municipiocotorro);
                puntoventa20009.setIdmunicipio(municipiocotorro.getIdmunicipio());
                puntoventa20009.setDireccion("Calle 32 Esquina l07. Purísima");
                puntoventa20009.setLatitud("23.037625");
                puntoventa20009.setLongitud("-82.263997222");
                puntoventa20009.setHorario("11:00AM - 7:30PM");
                puntoventa20009.setTelefono("No Tiene");
                puntoventa20009.setNombre("Purísima");
                daoSession.insertOrReplace(puntoventa20009);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa20010 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa20010.setCodigo("20010");
                puntoventa20010.setMunicipio(municipiocotorro);
                puntoventa20010.setIdmunicipio(municipiocotorro.getIdmunicipio());
                puntoventa20010.setDireccion("Ave.99 Plazoleta Alberro");
                puntoventa20010.setLatitud("23.0279527777778");
                puntoventa20010.setLongitud("-82.24468333");
                puntoventa20010.setHorario("11:00AM - 7:30PM");
                puntoventa20010.setTelefono("No Tiene");
                puntoventa20010.setNombre("Alberro 2");
                daoSession.insertOrReplace(puntoventa20010);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa20011 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa20011.setCodigo("20011");
                puntoventa20011.setMunicipio(municipiocotorro);
                puntoventa20011.setIdmunicipio(municipiocotorro.getIdmunicipio());
                puntoventa20011.setDireccion("103 y Final Santa Ana");
                puntoventa20011.setLatitud("23.0219");
                puntoventa20011.setLongitud("-82.255672222");
                puntoventa20011.setHorario("11:00AM - 7:30PM");
                puntoventa20011.setTelefono("No Tiene");
                puntoventa20011.setNombre("Santa Ana");
                daoSession.insertOrReplace(puntoventa20011);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa20012 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa20012.setCodigo("20012");
                puntoventa20012.setMunicipio(municipiocotorro);
                puntoventa20012.setIdmunicipio(municipiocotorro.getIdmunicipio());
                puntoventa20012.setDireccion("Calle 16 Esq.61 Dulce Nombre");
                puntoventa20012.setLatitud("23.0535194444444");
                puntoventa20012.setLongitud("-82.2672194444445");
                puntoventa20012.setHorario("11:00AM - 7:30PM");
                puntoventa20012.setTelefono("No Tiene");
                puntoventa20012.setNombre("Vista Alegre");
                daoSession.insertOrReplace(puntoventa20012);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa20013 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa20013.setCodigo("20013");
                puntoventa20013.setMunicipio(municipiocotorro);
                puntoventa20013.setIdmunicipio(municipiocotorro.getIdmunicipio());
                puntoventa20013.setDireccion("Calle 71 Esq. 32 Las Delicias");
                puntoventa20013.setLatitud("23.0438055555556");
                puntoventa20013.setLongitud("-82.2605166666667");
                puntoventa20013.setHorario("11:00AM - 7:30PM");
                puntoventa20013.setTelefono("No Tiene");
                puntoventa20013.setNombre("Delicias");
                daoSession.insertOrReplace(puntoventa20013);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa20014 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa20014.setCodigo("20014");
                puntoventa20014.setMunicipio(municipiocotorro);
                puntoventa20014.setIdmunicipio(municipiocotorro.getIdmunicipio());
                puntoventa20014.setDireccion("Callejon Xiriaco La Portada");
                puntoventa20014.setLatitud("22.9944444444444");
                puntoventa20014.setLongitud("-82.2271555555556");
                puntoventa20014.setHorario("11:00AM - 7:30PM");
                puntoventa20014.setTelefono("No Tiene");
                puntoventa20014.setNombre("La Portada");
                daoSession.insertOrReplace(puntoventa20014);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa20015 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa20015.setCodigo("20015");
                puntoventa20015.setMunicipio(municipiocotorro);
                puntoventa20015.setIdmunicipio(municipiocotorro.getIdmunicipio());
                puntoventa20015.setDireccion("Calle 95 Esq. 46");
                puntoventa20015.setLatitud("23.0402111111111");
                puntoventa20015.setLongitud("-82.2534472222222");
                puntoventa20015.setHorario("11:00AM - 7:30PM");
                puntoventa20015.setTelefono("No Tiene");
                puntoventa20015.setNombre("Lotería");
                daoSession.insertOrReplace(puntoventa20015);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa20016 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa20016.setCodigo("20016");
                puntoventa20016.setMunicipio(municipiocotorro);
                puntoventa20016.setIdmunicipio(municipiocotorro.getIdmunicipio());
                puntoventa20016.setDireccion("Carret.Central e Camino 1 y Camino 2");
                puntoventa20016.setLatitud("22.9863083333333");
                puntoventa20016.setLongitud("-82.2425444444444");
                puntoventa20016.setHorario("11:00AM - 7:30PM");
                puntoventa20016.setTelefono("No Tiene");
                puntoventa20016.setNombre("Santa Amelia");
                daoSession.insertOrReplace(puntoventa20016);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa20017 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa20017.setCodigo("20017");
                puntoventa20017.setMunicipio(municipiocotorro);
                puntoventa20017.setIdmunicipio(municipiocotorro.getIdmunicipio());
                puntoventa20017.setDireccion("Calle 26 entre 61 y 63 Torriente");
                puntoventa20017.setLatitud("23.0496444444444");
                puntoventa20017.setLongitud("-82.2635611111111");
                puntoventa20017.setHorario("11:00AM - 7:30PM");
                puntoventa20017.setTelefono("No Tiene");
                puntoventa20017.setNombre("Torriente");
                daoSession.insertOrReplace(puntoventa20017);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa20018 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa20018.setCodigo("20018");
                puntoventa20018.setMunicipio(municipiocotorro);
                puntoventa20018.setIdmunicipio(municipiocotorro.getIdmunicipio());
                puntoventa20018.setDireccion("Calle 69 entre 16 y 18 Centro Cotorro");
                puntoventa20018.setLatitud("23.0468277777778");
                puntoventa20018.setLongitud("-82.2695805555555");
                puntoventa20018.setHorario("11:00AM - 7:30PM");
                puntoventa20018.setTelefono("No Tiene");
                puntoventa20018.setNombre("Centro Cotorro");
                daoSession.insertOrReplace(puntoventa20018);

                //PUNTOS DE VENTA BOYEROS
                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa19001= new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa19001.setCodigo("19001");
                puntoventa19001.setMunicipio(municipioboyeros);
                puntoventa19001.setIdmunicipio(municipioboyeros.getIdmunicipio());
                puntoventa19001.setDireccion("373 e/178 y 180 Mulgoba");
                puntoventa19001.setLatitud("22.980608333");
                puntoventa19001.setLongitud("-82.379647222");
                puntoventa19001.setHorario("11:00AM - 7:30PM");
                puntoventa19001.setTelefono("No Tiene");
                puntoventa19001.setNombre("Mulgoba 1");
                daoSession.insertOrReplace(puntoventa19001);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa19002= new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa19002.setCodigo("19002");
                puntoventa19002.setMunicipio(municipioboyeros);
                puntoventa19002.setIdmunicipio(municipioboyeros.getIdmunicipio());
                puntoventa19002.setDireccion("Calz.Bejucal y linea Ferrocarril Boyeros");
                puntoventa19002.setLatitud("23.001408333");
                puntoventa19002.setLongitud("-82.38814444");
                puntoventa19002.setHorario("11:00AM - 7:30PM");
                puntoventa19002.setTelefono("No Tiene");
                puntoventa19002.setNombre("Lutgardita 1");
                daoSession.insertOrReplace(puntoventa19002);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa19003= new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa19003.setCodigo("19003");
                puntoventa19003.setMunicipio(municipioboyeros);
                puntoventa19003.setIdmunicipio(municipioboyeros.getIdmunicipio());
                puntoventa19003.setDireccion("240 e/ 247 y Final  ASM");
                puntoventa19003.setLatitud("23.002130555");
                puntoventa19003.setLongitud("-82.410680555");
                puntoventa19003.setHorario("11:00AM - 7:30PM");
                puntoventa19003.setTelefono("No Tiene");
                puntoventa19003.setNombre("A Santa María 1");
                daoSession.insertOrReplace(puntoventa19003);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa19004 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa19004.setCodigo("19004");
                puntoventa19004.setMunicipio(municipioboyeros);
                puntoventa19004.setIdmunicipio(municipioboyeros.getIdmunicipio());
                puntoventa19004.setDireccion("229-A Esq. 206 Fontanar");
                puntoventa19004.setLatitud("23.0251472222");
                puntoventa19004.setLongitud("-82.404725");
                puntoventa19004.setHorario("11:00AM - 7:30PM");
                puntoventa19004.setTelefono("No Tiene");
                puntoventa19004.setNombre("Fontanar 1");
                daoSession.insertOrReplace(puntoventa19004);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa19005 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa19005.setCodigo("19005");
                puntoventa19005.setMunicipio(municipioboyeros);
                puntoventa19005.setIdmunicipio(municipioboyeros.getIdmunicipio());
                puntoventa19005.setDireccion("E e/ 7ma y N Altahabana");
                puntoventa19005.setLatitud("23.059325");
                puntoventa19005.setLongitud("-82.395880555");
                puntoventa19005.setHorario("11:00AM - 7:30PM");
                puntoventa19005.setTelefono("No Tiene");
                puntoventa19005.setNombre("Altahabana 1");
                daoSession.insertOrReplace(puntoventa19005);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa19006 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa19006.setCodigo("19006");
                puntoventa19006.setMunicipio(municipioboyeros);
                puntoventa19006.setIdmunicipio(municipioboyeros.getIdmunicipio());
                puntoventa19006.setDireccion("8 esq. Parque Oeste Embil");
                puntoventa19006.setLatitud("23.07015");
                puntoventa19006.setLongitud("-82.388480555");
                puntoventa19006.setHorario("11:00AM - 7:30PM");
                puntoventa19006.setTelefono("No Tiene");
                puntoventa19006.setNombre("Embil");
                daoSession.insertOrReplace(puntoventa19006);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa19007 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa19007.setCodigo("19007");
                puntoventa19007.setMunicipio(municipioboyeros);
                puntoventa19007.setIdmunicipio(municipioboyeros.getIdmunicipio());
                puntoventa19007.setDireccion("243 e/ 230 y 234 ASM");
                puntoventa19007.setLatitud("23.014525");
                puntoventa19007.setLongitud("-82.411119444");
                puntoventa19007.setHorario("11:00AM - 7:30PM");
                puntoventa19007.setTelefono("No Tiene");
                puntoventa19007.setNombre("A Santa María 2");
                daoSession.insertOrReplace(puntoventa19007);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa19008 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa19008.setCodigo("19008");
                puntoventa19008.setMunicipio(municipioboyeros);
                puntoventa19008.setIdmunicipio(municipioboyeros.getIdmunicipio());
                puntoventa19008.setDireccion("A Esq. 6 Altahabana");
                puntoventa19008.setLatitud("23.066572222");
                puntoventa19008.setLongitud("-82.398797222");
                puntoventa19008.setHorario("11:00AM - 7:30PM");
                puntoventa19008.setTelefono("No Tiene");
                puntoventa19008.setNombre("Altahabana 2");
                daoSession.insertOrReplace(puntoventa19008);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa19009 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa19009.setCodigo("19009");
                puntoventa19009.setMunicipio(municipioboyeros);
                puntoventa19009.setIdmunicipio(municipioboyeros.getIdmunicipio());
                puntoventa19009.setDireccion("409 e/ 188 y 190 Santiago de las Vegas");
                puntoventa19009.setLatitud("22.9691972222");
                puntoventa19009.setLongitud("-82.387538888");
                puntoventa19009.setHorario("11:00AM - 7:30PM");
                puntoventa19009.setTelefono("No Tiene");
                puntoventa19009.setNombre("Santiago de las Vegas");
                daoSession.insertOrReplace(puntoventa19009);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa19010 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa19010.setCodigo("19010");
                puntoventa19010.setMunicipio(municipioboyeros);
                puntoventa19010.setIdmunicipio(municipioboyeros.getIdmunicipio());
                puntoventa19010.setDireccion("5ta y Carlos Nuñez Aldabó (Edif.)");
                puntoventa19010.setLatitud("23.05606666");
                puntoventa19010.setLongitud("-82.3873527777");
                puntoventa19010.setHorario("11:00AM - 7:30PM");
                puntoventa19010.setTelefono("No Tiene");
                puntoventa19010.setNombre("A Santa María 1");
                daoSession.insertOrReplace(puntoventa19010);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa19011 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa19011.setCodigo("19011");
                puntoventa19011.setMunicipio(municipioboyeros);
                puntoventa19011.setIdmunicipio(municipioboyeros.getIdmunicipio());
                puntoventa19011.setDireccion("270 Esq. 283 Wajay");
                puntoventa19011.setLatitud("23.0031722222222");
                puntoventa19011.setLongitud("-82.4273805555556");
                puntoventa19011.setHorario("11:00AM - 7:30PM");
                puntoventa19011.setTelefono("No Tiene");
                puntoventa19011.setNombre("Wajay");
                daoSession.insertOrReplace(puntoventa19011);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa19012 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa19012.setCodigo("19012");
                puntoventa19012.setMunicipio(municipioboyeros);
                puntoventa19012.setIdmunicipio(municipioboyeros.getIdmunicipio());
                puntoventa19012.setDireccion("160 e/ 269 y 271 Río Verde");
                puntoventa19012.setLatitud("23.0138888888889");
                puntoventa19012.setLongitud("-82.3928138888889");
                puntoventa19012.setHorario("11:00AM - 7:30PM");
                puntoventa19012.setTelefono("No Tiene");
                puntoventa19012.setNombre("Río Verde");
                daoSession.insertOrReplace(puntoventa19012);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa19013 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa19013.setCodigo("19013");
                puntoventa19013.setMunicipio(municipioboyeros);
                puntoventa19013.setIdmunicipio(municipioboyeros.getIdmunicipio());
                puntoventa19013.setDireccion("391 esq.180  Villanueva");
                puntoventa19013.setLatitud("22.9748777777778");
                puntoventa19013.setLongitud("-82.382925");
                puntoventa19013.setHorario("11:00AM - 7:30PM");
                puntoventa19013.setTelefono("No Tiene");
                puntoventa19013.setNombre("Villanueva");
                daoSession.insertOrReplace(puntoventa19013);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa19014 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa19014.setCodigo("19014");
                puntoventa19014.setMunicipio(municipioboyeros);
                puntoventa19014.setIdmunicipio(municipioboyeros.getIdmunicipio());
                puntoventa19014.setDireccion("184 e/ 413 y 419  La Aurora");
                puntoventa19014.setLatitud("22.9666027777778");
                puntoventa19014.setLongitud("-82.3841416666667");
                puntoventa19014.setHorario("11:00AM - 7:30PM");
                puntoventa19014.setTelefono("No Tiene");
                puntoventa19014.setNombre("La Aurora");
                daoSession.insertOrReplace(puntoventa19014);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa19015 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa19015.setCodigo("19015");
                puntoventa19015.setMunicipio(municipioboyeros);
                puntoventa19015.setIdmunicipio(municipioboyeros.getIdmunicipio());
                puntoventa19015.setDireccion("Carret.Varona y Linea de Ferrocarril");
                puntoventa19015.setLatitud("23.0546805555556");
                puntoventa19015.setLongitud("-82.22407777777778");
                puntoventa19015.setHorario("11:00AM - 7:30PM");
                puntoventa19015.setTelefono("No Tiene");
                puntoventa19015.setNombre("Capdevila");
                daoSession.insertOrReplace(puntoventa19015);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa19016 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa19016.setCodigo("19016");
                puntoventa19016.setMunicipio(municipioboyeros);
                puntoventa19016.setIdmunicipio(municipioboyeros.getIdmunicipio());
                puntoventa19016.setDireccion("277 Esq. 204 Panamérica");
                puntoventa19016.setLatitud("23.0077972222222");
                puntoventa19016.setLongitud("-82.3986027777778");
                puntoventa19016.setHorario("11:00AM - 7:30PM");
                puntoventa19016.setTelefono("No Tiene");
                puntoventa19016.setNombre("Panamérica");
                daoSession.insertOrReplace(puntoventa19016);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa19017 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa19017.setCodigo("19017");
                puntoventa19017.setMunicipio(municipioboyeros);
                puntoventa19017.setIdmunicipio(municipioboyeros.getIdmunicipio());
                puntoventa19017.setDireccion("1ra Sur e/180 y Final 1 de Mayo");
                puntoventa19017.setLatitud("22.9880277777778");
                puntoventa19017.setLongitud("-82.3839111111111");
                puntoventa19017.setHorario("11:00AM - 7:30PM");
                puntoventa19017.setTelefono("No Tiene");
                puntoventa19017.setNombre("1 de Mayo");
                daoSession.insertOrReplace(puntoventa19017);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa19018 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa19018.setCodigo("19018");
                puntoventa19018.setMunicipio(municipioboyeros);
                puntoventa19018.setIdmunicipio(municipioboyeros.getIdmunicipio());
                puntoventa19018.setDireccion("Paradero y Sta. Rosa Rincón");
                puntoventa19018.setLatitud("22.9512805555556");
                puntoventa19018.setLongitud("-82.4176527777778");
                puntoventa19018.setHorario("11:00AM - 7:30PM");
                puntoventa19018.setTelefono("No Tiene");
                puntoventa19018.setNombre("Rincón");
                daoSession.insertOrReplace(puntoventa19018);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa19019 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa19019.setCodigo("19019");
                puntoventa19019.setMunicipio(municipioboyeros);
                puntoventa19019.setIdmunicipio(municipioboyeros.getIdmunicipio());
                puntoventa19019.setDireccion("22 e/ 107 y Clza Managua SM");
                puntoventa19019.setLatitud("22.9679944444444");
                puntoventa19019.setLongitud("-82.3543166666667");
                puntoventa19019.setHorario("11:00AM - 7:30PM");
                puntoventa19019.setTelefono("No Tiene");
                puntoventa19019.setNombre("Sierra Maestra 1");
                daoSession.insertOrReplace(puntoventa19019);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa19020 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa19020.setCodigo("19020");
                puntoventa19020.setMunicipio(municipioboyeros);
                puntoventa19020.setIdmunicipio(municipioboyeros.getIdmunicipio());
                puntoventa19020.setDireccion("212 Esq. 413 La Catalina");
                puntoventa19020.setLatitud("22.9705055555556");
                puntoventa19020.setLongitud("-82.3953916666667");
                puntoventa19020.setHorario("11:00AM - 7:30PM");
                puntoventa19020.setTelefono("No Tiene");
                puntoventa19020.setNombre("La Catalina");
                daoSession.insertOrReplace(puntoventa19020);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa19021 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa19021.setCodigo("19021");
                puntoventa19021.setMunicipio(municipioboyeros);
                puntoventa19021.setIdmunicipio(municipioboyeros.getIdmunicipio());
                puntoventa19021.setDireccion("Beatriz esq. 10, Altahabana");
                puntoventa19021.setLatitud("23.0583055555556");
                puntoventa19021.setLongitud("-82.4015944444445");
                puntoventa19021.setHorario("11:00AM - 7:30PM");
                puntoventa19021.setTelefono("No Tiene");
                puntoventa19021.setNombre("Beatriz esq. 10");
                daoSession.insertOrReplace(puntoventa19021);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa19022 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa19022.setCodigo("19022");
                puntoventa19022.setMunicipio(municipioboyeros);
                puntoventa19022.setIdmunicipio(municipioboyeros.getIdmunicipio());
                puntoventa19022.setDireccion("290 Esq, 227 Juan M.Márquez");
                puntoventa19022.setLatitud("23.0064527777778");
                puntoventa19022.setLongitud("-82.4425166666667");
                puntoventa19022.setHorario("11:00AM - 7:30PM");
                puntoventa19022.setTelefono("No Tiene");
                puntoventa19022.setNombre("Juan M.Márquez");
                daoSession.insertOrReplace(puntoventa19022);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa19023 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa19023.setCodigo("19023");
                puntoventa19023.setMunicipio(municipioboyeros);
                puntoventa19023.setIdmunicipio(municipioboyeros.getIdmunicipio());
                puntoventa19023.setDireccion("203 e/ 292 y 294 El Chico");
                puntoventa19023.setLatitud("23.0131");
                puntoventa19023.setLongitud("-82.4276055555556");
                puntoventa19023.setHorario("11:00AM - 7:30PM");
                puntoventa19023.setTelefono("No Tiene");
                puntoventa19023.setNombre("El Chico");
                daoSession.insertOrReplace(puntoventa19023);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa19024 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa19024.setCodigo("19024");
                puntoventa19024.setMunicipio(municipioboyeros);
                puntoventa19024.setIdmunicipio(municipioboyeros.getIdmunicipio());
                puntoventa19024.setDireccion("206 Esq. 229 Fontanar");
                puntoventa19024.setLatitud("23.0222833333333");
                puntoventa19024.setLongitud("-82.4152777777778");
                puntoventa19024.setHorario("11:00AM - 7:30PM");
                puntoventa19024.setTelefono("No Tiene");
                puntoventa19024.setNombre("Fontanar 2");
                daoSession.insertOrReplace(puntoventa19024);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa19025 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa19025.setCodigo("19025");
                puntoventa19025.setMunicipio(municipioboyeros);
                puntoventa19025.setIdmunicipio(municipioboyeros.getIdmunicipio());
                puntoventa19025.setDireccion("363 e/160 y 164 Mulgoba Viejo");
                puntoventa19025.setLatitud("22.9830916666667");
                puntoventa19025.setLongitud("-82.3759444444444");
                puntoventa19025.setHorario("11:00AM - 7:30PM");
                puntoventa19025.setTelefono("No Tiene");
                puntoventa19025.setNombre("Mulgoba 2");
                daoSession.insertOrReplace(puntoventa19025);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa19026 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa19026.setCodigo("19026");
                puntoventa19026.setMunicipio(municipioboyeros);
                puntoventa19026.setIdmunicipio(municipioboyeros.getIdmunicipio());
                puntoventa19026.setDireccion(" 107 e/ 28 y 32 Sierra Maestra");
                puntoventa19026.setLatitud("22.9740388888889");
                puntoventa19026.setLongitud("-82.3684388888889");
                puntoventa19026.setHorario("11:00AM - 7:30PM");
                puntoventa19026.setTelefono("No Tiene");
                puntoventa19026.setNombre("Sierra Maestra 2");
                daoSession.insertOrReplace(puntoventa19026);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa19027 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa19027.setCodigo("19027");
                puntoventa19027.setMunicipio(municipioboyeros);
                puntoventa19027.setIdmunicipio(municipioboyeros.getIdmunicipio());
                puntoventa19027.setDireccion("170 esq. 327 Lugardita");
                puntoventa19027.setLatitud("22.9978111111111");
                puntoventa19027.setLongitud("-82.3838");
                puntoventa19027.setHorario("11:00AM - 7:30PM");
                puntoventa19027.setTelefono("No Tiene");
                puntoventa19027.setNombre("Lugardita 2");
                daoSession.insertOrReplace(puntoventa19027);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa19028 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa19028.setCodigo("19028");
                puntoventa19028.setMunicipio(municipioboyeros);
                puntoventa19028.setIdmunicipio(municipioboyeros.getIdmunicipio());
                puntoventa19028.setDireccion("156 s/n e/285 y 287 Nazareno");
                puntoventa19028.setLatitud("23.0090861111111");
                puntoventa19028.setLongitud("-82.3842138888889");
                puntoventa19028.setHorario("11:00AM - 7:30PM");
                puntoventa19028.setTelefono("No Tiene");
                puntoventa19028.setNombre("Nazareno");
                daoSession.insertOrReplace(puntoventa19028);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa19029 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa19029.setCodigo("19029");
                puntoventa19029.setMunicipio(municipioboyeros);
                puntoventa19029.setIdmunicipio(municipioboyeros.getIdmunicipio());
                puntoventa19029.setDireccion("289 e/ 188 y 190 Dinorah");
                puntoventa19029.setLatitud("23.0056305555556");
                puntoventa19029.setLongitud("-82.3899833333333");
                puntoventa19029.setHorario("11:00AM - 7:30PM");
                puntoventa19029.setTelefono("No Tiene");
                puntoventa19029.setNombre("Dinorah");
                daoSession.insertOrReplace(puntoventa19029);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa19030 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa19030.setCodigo("19030");
                puntoventa19030.setMunicipio(municipioboyeros);
                puntoventa19030.setIdmunicipio(municipioboyeros.getIdmunicipio());
                puntoventa19030.setDireccion("287 e/ 102 y 104 Calabazar");
                puntoventa19030.setLatitud("23.0158027777778");
                puntoventa19030.setLongitud("-82.3711888888889");
                puntoventa19030.setHorario("11:00AM - 7:30PM");
                puntoventa19030.setTelefono("No Tiene");
                puntoventa19030.setNombre("Calabazar");
                daoSession.insertOrReplace(puntoventa19030);


                //PUNTOS DE VENTA MARIANAO (OK)
                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa50002 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa50002.setCodigo("50002");
                puntoventa50002.setMunicipio(municipiomarianao);
                puntoventa50002.setIdmunicipio(municipiomarianao.getIdmunicipio());
                puntoventa50002.setDireccion("119 y 120. Toledo");
                puntoventa50002.setLatitud("23.05571111");
                puntoventa50002.setLongitud("-82.42227222");
                puntoventa50002.setHorario("11:00AM - 7:30PM");
                puntoventa50002.setTelefono("No Tiene");
                puntoventa50002.setNombre("Toledo");
                daoSession.insertOrReplace(puntoventa50002);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa50003 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa50003.setCodigo("50003");
                puntoventa50003.setMunicipio(municipiomarianao);
                puntoventa50003.setIdmunicipio(municipiomarianao.getIdmunicipio());
                puntoventa50003.setDireccion("134. Esq 57");
                puntoventa50003.setLatitud("23.069886111");
                puntoventa50003.setLongitud("-82.43475555");
                puntoventa50003.setHorario("11:00AM - 7:30PM");
                puntoventa50003.setTelefono("No Tiene");
                puntoventa50003.setNombre("134. Esq 57");
                daoSession.insertOrReplace(puntoventa50003);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa50004 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa50004.setCodigo("50004");
                puntoventa50004.setMunicipio(municipiomarianao);
                puntoventa50004.setIdmunicipio(municipiomarianao.getIdmunicipio());
                puntoventa50004.setDireccion("130 y 41");
                puntoventa50004.setLatitud("23.07414444");
                puntoventa50004.setLongitud("-82.43614444");
                puntoventa50004.setHorario("11:00AM - 7:30PM");
                puntoventa50004.setTelefono("No Tiene");
                puntoventa50004.setNombre("130 y 41");
                daoSession.insertOrReplace(puntoventa50004);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa50005 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa50005.setCodigo("50005");
                puntoventa50005.setMunicipio(municipiomarianao);
                puntoventa50005.setIdmunicipio(municipiomarianao.getIdmunicipio());
                puntoventa50005.setDireccion("96 y 97");
                puntoventa50005.setLatitud("23.0762277777778");
                puntoventa50005.setLongitud("-82.4158");
                puntoventa50005.setHorario("11:00AM - 7:30PM");
                puntoventa50005.setTelefono("No Tiene");
                puntoventa50005.setNombre("96 y 97");
                daoSession.insertOrReplace(puntoventa50005);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa50006 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa50006.setCodigo("50006");
                puntoventa50006.setMunicipio(municipiomarianao);
                puntoventa50006.setIdmunicipio(municipiomarianao.getIdmunicipio());
                puntoventa50006.setDireccion("128b y 95");
                puntoventa50006.setLatitud("23.0619611111111");
                puntoventa50006.setLongitud("-82.42935");
                puntoventa50006.setHorario("11:00AM - 7:30PM");
                puntoventa50006.setTelefono("No Tiene");
                puntoventa50006.setNombre("128b y 95");
                daoSession.insertOrReplace(puntoventa50006);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa50008 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa50008.setCodigo("50008");
                puntoventa50008.setMunicipio(municipiomarianao);
                puntoventa50008.setIdmunicipio(municipiomarianao.getIdmunicipio());
                puntoventa50008.setDireccion("122 y 47");
                puntoventa50008.setLatitud("23.0742972222222");
                puntoventa50008.setLongitud("-82.4317416666667");
                puntoventa50008.setHorario("11:00AM - 7:30PM");
                puntoventa50008.setTelefono("No Tiene");
                puntoventa50008.setNombre("122 y 47");
                daoSession.insertOrReplace(puntoventa50008);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa50009 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa50009.setCodigo("50009");
                puntoventa50009.setMunicipio(municipiomarianao);
                puntoventa50009.setIdmunicipio(municipiomarianao.getIdmunicipio());
                puntoventa50009.setDireccion("Calle 100. Reparto Puente Nuevo");
                puntoventa50009.setLatitud("23.0715444444444");
                puntoventa50009.setLongitud("-82.4057166666667");
                puntoventa50009.setHorario("11:00AM - 7:30PM");
                puntoventa50009.setTelefono("No Tiene");
                puntoventa50009.setNombre("Puente Nuevo");
                daoSession.insertOrReplace(puntoventa50009);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa50010 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa50010.setCodigo("50010");
                puntoventa50010.setMunicipio(municipiomarianao);
                puntoventa50010.setIdmunicipio(municipiomarianao.getIdmunicipio());
                puntoventa50010.setDireccion("102 y 33");
                puntoventa50010.setLatitud("23.084825");
                puntoventa50010.setLongitud("-82.4264277777778");
                puntoventa50010.setHorario("11:00AM - 7:30PM");
                puntoventa50010.setTelefono("No Tiene");
                puntoventa50010.setNombre("102 y 33");
                daoSession.insertOrReplace(puntoventa50010);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa50011 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa50011.setCodigo("50011");
                puntoventa50011.setMunicipio(municipiomarianao);
                puntoventa50011.setIdmunicipio(municipiomarianao.getIdmunicipio());
                puntoventa50011.setDireccion("120b y 27");
                puntoventa50011.setLatitud("23.0832944444444");
                puntoventa50011.setLongitud("-82.4375194444444");
                puntoventa50011.setHorario("11:00AM - 7:30PM");
                puntoventa50011.setTelefono("No Tiene");
                puntoventa50011.setNombre("120b y 27");
                daoSession.insertOrReplace(puntoventa50011);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa50012 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa50012.setCodigo("50012");
                puntoventa50012.setMunicipio(municipiomarianao);
                puntoventa50012.setIdmunicipio(municipiomarianao.getIdmunicipio());
                puntoventa50012.setDireccion("67 e/ 114 y 116");
                puntoventa50012.setLatitud("23.0723416666667");
                puntoventa50012.setLongitud("-82.4250583333333");
                puntoventa50012.setHorario("11:00AM - 7:30PM");
                puntoventa50012.setTelefono("No Tiene");
                puntoventa50012.setNombre("67 e/ 114 y 116");
                daoSession.insertOrReplace(puntoventa50012);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa50013 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa50013.setCodigo("50013");
                puntoventa50013.setMunicipio(municipiomarianao);
                puntoventa50013.setIdmunicipio(municipiomarianao.getIdmunicipio());
                puntoventa50013.setDireccion("33 y 128");
                puntoventa50013.setLatitud("23.0780166666667");
                puntoventa50013.setLongitud("-82.4374666666667");
                puntoventa50013.setHorario("11:00AM - 7:30PM");
                puntoventa50013.setTelefono("No Tiene");
                puntoventa50013.setNombre("33 y 128");
                daoSession.insertOrReplace(puntoventa50013);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa50014 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa50014.setCodigo("50014");
                puntoventa50014.setMunicipio(municipiomarianao);
                puntoventa50014.setIdmunicipio(municipiomarianao.getIdmunicipio());
                puntoventa50014.setDireccion("142 y 41");
                puntoventa50014.setLatitud("23.0729388888889");
                puntoventa50014.setLongitud("-82.4388916666667");
                puntoventa50014.setHorario("11:00AM - 7:30PM");
                puntoventa50014.setTelefono("No Tiene");
                puntoventa50014.setNombre("142 y 41");
                daoSession.insertOrReplace(puntoventa50014);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa50015 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa50015.setCodigo("50015");
                puntoventa50015.setMunicipio(municipiomarianao);
                puntoventa50015.setIdmunicipio(municipiomarianao.getIdmunicipio());
                puntoventa50015.setDireccion("132 Esq. 85");
                puntoventa50015.setLatitud("23.0645777777778");
                puntoventa50015.setLongitud("-82.4320333333333");
                puntoventa50015.setHorario("11:00AM - 7:30PM");
                puntoventa50015.setTelefono("No Tiene");
                puntoventa50015.setNombre("132 Esq. 85");
                daoSession.insertOrReplace(puntoventa50015);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa50016 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa50016.setCodigo("50016");
                puntoventa50016.setMunicipio(municipiomarianao);
                puntoventa50016.setIdmunicipio(municipiomarianao.getIdmunicipio());
                puntoventa50016.setDireccion("112 Esq. 41");
                puntoventa50016.setLatitud("23.0792583333333");
                puntoventa50016.setLongitud("-82.428375");
                puntoventa50016.setHorario("11:00AM - 7:30PM");
                puntoventa50016.setTelefono("No Tiene");
                puntoventa50016.setNombre("112 Esq.41");
                daoSession.insertOrReplace(puntoventa50016);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa50017 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa50017.setCodigo("50017");
                puntoventa50017.setMunicipio(municipiomarianao);
                puntoventa50017.setIdmunicipio(municipiomarianao.getIdmunicipio());
                puntoventa50017.setDireccion("63 y 122");
                puntoventa50017.setLatitud("23.0711333333333");
                puntoventa50017.setLongitud("-82.4295222222222");
                puntoventa50017.setHorario("11:00AM - 7:30PM");
                puntoventa50017.setTelefono("No Tiene");
                puntoventa50017.setNombre("63 y 122");
                daoSession.insertOrReplace(puntoventa50017);


                //PUNTOS DE VENTA LA LISA
                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa18002 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa18002.setCodigo("50004");
                puntoventa18002.setMunicipio(municipioliza);
                puntoventa18002.setIdmunicipio(municipioliza.getIdmunicipio());
                puntoventa18002.setDireccion("252 / 35 y 37, San Agustín");
                puntoventa18002.setLatitud("23.0527777");
                puntoventa18002.setLongitud("-82.46655");
                puntoventa18002.setHorario("11:00AM - 7:30PM");
                puntoventa18002.setTelefono("No Tiene");
                puntoventa18002.setNombre("San Agustín 2");
                daoSession.insertOrReplace(puntoventa18002);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa18003 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa18003.setCodigo("18003");
                puntoventa18003.setMunicipio(municipioliza);
                puntoventa18003.setIdmunicipio(municipioliza.getIdmunicipio());
                puntoventa18003.setDireccion("251 / 40 y 42, Punta Brava");
                puntoventa18003.setLatitud("23.01687222");
                puntoventa18003.setLongitud("-82.496858333");
                puntoventa18003.setHorario("11:00AM - 7:30PM");
                puntoventa18003.setTelefono("No Tiene");
                puntoventa18003.setNombre("Punta Brava");
                daoSession.insertOrReplace(puntoventa18003);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa18004 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa18004.setCodigo("18004");
                puntoventa18004.setMunicipio(municipioliza);
                puntoventa18004.setIdmunicipio(municipioliza.getIdmunicipio());
                puntoventa18004.setDireccion("243 / 28 y 14, XX Aniversario");
                puntoventa18004.setLatitud("23.023563888");
                puntoventa18004.setLongitud("-82.48875833");
                puntoventa18004.setHorario("11:00AM - 7:30PM");
                puntoventa18004.setTelefono("No Tiene");
                puntoventa18004.setNombre("XX Aniversario");
                daoSession.insertOrReplace(puntoventa18004);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa18005 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa18005.setCodigo("18005");
                puntoventa18005.setMunicipio(municipioliza);
                puntoventa18005.setIdmunicipio(municipioliza.getIdmunicipio());
                puntoventa18005.setDireccion("77 / 216 y 218, Balcón Arimao");
                puntoventa18005.setLatitud("23.0557472222222");
                puntoventa18005.setLongitud("-82.4488194444445");
                puntoventa18005.setHorario("11:00AM - 7:30PM");
                puntoventa18005.setTelefono("No Tiene");
                puntoventa18005.setNombre("77 y 216");
                daoSession.insertOrReplace(puntoventa18005);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa18006 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa18006.setCodigo("18006");
                puntoventa18006.setMunicipio(municipioliza);
                puntoventa18006.setIdmunicipio(municipioliza.getIdmunicipio());
                puntoventa18006.setDireccion("240 y 39, San Agustín");
                puntoventa18006.setLatitud("23.05535");
                puntoventa18006.setLongitud("-82.4631527777778");
                puntoventa18006.setHorario("11:00AM - 7:30PM");
                puntoventa18006.setTelefono("No Tiene");
                puntoventa18006.setNombre("San Agustín 3");
                daoSession.insertOrReplace(puntoventa18006);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa18007 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa18007.setCodigo("18007");
                puntoventa18007.setMunicipio(municipioliza);
                puntoventa18007.setIdmunicipio(municipioliza.getIdmunicipio());
                puntoventa18007.setDireccion("97 / 200 y 202 Altura de la Lisa");
                puntoventa18007.setLatitud("23.05655");
                puntoventa18007.setLongitud("-82.4426861111111");
                puntoventa18007.setHorario("11:00AM - 7:30PM");
                puntoventa18007.setTelefono("No Tiene");
                puntoventa18007.setNombre("Novoa");
                daoSession.insertOrReplace(puntoventa18007);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa18008 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa18008.setCodigo("18008");
                puntoventa18008.setMunicipio(municipioliza);
                puntoventa18008.setIdmunicipio(municipioliza.getIdmunicipio());
                puntoventa18008.setDireccion("222 / 23 y 25 Coronela");
                puntoventa18008.setLatitud("23.0671277777778");
                puntoventa18008.setLongitud("-82.4673805555556");
                puntoventa18008.setHorario("11:00AM - 7:30PM");
                puntoventa18008.setTelefono("No Tiene");
                puntoventa18008.setNombre("Coronela");
                daoSession.insertOrReplace(puntoventa18008);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa18009 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa18009.setCodigo("18009");
                puntoventa18009.setMunicipio(municipioliza);
                puntoventa18009.setIdmunicipio(municipioliza.getIdmunicipio());
                puntoventa18009.setDireccion("314 / 163 y 165 Valle Grande");
                puntoventa18009.setLatitud("23.0114916666667");
                puntoventa18009.setLongitud("-82.4721083333333");
                puntoventa18009.setHorario("11:00AM - 7:30PM");
                puntoventa18009.setTelefono("No Tiene");
                puntoventa18009.setNombre("Valle Grande");
                daoSession.insertOrReplace(puntoventa18009);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa18010 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa18010.setCodigo("18010");
                puntoventa18010.setMunicipio(municipioliza);
                puntoventa18010.setIdmunicipio(municipioliza.getIdmunicipio());
                puntoventa18010.setDireccion("44 / 283 y 285 Guatao");
                puntoventa18010.setLatitud("23.001725");
                puntoventa18010.setLongitud("-82.4967722222222");
                puntoventa18010.setHorario("11:00AM - 7:30PM");
                puntoventa18010.setTelefono("No Tiene");
                puntoventa18010.setNombre("Guatao");
                daoSession.insertOrReplace(puntoventa18010);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa18011 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa18011.setCodigo("18011");
                puntoventa18011.setMunicipio(municipioliza);
                puntoventa18011.setIdmunicipio(municipioliza.getIdmunicipio());
                puntoventa18011.setDireccion("218 # 6718 / 67 y 69");
                puntoventa18011.setLatitud("23.0570861111111");
                puntoventa18011.setLongitud("-82.4520027777778");
                puntoventa18011.setHorario("11:00AM - 7:30PM");
                puntoventa18011.setTelefono("No Tiene");
                puntoventa18011.setNombre("67 y 218");
                daoSession.insertOrReplace(puntoventa18011);

                //PUNTOS DE VENTA PLAYA (OK)
                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa15001 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa15001.setCodigo("15001");
                puntoventa15001.setMunicipio(municipioplaya);
                puntoventa15001.setIdmunicipio(municipioplaya.getIdmunicipio());
                puntoventa15001.setDireccion("9na y 112");
                puntoventa15001.setLatitud("23.094577777");
                puntoventa15001.setLongitud("-82.44811111");
                puntoventa15001.setHorario("11:00AM - 7:30PM");
                puntoventa15001.setTelefono("No Tiene");
                puntoventa15001.setNombre("Romerillo");
                daoSession.insertOrReplace(puntoventa15001);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa15002 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa15002.setCodigo("15002");
                puntoventa15002.setMunicipio(municipioplaya);
                puntoventa15002.setIdmunicipio(municipioplaya.getIdmunicipio());
                puntoventa15002.setDireccion("1raB y 296");
                puntoventa15002.setLatitud("23.07824166");
                puntoventa15002.setLongitud("-82.51546666");
                puntoventa15002.setHorario("11:00AM - 7:30PM");
                puntoventa15002.setTelefono("No Tiene");
                puntoventa15002.setNombre("Santa Fe");
                daoSession.insertOrReplace(puntoventa15002);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa15003 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa15003.setCodigo("15003");
                puntoventa15003.setMunicipio(municipioplaya);
                puntoventa15003.setIdmunicipio(municipioplaya.getIdmunicipio());
                puntoventa15003.setDireccion("17 y 78");
                puntoventa15003.setLatitud("23.10026111");
                puntoventa15003.setLongitud("-82.432975");
                puntoventa15003.setHorario("11:00AM - 7:30PM");
                puntoventa15003.setTelefono("No Tiene");
                puntoventa15003.setNombre("17 y 18");
                daoSession.insertOrReplace(puntoventa15003);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa15005 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa15005.setCodigo("15005");
                puntoventa15005.setMunicipio(municipioplaya);
                puntoventa15005.setIdmunicipio(municipioplaya.getIdmunicipio());
                puntoventa15005.setDireccion("1ra y 172");
                puntoventa15005.setLatitud("23.0919416666667");
                puntoventa15005.setLongitud("-82.4649888888889");
                puntoventa15005.setHorario("11:00AM - 7:30PM");
                puntoventa15005.setTelefono("No Tiene");
                puntoventa15005.setNombre("Flores");
                daoSession.insertOrReplace(puntoventa15005);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa15009 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa15009.setCodigo("15009");
                puntoventa15009.setMunicipio(municipioplaya);
                puntoventa15009.setIdmunicipio(municipioplaya.getIdmunicipio());
                puntoventa15009.setDireccion("13 y 198");
                puntoventa15009.setLatitud("23.0827333333333");
                puntoventa15009.setLongitud("-82.4693611111111");
                puntoventa15009.setHorario("11:00AM - 7:30PM");
                puntoventa15009.setTelefono("No Tiene");
                puntoventa15009.setNombre("Siboney");
                daoSession.insertOrReplace(puntoventa15009);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa15010 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa15010.setCodigo("15010");
                puntoventa15010.setMunicipio(municipioplaya);
                puntoventa15010.setIdmunicipio(municipioplaya.getIdmunicipio());
                puntoventa15010.setDireccion("23 y 308");
                puntoventa15010.setLatitud("23.0524555555556");
                puntoventa15010.setLongitud("-82.4832527777778");
                puntoventa15010.setHorario("11:00AM - 7:30PM");
                puntoventa15010.setTelefono("No Tiene");
                puntoventa15010.setNombre("Barbosa");
                daoSession.insertOrReplace(puntoventa15010);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa15011 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa15011.setCodigo("15011");
                puntoventa15011.setMunicipio(municipioplaya);
                puntoventa15011.setIdmunicipio(municipioplaya.getIdmunicipio());
                puntoventa15011.setDireccion("3ra y 238");
                puntoventa15011.setLatitud("23.089875");
                puntoventa15011.setLongitud("-82.4877333333333");
                puntoventa15011.setHorario("11:00AM - 7:30PM");
                puntoventa15011.setTelefono("No Tiene");
                puntoventa15011.setNombre("Jaimanita");
                daoSession.insertOrReplace(puntoventa15011);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa15012 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa15012.setCodigo("15012");
                puntoventa15012.setMunicipio(municipioplaya);
                puntoventa15012.setIdmunicipio(municipioplaya.getIdmunicipio());
                puntoventa15012.setDireccion("156 y 1ra");
                puntoventa15012.setLatitud("23.0955777777778");
                puntoventa15012.setLongitud("-82.45995");
                puntoventa15012.setHorario("11:00AM - 7:30PM");
                puntoventa15012.setTelefono("No Tiene");
                puntoventa15012.setNombre("Náutico");
                daoSession.insertOrReplace(puntoventa15012);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa15013 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa15013.setCodigo("15013");
                puntoventa15013.setMunicipio(municipioplaya);
                puntoventa15013.setIdmunicipio(municipioplaya.getIdmunicipio());
                puntoventa15013.setDireccion("19 B y 214");
                puntoventa15013.setLatitud("23.0729305555556");
                puntoventa15013.setLongitud("-82.466175");
                puntoventa15013.setHorario("11:00AM - 7:30PM");
                puntoventa15013.setTelefono("No Tiene");
                puntoventa15013.setNombre("Atabey");
                daoSession.insertOrReplace(puntoventa15013);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa15014 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa15014.setCodigo("15014");
                puntoventa15014.setMunicipio(municipioplaya);
                puntoventa15014.setIdmunicipio(municipioplaya.getIdmunicipio());
                puntoventa15014.setDireccion("274 e/ 9na y 5ta");
                puntoventa15014.setLatitud("23.0784055555556");
                puntoventa15014.setLongitud("-82.5049805555556");
                puntoventa15014.setHorario("11:00AM - 7:30PM");
                puntoventa15014.setTelefono("No Tiene");
                puntoventa15014.setNombre("Juan M Márquez");
                daoSession.insertOrReplace(puntoventa15014);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa15015 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa15015.setCodigo("15015");
                puntoventa15015.setMunicipio(municipioplaya);
                puntoventa15015.setIdmunicipio(municipioplaya.getIdmunicipio());
                puntoventa15015.setDireccion("7ma y 92");
                puntoventa15015.setLatitud("23.0984888888889");
                puntoventa15015.setLongitud("-82.4433916666667");
                puntoventa15015.setHorario("11:00AM - 7:30PM");
                puntoventa15015.setTelefono("No Tiene");
                puntoventa15015.setNombre("7ma y 92");
                daoSession.insertOrReplace(puntoventa15015);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa15017 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa15017.setCodigo("15017");
                puntoventa15017.setMunicipio(municipioplaya);
                puntoventa15017.setIdmunicipio(municipioplaya.getIdmunicipio());
                puntoventa15017.setDireccion("190 y 37");
                puntoventa15017.setLatitud("23.0703694444444");
                puntoventa15017.setLongitud("-82.4497888888889");
                puntoventa15017.setHorario("11:00AM - 7:30PM");
                puntoventa15017.setTelefono("No Tiene");
                puntoventa15017.setNombre("Genética");
                daoSession.insertOrReplace(puntoventa15017);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa15018 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa15018.setCodigo("15018");
                puntoventa15018.setMunicipio(municipioplaya);
                puntoventa15018.setIdmunicipio(municipioplaya.getIdmunicipio());
                puntoventa15018.setDireccion("3ra y 308");
                puntoventa15018.setLatitud("23.0715638888889");
                puntoventa15018.setLongitud("-82.5237444444444");
                puntoventa15018.setHorario("11:00AM - 7:30PM");
                puntoventa15018.setTelefono("No Tiene");
                puntoventa15018.setNombre("El Bajo");
                daoSession.insertOrReplace(puntoventa15018);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa15021 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa15021.setCodigo("15021");
                puntoventa15021.setMunicipio(municipioplaya);
                puntoventa15021.setIdmunicipio(municipioplaya.getIdmunicipio());
                puntoventa15021.setDireccion("240 y 5ta");
                puntoventa15021.setLatitud("23.0861944444444");
                puntoventa15021.setLongitud("-82.4871944444444");
                puntoventa15021.setHorario("11:00AM - 7:30PM");
                puntoventa15021.setTelefono("No Tiene");
                puntoventa15021.setNombre("Jaimanita");
                daoSession.insertOrReplace(puntoventa15021);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa15022 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa15022.setCodigo("15022");
                puntoventa15022.setMunicipio(municipioplaya);
                puntoventa15022.setIdmunicipio(municipioplaya.getIdmunicipio());
                puntoventa15022.setDireccion("306 e/ 17 y 19");
                puntoventa15022.setLatitud("23.0670194444444");
                puntoventa15022.setLongitud("-82.5182083333333");
                puntoventa15022.setHorario("11:00AM - 7:30PM");
                puntoventa15022.setTelefono("No Tiene");
                puntoventa15022.setNombre("Roble");
                daoSession.insertOrReplace(puntoventa15022);


                //PUNTOS DE VENTA PLAZA (OK)
                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa40002 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa40002.setCodigo("40002");
                puntoventa40002.setMunicipio(municipioplaza);
                puntoventa40002.setIdmunicipio(municipioplaza.getIdmunicipio());
                puntoventa40002.setDireccion("26 y Santa Rosa");
                puntoventa40002.setLatitud("23.10649722");
                puntoventa40002.setLongitud("-82.396638888");
                puntoventa40002.setHorario("11:00AM - 7:30PM");
                puntoventa40002.setTelefono("No Tiene");
                puntoventa40002.setNombre("26 y Santa Rosa");
                daoSession.insertOrReplace(puntoventa40002);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa40003 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa40003.setCodigo("40003");
                puntoventa40003.setMunicipio(municipioplaza);
                puntoventa40003.setIdmunicipio(municipioplaza.getIdmunicipio());
                puntoventa40003.setDireccion("El Bosque");
                puntoventa40003.setLatitud("23.11418333");
                puntoventa40003.setLongitud("-82.408625");
                puntoventa40003.setHorario("11:00AM - 7:30PM");
                puntoventa40003.setTelefono("No Tiene");
                puntoventa40003.setNombre("El Bosque");
                daoSession.insertOrReplace(puntoventa40003);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa40007 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa40007.setCodigo("40007");
                puntoventa40007.setMunicipio(municipioplaza);
                puntoventa40007.setIdmunicipio(municipioplaza.getIdmunicipio());
                puntoventa40007.setDireccion("32 e/ 17 y 19");
                puntoventa40007.setLatitud("23.12293888");
                puntoventa40007.setLongitud("-82.410030555");
                puntoventa40007.setHorario("11:00AM - 7:30PM");
                puntoventa40007.setTelefono("No Tiene");
                puntoventa40007.setNombre("El Fanguito");
                daoSession.insertOrReplace(puntoventa40007);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa40008 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa40008.setCodigo("40008");
                puntoventa40008.setMunicipio(municipioplaza);
                puntoventa40008.setIdmunicipio(municipioplaza.getIdmunicipio());
                puntoventa40008.setDireccion("Estancia y Lombillo");
                puntoventa40008.setLatitud("23.1171472222222");
                puntoventa40008.setLongitud("-82.3908222222222");
                puntoventa40008.setHorario("11:00AM - 7:30PM");
                puntoventa40008.setTelefono("No Tiene");
                puntoventa40008.setNombre("Estancia y Lombillo");
                daoSession.insertOrReplace(puntoventa40008);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa40009 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa40009.setCodigo("40009");
                puntoventa40009.setMunicipio(municipioplaza);
                puntoventa40009.setIdmunicipio(municipioplaza.getIdmunicipio());
                puntoventa40009.setDireccion("Tulipán y Callejón de los Protestantes");
                puntoventa40009.setLatitud("23.1169444444444");
                puntoventa40009.setLongitud("-82.3978722222222");
                puntoventa40009.setHorario("11:00AM - 7:30PM");
                puntoventa40009.setTelefono("No Tiene");
                puntoventa40009.setNombre("Mariposa");
                daoSession.insertOrReplace(puntoventa40009);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa40010 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa40010.setCodigo("40010");
                puntoventa40010.setMunicipio(municipioplaza);
                puntoventa40010.setIdmunicipio(municipioplaza.getIdmunicipio());
                puntoventa40010.setDireccion("Protestante esquina Tulipán");
                puntoventa40010.setLatitud("23.1170416666667");
                puntoventa40010.setLongitud("-82.39745");
                puntoventa40010.setHorario("11:00AM - 7:30PM");
                puntoventa40010.setTelefono("No Tiene");
                puntoventa40010.setNombre("La Tropical");
                daoSession.insertOrReplace(puntoventa40010);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa40011 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa40011.setCodigo("40011");
                puntoventa40011.setMunicipio(municipioplaza);
                puntoventa40011.setIdmunicipio(municipioplaza.getIdmunicipio());
                puntoventa40011.setDireccion("11 y C");
                puntoventa40011.setLatitud("23.1385138888889");
                puntoventa40011.setLongitud("-82.3960472222222");
                puntoventa40011.setHorario("11:00AM - 7:30PM");
                puntoventa40011.setTelefono("No Tiene");
                puntoventa40011.setNombre("Vedado");
                daoSession.insertOrReplace(puntoventa40011);

                //PUNTOS DE VENTA HABANA DEL ESTE
                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa10001 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa10001.setCodigo("10001");
                puntoventa10001.setMunicipio(municipiohabanadeleste);
                puntoventa10001.setIdmunicipio(municipiohabanadeleste.getIdmunicipio());
                puntoventa10001.setDireccion("Calle 162 D e/e 3era C y 162 C Zona 1 Alamar");
                puntoventa10001.setLatitud("23.166213888");
                puntoventa10001.setLongitud("-82.280186111");
                puntoventa10001.setHorario("11:00AM - 7:30PM");
                puntoventa10001.setTelefono("No Tiene");
                puntoventa10001.setNombre("Alamar 1");
                daoSession.insertOrReplace(puntoventa10001);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa10002 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa10002.setCodigo("10002");
                puntoventa10002.setMunicipio(municipiohabanadeleste);
                puntoventa10002.setIdmunicipio(municipiohabanadeleste.getIdmunicipio());
                puntoventa10002.setDireccion("Ave 5ta Esq 160 Zona 5 Alamar");
                puntoventa10002.setLatitud("23.160302777");
                puntoventa10002.setLongitud("-82.282180555");
                puntoventa10002.setHorario("11:00AM - 7:30PM");
                puntoventa10002.setTelefono("No Tiene");
                puntoventa10002.setNombre("Alamar 5");
                daoSession.insertOrReplace(puntoventa10002);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa10003 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa10003.setCodigo("10003");
                puntoventa10003.setMunicipio(municipiohabanadeleste);
                puntoventa10003.setIdmunicipio(municipiohabanadeleste.getIdmunicipio());
                puntoventa10003.setDireccion("Calle 162 e/e 7ma Avenida y 5ta G Zona 6 Alamar");
                puntoventa10003.setLatitud("23.156472222");
                puntoventa10003.setLongitud("-82.27677777");
                puntoventa10003.setHorario("11:00AM - 7:30PM");
                puntoventa10003.setTelefono("No Tiene");
                puntoventa10003.setNombre("Alamar 6");
                daoSession.insertOrReplace(puntoventa10003);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa10004 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa10004.setCodigo("10004");
                puntoventa10004.setMunicipio(municipiohabanadeleste);
                puntoventa10004.setIdmunicipio(municipiohabanadeleste.getIdmunicipio());
                puntoventa10004.setDireccion("Calle 166 e/e 5ta E y 5ta G Zona 8 Alamar");
                puntoventa10004.setLatitud("23.1604833333333");
                puntoventa10004.setLongitud("-82.2712138888889");
                puntoventa10004.setHorario("11:00AM - 7:30PM");
                puntoventa10004.setTelefono("No Tiene");
                puntoventa10004.setNombre("Alamar 7");
                daoSession.insertOrReplace(puntoventa10004);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa10005 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa10005.setCodigo("10005");
                puntoventa10005.setMunicipio(municipiohabanadeleste);
                puntoventa10005.setIdmunicipio(municipiohabanadeleste.getIdmunicipio());
                puntoventa10005.setDireccion("Calle 160 C e/e Avenida 7ma C y 7ma D Zona 11 Alamar");
                puntoventa10005.setLatitud("23.1522722222222");
                puntoventa10005.setLongitud("-82.2811416666667");
                puntoventa10005.setHorario("11:00AM - 7:30PM");
                puntoventa10005.setTelefono("No Tiene");
                puntoventa10005.setNombre("Alamar 11");
                daoSession.insertOrReplace(puntoventa10005);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa10006 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa10006.setCodigo("10006");
                puntoventa10006.setMunicipio(municipiohabanadeleste);
                puntoventa10006.setIdmunicipio(municipiohabanadeleste.getIdmunicipio());
                puntoventa10006.setDireccion("Calle 7ma e/e 180 y 172 Zona 12 Alamar");
                puntoventa10006.setLatitud("23.1633333333333");
                puntoventa10006.setLongitud("-82.2667611111111");
                puntoventa10006.setHorario("11:00AM - 7:30PM");
                puntoventa10006.setTelefono("No Tiene");
                puntoventa10006.setNombre("Alamar 12");
                daoSession.insertOrReplace(puntoventa10006);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa10007 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa10007.setCodigo("10007");
                puntoventa10007.setMunicipio(municipiohabanadeleste);
                puntoventa10007.setIdmunicipio(municipiohabanadeleste.getIdmunicipio());
                puntoventa10007.setDireccion("Calle 82 e/e 1era B y 1era D Zona 15 Alamar");
                puntoventa10007.setLatitud("23.1709972222222");
                puntoventa10007.setLongitud("-82.2620916666667");
                puntoventa10007.setHorario("11:00AM - 7:30PM");
                puntoventa10007.setTelefono("No Tiene");
                puntoventa10007.setNombre("Alamar 15");
                daoSession.insertOrReplace(puntoventa10007);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa10008 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa10008.setCodigo("10008");
                puntoventa10008.setMunicipio(municipiohabanadeleste);
                puntoventa10008.setIdmunicipio(municipiohabanadeleste.getIdmunicipio());
                puntoventa10008.setDireccion("Calle 3era B Zona # 25 al lado del mercado la Curva Alamar");
                puntoventa10008.setLatitud("23.1680944444444");
                puntoventa10008.setLongitud("-82.273225");
                puntoventa10008.setHorario("11:00AM - 7:30PM");
                puntoventa10008.setTelefono("No Tiene");
                puntoventa10008.setNombre("Alamar 25");
                daoSession.insertOrReplace(puntoventa10008);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa10009 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa10009.setCodigo("10009");
                puntoventa10009.setMunicipio(municipiohabanadeleste);
                puntoventa10009.setIdmunicipio(municipiohabanadeleste.getIdmunicipio());
                puntoventa10009.setDireccion("Calle 168 C e/e 1era C y Avenida 3era Zona # 24 Alamar");
                puntoventa10009.setLatitud("23.1694972222222");
                puntoventa10009.setLongitud("-82.2760361111111");
                puntoventa10009.setHorario("11:00AM - 7:30PM");
                puntoventa10009.setTelefono("No Tiene");
                puntoventa10009.setNombre("Alamar 24");
                daoSession.insertOrReplace(puntoventa10009);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventa10010 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventa10010.setCodigo("10010");
                puntoventa10010.setMunicipio(municipiohabanadeleste);
                puntoventa10010.setIdmunicipio(municipiohabanadeleste.getIdmunicipio());
                puntoventa10010.setDireccion("Calle 190 e/e 1era C y 1era D Micro X Alamar");
                puntoventa10010.setLatitud("23.1719");
                puntoventa10010.setLongitud("-82.2549305555556");
                puntoventa10010.setHorario("11:00AM - 7:30PM");
                puntoventa10010.setTelefono("No Tiene");
                puntoventa10010.setNombre("Micro X");
                daoSession.insertOrReplace(puntoventa10010);

                /*//PROVINCIA ISLA DE LA JUVENTUD
                Provincia provinciaisla = new Provincia();
                provinciaisla.setNombre("Isla de la Juventud");
                provinciaisla.setActiva(false);
                daoSession.insertOrReplace(provinciaisla);*/

                /*//PROVINCIA MAYABEQUE
                /*Provincia provinciamayabeque = new Provincia();
                provinciamayabeque.setNombre("Mayabeque");
                provinciamayabeque.setActiva(false);
                daoSession.insertOrReplace(provinciamayabeque);*/

                //PROVINCIA MATANZAS
                Provincia provinciamatanzas = new Provincia();
                provinciamatanzas.setNombre("Matanzas");
                provinciamatanzas.setActiva(false);
                provinciamatanzas.setLatitud("23.03778");
                provinciamatanzas.setLongitud("-81.57723");
                daoSession.insertOrReplace(provinciamatanzas);

                Empresacomercializadora empresacomercializadoramatanzas = new Empresacomercializadora();
                empresacomercializadoramatanzas.setNombre("División Territorial de Comercialización de Combustible Matanzas");
                empresacomercializadoramatanzas.setProvincia(provinciamatanzas);
                empresacomercializadoramatanzas.setIdprovincia(provinciamatanzas.getIdprovincia());
                empresacomercializadoramatanzas.setDireccion("Km 4.3 Zona Industrial, Versalles. Matanzas");
                empresacomercializadoramatanzas.setTelefono("45282200 Ext 1024");
                empresacomercializadoramatanzas.setHorario("Lunes a viernes de 7.30 am a 5.00 pm");
                empresacomercializadoramatanzas.setLatitud("23.069425");
                empresacomercializadoramatanzas.setLongitud("-81.535697");
                daoSession.insertOrReplace(empresacomercializadoramatanzas);

                Serviciosmecanicos serviciosmecanicosmatanzas = new Serviciosmecanicos();
                serviciosmecanicosmatanzas.setNombre("Servicios Mecánicos Matanzas");
                serviciosmecanicosmatanzas.setProvincia(provinciamatanzas);
                serviciosmecanicosmatanzas.setIdprovincia(provinciamatanzas.getIdprovincia());
                serviciosmecanicosmatanzas.setDireccion("Km 4.3 Zona Industrial. Versalles Matanzas");
                serviciosmecanicosmatanzas.setTelefono("45282223 y 45282244");
                serviciosmecanicosmatanzas.setHorario("Lunes a jueves de 7.30 am a 12.30 y de 1.00 a 4.45 pm, viernes de7.30 am a 12.30 pm y de 1.00 pm a 3.45 pm");
                serviciosmecanicosmatanzas.setLatitud("23.057342");
                serviciosmecanicosmatanzas.setLongitud("-81.550606");
                daoSession.insertOrReplace(serviciosmecanicosmatanzas);

                Atencionclientes atencionclientesmatanzas = new Atencionclientes();
                atencionclientesmatanzas.setNombre("Oficina de Atención al Cliente Matanzas");
                atencionclientesmatanzas.setProvincia(provinciamatanzas);
                atencionclientesmatanzas.setIdprovincia(provinciamatanzas.getIdprovincia());
                atencionclientesmatanzas.setDireccion("Km 4.3 Zona Industrial, Versalles. Matanzas");
                atencionclientesmatanzas.setTelefono("45253826");
                atencionclientesmatanzas.setHorario("Lunes a jueves de 7.30 am a 12.30 y de 1.00 a 4.45 pm, viernes de7.30 am a 12.30 pm y de 1.00 pm a 3.45 pm");
                atencionclientesmatanzas.setLatitud("23.069425");
                atencionclientesmatanzas.setLongitud("-81.535697");
                daoSession.insertOrReplace(atencionclientesmatanzas);

                //MUNICIPIOS //PROVINCIA MATANZAS
                cu.tecnomatica.android.glp.database.greendao.Municipio municipiomatanzas = new cu.tecnomatica.android.glp.database.greendao.Municipio();
                municipiomatanzas.setNombre("Matanzas");
                municipiomatanzas.setProvincia(provinciamatanzas);
                municipiomatanzas.setActivo(true);
                daoSession.insertOrReplace(municipiomatanzas);

                cu.tecnomatica.android.glp.database.greendao.Municipio municipiolimonar = new cu.tecnomatica.android.glp.database.greendao.Municipio();
                municipiolimonar.setNombre("Limonar");
                municipiolimonar.setProvincia(provinciamatanzas);
                municipiolimonar.setActivo(false);
                daoSession.insertOrReplace(municipiolimonar);

                cu.tecnomatica.android.glp.database.greendao.Municipio municipiocardenas = new cu.tecnomatica.android.glp.database.greendao.Municipio();
                municipiocardenas.setNombre("Cárdenas");
                municipiocardenas.setProvincia(provinciamatanzas);
                municipiocardenas.setActivo(false);
                daoSession.insertOrReplace(municipiocardenas);

                cu.tecnomatica.android.glp.database.greendao.Municipio municipiojagueygrande = new cu.tecnomatica.android.glp.database.greendao.Municipio();
                municipiojagueygrande.setNombre("Jaguey Grande");
                municipiojagueygrande.setProvincia(provinciamatanzas);
                municipiojagueygrande.setActivo(false);
                daoSession.insertOrReplace(municipiojagueygrande);

                cu.tecnomatica.android.glp.database.greendao.Municipio municipiocolon = new cu.tecnomatica.android.glp.database.greendao.Municipio();
                municipiocolon.setNombre("Colón");
                municipiocolon.setProvincia(provinciamatanzas);
                municipiocolon.setActivo(false);
                daoSession.insertOrReplace(municipiocolon);

                cu.tecnomatica.android.glp.database.greendao.Municipio municipiomarti = new cu.tecnomatica.android.glp.database.greendao.Municipio();
                municipiomarti.setNombre("Martí");
                municipiomarti.setProvincia(provinciamatanzas);
                municipiomarti.setActivo(false);
                daoSession.insertOrReplace(municipiomarti);

                cu.tecnomatica.android.glp.database.greendao.Municipio municipiolosarabos = new cu.tecnomatica.android.glp.database.greendao.Municipio();
                municipiolosarabos.setNombre("Los Arabos");
                municipiolosarabos.setProvincia(provinciamatanzas);
                municipiolosarabos.setActivo(false);
                daoSession.insertOrReplace(municipiolosarabos);

                //CASAS COMERCIALES //PROVINCIA MATANZAS

                cu.tecnomatica.android.glp.database.greendao.Casacomercial casacomercialmatanzas = new cu.tecnomatica.android.glp.database.greendao.Casacomercial();

                casacomercialmatanzas.setIdmunicipio(municipiomatanzas.getIdmunicipio());
                casacomercialmatanzas.setMunicipio(municipiomatanzas);
                casacomercialmatanzas.setDireccion("Calle Luis Cuni esquina Las Mercedes. Matanzas");
                casacomercialmatanzas.setTelefono("45273416 y 45273476");
                casacomercialmatanzas.setHorario("Lunes a viernes de 7.30 am a 4.00 pm Excepto los Martes");
                casacomercialmatanzas.setNombre("CC Pinar del Río");
                casacomercialmatanzas.setLatitud("23.039683");
                casacomercialmatanzas.setLongitud("-81.571094");
                daoSession.insertOrReplace(casacomercialmatanzas);

                cu.tecnomatica.android.glp.database.greendao.Casacomercial casacomercialmatanzaslimonar = new cu.tecnomatica.android.glp.database.greendao.Casacomercial();

                casacomercialmatanzaslimonar.setIdmunicipio(municipiolimonar.getIdmunicipio());
                casacomercialmatanzaslimonar.setMunicipio(municipiolimonar);
                casacomercialmatanzaslimonar.setDireccion("Calle Luis Cuni esquina Las Mercedes. Matanzas");
                casacomercialmatanzaslimonar.setTelefono("45273416 y 45273476");
                casacomercialmatanzaslimonar.setHorario("Lunes a viernes de 7.30 am a 4.00 pm Excepto los Martes");
                casacomercialmatanzaslimonar.setNombre("CC Pinar del Río");
                casacomercialmatanzaslimonar.setLatitud("23.039683");
                casacomercialmatanzaslimonar.setLongitud("-81.571094");
                daoSession.insertOrReplace(casacomercialmatanzaslimonar);

                cu.tecnomatica.android.glp.database.greendao.Casacomercial casacomercialmatanzascardenas = new cu.tecnomatica.android.glp.database.greendao.Casacomercial();

                casacomercialmatanzascardenas.setIdmunicipio(municipiocardenas.getIdmunicipio());
                casacomercialmatanzascardenas.setMunicipio(municipiocardenas);
                casacomercialmatanzascardenas.setDireccion("Calle Luis Cuni esquina Las Mercedes. Matanzas");
                casacomercialmatanzascardenas.setTelefono("45273416 y 45273476");
                casacomercialmatanzascardenas.setHorario("Lunes a viernes de 7.30 am a 4.00 pm Excepto los Martes");
                casacomercialmatanzascardenas.setNombre("CC Pinar del Río");
                casacomercialmatanzascardenas.setLatitud("23.039683");
                casacomercialmatanzascardenas.setLongitud("-81.571094");
                daoSession.insertOrReplace(casacomercialmatanzascardenas);

                cu.tecnomatica.android.glp.database.greendao.Casacomercial casacomercialmatanzasjaguey = new cu.tecnomatica.android.glp.database.greendao.Casacomercial();

                casacomercialmatanzasjaguey.setIdmunicipio(municipiojagueygrande.getIdmunicipio());
                casacomercialmatanzasjaguey.setMunicipio(municipiojagueygrande);
                casacomercialmatanzasjaguey.setDireccion("Calle Luis Cuni esquina Las Mercedes. Matanzas");
                casacomercialmatanzasjaguey.setTelefono("45273416 y 45273476");
                casacomercialmatanzasjaguey.setHorario("Lunes a viernes de 7.30 am a 4.00 pm Excepto los Martes");
                casacomercialmatanzasjaguey.setNombre("CC Pinar del Río");
                casacomercialmatanzasjaguey.setLatitud("23.039683");
                casacomercialmatanzasjaguey.setLongitud("-81.571094");
                daoSession.insertOrReplace(casacomercialmatanzasjaguey);

                cu.tecnomatica.android.glp.database.greendao.Casacomercial casacomercialmatanzascolon = new cu.tecnomatica.android.glp.database.greendao.Casacomercial();

                casacomercialmatanzascolon.setIdmunicipio(municipiocolon.getIdmunicipio());
                casacomercialmatanzascolon.setMunicipio(municipiocolon);
                casacomercialmatanzascolon.setDireccion("Calle Luis Cuni esquina Las Mercedes. Matanzas");
                casacomercialmatanzascolon.setTelefono("45273416 y 45273476");
                casacomercialmatanzascolon.setHorario("Lunes a viernes de 7.30 am a 4.00 pm Excepto los Martes");
                casacomercialmatanzascolon.setNombre("CC Pinar del Río");
                casacomercialmatanzascolon.setLatitud("23.039683");
                casacomercialmatanzascolon.setLongitud("-81.571094");
                daoSession.insertOrReplace(casacomercialmatanzascolon);

                cu.tecnomatica.android.glp.database.greendao.Casacomercial casacomercialmatanzasmarti = new cu.tecnomatica.android.glp.database.greendao.Casacomercial();

                casacomercialmatanzasmarti.setIdmunicipio(municipiomarti.getIdmunicipio());
                casacomercialmatanzasmarti.setMunicipio(municipiomarti);
                casacomercialmatanzasmarti.setDireccion("Calle Luis Cuni esquina Las Mercedes. Matanzas");
                casacomercialmatanzasmarti.setTelefono("45273416 y 45273476");
                casacomercialmatanzasmarti.setHorario("Lunes a viernes de 7.30 am a 4.00 pm Excepto los Martes");
                casacomercialmatanzasmarti.setNombre("CC Pinar del Río");
                casacomercialmatanzasmarti.setLatitud("23.039683");
                casacomercialmatanzasmarti.setLongitud("-81.571094");
                daoSession.insertOrReplace(casacomercialmatanzasmarti);

                cu.tecnomatica.android.glp.database.greendao.Casacomercial casacomercialmatanzasarabos = new cu.tecnomatica.android.glp.database.greendao.Casacomercial();

                casacomercialmatanzasarabos.setIdmunicipio(municipiolosarabos.getIdmunicipio());
                casacomercialmatanzasarabos.setMunicipio(municipiolosarabos);
                casacomercialmatanzasarabos.setDireccion("Calle Luis Cuni esquina Las Mercedes. Matanzas");
                casacomercialmatanzasarabos.setTelefono("45273416 y 45273476");
                casacomercialmatanzasarabos.setHorario("Lunes a viernes de 7.30 am a 4.00 pm Excepto los Martes");
                casacomercialmatanzasarabos.setNombre("CC Pinar del Río");
                casacomercialmatanzasarabos.setLatitud("23.039683");
                casacomercialmatanzasarabos.setLongitud("-81.571094");
                daoSession.insertOrReplace(casacomercialmatanzasarabos);


                //PUNTOS DE VENTA //PROVINCIA MATANZAS

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventamatanzas1 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventamatanzas1.setCodigo("Matanzas1");
                puntoventamatanzas1.setMunicipio(municipiomatanzas);
                puntoventamatanzas1.setIdmunicipio(municipiomatanzas.getIdmunicipio());
                puntoventamatanzas1.setDireccion("Calle Colon esquina Santa Matilde");
                puntoventamatanzas1.setLatitud("23.05635");
                puntoventamatanzas1.setLongitud("-81.566428");
                puntoventamatanzas1.setHorario("Lunes a viernes: 7.30am a 12.30pm y de 1.00pm a 4.00pm. Sabados: 7.30am a 11.30am");
                puntoventamatanzas1.setTelefono("No Tiene");
                puntoventamatanzas1.setNombre("Versalles");
                daoSession.insertOrReplace(puntoventamatanzas1);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventamatanzas2 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventamatanzas2.setCodigo("Matanzas2");
                puntoventamatanzas2.setMunicipio(municipiomatanzas);
                puntoventamatanzas2.setIdmunicipio(municipiomatanzas.getIdmunicipio());
                puntoventamatanzas2.setDireccion("Calle 119 e/ 117 y 332");
                puntoventamatanzas2.setLatitud("23.036444");
                puntoventamatanzas2.setLongitud("-81.593403");
                puntoventamatanzas2.setHorario("Lunes a viernes: 7.30am a 12.30pm y de 1.00pm a 4.00pm. Sabados: 7.30am a 11.30am");
                puntoventamatanzas2.setTelefono("No Tiene");
                puntoventamatanzas2.setNombre("Armando Mestre");
                daoSession.insertOrReplace(puntoventamatanzas2);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventamatanzas3 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventamatanzas3.setCodigo("Matanzas3");
                puntoventamatanzas3.setMunicipio(municipiomatanzas);
                puntoventamatanzas3.setIdmunicipio(municipiomatanzas.getIdmunicipio());
                puntoventamatanzas3.setDireccion("Calle 5TA e/ 4TA Y 6TA  Reparto Camilo");
                puntoventamatanzas3.setLatitud("23.031814");
                puntoventamatanzas3.setLongitud("-81.578597");
                puntoventamatanzas3.setHorario("Lunes a viernes: 7.30am a 12.30pm y de 1.00pm a 4.00pm. Sabados: 7.30am a 11.30am");
                puntoventamatanzas3.setTelefono("No Tiene");
                puntoventamatanzas3.setNombre("Camilo Cienfuegos");
                daoSession.insertOrReplace(puntoventamatanzas3);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventamatanzas4 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventamatanzas4.setCodigo("Matanzas4");
                puntoventamatanzas4.setMunicipio(municipiomatanzas);
                puntoventamatanzas4.setIdmunicipio(municipiomatanzas.getIdmunicipio());
                puntoventamatanzas4.setDireccion("Calle 202 e/ 131 Y 133  Playa");
                puntoventamatanzas4.setLatitud("23.036494");
                puntoventamatanzas4.setLongitud("-81.541653");
                puntoventamatanzas4.setHorario("Lunes a viernes: 7.30am a 12.30pm y de 1.00pm a 4.00pm. Sabados: 7.30am a 11.30am");
                puntoventamatanzas4.setTelefono("No Tiene");
                puntoventamatanzas4.setNombre("13 Plantas");
                daoSession.insertOrReplace(puntoventamatanzas4);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventamatanzas5 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventamatanzas5.setCodigo("Matanzas5");
                puntoventamatanzas5.setMunicipio(municipiomatanzas);
                puntoventamatanzas5.setIdmunicipio(municipiomatanzas.getIdmunicipio());
                puntoventamatanzas5.setDireccion("Calle 134 e/ Carretera Central y 141");
                puntoventamatanzas5.setLatitud("23.027806");
                puntoventamatanzas5.setLongitud("-81.517467");
                puntoventamatanzas5.setHorario("Lunes a viernes: 7.30am a 12.30pm y de 1.00pm a 4.00pm. Sabados: 7.30am a 11.30am");
                puntoventamatanzas5.setTelefono("No Tiene");
                puntoventamatanzas5.setNombre("Gelpi");
                daoSession.insertOrReplace(puntoventamatanzas5);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventamatanzas6 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventamatanzas6.setCodigo("Matanzas6");
                puntoventamatanzas6.setMunicipio(municipiomatanzas);
                puntoventamatanzas6.setIdmunicipio(municipiomatanzas.getIdmunicipio());
                puntoventamatanzas6.setDireccion("Calle 143 e/140  Y 142  Pastorita");
                puntoventamatanzas6.setLatitud("23.037469");
                puntoventamatanzas6.setLongitud("-81.520386");
                puntoventamatanzas6.setHorario("Lunes a viernes: 7.30am a 12.30pm y de 1.00pm a 4.00pm. Sabados: 7.30am a 11.30am");
                puntoventamatanzas6.setTelefono("No Tiene");
                puntoventamatanzas6.setNombre("Reynold García");
                daoSession.insertOrReplace(puntoventamatanzas6);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventamatanzas7 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventamatanzas7.setCodigo("Matanzas7");
                puntoventamatanzas7.setMunicipio(municipiomatanzas);
                puntoventamatanzas7.setIdmunicipio(municipiomatanzas.getIdmunicipio());
                puntoventamatanzas7.setDireccion("Calle San Luis e/ Calzada Esteban y Desamparados");
                puntoventamatanzas7.setLatitud("23.032922");
                puntoventamatanzas7.setLongitud("-81.581931");
                puntoventamatanzas7.setHorario("Lunes a viernes: 7.30am a 12.30pm y de 1.00pm a 4.00pm. Sabados: 7.30am a 11.30am");
                puntoventamatanzas7.setTelefono("No Tiene");
                puntoventamatanzas7.setNombre("Parque Maceo");
                daoSession.insertOrReplace(puntoventamatanzas7);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventamatanzas8 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventamatanzas8.setCodigo("Matanzas8");
                puntoventamatanzas8.setMunicipio(municipiomatanzas);
                puntoventamatanzas8.setIdmunicipio(municipiomatanzas.getIdmunicipio());
                puntoventamatanzas8.setDireccion("Calle Marti e/ Cespedes, Ceiba Mocha");
                puntoventamatanzas8.setLatitud("22.981436");
                puntoventamatanzas8.setLongitud("-81.722036");
                puntoventamatanzas8.setHorario("Lunes a viernes: 7.30am a 12.30pm y de 1.00pm a 4.00pm. Sabados: 7.30am a 11.30am");
                puntoventamatanzas8.setTelefono("No Tiene");
                puntoventamatanzas8.setNombre("Ceiba Mocha");
                daoSession.insertOrReplace(puntoventamatanzas8);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventamatanzas9 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventamatanzas9.setCodigo("Matanzas9");
                puntoventamatanzas9.setMunicipio(municipiomatanzas);
                puntoventamatanzas9.setIdmunicipio(municipiomatanzas.getIdmunicipio());
                puntoventamatanzas9.setDireccion("Calle Alvarez  e/ Mujica y San Gabriel");
                puntoventamatanzas9.setLatitud("23.043822");
                puntoventamatanzas9.setLongitud("-81.587339");
                puntoventamatanzas9.setHorario("Lunes a viernes: 7.30am a 12.30pm y de 1.00pm a 4.00pm. Sabados: 7.30am a 11.30am");
                puntoventamatanzas9.setTelefono("No Tiene");
                puntoventamatanzas9.setNombre("Álvarez");
                daoSession.insertOrReplace(puntoventamatanzas9);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventamatanzas10 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventamatanzas10.setCodigo("Matanzas10");
                puntoventamatanzas10.setMunicipio(municipiomatanzas);
                puntoventamatanzas10.setIdmunicipio(municipiomatanzas.getIdmunicipio());
                puntoventamatanzas10.setDireccion("Calle Larga de Escoto e/  214 y 216 Playa");
                puntoventamatanzas10.setLatitud("23.035481");
                puntoventamatanzas10.setLongitud("-81.547939");
                puntoventamatanzas10.setHorario("Lunes a viernes: 7.30am a 12.30pm y de 1.00pm a 4.00pm. Sabados: 7.30am a 11.30am");
                puntoventamatanzas10.setTelefono("No Tiene");
                puntoventamatanzas10.setNombre("Bellamar");
                daoSession.insertOrReplace(puntoventamatanzas10);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventamatanzas11 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventamatanzas11.setCodigo("Matanzas11");
                puntoventamatanzas11.setMunicipio(municipiomatanzas);
                puntoventamatanzas11.setIdmunicipio(municipiomatanzas.getIdmunicipio());
                puntoventamatanzas11.setDireccion("Carretera Yumuri");
                puntoventamatanzas11.setLatitud("23.052622");
                puntoventamatanzas11.setLongitud("-81.574114");
                puntoventamatanzas11.setHorario("Lunes a viernes: 7.30am a 12.30pm y de 1.00pm a 4.00pm. Sabados: 7.30am a 11.30am");
                puntoventamatanzas11.setTelefono("No Tiene");
                puntoventamatanzas11.setNombre("Hersey");
                daoSession.insertOrReplace(puntoventamatanzas11);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventamatanzas12 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventamatanzas12.setCodigo("Matanzas12");
                puntoventamatanzas12.setMunicipio(municipiomatanzas);
                puntoventamatanzas12.setIdmunicipio(municipiomatanzas.getIdmunicipio());
                puntoventamatanzas12.setDireccion("Calle Jovellanos  e/ Daoiz y Velarde");
                puntoventamatanzas12.setLatitud("23.039683");
                puntoventamatanzas12.setLongitud("-81.571094");
                puntoventamatanzas12.setHorario("Lunes a viernes: 7.30am a 12.30pm y de 1.00pm a 4.00pm. Sabados: 7.30am a 11.30am");
                puntoventamatanzas12.setTelefono("No Tiene");
                puntoventamatanzas12.setNombre("La Marina");
                daoSession.insertOrReplace(puntoventamatanzas12);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventamatanzas13 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventamatanzas13.setCodigo("Matanzas13");
                puntoventamatanzas13.setMunicipio(municipiomatanzas);
                puntoventamatanzas13.setIdmunicipio(municipiomatanzas.getIdmunicipio());
                puntoventamatanzas13.setDireccion("Calle 111  e/ 330 y 332");
                puntoventamatanzas13.setLatitud("23.040153");
                puntoventamatanzas13.setLongitud("-81.594411");
                puntoventamatanzas13.setHorario("Lunes a viernes: 7.30am a 12.30pm y de 1.00pm a 4.00pm. Sabados: 7.30am a 11.30am");
                puntoventamatanzas13.setTelefono("No Tiene");
                puntoventamatanzas13.setNombre("Calzada del Naranjal");
                daoSession.insertOrReplace(puntoventamatanzas13);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventamatanzas14 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventamatanzas14.setCodigo("Matanzas14");
                puntoventamatanzas14.setMunicipio(municipiomatanzas);
                puntoventamatanzas14.setIdmunicipio(municipiomatanzas.getIdmunicipio());
                puntoventamatanzas14.setDireccion("Calle Real  Carretera  Triunvirato Final");
                puntoventamatanzas14.setLatitud("22.981386");
                puntoventamatanzas14.setLongitud("-81.513008");
                puntoventamatanzas14.setHorario("Lunes a viernes: 7.30am a 12.30pm y de 1.00pm a 4.00pm. Sabados: 7.30am a 11.30am");
                puntoventamatanzas14.setTelefono("No Tiene");
                puntoventamatanzas14.setNombre("Guanabaca");
                daoSession.insertOrReplace(puntoventamatanzas14);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventamatanzas15 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventamatanzas15.setCodigo("Matanzas1");
                puntoventamatanzas15.setMunicipio(municipiomatanzas);
                puntoventamatanzas15.setIdmunicipio(municipiomatanzas.getIdmunicipio());
                puntoventamatanzas15.setDireccion("Calle Jauregui e/ San Gabriel y Capricho");
                puntoventamatanzas15.setLatitud("23.052219");
                puntoventamatanzas15.setLongitud("-81.596569");
                puntoventamatanzas15.setHorario("Lunes a viernes: 7.30am a 12.30pm y de 1.00pm a 4.00pm. Sabados: 7.30am a 11.30am");
                puntoventamatanzas15.setTelefono("No Tiene");
                puntoventamatanzas15.setNombre("Los Mangos");
                daoSession.insertOrReplace(puntoventamatanzas15);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventamatanzas16 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventamatanzas16.setCodigo("Matanzas16");
                puntoventamatanzas16.setMunicipio(municipiomatanzas);
                puntoventamatanzas16.setIdmunicipio(municipiomatanzas.getIdmunicipio());
                puntoventamatanzas16.setDireccion("Calle 346 e/ 101 y 103 Naranjal Norte");
                puntoventamatanzas16.setLatitud("23.038753");
                puntoventamatanzas16.setLongitud("-81.601056");
                puntoventamatanzas16.setHorario("Lunes a viernes: 7.30am a 12.30pm y de 1.00pm a 4.00pm. Sabados: 7.30am a 11.30am");
                puntoventamatanzas16.setTelefono("No Tiene");
                puntoventamatanzas16.setNombre("Naranjal Norte");
                daoSession.insertOrReplace(puntoventamatanzas16);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventamatanzas17 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventamatanzas17.setCodigo("Matanzas17");
                puntoventamatanzas17.setMunicipio(municipiomatanzas);
                puntoventamatanzas17.setIdmunicipio(municipiomatanzas.getIdmunicipio());
                puntoventamatanzas17.setDireccion("Detrás de los edificios de ECASA");
                puntoventamatanzas17.setLatitud("23.082142");
                puntoventamatanzas17.setLongitud("-81.443572");
                puntoventamatanzas17.setHorario("Lunes a viernes: 7.30am a 12.30pm y de 1.00pm a 4.00pm. Sabados: 7.30am a 11.30am");
                puntoventamatanzas17.setTelefono("No Tiene");
                puntoventamatanzas17.setNombre("Carbonera");
                daoSession.insertOrReplace(puntoventamatanzas17);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventamatanzas18 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventamatanzas18.setCodigo("Matanzas19");
                puntoventamatanzas18.setMunicipio(municipiomatanzas);
                puntoventamatanzas18.setIdmunicipio(municipiomatanzas.getIdmunicipio());
                puntoventamatanzas18.setDireccion("Calle Luis Cuni esquina Las Mercedes");
                puntoventamatanzas18.setLatitud("23.05635");
                puntoventamatanzas18.setLongitud("-81.566428");
                puntoventamatanzas18.setHorario("Lunes a viernes: 7.30am a 12.30pm y de 1.00pm a 4.00pm. Sabados: 7.30am a 11.30am");
                puntoventamatanzas18.setTelefono("No Tiene");
                puntoventamatanzas18.setNombre("Playa");
                daoSession.insertOrReplace(puntoventamatanzas18);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventamatanzas19 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventamatanzas19.setCodigo("Matanzas19");
                puntoventamatanzas19.setMunicipio(municipiolimonar);
                puntoventamatanzas19.setIdmunicipio(municipiolimonar.getIdmunicipio());
                puntoventamatanzas19.setDireccion("Calle 2da e/ 1ra Y 3ra");
                puntoventamatanzas19.setLatitud("22.944583");
                puntoventamatanzas19.setLongitud("-81.513192");
                puntoventamatanzas19.setHorario("Lunes a viernes: 7.30am a 12.30pm y de 1.00pm a 4.00pm. Sabados: 7.30am a 11.30am");
                puntoventamatanzas19.setTelefono("No Tiene");
                puntoventamatanzas19.setNombre("Triunvirato");
                daoSession.insertOrReplace(puntoventamatanzas19);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventamatanzas20 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventamatanzas20.setCodigo("Matanzas20");
                puntoventamatanzas20.setMunicipio(municipiolimonar);
                puntoventamatanzas20.setIdmunicipio(municipiolimonar.getIdmunicipio());
                puntoventamatanzas20.setDireccion("Calle Central Final");
                puntoventamatanzas20.setLatitud("22.947803");
                puntoventamatanzas20.setLongitud("-81.407375");
                puntoventamatanzas20.setHorario("Lunes a viernes: 7.30am a 12.30pm y de 1.00pm a 4.00pm. Sabados: 7.30am a 11.30am");
                puntoventamatanzas20.setTelefono("No Tiene");
                puntoventamatanzas20.setNombre("Limonar");
                daoSession.insertOrReplace(puntoventamatanzas20);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventamatanzas21 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventamatanzas21.setCodigo("Matanzas21");
                puntoventamatanzas21.setMunicipio(municipiolimonar);
                puntoventamatanzas21.setIdmunicipio(municipiolimonar.getIdmunicipio());
                puntoventamatanzas21.setDireccion("Unidad Militar 2020   (Campo de Tiro La Victoria)");
                puntoventamatanzas21.setLatitud("22.927908");
                puntoventamatanzas21.setLongitud("-81.411578");
                puntoventamatanzas21.setHorario("Lunes a viernes: 7.30am a 12.30pm y de 1.00pm a 4.00pm. Sabados: 7.30am a 11.30am");
                puntoventamatanzas21.setTelefono("No Tiene");
                puntoventamatanzas21.setNombre("La Victoria");
                daoSession.insertOrReplace(puntoventamatanzas21);


                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventamatanzas22 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventamatanzas22.setCodigo("Matanzas22");
                puntoventamatanzas22.setMunicipio(municipiocardenas);
                puntoventamatanzas22.setIdmunicipio(municipiocardenas.getIdmunicipio());
                puntoventamatanzas22.setDireccion("Reparto 2 de Diciembre");
                puntoventamatanzas22.setLatitud("23.012306");
                puntoventamatanzas22.setLongitud("-81.232369");
                puntoventamatanzas22.setHorario("Lunes a viernes: 7.30am a 12.30pm y de 1.00pm a 4.00pm. Sabados: 7.30am a 11.30am");
                puntoventamatanzas22.setTelefono("No Tiene");
                puntoventamatanzas22.setNombre("Fines");
                daoSession.insertOrReplace(puntoventamatanzas22);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventamatanzas23 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventamatanzas23.setCodigo("Matanzas23");
                puntoventamatanzas23.setMunicipio(municipiocardenas);
                puntoventamatanzas23.setIdmunicipio(municipiocardenas.getIdmunicipio());
                puntoventamatanzas23.setDireccion("Antigua fábrica de materiales de construcción a la salida de Cárdenas");
                puntoventamatanzas23.setLatitud("23.022203");
                puntoventamatanzas23.setLongitud("-81.118897");
                puntoventamatanzas23.setHorario("Lunes a viernes: 7.30am a 12.30pm y de 1.00pm a 4.00pm. Sabados: 7.30am a 11.30am");
                puntoventamatanzas23.setTelefono("No Tiene");
                puntoventamatanzas23.setNombre("Calera");
                daoSession.insertOrReplace(puntoventamatanzas23);


                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventamatanzas24 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventamatanzas24.setCodigo("Matanzas24");
                puntoventamatanzas24.setMunicipio(municipiojagueygrande);
                puntoventamatanzas24.setIdmunicipio(municipiojagueygrande.getIdmunicipio());
                puntoventamatanzas24.setDireccion("Calle 24 e/ 19 y 21");
                puntoventamatanzas24.setLatitud("22.530547");
                puntoventamatanzas24.setLongitud("-81.134019");
                puntoventamatanzas24.setHorario("Lunes a viernes: 7.30am a 12.30pm y de 1.00pm a 4.00pm. Sabados: 7.30am a 11.30am");
                puntoventamatanzas24.setTelefono("No Tiene");
                puntoventamatanzas24.setNombre("San Bernardo");
                daoSession.insertOrReplace(puntoventamatanzas24);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventamatanzas25 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventamatanzas25.setCodigo("Matanzas25");
                puntoventamatanzas25.setMunicipio(municipiojagueygrande);
                puntoventamatanzas25.setIdmunicipio(municipiojagueygrande.getIdmunicipio());
                puntoventamatanzas25.setDireccion("Calle Biblioteca Final");
                puntoventamatanzas25.setLatitud("22.498947");
                puntoventamatanzas25.setLongitud("-81.134019");
                puntoventamatanzas25.setHorario("Lunes a viernes: 7.30am a 12.30pm y de 1.00pm a 4.00pm. Sabados: 7.30am a 11.30am");
                puntoventamatanzas25.setTelefono("No Tiene");
                puntoventamatanzas25.setNombre("Australia");
                daoSession.insertOrReplace(puntoventamatanzas25);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventamatanzas26 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventamatanzas26.setCodigo("Matanzas26");
                puntoventamatanzas26.setMunicipio(municipiojagueygrande);
                puntoventamatanzas26.setIdmunicipio(municipiojagueygrande.getIdmunicipio());
                puntoventamatanzas26.setDireccion("Poblado San José de Marcos");
                puntoventamatanzas26.setLatitud("22.654122");
                puntoventamatanzas26.setLongitud("-81.196544");
                puntoventamatanzas26.setHorario("Se Venden en 2 Partes");
                puntoventamatanzas26.setTelefono("No Tiene");
                puntoventamatanzas26.setNombre("San Marco");
                daoSession.insertOrReplace(puntoventamatanzas26);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventamatanzas27 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventamatanzas27.setCodigo("Matanzas27");
                puntoventamatanzas27.setMunicipio(municipiojagueygrande);
                puntoventamatanzas27.setIdmunicipio(municipiojagueygrande.getIdmunicipio());
                puntoventamatanzas27.setDireccion("T. 23");
                puntoventamatanzas27.setLatitud("22.609531");
                puntoventamatanzas27.setLongitud("-81.205097");
                puntoventamatanzas27.setHorario("Venta Eventual. Punto Cerrado");
                puntoventamatanzas27.setTelefono("No Tiene");
                puntoventamatanzas27.setNombre("Juan de Mata Reyes");
                daoSession.insertOrReplace(puntoventamatanzas27);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventamatanzas28 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventamatanzas28.setCodigo("Matanzas28");
                puntoventamatanzas28.setMunicipio(municipiojagueygrande);
                puntoventamatanzas28.setIdmunicipio(municipiojagueygrande.getIdmunicipio());
                puntoventamatanzas28.setDireccion("Calle 28 A Final");
                puntoventamatanzas28.setLatitud("22.586658");
                puntoventamatanzas28.setLongitud("-81.241086");
                puntoventamatanzas28.setHorario("Lunes a viernes: 7.30am a 12.30pm y de 1.00pm a 4.00pm. Sabados: 7.30am a 11.30am");
                puntoventamatanzas28.setTelefono("No Tiene");
                puntoventamatanzas28.setNombre("Torriente");
                daoSession.insertOrReplace(puntoventamatanzas28);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventamatanzas29 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventamatanzas29.setCodigo("Matanzas29");
                puntoventamatanzas29.setMunicipio(municipiojagueygrande);
                puntoventamatanzas29.setIdmunicipio(municipiojagueygrande.getIdmunicipio());
                puntoventamatanzas29.setDireccion("Calle Guamajal e/ Independencia y Final");
                puntoventamatanzas29.setLatitud("22.672467");
                puntoventamatanzas29.setLongitud("-81.116933");
                puntoventamatanzas29.setHorario("Lunes a viernes: 7.30am a 12.30pm y de 1.00pm a 4.00pm. Sabados: 7.30am a 11.30am");
                puntoventamatanzas29.setTelefono("No Tiene");
                puntoventamatanzas29.setNombre("Agramonte");
                daoSession.insertOrReplace(puntoventamatanzas29);


                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventamatanzas30 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventamatanzas30.setCodigo("Matanzas30");
                puntoventamatanzas30.setMunicipio(municipiocolon);
                puntoventamatanzas30.setIdmunicipio(municipiocolon.getIdmunicipio());
                puntoventamatanzas30.setDireccion("Concha No 191 Final");
                puntoventamatanzas30.setLatitud("22.716861");
                puntoventamatanzas30.setLongitud("-80.917469");
                puntoventamatanzas30.setHorario("Lunes a viernes: 7.30am a 12.30pm y de 1.00pm a 4.00pm. Sabados: 7.30am a 11.30am");
                puntoventamatanzas30.setTelefono("45316740");
                puntoventamatanzas30.setNombre("Colón");
                daoSession.insertOrReplace(puntoventamatanzas30);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventamatanzas31 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventamatanzas31.setCodigo("Matanzas31");
                puntoventamatanzas31.setMunicipio(municipiomarti);
                puntoventamatanzas31.setIdmunicipio(municipiomarti.getIdmunicipio());
                puntoventamatanzas31.setDireccion("Clotilde García e/ Frank País y Julio A. Mella");
                puntoventamatanzas31.setLatitud("22.945542");
                puntoventamatanzas31.setLongitud("-80.916158");
                puntoventamatanzas31.setHorario("Lunes a viernes: 7.30am a 12.30pm y de 1.00pm a 4.00pm. Sabados: 7.30am a 11.30am");
                puntoventamatanzas31.setTelefono("No Tiene");
                puntoventamatanzas31.setNombre("Martí");
                daoSession.insertOrReplace(puntoventamatanzas31);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventamatanzas32 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventamatanzas32.setCodigo("Matanzas32");
                puntoventamatanzas32.setMunicipio(municipiolosarabos);
                puntoventamatanzas32.setIdmunicipio(municipiolosarabos.getIdmunicipio());
                puntoventamatanzas32.setDireccion("Carretera Macagua No 6, Los Arabos");
                puntoventamatanzas32.setLatitud("22.733528");
                puntoventamatanzas32.setLongitud("-80.712644");
                puntoventamatanzas32.setHorario("Lunes a viernes: 7.30am a 12.30pm y de 1.00pm a 4.00pm. Sabados: 7.30am a 11.30am");
                puntoventamatanzas32.setTelefono("No Tiene");
                puntoventamatanzas32.setNombre("Los Arabos");
                daoSession.insertOrReplace(puntoventamatanzas32);


                //PROVINCIA CIENFUEGOS
                Provincia provinciacienfuegos = new Provincia();
                provinciacienfuegos.setNombre("Cienfuegos");
                provinciacienfuegos.setActiva(false);
                provinciacienfuegos.setLatitud("22.14575");
                provinciacienfuegos.setLongitud("-80.45264");
                daoSession.insertOrReplace(provinciacienfuegos);

                Empresacomercializadora empresacomercializadoracienfuegos = new Empresacomercializadora();
                empresacomercializadoracienfuegos.setNombre("División Territorial de Comercialización de Combustible Cienfuegos");
                empresacomercializadoracienfuegos.setProvincia(provinciacienfuegos);
                empresacomercializadoracienfuegos.setIdprovincia(provinciacienfuegos.getIdprovincia());
                empresacomercializadoracienfuegos.setDireccion("Finca Carolina, Refinería Cienfuegos");
                empresacomercializadoracienfuegos.setTelefono("43547184 ext 16102");
                empresacomercializadoracienfuegos.setHorario("Lunes a viernes de 7.00 am a 4.20 pm");
                empresacomercializadoracienfuegos.setLatitud("22.190465");
                empresacomercializadoracienfuegos.setLongitud("-80.516724");
                daoSession.insertOrReplace(empresacomercializadoracienfuegos);

                Serviciosmecanicos serviciosmecanicoscienfuegos = new Serviciosmecanicos();
                serviciosmecanicoscienfuegos.setNombre("Servicios Mecánicos Cienfuegos");
                serviciosmecanicoscienfuegos.setProvincia(provinciacienfuegos);
                serviciosmecanicoscienfuegos.setIdprovincia(provinciacienfuegos.getIdprovincia());
                serviciosmecanicoscienfuegos.setDireccion("Finca Carolina, Refinería Cienfuegos");
                serviciosmecanicoscienfuegos.setTelefono("43547184 ext 16159. Directo 43547224");
                serviciosmecanicoscienfuegos.setHorario("24 horas");
                serviciosmecanicoscienfuegos.setLatitud("22.190465");
                serviciosmecanicoscienfuegos.setLongitud("-80.516724");
                daoSession.insertOrReplace(serviciosmecanicoscienfuegos);

                Atencionclientes atencionclientescienfuegos = new Atencionclientes();
                atencionclientescienfuegos.setNombre("Oficina de Atención al Cliente Cienfuegos");
                atencionclientescienfuegos.setProvincia(provinciacienfuegos);
                atencionclientescienfuegos.setIdprovincia(provinciacienfuegos.getIdprovincia());
                atencionclientescienfuegos.setDireccion("Ave 56 No. 3705 e/ 37 y 39");
                atencionclientescienfuegos.setTelefono("43547184 ext 16160, 16161, 16162 y 16179");
                atencionclientescienfuegos.setHorario("Lunes a Viernes 7.00am - 4.20pm");
                atencionclientescienfuegos.setLatitud("22.14607");
                atencionclientescienfuegos.setLongitud("-80.447562");
                daoSession.insertOrReplace(atencionclientescienfuegos);

                //MUNICIPIOS //PROVINCIA CIENFUEGOS
                cu.tecnomatica.android.glp.database.greendao.Municipio municipiocienfuegos = new cu.tecnomatica.android.glp.database.greendao.Municipio();
                municipiocienfuegos.setNombre("Cienfuegos");
                municipiocienfuegos.setProvincia(provinciacienfuegos);
                municipiocienfuegos.setActivo(true);
                daoSession.insertOrReplace(municipiocienfuegos);

                cu.tecnomatica.android.glp.database.greendao.Municipio municipioabreus = new cu.tecnomatica.android.glp.database.greendao.Municipio();
                municipioabreus.setNombre("Abreus");
                municipioabreus.setProvincia(provinciacienfuegos);
                municipioabreus.setActivo(false);
                daoSession.insertOrReplace(municipioabreus);

                cu.tecnomatica.android.glp.database.greendao.Municipio municipiocruces = new cu.tecnomatica.android.glp.database.greendao.Municipio();
                municipiocruces.setNombre("Cruces");
                municipiocruces.setProvincia(provinciacienfuegos);
                municipiocruces.setActivo(false);
                daoSession.insertOrReplace(municipiocruces);

                cu.tecnomatica.android.glp.database.greendao.Municipio municipioaguada = new cu.tecnomatica.android.glp.database.greendao.Municipio();
                municipioaguada.setNombre("Aguada de Pasajeros");
                municipioaguada.setProvincia(provinciacienfuegos);
                municipioaguada.setActivo(false);
                daoSession.insertOrReplace(municipioaguada);

                cu.tecnomatica.android.glp.database.greendao.Municipio municipiocumanayagua = new cu.tecnomatica.android.glp.database.greendao.Municipio();
                municipiocumanayagua.setNombre("Cumanayagua");
                municipiocumanayagua.setProvincia(provinciacienfuegos);
                municipiocumanayagua.setActivo(false);
                daoSession.insertOrReplace(municipiocumanayagua);

                cu.tecnomatica.android.glp.database.greendao.Municipio municipiorodas = new cu.tecnomatica.android.glp.database.greendao.Municipio();
                municipiorodas.setNombre("Rodas");
                municipiorodas.setProvincia(provinciacienfuegos);
                municipiorodas.setActivo(false);
                daoSession.insertOrReplace(municipiorodas);

                cu.tecnomatica.android.glp.database.greendao.Municipio municipiopalmira = new cu.tecnomatica.android.glp.database.greendao.Municipio();
                municipiopalmira.setNombre("Palmira");
                municipiopalmira.setProvincia(provinciacienfuegos);
                municipiopalmira.setActivo(false);
                daoSession.insertOrReplace(municipiopalmira);

                cu.tecnomatica.android.glp.database.greendao.Municipio municipiolajas = new cu.tecnomatica.android.glp.database.greendao.Municipio();
                municipiolajas.setNombre("Lajas");
                municipiolajas.setProvincia(provinciacienfuegos);
                municipiolajas.setActivo(false);
                daoSession.insertOrReplace(municipiolajas);

                //CASAS COMERCIALES //PROVINCIA CIENFUEGOS

                cu.tecnomatica.android.glp.database.greendao.Casacomercial casacomercialcienfuegos = new cu.tecnomatica.android.glp.database.greendao.Casacomercial();

                casacomercialcienfuegos.setIdmunicipio(municipiocienfuegos.getIdmunicipio());
                casacomercialcienfuegos.setMunicipio(municipiocienfuegos);
                casacomercialcienfuegos.setDireccion("Ave 56 No. 3705 e/ 37 y 39");
                casacomercialcienfuegos.setTelefono("43547184 ext 16163, 16164, 16165. DIRECTO 43518657");
                casacomercialcienfuegos.setHorario("Lunes, Miércoles y Viernes 8:00am-12:00am y 1:00pm-5:00pm.  Martes y Jueves 9:00am-12:00am y 1:00pm-6:00pm. Sábados Alternos 8:00am-12:00am y 1:00pm-5:00pm");
                casacomercialcienfuegos.setNombre("CC Cienfuegos");
                casacomercialcienfuegos.setLatitud("22.14607");
                casacomercialcienfuegos.setLongitud("-80.447562");
                daoSession.insertOrReplace(casacomercialcienfuegos);

                cu.tecnomatica.android.glp.database.greendao.Casacomercial casacomercialabreus = new cu.tecnomatica.android.glp.database.greendao.Casacomercial();

                casacomercialabreus.setIdmunicipio(municipioabreus.getIdmunicipio());
                casacomercialabreus.setMunicipio(municipioabreus);
                casacomercialabreus.setDireccion("Ave 56 No. 3705 e/ 37 y 39");
                casacomercialabreus.setTelefono("43547184 ext 16163, 16164, 16165. DIRECTO 43518657");
                casacomercialabreus.setHorario("Lunes, Miércoles y Viernes 8:00am-12:00am y 1:00pm-5:00pm.  Martes y Jueves 9:00am-12:00am y 1:00pm-6:00pm. Sábados Alternos 8:00am-12:00am y 1:00pm-5:00pm");
                casacomercialabreus.setNombre("CC Cienfuegos");
                casacomercialabreus.setLatitud("22.14607");
                casacomercialabreus.setLongitud("-80.447562");
                daoSession.insertOrReplace(casacomercialabreus);

                cu.tecnomatica.android.glp.database.greendao.Casacomercial casacomercialcruces = new cu.tecnomatica.android.glp.database.greendao.Casacomercial();

                casacomercialcruces.setIdmunicipio(municipiocruces.getIdmunicipio());
                casacomercialcruces.setMunicipio(municipiocruces);
                casacomercialcruces.setDireccion("Ave 56 No. 3705 e/ 37 y 39");
                casacomercialcruces.setTelefono("43547184 ext 16163, 16164, 16165. DIRECTO 43518657");
                casacomercialcruces.setHorario("Lunes, Miércoles y Viernes 8:00am-12:00am y 1:00pm-5:00pm.  Martes y Jueves 9:00am-12:00am y 1:00pm-6:00pm. Sábados Alternos 8:00am-12:00am y 1:00pm-5:00pm");
                casacomercialcruces.setNombre("CC Cienfuegos");
                casacomercialcruces.setLatitud("22.14607");
                casacomercialcruces.setLongitud("-80.447562");
                daoSession.insertOrReplace(casacomercialcruces);

                cu.tecnomatica.android.glp.database.greendao.Casacomercial casacomercialaguada = new cu.tecnomatica.android.glp.database.greendao.Casacomercial();

                casacomercialaguada.setIdmunicipio(municipioaguada.getIdmunicipio());
                casacomercialaguada.setMunicipio(municipioaguada);
                casacomercialaguada.setDireccion("Ave 56 No. 3705 e/ 37 y 39");
                casacomercialaguada.setTelefono("43547184 ext 16163, 16164, 16165. DIRECTO 43518657");
                casacomercialaguada.setHorario("Lunes, Miércoles y Viernes 8:00am-12:00am y 1:00pm-5:00pm.  Martes y Jueves 9:00am-12:00am y 1:00pm-6:00pm. Sábados Alternos 8:00am-12:00am y 1:00pm-5:00pm");
                casacomercialaguada.setNombre("CC Cienfuegos");
                casacomercialaguada.setLatitud("22.14607");
                casacomercialaguada.setLongitud("-80.447562");
                daoSession.insertOrReplace(casacomercialaguada);

                cu.tecnomatica.android.glp.database.greendao.Casacomercial casacomercialcumanayagua = new cu.tecnomatica.android.glp.database.greendao.Casacomercial();

                casacomercialcumanayagua.setIdmunicipio(municipiocumanayagua.getIdmunicipio());
                casacomercialcumanayagua.setMunicipio(municipiocumanayagua);
                casacomercialcumanayagua.setDireccion("Ave 56 No. 3705 e/ 37 y 39");
                casacomercialcumanayagua.setTelefono("43547184 ext 16163, 16164, 16165. DIRECTO 43518657");
                casacomercialcumanayagua.setHorario("Lunes, Miércoles y Viernes 8:00am-12:00am y 1:00pm-5:00pm.  Martes y Jueves 9:00am-12:00am y 1:00pm-6:00pm. Sábados Alternos 8:00am-12:00am y 1:00pm-5:00pm");
                casacomercialcumanayagua.setNombre("CC Cienfuegos");
                casacomercialcumanayagua.setLatitud("22.14607");
                casacomercialcumanayagua.setLongitud("-80.447562");
                daoSession.insertOrReplace(casacomercialcumanayagua);

                cu.tecnomatica.android.glp.database.greendao.Casacomercial casacomercialrodas = new cu.tecnomatica.android.glp.database.greendao.Casacomercial();

                casacomercialrodas.setIdmunicipio(municipiorodas.getIdmunicipio());
                casacomercialrodas.setMunicipio(municipiorodas);
                casacomercialrodas.setDireccion("Ave 56 No. 3705 e/ 37 y 39");
                casacomercialrodas.setTelefono("43547184 ext 16163, 16164, 16165. DIRECTO 43518657");
                casacomercialrodas.setHorario("Lunes, Miércoles y Viernes 8:00am-12:00am y 1:00pm-5:00pm.  Martes y Jueves 9:00am-12:00am y 1:00pm-6:00pm. Sábados Alternos 8:00am-12:00am y 1:00pm-5:00pm");
                casacomercialrodas.setNombre("CC Cienfuegos");
                casacomercialrodas.setLatitud("22.14607");
                casacomercialrodas.setLongitud("-80.447562");
                daoSession.insertOrReplace(casacomercialrodas);

                cu.tecnomatica.android.glp.database.greendao.Casacomercial casacomercialpalmira = new cu.tecnomatica.android.glp.database.greendao.Casacomercial();

                casacomercialpalmira.setIdmunicipio(municipiopalmira.getIdmunicipio());
                casacomercialpalmira.setMunicipio(municipiopalmira);
                casacomercialpalmira.setDireccion("Ave 56 No. 3705 e/ 37 y 39");
                casacomercialpalmira.setTelefono("43547184 ext 16163, 16164, 16165. DIRECTO 43518657");
                casacomercialpalmira.setHorario("Lunes, Miércoles y Viernes 8:00am-12:00am y 1:00pm-5:00pm.  Martes y Jueves 9:00am-12:00am y 1:00pm-6:00pm. Sábados Alternos 8:00am-12:00am y 1:00pm-5:00pm");
                casacomercialpalmira.setNombre("CC Cienfuegos");
                casacomercialpalmira.setLatitud("22.14607");
                casacomercialpalmira.setLongitud("-80.447562");
                daoSession.insertOrReplace(casacomercialpalmira);

                cu.tecnomatica.android.glp.database.greendao.Casacomercial casacomerciallajas = new cu.tecnomatica.android.glp.database.greendao.Casacomercial();

                casacomerciallajas.setIdmunicipio(municipiolajas.getIdmunicipio());
                casacomerciallajas.setMunicipio(municipiolajas);
                casacomerciallajas.setDireccion("Ave 56 No. 3705 e/ 37 y 39");
                casacomerciallajas.setTelefono("43547184 ext 16163, 16164, 16165. DIRECTO 43518657");
                casacomerciallajas.setHorario("Lunes, Miércoles y Viernes 8:00am-12:00am y 1:00pm-5:00pm.  Martes y Jueves 9:00am-12:00am y 1:00pm-6:00pm. Sábados Alternos 8:00am-12:00am y 1:00pm-5:00pm");
                casacomerciallajas.setNombre("CC Cienfuegos");
                casacomerciallajas.setLatitud("22.14607");
                casacomerciallajas.setLongitud("-80.447562");
                daoSession.insertOrReplace(casacomerciallajas);

                //PUNTOS DE VENTA //PROVINCIA CIENFUEGOS

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventacienguegos1 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventacienguegos1.setCodigo("Cienfuegos1");
                puntoventacienguegos1.setMunicipio(municipiocienfuegos);
                puntoventacienguegos1.setIdmunicipio(municipiocienfuegos.getIdmunicipio());
                puntoventacienguegos1.setDireccion("Unidad de Comercio");
                puntoventacienguegos1.setLatitud("22.060877");
                puntoventacienguegos1.setLongitud("-80.473771");
                puntoventacienguegos1.setHorario("Lunes, Miercoles y Viernes 8:00am-3:00pm");
                puntoventacienguegos1.setTelefono("No Tiene");
                puntoventacienguegos1.setNombre("Cen");
                daoSession.insertOrReplace(puntoventacienguegos1);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventacienguegos2 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventacienguegos2.setCodigo("Cienfuegos2");
                puntoventacienguegos2.setMunicipio(municipiocienfuegos);
                puntoventacienguegos2.setIdmunicipio(municipiocienfuegos.getIdmunicipio());
                puntoventacienguegos2.setDireccion("Carretera Rancho Luna");
                puntoventacienguegos2.setLatitud("22.052488");
                puntoventacienguegos2.setLongitud("-80.416313");
                puntoventacienguegos2.setHorario("Lunes, Miercoles y Viernes 8:00am-3:00pm");
                puntoventacienguegos2.setTelefono("No Tiene");
                puntoventacienguegos2.setNombre("Rancho Luna");
                daoSession.insertOrReplace(puntoventacienguegos2);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventacienguegos3 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventacienguegos3.setCodigo("Cienfuegos3");
                puntoventacienguegos3.setMunicipio(municipiocienfuegos);
                puntoventacienguegos3.setIdmunicipio(municipiocienfuegos.getIdmunicipio());
                puntoventacienguegos3.setDireccion("Calle F S/N Ave 3 Caonao");
                puntoventacienguegos3.setLatitud("22.167533");
                puntoventacienguegos3.setLongitud("-80.403054");
                puntoventacienguegos3.setHorario("Lunes, Martes, Miercoles y Viernes 8:00am-11:00am y 1:00pm-5:00pm. Jueves 2:00pm-6:00pm. Sábados 8:00am-11:00am");
                puntoventacienguegos3.setTelefono("43558739");
                puntoventacienguegos3.setNombre("Caonao");
                daoSession.insertOrReplace(puntoventacienguegos3);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventacienguegos4 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventacienguegos4.setCodigo("Cienfuegos4");
                puntoventacienguegos4.setMunicipio(municipiocienfuegos);
                puntoventacienguegos4.setIdmunicipio(municipiocienfuegos.getIdmunicipio());
                puntoventacienguegos4.setDireccion("Carretera a Rodas No. 2 Esquina Real");
                puntoventacienguegos4.setLatitud("22.19132");
                puntoventacienguegos4.setLongitud("-80.457493");
                puntoventacienguegos4.setHorario("Lunes, Martes, Miercoles y Viernes 8:00am-11:00am y 1:00pm-5:00pm. Jueves 2:00pm-6:00pm. Sábados 8:00am-11:00am");
                puntoventacienguegos4.setTelefono("No Tiene");
                puntoventacienguegos4.setNombre("Paraiso");
                daoSession.insertOrReplace(puntoventacienguegos4);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventacienguegos5 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventacienguegos5.setCodigo("Cienfuegos5");
                puntoventacienguegos5.setMunicipio(municipiocienfuegos);
                puntoventacienguegos5.setIdmunicipio(municipiocienfuegos.getIdmunicipio());
                puntoventacienguegos5.setDireccion("Ave B S/N e/ 4 y 5 O´bourke");
                puntoventacienguegos5.setLatitud("22.163644");
                puntoventacienguegos5.setLongitud("-80.463237");
                puntoventacienguegos5.setHorario("Lunes, Martes, Miercoles y Viernes 8:00am-11:00am y 1:00pm-5:00pm. Jueves 2:00pm-6:00pm. Sábados 8:00am-11:00am");
                puntoventacienguegos5.setTelefono("No Tiene");
                puntoventacienguegos5.setNombre("O'Bourke");
                daoSession.insertOrReplace(puntoventacienguegos5);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventacienguegos6 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventacienguegos6.setCodigo("Cienfuegos6");
                puntoventacienguegos6.setMunicipio(municipiocienfuegos);
                puntoventacienguegos6.setIdmunicipio(municipiocienfuegos.getIdmunicipio());
                puntoventacienguegos6.setDireccion("Calle Central S/N e/ Calle Real y Carretera Trinidad, Pepito Tey");
                puntoventacienguegos6.setLatitud("22.127055");
                puntoventacienguegos6.setLongitud("-80.336893");
                puntoventacienguegos6.setHorario("Lunes, Martes, Miercoles y Viernes 8:00am-11:00am y 1:00pm-5:00pm. Jueves 2:00pm-6:00pm. Sábados 8:00am-11:00am");
                puntoventacienguegos6.setTelefono("43545250");
                puntoventacienguegos6.setNombre("Pepito Tey");
                daoSession.insertOrReplace(puntoventacienguegos6);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventacienguegos7 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventacienguegos7.setCodigo("Cienfuegos7");
                puntoventacienguegos7.setMunicipio(municipiocienfuegos);
                puntoventacienguegos7.setIdmunicipio(municipiocienfuegos.getIdmunicipio());
                puntoventacienguegos7.setDireccion("Calle Maceo S/N e/ Mango y Calle A, Guao");
                puntoventacienguegos7.setLatitud("22.141323");
                puntoventacienguegos7.setLongitud("-80.4754");
                puntoventacienguegos7.setHorario("Lunes, Martes, Miercoles y Viernes 8:00am-11:00am y 1:00pm-5:00pm. Jueves 2:00pm-6:00pm. Sábados 8:00am-11:00am");
                puntoventacienguegos7.setTelefono("43545275");
                puntoventacienguegos7.setNombre("Guao");
                daoSession.insertOrReplace(puntoventacienguegos7);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventacienguegos8 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventacienguegos8.setCodigo("Cienfuegos8");
                puntoventacienguegos8.setMunicipio(municipiocienfuegos);
                puntoventacienguegos8.setIdmunicipio(municipiocienfuegos.getIdmunicipio());
                puntoventacienguegos8.setDireccion("Ave 88 S/N e/ 31 y 33");
                puntoventacienguegos8.setLatitud("22.168648");
                puntoventacienguegos8.setLongitud("-80.448135");
                puntoventacienguegos8.setHorario("Lunes, Martes, Miercoles y Viernes 8:00am-11:00am y 1:00pm-5:00pm. Jueves 2:00pm-6:00pm. Sábados 8:00am-11:00am");
                puntoventacienguegos8.setTelefono("43516199");
                puntoventacienguegos8.setNombre("Pastorita");
                daoSession.insertOrReplace(puntoventacienguegos8);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventacienguegos9 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventacienguegos9.setCodigo("Cienfuegos9");
                puntoventacienguegos9.setMunicipio(municipiocienfuegos);
                puntoventacienguegos9.setIdmunicipio(municipiocienfuegos.getIdmunicipio());
                puntoventacienguegos9.setDireccion("Calle 19 S/N e/ Ave 48 y Ave 50");
                puntoventacienguegos9.setLatitud("22.144055");
                puntoventacienguegos9.setLongitud("-80.457316");
                puntoventacienguegos9.setHorario("Lunes, Martes, Miercoles y Viernes 8:00am-11:00am y 1:00pm-5:00pm. Jueves 2:00pm-6:00pm. Sábados 8:00am-11:00am");
                puntoventacienguegos9.setTelefono("43597009");
                puntoventacienguegos9.setNombre("Reina");
                daoSession.insertOrReplace(puntoventacienguegos9);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventacienguegos10 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventacienguegos10.setCodigo("Cienfuegos10");
                puntoventacienguegos10.setMunicipio(municipiocienfuegos);
                puntoventacienguegos10.setIdmunicipio(municipiocienfuegos.getIdmunicipio());
                puntoventacienguegos10.setDireccion("Ave 48 No. 49801 e/ 63 y 65");
                puntoventacienguegos10.setLatitud("22.141201");
                puntoventacienguegos10.setLongitud("-80.436075");
                puntoventacienguegos10.setHorario("Lunes, Martes, Miercoles y Viernes 8:00am-11:00am y 1:00pm-5:00pm. Jueves 2:00pm-6:00pm. Sábados 8:00am-11:00am");
                puntoventacienguegos10.setTelefono("No Tiene");
                puntoventacienguegos10.setNombre("Arizona");
                daoSession.insertOrReplace(puntoventacienguegos10);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventacienguegos11 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventacienguegos11.setCodigo("Cienfuegos11");
                puntoventacienguegos11.setMunicipio(municipiocienfuegos);
                puntoventacienguegos11.setIdmunicipio(municipiocienfuegos.getIdmunicipio());
                puntoventacienguegos11.setDireccion("16NE S/N e/ 89 y 91");
                puntoventacienguegos11.setLatitud("22.150979");
                puntoventacienguegos11.setLongitud("-80.425098");
                puntoventacienguegos11.setHorario("Lunes, Martes, Miercoles y Viernes 8:00am-11:00am y 1:00pm-5:00pm. Jueves 2:00pm-6:00pm. Sábados 8:00am-11:00am");
                puntoventacienguegos11.setTelefono("43556070");
                puntoventacienguegos11.setNombre("Tulipán");
                daoSession.insertOrReplace(puntoventacienguegos11);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventacienguegos12 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventacienguegos12.setCodigo("Cienfuegos12");
                puntoventacienguegos12.setMunicipio(municipiocienfuegos);
                puntoventacienguegos12.setIdmunicipio(municipiocienfuegos.getIdmunicipio());
                puntoventacienguegos12.setDireccion("Ave 50 No. 4923 e/ 49 y 51");
                puntoventacienguegos12.setLatitud("22.1427");
                puntoventacienguegos12.setLongitud("-80.442033");
                puntoventacienguegos12.setHorario("Lunes, Martes, Miercoles y Viernes 8:00am-11:00am y 1:00pm-5:00pm. Jueves 2:00pm-6:00pm. Sábados 8:00am-11:00am");
                puntoventacienguegos12.setTelefono("43556336");
                puntoventacienguegos12.setNombre("50 Y 51");
                daoSession.insertOrReplace(puntoventacienguegos12);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventacienguegos13 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventacienguegos13.setCodigo("Cienfuegos13");
                puntoventacienguegos13.setMunicipio(municipiocienfuegos);
                puntoventacienguegos13.setIdmunicipio(municipiocienfuegos.getIdmunicipio());
                puntoventacienguegos13.setDireccion("Calle 85 S/N e/ 28 y 30");
                puntoventacienguegos13.setLatitud("22.12916");
                puntoventacienguegos13.setLongitud("-80.428018");
                puntoventacienguegos13.setHorario("Lunes, Martes, Miercoles y Viernes 8:00am-11:00am y 1:00pm-5:00pm. Jueves 2:00pm-6:00pm. Sábados 8:00am-11:00am");
                puntoventacienguegos13.setTelefono("43523909");
                puntoventacienguegos13.setNombre("Junco Sur");
                daoSession.insertOrReplace(puntoventacienguegos13);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventacienguegos14 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventacienguegos14.setCodigo("Cienfuegos14");
                puntoventacienguegos14.setMunicipio(municipiocienfuegos);
                puntoventacienguegos14.setIdmunicipio(municipiocienfuegos.getIdmunicipio());
                puntoventacienguegos14.setDireccion("Calle 63 KM 3.5 S/N");
                puntoventacienguegos14.setLatitud("22.161143");
                puntoventacienguegos14.setLongitud("-80.43889");
                puntoventacienguegos14.setHorario("Lunes, Martes, Miercoles y Viernes 8:00am-11:00am y 1:00pm-5:00pm. Jueves 2:00pm-6:00pm. Sábados 8:00am-11:00am");
                puntoventacienguegos14.setTelefono("43523911");
                puntoventacienguegos14.setNombre("Pueblo Grifo");
                daoSession.insertOrReplace(puntoventacienguegos14);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventacienguegos15 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventacienguegos15.setCodigo("Cienfuegos15");
                puntoventacienguegos15.setMunicipio(municipiocienfuegos);
                puntoventacienguegos15.setIdmunicipio(municipiocienfuegos.getIdmunicipio());
                puntoventacienguegos15.setDireccion("Calle 31 S/N e/ 64 y 66");
                puntoventacienguegos15.setLatitud("22.150374");
                puntoventacienguegos15.setLongitud("-80.450657");
                puntoventacienguegos15.setHorario("Lunes, Martes, Miercoles y Viernes 8:00am-11:00am y 1:00pm-5:00pm. Jueves 2:00pm-6:00pm. Sábados 8:00am-11:00am");
                puntoventacienguegos15.setTelefono("43516250");
                puntoventacienguegos15.setNombre("Punta Cotica");
                daoSession.insertOrReplace(puntoventacienguegos15);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventacienguegos16 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventacienguegos16.setCodigo("Cienfuegos16");
                puntoventacienguegos16.setMunicipio(municipiocienfuegos);
                puntoventacienguegos16.setIdmunicipio(municipiocienfuegos.getIdmunicipio());
                puntoventacienguegos16.setDireccion("Ave 49 S/N e/ 70 y 71");
                puntoventacienguegos16.setLatitud("22.152186");
                puntoventacienguegos16.setLongitud("-80.444686");
                puntoventacienguegos16.setHorario("Lunes, Martes, Miercoles y Viernes 8:00am-11:00am y 1:00pm-5:00pm. Jueves 2:00pm-6:00pm. Sábados 8:00am-11:00am");
                puntoventacienguegos16.setTelefono("43516451");
                puntoventacienguegos16.setNombre("San Lázaro");
                daoSession.insertOrReplace(puntoventacienguegos16);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventacienguegos17 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventacienguegos17.setCodigo("Cienfuegos17");
                puntoventacienguegos17.setMunicipio(municipiocienfuegos);
                puntoventacienguegos17.setIdmunicipio(municipiocienfuegos.getIdmunicipio());
                puntoventacienguegos17.setDireccion("Ave 16 S/N e/ 53 y 55");
                puntoventacienguegos17.setLatitud("22.127842");
                puntoventacienguegos17.setLongitud("-80.45202");
                puntoventacienguegos17.setHorario("Lunes, Martes, Miercoles y Viernes 8:00am-11:00am y 1:00pm-5:00pm. Jueves 2:00pm-6:00pm. Sábados 8:00am-11:00am");
                puntoventacienguegos17.setTelefono("43516212");
                puntoventacienguegos17.setNombre("Playa Alegre");
                daoSession.insertOrReplace(puntoventacienguegos17);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventacienguegos18 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventacienguegos18.setCodigo("Cienfuegos18");
                puntoventacienguegos18.setMunicipio(municipiocienfuegos);
                puntoventacienguegos18.setIdmunicipio(municipiocienfuegos.getIdmunicipio());
                puntoventacienguegos18.setDireccion("Calle 35 S/N e/ Ave 6 y Ave 8");
                puntoventacienguegos18.setLatitud("22.123729");
                puntoventacienguegos18.setLongitud("-80.45202");
                puntoventacienguegos18.setHorario("Lunes, Martes, Miercoles y Viernes 8:00am-11:00am y 1:00pm-5:00pm. Jueves 2:00pm-6:00pm. Sábados 8:00am-11:00am");
                puntoventacienguegos18.setTelefono("43523908");
                puntoventacienguegos18.setNombre("Punta Gorda");
                daoSession.insertOrReplace(puntoventacienguegos18);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventacienguegos19 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventacienguegos19.setCodigo("Cienfuegos19");
                puntoventacienguegos19.setMunicipio(municipioabreus);
                puntoventacienguegos19.setIdmunicipio(municipioabreus.getIdmunicipio());
                puntoventacienguegos19.setDireccion("Calle Panchito Gómez S/N e/ Libertad y Martí");
                puntoventacienguegos19.setLatitud("22.278473");
                puntoventacienguegos19.setLongitud("-80.567037");
                puntoventacienguegos19.setHorario("Lunes, Martes, Miercoles y Viernes 8:00am-11:00am y 1:00pm-5:00pm. Jueves 2:00pm-6:00pm. Sábados 8:00am-11:00am");
                puntoventacienguegos19.setTelefono("No Tiene");
                puntoventacienguegos19.setNombre("Abreus");
                daoSession.insertOrReplace(puntoventacienguegos19);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventacienguegos20 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventacienguegos20.setCodigo("Cienfuegos20");
                puntoventacienguegos20.setMunicipio(municipiocruces);
                puntoventacienguegos20.setIdmunicipio(municipiocruces.getIdmunicipio());
                puntoventacienguegos20.setDireccion("Calle Andreita S/N e/ Ávalos y Abreus");
                puntoventacienguegos20.setLatitud("22.336825");
                puntoventacienguegos20.setLongitud("-80.270573");
                puntoventacienguegos20.setHorario("Lunes, Martes, Miercoles y Viernes 8:00am-11:00am y 1:00pm-5:00pm. Jueves 2:00pm-6:00pm. Sábados 8:00am-11:00am");
                puntoventacienguegos20.setTelefono("43572220");
                puntoventacienguegos20.setNombre("Cruces");
                daoSession.insertOrReplace(puntoventacienguegos20);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventacienguegos21 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventacienguegos21.setCodigo("Cienfuegos21");
                puntoventacienguegos21.setMunicipio(municipioaguada);
                puntoventacienguegos21.setIdmunicipio(municipioaguada.getIdmunicipio());
                puntoventacienguegos21.setDireccion("Calle Mayía Rodríguez S/N e/ Pimargal y Máximo Gómez");
                puntoventacienguegos21.setLatitud("22.385851");
                puntoventacienguegos21.setLongitud("-80.847920");
                puntoventacienguegos21.setHorario("Lunes, Martes, Miercoles y Viernes 8:00am-11:00am y 1:00pm-5:00pm. Jueves 2:00pm-6:00pm. Sábados 8:00am-11:00am");
                puntoventacienguegos21.setTelefono("43263862");
                puntoventacienguegos21.setNombre("Aguada");
                daoSession.insertOrReplace(puntoventacienguegos21);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventacienguegos22 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventacienguegos22.setCodigo("Cienfuegos22");
                puntoventacienguegos22.setMunicipio(municipiocumanayagua);
                puntoventacienguegos22.setIdmunicipio(municipiocumanayagua.getIdmunicipio());
                puntoventacienguegos22.setDireccion("Calle Ceibabo Final S/N");
                puntoventacienguegos22.setLatitud("22.139606");
                puntoventacienguegos22.setLongitud("-80.205264");
                puntoventacienguegos22.setHorario("Lunes, Martes, Miercoles y Viernes 8:00am-11:00am y 1:00pm-5:00pm. Jueves 2:00pm-6:00pm. Sábados 8:00am-11:00am");
                puntoventacienguegos22.setTelefono("No Tiene");
                puntoventacienguegos22.setNombre("Cumanayagua");
                daoSession.insertOrReplace(puntoventacienguegos22);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventacienguegos23 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventacienguegos23.setCodigo("Cienfuegos23");
                puntoventacienguegos23.setMunicipio(municipiorodas);
                puntoventacienguegos23.setIdmunicipio(municipiorodas.getIdmunicipio());
                puntoventacienguegos23.setDireccion("Calle Emilio Ruiz S/N e/ Diego García y San Esteban");
                puntoventacienguegos23.setLatitud("22.342638");
                puntoventacienguegos23.setLongitud("-80.559022");
                puntoventacienguegos23.setHorario("Lunes, Martes, Miercoles y Viernes 8:00am-11:00am y 1:00pm-5:00pm. Jueves 2:00pm-6:00pm. Sábados 8:00am-11:00am");
                puntoventacienguegos23.setTelefono("43549813");
                puntoventacienguegos23.setNombre("Rodas");
                daoSession.insertOrReplace(puntoventacienguegos23);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventacienguegos24 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventacienguegos24.setCodigo("Cienfuegos24");
                puntoventacienguegos24.setMunicipio(municipiopalmira);
                puntoventacienguegos24.setIdmunicipio(municipiopalmira.getIdmunicipio());
                puntoventacienguegos24.setDireccion("Calle San Pedro S/N e/ Céspedes y Americo");
                puntoventacienguegos24.setLatitud("22.24655");
                puntoventacienguegos24.setLongitud("-80.392218");
                puntoventacienguegos24.setHorario("Lunes, Martes, Miercoles y Viernes 8:00am-11:00am y 1:00pm-5:00pm. Jueves 2:00pm-6:00pm. Sábados 8:00am-11:00am");
                puntoventacienguegos24.setTelefono("43546836");
                puntoventacienguegos24.setNombre("Palmira");
                daoSession.insertOrReplace(puntoventacienguegos24);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventacienguegos25 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventacienguegos25.setCodigo("Cienfuegos25");
                puntoventacienguegos25.setMunicipio(municipiolajas);
                puntoventacienguegos25.setIdmunicipio(municipiolajas.getIdmunicipio());
                puntoventacienguegos25.setDireccion("Calle Nueva S/N e/ Calixto García y Goitizolo");
                puntoventacienguegos25.setLatitud("22.416999");
                puntoventacienguegos25.setLongitud("-80.291171");
                puntoventacienguegos25.setHorario("Lunes, Martes, Miercoles y Viernes 8:00am-11:00am y 1:00pm-5:00pm. Jueves 2:00pm-6:00pm. Sábados 8:00am-11:00am");
                puntoventacienguegos25.setTelefono("No Tiene");
                puntoventacienguegos25.setNombre("Lajas");
                daoSession.insertOrReplace(puntoventacienguegos25);


                /*//PROVINCIA VILLA CLARA
                /*Provincia provincivillaclara = new Provincia();
                provincivillaclara.setNombre("Villa Clara");
                provincivillaclara.setActiva(false);
                daoSession.insertOrReplace(provincivillaclara);*/

                //PROVINCIA SANCTI SPÍRITUS
                Provincia provinciasanctispiritus = new Provincia();
                provinciasanctispiritus.setNombre("Sancti Spíritus");
                provinciasanctispiritus.setLatitud("21.92758");
                provinciasanctispiritus.setLongitud("-79.44319");
                provinciasanctispiritus.setActiva(false);
                daoSession.insertOrReplace(provinciasanctispiritus);

                Empresacomercializadora empresacomercializadorasanctispiritus = new Empresacomercializadora();
                empresacomercializadorasanctispiritus.setNombre("Empresa Comercializadora de Combustible Sancti Spíritus");
                empresacomercializadorasanctispiritus.setProvincia(provinciasanctispiritus);
                empresacomercializadorasanctispiritus.setIdprovincia(provinciasanctispiritus.getIdprovincia());
                empresacomercializadorasanctispiritus.setDireccion("Calle Céspedez #1. Cabaiguán");
                empresacomercializadorasanctispiritus.setTelefono("41663658, 41662202 ext 176");
                empresacomercializadorasanctispiritus.setHorario("Lunes a Jueves de 7:00am a 11:00pm y de 11:30pm a 4:30pm. Viernes 7:00am a 11:00pm y de 11:30pm a 3:30pm");
                empresacomercializadorasanctispiritus.setLatitud("22.072656");
                empresacomercializadorasanctispiritus.setLongitud("-79.497433");
                daoSession.insertOrReplace(empresacomercializadorasanctispiritus);

                Serviciosmecanicos serviciosmecanicossanctispiritus = new Serviciosmecanicos();
                serviciosmecanicossanctispiritus.setNombre("Servicios Mecánicos Sancti Spíritus");
                serviciosmecanicossanctispiritus.setProvincia(provinciasanctispiritus);
                serviciosmecanicossanctispiritus.setIdprovincia(provinciasanctispiritus.getIdprovincia());
                serviciosmecanicossanctispiritus.setDireccion("Calle Céspedez #1. Cabaiguán");
                serviciosmecanicossanctispiritus.setTelefono("41662202 ext 142");
                serviciosmecanicossanctispiritus.setHorario("Lunes a Jueves de 7:00am a 11:00pm y de 11:30pm a 4:30pm. Viernes 7:00am a 11:00pm y de 11:30pm a 3:30pm");
                serviciosmecanicossanctispiritus.setLatitud("22.072656");
                serviciosmecanicossanctispiritus.setLongitud("-79.497433");
                daoSession.insertOrReplace(serviciosmecanicossanctispiritus);

                Atencionclientes atencionclientessanctispiritus = new Atencionclientes();
                atencionclientessanctispiritus.setNombre("Oficina de Atención al Cliente Sancti Spíritus");
                atencionclientessanctispiritus.setProvincia(provinciasanctispiritus);
                atencionclientessanctispiritus.setIdprovincia(provinciasanctispiritus.getIdprovincia());
                atencionclientessanctispiritus.setDireccion("Calle Céspedez #1. Cabaiguán");
                atencionclientessanctispiritus.setTelefono("41664020, 41662202 ext 159");
                atencionclientessanctispiritus.setHorario("Lunes a Jueves de 7:00am a 11:00pm y de 11:30pm a 4:30pm. Viernes 7:00am a 11:00pm y de 11:30pm a 3:30pm");
                atencionclientessanctispiritus.setLatitud("22.072656");
                atencionclientessanctispiritus.setLongitud("-79.497433");
                daoSession.insertOrReplace(atencionclientessanctispiritus);

                //MUNICIPIOS //PROVINCIA SANCTI SPÍRITUS

                cu.tecnomatica.android.glp.database.greendao.Municipio municipiosanctispiritus = new cu.tecnomatica.android.glp.database.greendao.Municipio();
                municipiosanctispiritus.setNombre("Sancti Spíritus");
                municipiosanctispiritus.setProvincia(provinciasanctispiritus);
                municipiosanctispiritus.setActivo(true);
                daoSession.insertOrReplace(municipiosanctispiritus);

                cu.tecnomatica.android.glp.database.greendao.Municipio municipiotaguasco = new cu.tecnomatica.android.glp.database.greendao.Municipio();
                municipiotaguasco.setNombre("Taguasco");
                municipiotaguasco.setProvincia(provinciasanctispiritus);
                municipiotaguasco.setActivo(false);
                daoSession.insertOrReplace(municipiotaguasco);

                cu.tecnomatica.android.glp.database.greendao.Municipio municipiolasierpe = new cu.tecnomatica.android.glp.database.greendao.Municipio();
                municipiolasierpe.setNombre("La Sierpe");
                municipiolasierpe.setProvincia(provinciasanctispiritus);
                municipiolasierpe.setActivo(false);
                daoSession.insertOrReplace(municipiolasierpe);

                cu.tecnomatica.android.glp.database.greendao.Municipio municipiotrinidad = new cu.tecnomatica.android.glp.database.greendao.Municipio();
                municipiotrinidad.setNombre("Trinidad");
                municipiotrinidad.setProvincia(provinciasanctispiritus);
                municipiotrinidad.setActivo(false);
                daoSession.insertOrReplace(municipiotrinidad);

                //CASAS COMERCIALES //PROVINCIA SANCTI SPÍRITUS

                cu.tecnomatica.android.glp.database.greendao.Casacomercial casacomercialsanctispiritus = new cu.tecnomatica.android.glp.database.greendao.Casacomercial();

                casacomercialsanctispiritus.setIdmunicipio(municipiosanctispiritus.getIdmunicipio());
                casacomercialsanctispiritus.setMunicipio(municipiosanctispiritus);
                casacomercialsanctispiritus.setDireccion("Bartolomé Masó # 267 Esquina Santa Elena");
                casacomercialsanctispiritus.setTelefono("41328255 Y 41328228");
                casacomercialsanctispiritus.setHorario("Lunes a Jueves de 7:00am a 11:00pm y de 11:30pm a 4:30pm. Viernes 7:00am a 11:00pm y de 11:30pm a 3:30pm");
                casacomercialsanctispiritus.setNombre("CC Sancti Spíritus");
                casacomercialsanctispiritus.setLatitud("21.93703");
                casacomercialsanctispiritus.setLongitud("-79.441078");
                daoSession.insertOrReplace(casacomercialsanctispiritus);

                cu.tecnomatica.android.glp.database.greendao.Casacomercial casacomercialtrinidad = new cu.tecnomatica.android.glp.database.greendao.Casacomercial();

                casacomercialtrinidad.setIdmunicipio(municipiotrinidad.getIdmunicipio());
                casacomercialtrinidad.setMunicipio(municipiotrinidad);
                casacomercialtrinidad.setDireccion("Bartolomé Masó # 267 Esquina Santa Elena");
                casacomercialtrinidad.setTelefono("41328255 Y 41328228");
                casacomercialtrinidad.setHorario("Lunes a Jueves de 7:00am a 11:00pm y de 11:30pm a 4:30pm. Viernes 7:00am a 11:00pm y de 11:30pm a 3:30pm");
                casacomercialtrinidad.setNombre("CC Sancti Spíritus");
                casacomercialtrinidad.setLatitud("21.93703");
                casacomercialtrinidad.setLongitud("-79.441078");
                daoSession.insertOrReplace(casacomercialtrinidad);

                cu.tecnomatica.android.glp.database.greendao.Casacomercial casacomercialtaguasco = new cu.tecnomatica.android.glp.database.greendao.Casacomercial();

                casacomercialtaguasco.setIdmunicipio(municipiotaguasco.getIdmunicipio());
                casacomercialtaguasco.setMunicipio(municipiotaguasco);
                casacomercialtaguasco.setDireccion("Bartolomé Masó # 267 Esquina Santa Elena");
                casacomercialtaguasco.setTelefono("41328255 Y 41328228");
                casacomercialtaguasco.setHorario("Lunes a Jueves de 7:00am a 11:00pm y de 11:30pm a 4:30pm. Viernes 7:00am a 11:00pm y de 11:30pm a 3:30pm");
                casacomercialtaguasco.setNombre("CC Sancti Spíritus");
                casacomercialtaguasco.setLatitud("21.93703");
                casacomercialtaguasco.setLongitud("-79.441078");
                daoSession.insertOrReplace(casacomercialtaguasco);

                cu.tecnomatica.android.glp.database.greendao.Casacomercial casacomerciallasierpe = new cu.tecnomatica.android.glp.database.greendao.Casacomercial();

                casacomerciallasierpe.setIdmunicipio(municipiolasierpe.getIdmunicipio());
                casacomerciallasierpe.setMunicipio(municipiolasierpe);
                casacomerciallasierpe.setDireccion("Bartolomé Masó # 267 Esquina Santa Elena");
                casacomerciallasierpe.setTelefono("41328255 Y 41328228");
                casacomerciallasierpe.setHorario("Lunes a Jueves de 7:00am a 11:00pm y de 11:30pm a 4:30pm. Viernes 7:00am a 11:00pm y de 11:30pm a 3:30pm");
                casacomerciallasierpe.setNombre("CC Sancti Spíritus");
                casacomerciallasierpe.setLatitud("21.93703");
                casacomerciallasierpe.setLongitud("-79.441078");
                daoSession.insertOrReplace(casacomerciallasierpe);

                //PUNTOS DE VENTA //PROVINCIA SANCTI SPÍRITUS

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventasanctispiritus1 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventasanctispiritus1.setCodigo("SanctiSpíritus1");
                puntoventasanctispiritus1.setMunicipio(municipiosanctispiritus);
                puntoventasanctispiritus1.setIdmunicipio(municipiosanctispiritus.getIdmunicipio());
                puntoventasanctispiritus1.setDireccion("Calle 12 e/ Calle 9 y Calle13");
                puntoventasanctispiritus1.setLatitud("21.936843");
                puntoventasanctispiritus1.setLongitud("-79.452352");
                puntoventasanctispiritus1.setHorario("Lunes a Viernes de 8:00am a 2:00pm");
                puntoventasanctispiritus1.setTelefono("No Tiene");
                puntoventasanctispiritus1.setNombre("Camino de la Habana");
                daoSession.insertOrReplace(puntoventasanctispiritus1);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventasanctispiritus2 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventasanctispiritus2.setCodigo("SanctiSpíritus2");
                puntoventasanctispiritus2.setMunicipio(municipiosanctispiritus);
                puntoventasanctispiritus2.setIdmunicipio(municipiosanctispiritus.getIdmunicipio());
                puntoventasanctispiritus2.setDireccion("San Román Final. Reparto Román");
                puntoventasanctispiritus2.setLatitud("21.941547");
                puntoventasanctispiritus2.setLongitud("-79.44216");
                puntoventasanctispiritus2.setHorario("Lunes a Viernes de 8:00am a 2:00pm");
                puntoventasanctispiritus2.setTelefono("41336699");
                puntoventasanctispiritus2.setNombre("Garaita");
                daoSession.insertOrReplace(puntoventasanctispiritus2);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventasanctispiritus3 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventasanctispiritus3.setCodigo("SanctiSpíritus3");
                puntoventasanctispiritus3.setMunicipio(municipiosanctispiritus);
                puntoventasanctispiritus3.setIdmunicipio(municipiosanctispiritus.getIdmunicipio());
                puntoventasanctispiritus3.setDireccion("Raimundo de Pisa Final. Olivos I");
                puntoventasanctispiritus3.setLatitud("21.931638");
                puntoventasanctispiritus3.setLongitud("-79.429353");
                puntoventasanctispiritus3.setHorario("Lunes a Viernes de 8:00am a 2:00pm");
                puntoventasanctispiritus3.setTelefono("No Tiene");
                puntoventasanctispiritus3.setNombre("Olivos 1");
                daoSession.insertOrReplace(puntoventasanctispiritus3);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventasanctispiritus4 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventasanctispiritus4.setCodigo("SanctiSpíritus4");
                puntoventasanctispiritus4.setMunicipio(municipiosanctispiritus);
                puntoventasanctispiritus4.setIdmunicipio(municipiosanctispiritus.getIdmunicipio());
                puntoventasanctispiritus4.setDireccion("1ra del Oeste e/ 3ra de Oeste y Callle B. Colón");
                puntoventasanctispiritus4.setLatitud("21.921365");
                puntoventasanctispiritus4.setLongitud("-79.447816");
                puntoventasanctispiritus4.setHorario("Lunes a Viernes de 8:00am a 2:00pm");
                puntoventasanctispiritus4.setTelefono("No Tiene");
                puntoventasanctispiritus4.setNombre("Colón");
                daoSession.insertOrReplace(puntoventasanctispiritus4);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventasanctispiritus5 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventasanctispiritus5.setCodigo("SanctiSpíritus5");
                puntoventasanctispiritus5.setMunicipio(municipiosanctispiritus);
                puntoventasanctispiritus5.setIdmunicipio(municipiosanctispiritus.getIdmunicipio());
                puntoventasanctispiritus5.setDireccion("Frente al Edificio 18");
                puntoventasanctispiritus5.setLatitud("21.938818");
                puntoventasanctispiritus5.setLongitud("-79.432497");
                puntoventasanctispiritus5.setHorario("Lunes a Viernes de 8:00am a 2:00pm");
                puntoventasanctispiritus5.setTelefono("41328249");
                puntoventasanctispiritus5.setNombre("Olivos 2");
                daoSession.insertOrReplace(puntoventasanctispiritus5);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventasanctispiritus6 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventasanctispiritus6.setCodigo("SanctiSpíritus6");
                puntoventasanctispiritus6.setMunicipio(municipiosanctispiritus);
                puntoventasanctispiritus6.setIdmunicipio(municipiosanctispiritus.getIdmunicipio());
                puntoventasanctispiritus6.setDireccion("Placita zona de Desarrollo Huerto Escolar. Kilo 12");
                puntoventasanctispiritus6.setLatitud("21.947372");
                puntoventasanctispiritus6.setLongitud("-79.449292");
                puntoventasanctispiritus6.setHorario("Lunes a Viernes de 8:00am a 2:00pm");
                puntoventasanctispiritus6.setTelefono("No Tiene");
                puntoventasanctispiritus6.setNombre("Huerto Escolar 2");
                daoSession.insertOrReplace(puntoventasanctispiritus6);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventasanctispiritus7 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventasanctispiritus7.setCodigo("SanctiSpíritus7");
                puntoventasanctispiritus7.setMunicipio(municipiosanctispiritus);
                puntoventasanctispiritus7.setIdmunicipio(municipiosanctispiritus.getIdmunicipio());
                puntoventasanctispiritus7.setDireccion("Raimundo Interior. Jesús María");
                puntoventasanctispiritus7.setLatitud("21.92687");
                puntoventasanctispiritus7.setLongitud("-79.437647");
                puntoventasanctispiritus7.setHorario("Lunes a Viernes de 8:00am a 2:00pm");
                puntoventasanctispiritus7.setTelefono("No Tiene");
                puntoventasanctispiritus7.setNombre("Jesús María");
                daoSession.insertOrReplace(puntoventasanctispiritus7);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventasanctispiritus8 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventasanctispiritus8.setCodigo("SanctiSpíritus8");
                puntoventasanctispiritus8.setMunicipio(municipiotaguasco);
                puntoventasanctispiritus8.setIdmunicipio(municipiotaguasco.getIdmunicipio());
                puntoventasanctispiritus8.setDireccion("Carretera Fábrica de Cemento");
                puntoventasanctispiritus8.setLatitud("21.99639");
                puntoventasanctispiritus8.setLongitud("-79.30452");
                puntoventasanctispiritus8.setHorario("Lunes a viernes de 7:00am a 11:00am y de 12:00pm a 5:00pm");
                puntoventasanctispiritus8.setTelefono("No Tiene");
                puntoventasanctispiritus8.setNombre("Siguaney");
                daoSession.insertOrReplace(puntoventasanctispiritus8);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventasanctispiritus9 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventasanctispiritus9.setCodigo("SanctiSpíritus9");
                puntoventasanctispiritus9.setMunicipio(municipiolasierpe);
                puntoventasanctispiritus9.setIdmunicipio(municipiolasierpe.getIdmunicipio());
                puntoventasanctispiritus9.setDireccion("Avenina #2");
                puntoventasanctispiritus9.setLatitud("21.770392");
                puntoventasanctispiritus9.setLongitud("-79.271115");
                puntoventasanctispiritus9.setHorario("Lunes a viernes de 7:00am a 11:00am y de 12:00pm a 5:00pm");
                puntoventasanctispiritus9.setTelefono("No Tiene");
                puntoventasanctispiritus9.setNombre("La Sierpe");
                daoSession.insertOrReplace(puntoventasanctispiritus9);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventasanctispiritus10 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventasanctispiritus10.setCodigo("SanctiSpíritus10");
                puntoventasanctispiritus10.setMunicipio(municipiotrinidad);
                puntoventasanctispiritus10.setIdmunicipio(municipiotrinidad.getIdmunicipio());
                puntoventasanctispiritus10.setDireccion("Frank País. Reparto Armando Mestre");
                puntoventasanctispiritus10.setLatitud("21.793433");
                puntoventasanctispiritus10.setLongitud("-79.981485");
                puntoventasanctispiritus10.setHorario("Lunes a viernes de 7:00am a 11:00am y de 12:00pm a 5:00pm");
                puntoventasanctispiritus10.setTelefono("41996625");
                puntoventasanctispiritus10.setNombre("Trinidad");
                daoSession.insertOrReplace(puntoventasanctispiritus10);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventasanctispiritus11 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventasanctispiritus11.setCodigo("SanctiSpíritus11");
                puntoventasanctispiritus11.setMunicipio(municipiotrinidad);
                puntoventasanctispiritus11.setIdmunicipio(municipiotrinidad.getIdmunicipio());
                puntoventasanctispiritus11.setDireccion("Jesús Menéndez. Topes de Collante");
                puntoventasanctispiritus11.setLatitud("21.913031");
                puntoventasanctispiritus11.setLongitud("-80.01338");
                puntoventasanctispiritus11.setHorario("Lunes a viernes de 7:00am a 11:00am y de 12:00pm a 5:00pm");
                puntoventasanctispiritus11.setTelefono("42540280");
                puntoventasanctispiritus11.setNombre("Topes de Collante");
                daoSession.insertOrReplace(puntoventasanctispiritus11);


                //PROVINCIA CIEGO DE ÁVILA
                Provincia provinciaciego = new Provincia();
                provinciaciego.setNombre("Ciego de Ávila");
                provinciaciego.setLatitud("21.84198");
                provinciaciego.setLongitud("-78.76011");
                provinciaciego.setActiva(false);
                daoSession.insertOrReplace(provinciaciego);

                Empresacomercializadora empresacomercializadoraciego = new Empresacomercializadora();
                empresacomercializadoraciego.setNombre("Empresa Comercializadora de Combustible Ciego de Ávila");
                empresacomercializadoraciego.setProvincia(provinciaciego);
                empresacomercializadoraciego.setIdprovincia(provinciaciego.getIdprovincia());
                empresacomercializadoraciego.setDireccion("PEDRO MARTINEZ BRITO #201. CANALETA");
                empresacomercializadoraciego.setTelefono("33227648 Y 33227336");
                empresacomercializadoraciego.setHorario("Lunes a Jueves de 7:30am a 12:30pm y de 1:00pm a 5:00pm. Viernes 7:30am a 12:30pm y de 1:00pm a 4:00pm");
                empresacomercializadoraciego.setLatitud("21.843587");
                empresacomercializadoraciego.setLongitud("-78.778241");
                daoSession.insertOrReplace(empresacomercializadoraciego);

                Serviciosmecanicos serviciosmecanicosciego = new Serviciosmecanicos();
                serviciosmecanicosciego.setNombre("Servicios Mecánicos Ciego de Ávila");
                serviciosmecanicosciego.setProvincia(provinciaciego);
                serviciosmecanicosciego.setIdprovincia(provinciaciego.getIdprovincia());
                serviciosmecanicosciego.setDireccion("PEDRO MARTINEZ BRITO #201. CANALETA");
                serviciosmecanicosciego.setTelefono("33228761");
                serviciosmecanicosciego.setHorario("Lunes a Jueves de 7:30am a 12:30pm y de 1:00pm a 5:00pm. Viernes 7:30am a 12:30pm y de 1:00pm a 4:00pm");
                serviciosmecanicosciego.setLatitud("21.843587");
                serviciosmecanicosciego.setLongitud("-78.778241");
                daoSession.insertOrReplace(serviciosmecanicosciego);

                Atencionclientes atencionclientesciego = new Atencionclientes();
                atencionclientesciego.setNombre("Oficina de Atención al Cliente Ciego de Ávila");
                atencionclientesciego.setProvincia(provinciaciego);
                atencionclientesciego.setIdprovincia(provinciaciego.getIdprovincia());
                atencionclientesciego.setDireccion("PEDRO MARTINEZ BRITO #201. CANALETA");
                atencionclientesciego.setTelefono("33212420");
                atencionclientesciego.setHorario("Lunes a Jueves de 7:30am a 12:30pm y de 1:00pm a 5:00pm. Viernes 7:30am a 12:30pm y de 1:00pm a 4:00pm");
                atencionclientesciego.setLatitud("21.843587");
                atencionclientesciego.setLongitud("-78.778241");
                daoSession.insertOrReplace(atencionclientesciego);

                //MUNICIPIOS //PROVINCIA CIEGO DE ÁVILA

                cu.tecnomatica.android.glp.database.greendao.Municipio municipiociego = new cu.tecnomatica.android.glp.database.greendao.Municipio();
                municipiociego.setNombre("Ciego de Ávila");
                municipiociego.setProvincia(provinciaciego);
                municipiociego.setActivo(true);
                daoSession.insertOrReplace(municipiociego);

                cu.tecnomatica.android.glp.database.greendao.Municipio municipiovenezuela = new cu.tecnomatica.android.glp.database.greendao.Municipio();
                municipiovenezuela.setNombre("Venezuela");
                municipiovenezuela.setProvincia(provinciaciego);
                municipiovenezuela.setActivo(false);
                daoSession.insertOrReplace(municipiovenezuela);

                cu.tecnomatica.android.glp.database.greendao.Municipio municipiomoron = new cu.tecnomatica.android.glp.database.greendao.Municipio();
                municipiomoron.setNombre("Morón");
                municipiomoron.setProvincia(provinciaciego);
                municipiomoron.setActivo(false);
                daoSession.insertOrReplace(municipiomoron);

                cu.tecnomatica.android.glp.database.greendao.Municipio municipiociroredondo = new cu.tecnomatica.android.glp.database.greendao.Municipio();
                municipiociroredondo.setNombre("Ciro Redondo");
                municipiociroredondo.setProvincia(provinciaciego);
                municipiociroredondo.setActivo(false);
                daoSession.insertOrReplace(municipiociroredondo);

                //CASAS COMERCIALES //PROVINCIA CIEGO DE ÁVILA

                cu.tecnomatica.android.glp.database.greendao.Casacomercial casacomercialciego = new cu.tecnomatica.android.glp.database.greendao.Casacomercial();

                casacomercialciego.setIdmunicipio(municipiociego.getIdmunicipio());
                casacomercialciego.setMunicipio(municipiociego);
                casacomercialciego.setDireccion("PEDRO MARTINEZ BRITO #201. CANALETA");
                casacomercialciego.setTelefono("33228761");
                casacomercialciego.setHorario("Lunes a Jueves de 7:30am a 12:30pm y de 1:00pm a 5:00pm. Viernes 7:30am a 12:30pm y de 1:00pm a 4:00pm");
                casacomercialciego.setNombre("CC Ciego de Ávila");
                casacomercialciego.setLatitud("21.843587");
                casacomercialciego.setLongitud("-78.778241");
                daoSession.insertOrReplace(casacomercialciego);

                cu.tecnomatica.android.glp.database.greendao.Casacomercial casacomercialvenezuela = new cu.tecnomatica.android.glp.database.greendao.Casacomercial();

                casacomercialvenezuela.setIdmunicipio(municipiovenezuela.getIdmunicipio());
                casacomercialvenezuela.setMunicipio(municipiovenezuela);
                casacomercialvenezuela.setDireccion("PEDRO MARTINEZ BRITO #201. CANALETA");
                casacomercialvenezuela.setTelefono("33228761");
                casacomercialvenezuela.setHorario("Lunes a Jueves de 7:30am a 12:30pm y de 1:00pm a 5:00pm. Viernes 7:30am a 12:30pm y de 1:00pm a 4:00pm");
                casacomercialvenezuela.setNombre("CC Ciego de Ávila");
                casacomercialvenezuela.setLatitud("21.843587");
                casacomercialvenezuela.setLongitud("-78.778241");
                daoSession.insertOrReplace(casacomercialvenezuela);

                cu.tecnomatica.android.glp.database.greendao.Casacomercial casacomercialmoron = new cu.tecnomatica.android.glp.database.greendao.Casacomercial();

                casacomercialmoron.setIdmunicipio(municipiomoron.getIdmunicipio());
                casacomercialmoron.setMunicipio(municipiomoron);
                casacomercialmoron.setDireccion("PEDRO MARTINEZ BRITO #201. CANALETA");
                casacomercialmoron.setTelefono("33228761");
                casacomercialmoron.setHorario("Lunes a Jueves de 7:30am a 12:30pm y de 1:00pm a 5:00pm. Viernes 7:30am a 12:30pm y de 1:00pm a 4:00pm");
                casacomercialmoron.setNombre("CC Ciego de Ávila");
                casacomercialmoron.setLatitud("21.843587");
                casacomercialmoron.setLongitud("-78.778241");
                daoSession.insertOrReplace(casacomercialmoron);

                cu.tecnomatica.android.glp.database.greendao.Casacomercial casacomercialciroredondo = new cu.tecnomatica.android.glp.database.greendao.Casacomercial();

                casacomercialciroredondo.setIdmunicipio(municipiociroredondo.getIdmunicipio());
                casacomercialciroredondo.setMunicipio(municipiociroredondo);
                casacomercialciroredondo.setDireccion("PEDRO MARTINEZ BRITO #201. CANALETA");
                casacomercialciroredondo.setTelefono("33228761");
                casacomercialciroredondo.setHorario("Lunes a Jueves de 7:30am a 12:30pm y de 1:00pm a 5:00pm. Viernes 7:30am a 12:30pm y de 1:00pm a 4:00pm");
                casacomercialciroredondo.setNombre("CC Ciego de Ávila");
                casacomercialciroredondo.setLatitud("21.843587");
                casacomercialciroredondo.setLongitud("-78.778241");
                daoSession.insertOrReplace(casacomercialciroredondo);

                //PUNTOS DE VENTA //PROVINCIA CIEGO DE ÁVILA

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventaciego1 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventaciego1.setCodigo("CiegodeÁvila1");
                puntoventaciego1.setMunicipio(municipiociego);
                puntoventaciego1.setIdmunicipio(municipiociego.getIdmunicipio());
                puntoventaciego1.setDireccion("MICRO C");
                puntoventaciego1.setLatitud("21.84955");
                puntoventaciego1.setLongitud("-78.774419");
                puntoventaciego1.setHorario("8:00am  a 3.00pm lunes, miercoles, viernes y sabados alternos, mates y jueves de 7:00am a 11:00am y de 3.30pm a 6:00pm");
                puntoventaciego1.setTelefono("33212758");
                puntoventaciego1.setNombre("MICRO C");
                daoSession.insertOrReplace(puntoventaciego1);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventaciego2 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventaciego2.setCodigo("CiegodeÁvila2");
                puntoventaciego2.setMunicipio(municipiociego);
                puntoventaciego2.setIdmunicipio(municipiociego.getIdmunicipio());
                puntoventaciego2.setDireccion("EDUARDO MÁRMOL S/N");
                puntoventaciego2.setLatitud("21.841388");
                puntoventaciego2.setLongitud("-78.748924");
                puntoventaciego2.setHorario("8:00am  a 3.00pm lunes, miercoles, viernes y sabados alternos, mates y jueves de 7:00am a 11:00am y de 3.30pm a 6:00pm");
                puntoventaciego2.setTelefono("33212751");
                puntoventaciego2.setNombre("MICRO A-1");
                daoSession.insertOrReplace(puntoventaciego2);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventaciego3 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventaciego3.setCodigo("CiegodeÁvila3");
                puntoventaciego3.setMunicipio(municipiociego);
                puntoventaciego3.setIdmunicipio(municipiociego.getIdmunicipio());
                puntoventaciego3.setDireccion("PASAJE 6TA S/N");
                puntoventaciego3.setLatitud("21.846544");
                puntoventaciego3.setLongitud("-78.74857");
                puntoventaciego3.setHorario("8:00am  a 3.00pm lunes, miercoles, viernes y sabados alternos, mates y jueves de 7:00am a 11:00am y de 3.30pm a 6:00pm");
                puntoventaciego3.setTelefono("33212749");
                puntoventaciego3.setNombre("MICRO A-2");
                daoSession.insertOrReplace(puntoventaciego3);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventaciego4 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventaciego4.setCodigo("CiegodeÁvila4");
                puntoventaciego4.setMunicipio(municipiociego);
                puntoventaciego4.setIdmunicipio(municipiociego.getIdmunicipio());
                puntoventaciego4.setDireccion("CALLE MARCIAL GÓMEZ S/N / BENAVIDES Y CHICHO TORRES");
                puntoventaciego4.setLatitud("21.846404");
                puntoventaciego4.setLongitud("-78.757866");
                puntoventaciego4.setHorario("8:00am  a 3.00pm lunes, miercoles, viernes y sabados alternos, mates y jueves de 7:00am a 11:00am y de 3.30pm a 6:00pm");
                puntoventaciego4.setTelefono("33206868");
                puntoventaciego4.setNombre("MARCIAL GÓMEZ");
                daoSession.insertOrReplace(puntoventaciego4);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventaciego5 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventaciego5.setCodigo("CiegodeÁvila5");
                puntoventaciego5.setMunicipio(municipiociego);
                puntoventaciego5.setIdmunicipio(municipiociego.getIdmunicipio());
                puntoventaciego5.setDireccion("CALLE MACEO S/N /CUBA Y CIEGO DE AVILA");
                puntoventaciego5.setLatitud("21.838781");
                puntoventaciego5.setLongitud("-78.76313");
                puntoventaciego5.setHorario("8:00am  a 3.00pm lunes, miercoles, viernes y sabados alternos, mates y jueves de 7:00am a 11:00am y de 3.30pm a 6:00pm");
                puntoventaciego5.setTelefono("33219711");
                puntoventaciego5.setNombre("MACEO");
                daoSession.insertOrReplace(puntoventaciego5);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventaciego7 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventaciego7.setCodigo("CiegodeÁvila7");
                puntoventaciego7.setMunicipio(municipiociego);
                puntoventaciego7.setIdmunicipio(municipiociego.getIdmunicipio());
                puntoventaciego7.setDireccion("CALLE 1RA / J y F");
                puntoventaciego7.setLatitud("21.83199");
                puntoventaciego7.setLongitud("-78.771513");
                puntoventaciego7.setHorario("8:00am  a 3.00pm lunes, miercoles, viernes y sabados alternos, mates y jueves de 7:00am a 11:00am y de 3.30pm a 6:00pm");
                puntoventaciego7.setTelefono("33208809");
                puntoventaciego7.setNombre("LUGONES");
                daoSession.insertOrReplace(puntoventaciego7);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventaciego8 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventaciego8.setCodigo("CiegodeÁvila8");
                puntoventaciego8.setMunicipio(municipiovenezuela);
                puntoventaciego8.setIdmunicipio(municipiovenezuela.getIdmunicipio());
                puntoventaciego8.setDireccion("CALLE 9NA/ 34 Y 36");
                puntoventaciego8.setLatitud("21.742495");
                puntoventaciego8.setLongitud("-78.79685");
                puntoventaciego8.setHorario("lunes y viernes de 8:30 a 2:30pm");
                puntoventaciego8.setTelefono("No Tiene");
                puntoventaciego8.setNombre("Venezuela");
                daoSession.insertOrReplace(puntoventaciego8);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventaciego9 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventaciego9.setCodigo("CiegodeÁvila9");
                puntoventaciego9.setMunicipio(municipiomoron);
                puntoventaciego9.setIdmunicipio(municipiomoron.getIdmunicipio());
                puntoventaciego9.setDireccion("CALLE LIBERTAD S/N e/ MARCIAL GOMEZ Y SALOME MACHADO");
                puntoventaciego9.setLatitud("22.112235");
                puntoventaciego9.setLongitud("-78.634438");
                puntoventaciego9.setHorario("lunes 7:30-12:00 Y 12:30-5:00. martes7:30-12:30. miercoles7:00-12 Y 1:00-5:00. jueves7:30-12:30 Y 1:00- 5:00. viernes 7:00-12:00");
                puntoventaciego9.setTelefono("33502290");
                puntoventaciego9.setNombre("Morón");
                daoSession.insertOrReplace(puntoventaciego9);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventaciego10 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventaciego10.setCodigo("CiegodeÁvila10");
                puntoventaciego10.setMunicipio(municipiociroredondo);
                puntoventaciego10.setIdmunicipio(municipiociroredondo.getIdmunicipio());
                puntoventaciego10.setDireccion("CALLE QUINTIN BANDERA S/N / RAFAEL PARDO Y AVENIDA DE LOS PATRIOTAS");
                puntoventaciego10.setLatitud("22.015973");
                puntoventaciego10.setLongitud("-78.708601");
                puntoventaciego10.setHorario("lunes y viernes 8:00am a 2:00pm. miércoles de 1:00pm a 7:00pm");
                puntoventaciego10.setTelefono("33536626");
                puntoventaciego10.setNombre("Pina Oeste");
                daoSession.insertOrReplace(puntoventaciego10);


                //PROVINCIA CAMAGUEY
                Provincia provinciacamaguey = new Provincia();
                provinciacamaguey.setNombre("Camaguey");
                provinciacamaguey.setLatitud("21.37896");
                provinciacamaguey.setLongitud("-77.91841");
                provinciacamaguey.setActiva(false);
                daoSession.insertOrReplace(provinciacamaguey);

                Empresacomercializadora empresacomercializadoracamaguey = new Empresacomercializadora();
                empresacomercializadoracamaguey.setNombre("Empresa Comercializadora de Combustible Camaguey");
                empresacomercializadoracamaguey.setProvincia(provinciacamaguey);
                empresacomercializadoracamaguey.setIdprovincia(provinciacamaguey.getIdprovincia());
                empresacomercializadoracamaguey.setDireccion("Ave B e/ Carretera Central Este Km 4 1/2 y Calle C. Alturas de Jayamá. Camaguey");
                empresacomercializadoracamaguey.setTelefono("32272826");
                empresacomercializadoracamaguey.setHorario("Lunes a Jueves 8:00am a 5:30pm. Viernes de 8:00am a 4:30pm");
                empresacomercializadoracamaguey.setLatitud("21.361498");
                empresacomercializadoracamaguey.setLongitud("-77.882841");
                daoSession.insertOrReplace(empresacomercializadoracamaguey);

                Serviciosmecanicos serviciosmecanicoscamaguey = new Serviciosmecanicos();
                serviciosmecanicoscamaguey.setNombre("Servicios Mecánicos Camaguey");
                serviciosmecanicoscamaguey.setProvincia(provinciacamaguey);
                serviciosmecanicoscamaguey.setIdprovincia(provinciacamaguey.getIdprovincia());
                serviciosmecanicoscamaguey.setDireccion("Camino Maraguán Km 2 ½ Camagüey");
                serviciosmecanicoscamaguey.setTelefono("32272826 Ext-19182, 52795428");
                serviciosmecanicoscamaguey.setHorario("Lunes a Jueves 8:00am a 5:30pm. Viernes 8:00am a 4:30pm");
                serviciosmecanicoscamaguey.setLatitud("21.355692");
                serviciosmecanicoscamaguey.setLongitud("-77.850311");
                daoSession.insertOrReplace(serviciosmecanicoscamaguey);

                Atencionclientes atencionclientescamaguey = new Atencionclientes();
                atencionclientescamaguey.setNombre("Oficina de Atención al Camaguey");
                atencionclientescamaguey.setProvincia(provinciacamaguey);
                atencionclientescamaguey.setIdprovincia(provinciacamaguey.getIdprovincia());
                atencionclientescamaguey.setDireccion("Ave B e/ Carretera Central Este Km 4 1/2 y Calle C. Alturas de Jayamá. Camaguey");
                atencionclientescamaguey.setTelefono("32273783, 32272826 Ext-19080-19083, 52700778");
                atencionclientescamaguey.setHorario("Lunes a Jueves 8:00am a 5:30pm. Viernes 8:00am a 4:30pm");
                atencionclientescamaguey.setLatitud("21.361498");
                atencionclientescamaguey.setLongitud("-77.882841");
                daoSession.insertOrReplace(atencionclientescamaguey);

                //MUNICIPIOS //PROVINCIA CAMAGUEY

                cu.tecnomatica.android.glp.database.greendao.Municipio municipiocamaguey = new cu.tecnomatica.android.glp.database.greendao.Municipio();
                municipiocamaguey.setNombre("Camaguey");
                municipiocamaguey.setProvincia(provinciacamaguey);
                municipiocamaguey.setActivo(true);
                daoSession.insertOrReplace(municipiocamaguey);

                cu.tecnomatica.android.glp.database.greendao.Municipio municipionuevitas = new cu.tecnomatica.android.glp.database.greendao.Municipio();
                municipionuevitas.setNombre("Nuevitas");
                municipionuevitas.setProvincia(provinciacamaguey);
                municipionuevitas.setActivo(false);
                daoSession.insertOrReplace(municipionuevitas);

                //CASAS COMERCIALES //PROVINCIA CAMAGUEY

                cu.tecnomatica.android.glp.database.greendao.Casacomercial casacomercialcamaguey = new cu.tecnomatica.android.glp.database.greendao.Casacomercial();

                casacomercialcamaguey.setIdmunicipio(municipiocamaguey.getIdmunicipio());
                casacomercialcamaguey.setMunicipio(municipiocamaguey);
                casacomercialcamaguey.setDireccion("Ave B e/ Carretera Central Este Km 4 1/2 y Calle C. Alturas de Jayamá. Camaguey");
                casacomercialcamaguey.setTelefono("32272826");
                casacomercialcamaguey.setHorario("Lunes a Jueves 8.00am a 5.30 pm. Viernes de 8 am a 4.30 pm");
                casacomercialcamaguey.setNombre("CC Camaguey");
                casacomercialcamaguey.setLatitud("21.361498");
                casacomercialcamaguey.setLongitud("-77.882841");
                daoSession.insertOrReplace(casacomercialcamaguey);

                cu.tecnomatica.android.glp.database.greendao.Casacomercial casacomercialnuevitas = new cu.tecnomatica.android.glp.database.greendao.Casacomercial();

                casacomercialnuevitas.setIdmunicipio(municipionuevitas.getIdmunicipio());
                casacomercialnuevitas.setMunicipio(municipionuevitas);
                casacomercialnuevitas.setDireccion("Calle Sánchez Dol No 6 e/ Calixto García y Cienfuegos. Nuevitas");
                casacomercialnuevitas.setTelefono("32412036");
                casacomercialnuevitas.setHorario("Lunes a Viernes 8.00am a 12.00 pm y  3.00pm a 5.20pm. Sábado de 8:00am a 12.00pm");
                casacomercialnuevitas.setNombre("CC Nuevitas");
                casacomercialnuevitas.setLatitud("21.542984");
                casacomercialnuevitas.setLongitud("-77.258336");
                daoSession.insertOrReplace(casacomercialnuevitas);

                //PUNTOS DE VENTA //PROVINCIA CAMAGUEY

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventacamaguey1 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventacamaguey1.setCodigo("Camaguey1");
                puntoventacamaguey1.setMunicipio(municipiocamaguey);
                puntoventacamaguey1.setIdmunicipio(municipiocamaguey.getIdmunicipio());
                puntoventacamaguey1.setDireccion("Calle 10 e/ Calle A y Taller ECOI # 8 Reparto “El Porvenir”. Camagüey");
                puntoventacamaguey1.setLatitud("21.380284");
                puntoventacamaguey1.setLongitud("-77.904533");
                puntoventacamaguey1.setHorario("Lunes a Viernes 8:00am a 12:00pm y 3:00pm a 6:20pm. Sábados de 8:00am a 12m:00pm. Martes 3:00pm a 6:20pm");
                puntoventacamaguey1.setTelefono("32286826 Y 32272826 Ext-19217");
                puntoventacamaguey1.setNombre("Previsora");
                daoSession.insertOrReplace(puntoventacamaguey1);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventacamaguey2 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventacamaguey2.setCodigo("Camaguey2");
                puntoventacamaguey2.setMunicipio(municipiocamaguey);
                puntoventacamaguey2.setIdmunicipio(municipiocamaguey.getIdmunicipio());
                puntoventacamaguey2.setDireccion("Calle 1ta e/ Ave. Van Horne  y Pasaje Álvarez Rpto “La Zambrana");
                puntoventacamaguey2.setLatitud("21.380284");
                puntoventacamaguey2.setLongitud("-77.904533");
                puntoventacamaguey2.setHorario("Lunes a Viernes 8:00am a 12:00pm y 3:00pm a 6:20pm. Sábados de 8:00am a 12m:00pm. Martes 3:00pm a 6:20pm");
                puntoventacamaguey2.setTelefono("32295157 Y 32272826 Ext-19210");
                puntoventacamaguey2.setNombre("La Zambrana");
                daoSession.insertOrReplace(puntoventacamaguey2);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventacamaguey3 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventacamaguey3.setCodigo("Camaguey3");
                puntoventacamaguey3.setMunicipio(municipiocamaguey);
                puntoventacamaguey3.setIdmunicipio(municipiocamaguey.getIdmunicipio());
                puntoventacamaguey3.setDireccion("Esquina e/ calle 2da y Prolongación Calle A Micro distrito “Ignacio Agramante”");
                puntoventacamaguey3.setLatitud("21.401005");
                puntoventacamaguey3.setLongitud("-77.942426");
                puntoventacamaguey3.setHorario("Lunes a Viernes 8:00am a 12:00pm y 3:00pm a 6:20pm. Sábados de 8:00am a 12m:00pm. Martes 3:00pm a 6:20pm");
                puntoventacamaguey3.setTelefono("32292499 Y 32272826 Ext-19209");
                puntoventacamaguey3.setNombre("Planta Mecánica");
                daoSession.insertOrReplace(puntoventacamaguey3);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventacamaguey4 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventacamaguey4.setCodigo("Camaguey4");
                puntoventacamaguey4.setMunicipio(municipiocamaguey);
                puntoventacamaguey4.setIdmunicipio(municipiocamaguey.getIdmunicipio());
                puntoventacamaguey4.setDireccion("Calle Santayana e/ Prolongación de Santayana y Francisquito");
                puntoventacamaguey4.setLatitud("21.390069");
                puntoventacamaguey4.setLongitud("-77.920072");
                puntoventacamaguey4.setHorario("Lunes a Viernes 8:00am a 12:00pm y 3:00pm a 6:20pm. Sábados de 8:00am a 12m:00pm. Martes 3:00pm a 6:20pm");
                puntoventacamaguey4.setTelefono("32296069 Y 32272826 Ext-19215");
                puntoventacamaguey4.setNombre("Santayana");
                daoSession.insertOrReplace(puntoventacamaguey4);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventacamaguey5 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventacamaguey5.setCodigo("Camaguey5");
                puntoventacamaguey5.setMunicipio(municipiocamaguey);
                puntoventacamaguey5.setIdmunicipio(municipiocamaguey.getIdmunicipio());
                puntoventacamaguey5.setDireccion("Calle 4ta e/ I y J Reparto “Victoria de Girón”");
                puntoventacamaguey5.setLatitud("21.403333");
                puntoventacamaguey5.setLongitud("-77.870986");
                puntoventacamaguey5.setHorario("Lunes a Viernes 8:00am a 12:00pm y 3:00pm a 6:20pm. Sábados de 8:00am a 12m:00pm. Martes 3:00pm a 6:20pm");
                puntoventacamaguey5.setTelefono("32263190 Y 32272826 Ext-19208");
                puntoventacamaguey5.setNombre("El Lenin");
                daoSession.insertOrReplace(puntoventacamaguey5);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventacamaguey6 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventacamaguey6.setCodigo("Camaguey6");
                puntoventacamaguey6.setMunicipio(municipiocamaguey);
                puntoventacamaguey6.setIdmunicipio(municipiocamaguey.getIdmunicipio());
                puntoventacamaguey6.setDireccion("Paseo e/ Ave. Mónaco Sur y Ave 1ra Paralela Reparto “Julio A. Mella”");
                puntoventacamaguey6.setLatitud("21.360745");
                puntoventacamaguey6.setLongitud("-77.894089");
                puntoventacamaguey6.setHorario("Lunes a Viernes 8:00am a 12:00pm y 3:00pm a 6:20pm. Sábados de 8:00am a 12m:00pm. Martes 3:00pm a 6:20pm");
                puntoventacamaguey6.setTelefono("32273711 Y 32272826 Ext-19213");
                puntoventacamaguey6.setNombre("Mella 1");
                daoSession.insertOrReplace(puntoventacamaguey6);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventacamaguey7 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventacamaguey7.setCodigo("Camaguey7");
                puntoventacamaguey7.setMunicipio(municipiocamaguey);
                puntoventacamaguey7.setIdmunicipio(municipiocamaguey.getIdmunicipio());
                puntoventacamaguey7.setDireccion("Calle B e/ 1ra Paralela y Vial Acceso Edif. 12 Planta #4 Reparto “Julio A. Mella”");
                puntoventacamaguey7.setLatitud("21.360371");
                puntoventacamaguey7.setLongitud("-77.8899");
                puntoventacamaguey7.setHorario("Lunes a Viernes 8:00am a 12:00pm y 3:00pm a 6:20pm. Sábados de 8:00am a 12m:00pm. Martes 3:00pm a 6:20pm");
                puntoventacamaguey7.setTelefono("32273681 Y 32272826 Ext-19214");
                puntoventacamaguey7.setNombre("Mella 2");
                daoSession.insertOrReplace(puntoventacamaguey7);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventacamaguey8 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventacamaguey8.setCodigo("Camaguey8");
                puntoventacamaguey8.setMunicipio(municipiocamaguey);
                puntoventacamaguey8.setIdmunicipio(municipiocamaguey.getIdmunicipio());
                puntoventacamaguey8.setDireccion("Calle 1ra e/ Calle A y Chino Manuel. Reparto ‘Las Mercedes”");
                puntoventacamaguey8.setLatitud("21.38902");
                puntoventacamaguey8.setLongitud("-77.900776");
                puntoventacamaguey8.setHorario("Lunes a Viernes 8:00am a 12:00pm y 3:00pm a 6:20pm. Sábados de 8:00am a 12m:00pm. Martes 3:00pm a 6:20pm");
                puntoventacamaguey8.setTelefono("32280878 Y 32272826 Ext-19211");
                puntoventacamaguey8.setNombre("Las Mercedes");
                daoSession.insertOrReplace(puntoventacamaguey8);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventacamaguey9 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventacamaguey9.setCodigo("Camaguey9");
                puntoventacamaguey9.setMunicipio(municipiocamaguey);
                puntoventacamaguey9.setIdmunicipio(municipiocamaguey.getIdmunicipio());
                puntoventacamaguey9.setDireccion("Calle Principal Perú e/ Honduras y Guatemala Reparto “El Retiro”");
                puntoventacamaguey9.setLatitud("21.369666");
                puntoventacamaguey9.setLongitud("-77.900776");
                puntoventacamaguey9.setHorario("Lunes a Viernes 8:00am a 12:00pm y 3:00pm a 6:20pm. Sábados de 8:00am a 12m:00pm. Martes 3:00pm a 6:20pm");
                puntoventacamaguey9.setTelefono("32282672 Y 32272826 Ext-19212");
                puntoventacamaguey9.setNombre("El Retiro");
                daoSession.insertOrReplace(puntoventacamaguey9);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventacamaguey10 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventacamaguey10.setCodigo("Camaguey10");
                puntoventacamaguey10.setMunicipio(municipiocamaguey);
                puntoventacamaguey10.setIdmunicipio(municipiocamaguey.getIdmunicipio());
                puntoventacamaguey10.setDireccion("Carretera de Nuevitas km 18 Esquina calle Libertad. Altagracia");
                puntoventacamaguey10.setLatitud("21.445391");
                puntoventacamaguey10.setLongitud("-77.752173");
                puntoventacamaguey10.setHorario("Lunes a Viernes 8:00am a 12:00pm y 3:00pm a 6:20pm. Sábados de 8:00am a 12m:00pm. Martes 3:00pm a 6:20pm");
                puntoventacamaguey10.setTelefono("32272826 Y 32272826 Ext-19206");
                puntoventacamaguey10.setNombre("Altagracia");
                daoSession.insertOrReplace(puntoventacamaguey10);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventacamaguey11 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventacamaguey11.setCodigo("Camaguey11");
                puntoventacamaguey11.setMunicipio(municipiocamaguey);
                puntoventacamaguey11.setIdmunicipio(municipiocamaguey.getIdmunicipio());
                puntoventacamaguey11.setDireccion("Micro del Reparto Puerto Príncipe.");
                puntoventacamaguey11.setLatitud("21.398689");
                puntoventacamaguey11.setLongitud("-77.890833");
                puntoventacamaguey11.setHorario("Lunes a Viernes 8:00am a 12:00pm y 3:00pm a 6:20pm. Sábados de 8:00am a 12m:00pm. Martes 3:00pm a 6:20pm");
                puntoventacamaguey11.setTelefono("32262151 Y 32272826 Ext-19207");
                puntoventacamaguey11.setNombre("Puerto Príncipe");
                daoSession.insertOrReplace(puntoventacamaguey11);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventacamaguey12 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventacamaguey12.setCodigo("Camaguey12");
                puntoventacamaguey12.setMunicipio(municipiocamaguey);
                puntoventacamaguey12.setIdmunicipio(municipiocamaguey.getIdmunicipio());
                puntoventacamaguey12.setDireccion("Capdevila entre 25 de julio y Perrucho Figueredo. La vigía. Camagüey.");
                puntoventacamaguey12.setLatitud("21.39952");
                puntoventacamaguey12.setLongitud("-77.916107");
                puntoventacamaguey12.setHorario("Lunes a Viernes 8:00am a 12:00pm y 3:00pm a 6:20pm. Sábados de 8:00am a 12m:00pm. Martes 3:00pm a 6:20pm");
                puntoventacamaguey12.setTelefono("32251695 ");
                puntoventacamaguey12.setNombre("Plaza Méndez");
                daoSession.insertOrReplace(puntoventacamaguey12);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventacamaguey13 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventacamaguey13.setCodigo("Camaguey13");
                puntoventacamaguey13.setMunicipio(municipiocamaguey);
                puntoventacamaguey13.setIdmunicipio(municipiocamaguey.getIdmunicipio());
                puntoventacamaguey13.setDireccion("Calle 10 e/ Calle A y Taller ECOI # 8 Reparto “El Porvenir”. Camagüey");
                puntoventacamaguey13.setLatitud("21.376011");
                puntoventacamaguey13.setLongitud("-77.906738");
                puntoventacamaguey13.setHorario("Calle.2 No.4 Esquina. 3era Avenida Rpto. Garrido");
                puntoventacamaguey13.setTelefono("32211630");
                puntoventacamaguey13.setNombre("Garrido");
                daoSession.insertOrReplace(puntoventacamaguey13);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventacamaguey14 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventacamaguey14.setCodigo("Camaguey14");
                puntoventacamaguey14.setMunicipio(municipiocamaguey);
                puntoventacamaguey14.setIdmunicipio(municipiocamaguey.getIdmunicipio());
                puntoventacamaguey14.setDireccion("Calle B e/ Camino Vía Principal y Pasaje B. Reparto La Yaba");
                puntoventacamaguey14.setLatitud("21.345838");
                puntoventacamaguey14.setLongitud("-77.918345");
                puntoventacamaguey14.setHorario("Lunes a Viernes 8:00am a 12:00pm y 3:00pm a 6:20pm. Sábados de 8:00am a 12m:00pm. Martes 3:00pm a 6:20pm");
                puntoventacamaguey14.setTelefono("No Tiene");
                puntoventacamaguey14.setNombre("La Yaba");
                daoSession.insertOrReplace(puntoventacamaguey14);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventacamaguey15 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventacamaguey15.setCodigo("Camaguey15");
                puntoventacamaguey15.setMunicipio(municipiocamaguey);
                puntoventacamaguey15.setIdmunicipio(municipiocamaguey.getIdmunicipio());
                puntoventacamaguey15.setDireccion("Calle Palomino e/ Cupey y 3era. Reparto Agramonte");
                puntoventacamaguey15.setLatitud("21.389193");
                puntoventacamaguey15.setLongitud("-77.935733");
                puntoventacamaguey15.setHorario("Lunes a Viernes 8:00am a 12:00pm y 3:00pm a 6:20pm. Sábados de 8:00am a 12m:00pm. Martes 3:00pm a 6:20pm");
                puntoventacamaguey15.setTelefono("32202343");
                puntoventacamaguey15.setNombre("Agramonte");
                daoSession.insertOrReplace(puntoventacamaguey15);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventacamaguey17 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventacamaguey17.setCodigo("Camaguey17");
                puntoventacamaguey17.setMunicipio(municipiocamaguey);
                puntoventacamaguey17.setIdmunicipio(municipiocamaguey.getIdmunicipio());
                puntoventacamaguey17.setDireccion("Calle B  e/ calle 14 y calle 16. Reparto Guernica.");
                puntoventacamaguey17.setLatitud("21.397512");
                puntoventacamaguey17.setLongitud("-77.907954");
                puntoventacamaguey17.setHorario("Lunes a Viernes 8:00am a 12:00pm y 3:00pm a 6:20pm. Sábados de 8:00am a 12m:00pm. Martes 3:00pm a 6:20pm");
                puntoventacamaguey17.setTelefono("32240208");
                puntoventacamaguey17.setNombre("Guernica");
                daoSession.insertOrReplace(puntoventacamaguey17);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventacamaguey16 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventacamaguey16.setCodigo("Camaguey16");
                puntoventacamaguey16.setMunicipio(municipionuevitas);
                puntoventacamaguey16.setIdmunicipio(municipionuevitas.getIdmunicipio());
                puntoventacamaguey16.setDireccion("Calle Principal. Micro #1 Nuevitas");
                puntoventacamaguey16.setLatitud("21.545053");
                puntoventacamaguey16.setLongitud("-77.274175");
                puntoventacamaguey16.setHorario("Lunes a Viernes 8:00am a 12:00pm y 3:00pm a 6:20pm. Sábados de 8:00am a 12m:00pm. Martes 3:00pm a 6:20pm");
                puntoventacamaguey16.setTelefono("32412036 Y 32272826 Ext-19204");
                puntoventacamaguey16.setNombre("Nuevitas");
                daoSession.insertOrReplace(puntoventacamaguey16);

                //PROVINCIA LAS TUNAS
                Provincia provinciatunas = new Provincia();
                provinciatunas.setNombre("Las Tunas");
                provinciatunas.setLatitud("20.95689");
                provinciatunas.setLongitud("-76.95374");
                provinciatunas.setActiva(false);
                daoSession.insertOrReplace(provinciatunas);

                Empresacomercializadora empresacomercializadoralastunas = new Empresacomercializadora();
                empresacomercializadoralastunas.setNombre("División Territorial de Comercialización de Combustible Las Tunas");
                empresacomercializadoralastunas.setProvincia(provinciatunas);
                empresacomercializadoralastunas.setIdprovincia(provinciatunas.getIdprovincia());
                empresacomercializadoralastunas.setDireccion("Carretera Central  km 4 ½. San Antonio. Las Tunas");
                empresacomercializadoralastunas.setTelefono("31381850 Y 31381851");
                empresacomercializadoralastunas.setHorario("Lunes - Jueves 7:30am - 5:00pm. Viernes 7:30am - 4:00pm");
                empresacomercializadoralastunas.setLatitud("20.97937");
                empresacomercializadoralastunas.setLongitud("-76.995602");
                daoSession.insertOrReplace(empresacomercializadoralastunas);

                Serviciosmecanicos serviciosmecanicoslastunas = new Serviciosmecanicos();
                serviciosmecanicoslastunas.setNombre("Servicios Mécanicos Las Tunas");
                serviciosmecanicoslastunas.setProvincia(provinciatunas);
                serviciosmecanicoslastunas.setIdprovincia(provinciatunas.getIdprovincia());
                serviciosmecanicoslastunas.setDireccion("Calle Martí no 29 esq Fernando Suárez, Reparto Primero. Las Tunas");
                serviciosmecanicoslastunas.setTelefono("31346954");
                serviciosmecanicoslastunas.setHorario("Lunes - Jueves 7:30am - 5:00pm. Viernes 7:30am - 4:00pm");
                serviciosmecanicoslastunas.setLatitud("20.963498");
                serviciosmecanicoslastunas.setLongitud("-76.95009");
                daoSession.insertOrReplace(serviciosmecanicoslastunas);

                Atencionclientes atencionclienteslastunas = new Atencionclientes();
                atencionclienteslastunas.setNombre("Oficina de Atención al Cliente Las Tunas");
                atencionclienteslastunas.setProvincia(provinciatunas);
                atencionclienteslastunas.setIdprovincia(provinciatunas.getIdprovincia());
                atencionclienteslastunas.setDireccion("Calle Martí no 29 esq Fernando Suárez, Reparto Primero. Las Tunas");
                atencionclienteslastunas.setTelefono("31346959");
                atencionclienteslastunas.setHorario("Lunes - Jueves 7:30am - 5:00pm. Viernes 7:30am - 4:00pm");
                atencionclienteslastunas.setLatitud("20.963498");
                atencionclienteslastunas.setLongitud("-76.95009");
                daoSession.insertOrReplace(atencionclienteslastunas);

                //MUNICIPIOS //PROVINCIA LAS TUNAS

                cu.tecnomatica.android.glp.database.greendao.Municipio municipiolastunas = new cu.tecnomatica.android.glp.database.greendao.Municipio();
                municipiolastunas.setNombre("Las Tunas");
                municipiolastunas.setProvincia(provinciatunas);
                municipiolastunas.setActivo(true);
                daoSession.insertOrReplace(municipiolastunas);

                cu.tecnomatica.android.glp.database.greendao.Municipio municipiojesusmenendez = new cu.tecnomatica.android.glp.database.greendao.Municipio();
                municipiojesusmenendez.setNombre("Jesús Menéndez");
                municipiojesusmenendez.setProvincia(provinciatunas);
                municipiojesusmenendez.setActivo(false);
                daoSession.insertOrReplace(municipiojesusmenendez);

                cu.tecnomatica.android.glp.database.greendao.Municipio municipiopuertopadre = new cu.tecnomatica.android.glp.database.greendao.Municipio();
                municipiopuertopadre.setNombre("Puerto Padre");
                municipiopuertopadre.setProvincia(provinciatunas);
                municipiopuertopadre.setActivo(false);
                daoSession.insertOrReplace(municipiopuertopadre);

                cu.tecnomatica.android.glp.database.greendao.Municipio municipiomanati = new cu.tecnomatica.android.glp.database.greendao.Municipio();
                municipiomanati.setNombre("Manatí");
                municipiomanati.setProvincia(provinciatunas);
                municipiomanati.setActivo(false);
                daoSession.insertOrReplace(municipiomanati);

                cu.tecnomatica.android.glp.database.greendao.Municipio municipiomajibacoa = new cu.tecnomatica.android.glp.database.greendao.Municipio();
                municipiomajibacoa.setNombre("Majibacoa");
                municipiomajibacoa.setProvincia(provinciatunas);
                municipiomajibacoa.setActivo(false);
                daoSession.insertOrReplace(municipiomajibacoa);

                cu.tecnomatica.android.glp.database.greendao.Municipio municipiojobabo = new cu.tecnomatica.android.glp.database.greendao.Municipio();
                municipiojobabo.setNombre("Jobabo");
                municipiojobabo.setProvincia(provinciatunas);
                municipiojobabo.setActivo(false);
                daoSession.insertOrReplace(municipiojobabo);

                cu.tecnomatica.android.glp.database.greendao.Municipio municipiocolombia = new cu.tecnomatica.android.glp.database.greendao.Municipio();
                municipiocolombia.setNombre("Colombia");
                municipiocolombia.setProvincia(provinciatunas);
                municipiocolombia.setActivo(false);
                daoSession.insertOrReplace(municipiocolombia);

                cu.tecnomatica.android.glp.database.greendao.Municipio municipioamancio = new cu.tecnomatica.android.glp.database.greendao.Municipio();
                municipioamancio.setNombre("Amancio");
                municipioamancio.setProvincia(provinciatunas);
                municipioamancio.setActivo(false);
                daoSession.insertOrReplace(municipioamancio);

                //CASAS COMERCIALES //PROVINCIA LAS TUNAS

                cu.tecnomatica.android.glp.database.greendao.Casacomercial casacomerciallastunas = new cu.tecnomatica.android.glp.database.greendao.Casacomercial();

                casacomerciallastunas.setIdmunicipio(municipiolastunas.getIdmunicipio());
                casacomerciallastunas.setMunicipio(municipiolastunas);
                casacomerciallastunas.setDireccion("Calle Martí no 29 esq Fernando Suárez, Reparto Primero. Las Tunas");
                casacomerciallastunas.setTelefono("31346954");
                casacomerciallastunas.setHorario("Lunes - Jueves 7:30am - 5:00pm. Viernes 7:30am - 4:00pm");
                casacomerciallastunas.setNombre("CC Las Tunas");
                casacomerciallastunas.setLatitud("20.963498");
                casacomerciallastunas.setLongitud("-76.95009");
                daoSession.insertOrReplace(casacomerciallastunas);

                cu.tecnomatica.android.glp.database.greendao.Casacomercial casacomercialjesusmendez = new cu.tecnomatica.android.glp.database.greendao.Casacomercial();

                casacomercialjesusmendez.setIdmunicipio(municipiojesusmenendez.getIdmunicipio());
                casacomercialjesusmendez.setMunicipio(municipiojesusmenendez);
                casacomercialjesusmendez.setDireccion("Calle Martí no 29 esq Fernando Suárez, Reparto Primero. Las Tunas");
                casacomercialjesusmendez.setTelefono("31346954");
                casacomercialjesusmendez.setHorario("Lunes - Jueves 7:30am - 5:00pm. Viernes 7:30am - 4:00pm");
                casacomercialjesusmendez.setNombre("CC Las Tunas");
                casacomercialjesusmendez.setLatitud("20.963498");
                casacomercialjesusmendez.setLongitud("-76.95009");
                daoSession.insertOrReplace(casacomercialjesusmendez);

                cu.tecnomatica.android.glp.database.greendao.Casacomercial casacomercialpuertopadre = new cu.tecnomatica.android.glp.database.greendao.Casacomercial();

                casacomercialpuertopadre.setIdmunicipio(municipiopuertopadre.getIdmunicipio());
                casacomercialpuertopadre.setMunicipio(municipiopuertopadre);
                casacomercialpuertopadre.setDireccion("Calle Martí no 29 esq Fernando Suárez, Reparto Primero. Las Tunas");
                casacomercialpuertopadre.setTelefono("31346954");
                casacomercialpuertopadre.setHorario("Lunes - Jueves 7:30am - 5:00pm. Viernes 7:30am - 4:00pm");
                casacomercialpuertopadre.setNombre("CC Las Tunas");
                casacomercialpuertopadre.setLatitud("20.963498");
                casacomercialpuertopadre.setLongitud("-76.95009");
                daoSession.insertOrReplace(casacomercialpuertopadre);

                cu.tecnomatica.android.glp.database.greendao.Casacomercial casacomercialmanati = new cu.tecnomatica.android.glp.database.greendao.Casacomercial();

                casacomercialmanati.setIdmunicipio(municipiomanati.getIdmunicipio());
                casacomercialmanati.setMunicipio(municipiomanati);
                casacomercialmanati.setDireccion("Calle Martí no 29 esq Fernando Suárez, Reparto Primero. Las Tunas");
                casacomercialmanati.setTelefono("31346954");
                casacomercialmanati.setHorario("Lunes - Jueves 7:30am - 5:00pm. Viernes 7:30am - 4:00pm");
                casacomercialmanati.setNombre("CC Las Tunas");
                casacomercialmanati.setLatitud("20.963498");
                casacomercialmanati.setLongitud("-76.95009");
                daoSession.insertOrReplace(casacomercialmanati);

                cu.tecnomatica.android.glp.database.greendao.Casacomercial casacomercialmajibacoa = new cu.tecnomatica.android.glp.database.greendao.Casacomercial();

                casacomercialmajibacoa.setIdmunicipio(municipiomajibacoa.getIdmunicipio());
                casacomercialmajibacoa.setMunicipio(municipiomajibacoa);
                casacomercialmajibacoa.setDireccion("Calle Martí no 29 esq Fernando Suárez, Reparto Primero. Las Tunas");
                casacomercialmajibacoa.setTelefono("31346954");
                casacomercialmajibacoa.setHorario("Lunes - Jueves 7:30am - 5:00pm. Viernes 7:30am - 4:00pm");
                casacomercialmajibacoa.setNombre("CC Las Tunas");
                casacomercialmajibacoa.setLatitud("20.963498");
                casacomercialmajibacoa.setLongitud("-76.95009");
                daoSession.insertOrReplace(casacomercialmajibacoa);

                cu.tecnomatica.android.glp.database.greendao.Casacomercial casacomercialjobabo = new cu.tecnomatica.android.glp.database.greendao.Casacomercial();

                casacomercialjobabo.setIdmunicipio(municipiojobabo.getIdmunicipio());
                casacomercialjobabo.setMunicipio(municipiojobabo);
                casacomercialjobabo.setDireccion("Calle Martí no 29 esq Fernando Suárez, Reparto Primero. Las Tunas");
                casacomercialjobabo.setTelefono("31346954");
                casacomercialjobabo.setHorario("Lunes - Jueves 7:30am - 5:00pm. Viernes 7:30am - 4:00pm");
                casacomercialjobabo.setNombre("CC Las Tunas");
                casacomercialjobabo.setLatitud("20.963498");
                casacomercialjobabo.setLongitud("-76.95009");
                daoSession.insertOrReplace(casacomercialjobabo);

                cu.tecnomatica.android.glp.database.greendao.Casacomercial casacomercialcolombia = new cu.tecnomatica.android.glp.database.greendao.Casacomercial();

                casacomercialcolombia.setIdmunicipio(municipiocolombia.getIdmunicipio());
                casacomercialcolombia.setMunicipio(municipiocolombia);
                casacomercialcolombia.setDireccion("Calle Martí no 29 esq Fernando Suárez, Reparto Primero. Las Tunas");
                casacomercialcolombia.setTelefono("31346954");
                casacomercialcolombia.setHorario("Lunes - Jueves 7:30am - 5:00pm. Viernes 7:30am - 4:00pm");
                casacomercialcolombia.setNombre("CC Las Tunas");
                casacomercialcolombia.setLatitud("20.963498");
                casacomercialcolombia.setLongitud("-76.95009");
                daoSession.insertOrReplace(casacomercialcolombia);

                cu.tecnomatica.android.glp.database.greendao.Casacomercial casacomercialamancio = new cu.tecnomatica.android.glp.database.greendao.Casacomercial();

                casacomercialamancio.setIdmunicipio(municipioamancio.getIdmunicipio());
                casacomercialamancio.setMunicipio(municipioamancio);
                casacomercialamancio.setDireccion("Calle Martí no 29 esq Fernando Suárez, Reparto Primero. Las Tunas");
                casacomercialamancio.setTelefono("31346954");
                casacomercialamancio.setHorario("Lunes - Jueves 7:30am - 5:00pm. Viernes 7:30am - 4:00pm");
                casacomercialamancio.setNombre("CC Las Tunas");
                casacomercialamancio.setLatitud("20.963498");
                casacomercialamancio.setLongitud("-76.95009");
                daoSession.insertOrReplace(casacomercialamancio);

                //PUNTOS DE VENTA //PROVINCIA LAS TUNAS

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventalastunas1 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventalastunas1.setCodigo("LasTunas1");
                puntoventalastunas1.setMunicipio(municipiolastunas);
                puntoventalastunas1.setIdmunicipio(municipiolastunas.getIdmunicipio());
                puntoventalastunas1.setDireccion("Lora s/n e/ Adolfo Villamar y Gonzalo de Quezada, Reparto Primero");
                puntoventalastunas1.setLatitud("20.955377");
                puntoventalastunas1.setLongitud("-76.950445");
                puntoventalastunas1.setHorario("Lunes - Viernes 7:00am - 11:00am y 3:30pm - 6:30pm. Sábados 7:00am - 11:00am");
                puntoventalastunas1.setTelefono("31346957");
                puntoventalastunas1.setNombre("Lora");
                daoSession.insertOrReplace(puntoventalastunas1);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventalastunas2 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventalastunas2.setCodigo("LasTunas2");
                puntoventalastunas2.setMunicipio(municipiolastunas);
                puntoventalastunas2.setIdmunicipio(municipiolastunas.getIdmunicipio());
                puntoventalastunas2.setDireccion("José Tey s/n e/ 13 de Octubre y Angel Guerra Rpto. Pena");
                puntoventalastunas2.setLatitud("20.951651");
                puntoventalastunas2.setLongitud("-76.957053");
                puntoventalastunas2.setHorario("Lunes - Viernes 7:00am - 11:00am y 3:30pm - 6:30pm. Sábados 7:00am - 11:00am");
                puntoventalastunas2.setTelefono("31370146");
                puntoventalastunas2.setNombre("Cementerio");
                daoSession.insertOrReplace(puntoventalastunas2);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventalastunas3 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventalastunas3.setCodigo("LasTunas3");
                puntoventalastunas3.setMunicipio(municipiolastunas);
                puntoventalastunas3.setIdmunicipio(municipiolastunas.getIdmunicipio());
                puntoventalastunas3.setDireccion("Josué País s/n e/ Adolfo Villamar y Maceo Rpto Santo Domingo");
                puntoventalastunas3.setLatitud("20.963943");
                puntoventalastunas3.setLongitud("-76.956293");
                puntoventalastunas3.setHorario("Lunes - Viernes 7:00am - 11:00am y 3:30pm - 6:30pm. Sábados 7:00am - 11:00am");
                puntoventalastunas3.setTelefono("31370187");
                puntoventalastunas3.setNombre("Santo Domingo");
                daoSession.insertOrReplace(puntoventalastunas3);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventalastunas4 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventalastunas4.setCodigo("LasTunas4");
                puntoventalastunas4.setMunicipio(municipiolastunas);
                puntoventalastunas4.setIdmunicipio(municipiolastunas.getIdmunicipio());
                puntoventalastunas4.setDireccion("Calle 46 s/n e/ 33 y 35 Rpto. Las 40");
                puntoventalastunas4.setLatitud("20.972123");
                puntoventalastunas4.setLongitud("-76.950522");
                puntoventalastunas4.setHorario("Lunes - Viernes 7:00am - 11:00am y 3:30pm - 6:30pm. Sábados 7:00am - 11:00am");
                puntoventalastunas4.setTelefono("31396056");
                puntoventalastunas4.setNombre("Las 40");
                daoSession.insertOrReplace(puntoventalastunas4);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventalastunas5 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventalastunas5.setCodigo("LasTunas5");
                puntoventalastunas5.setMunicipio(municipiolastunas);
                puntoventalastunas5.setIdmunicipio(municipiolastunas.getIdmunicipio());
                puntoventalastunas5.setDireccion("Eddy Martínez no.60 e/ Genaro Rojas y A. Gómez. Reparto Buena Vista");
                puntoventalastunas5.setLatitud("20.96941");
                puntoventalastunas5.setLongitud("-76.940637");
                puntoventalastunas5.setHorario("Lunes - Viernes 7:00am - 11:00am y 3:30pm - 6:30pm. Sábados 7:00am - 11:00am");
                puntoventalastunas5.setTelefono("31396057");
                puntoventalastunas5.setNombre("Buena Vista");
                daoSession.insertOrReplace(puntoventalastunas5);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventalastunas6 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventalastunas6.setCodigo("LasTunas6");
                puntoventalastunas6.setMunicipio(municipiolastunas);
                puntoventalastunas6.setIdmunicipio(municipiolastunas.getIdmunicipio());
                puntoventalastunas6.setDireccion("Arsenio Pérez no 29 e/ E. Fonseca y H. Concepción. Reparto La Victoria");
                puntoventalastunas6.setLatitud("20.953618");
                puntoventalastunas6.setLongitud("-76.964928");
                puntoventalastunas6.setHorario("Lunes - Viernes 7:00am - 11:00am y 3:30pm - 6:30pm. Sábados 7:00am - 11:00am");
                puntoventalastunas6.setTelefono("31344670");
                puntoventalastunas6.setNombre("La Victoria");
                daoSession.insertOrReplace(puntoventalastunas6);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventalastunas7 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventalastunas7.setCodigo("LasTunas7");
                puntoventalastunas7.setMunicipio(municipiolastunas);
                puntoventalastunas7.setIdmunicipio(municipiolastunas.getIdmunicipio());
                puntoventalastunas7.setDireccion("Carretera Central  km 4 ½. San Antonio. Las Tunas");
                puntoventalastunas7.setLatitud("20.97937");
                puntoventalastunas7.setLongitud("-76.995602");
                puntoventalastunas7.setHorario("Lunes - Viernes 7:00am - 11:00am y 3:30pm - 6:30pm. Sábados 7:00am - 11:00am");
                puntoventalastunas7.setTelefono("31381847");
                puntoventalastunas7.setNombre("La Caldosa");
                daoSession.insertOrReplace(puntoventalastunas7);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventalastunas15 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventalastunas15.setCodigo("LasTunas15");
                puntoventalastunas15.setMunicipio(municipiolastunas);
                puntoventalastunas15.setIdmunicipio(municipiolastunas.getIdmunicipio());
                puntoventalastunas15.setDireccion("Calle 32 s/n. Reparto Los Pinos");
                puntoventalastunas15.setLatitud("20.968012");
                puntoventalastunas15.setLongitud("-76.928782");
                puntoventalastunas15.setHorario("Lunes - Viernes 7:00am - 11:00am y 3:30pm - 6:30pm. Sábados 7:00am - 11:00am");
                puntoventalastunas15.setTelefono("31391186");
                puntoventalastunas15.setNombre("Los Pinos");
                daoSession.insertOrReplace(puntoventalastunas15);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventalastunas8 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventalastunas8.setCodigo("LasTunas8");
                puntoventalastunas8.setMunicipio(municipiojesusmenendez);
                puntoventalastunas8.setIdmunicipio(municipiojesusmenendez.getIdmunicipio());
                puntoventalastunas8.setDireccion("Calle 28 s/n, Cenicero. Reparto El Batey");
                puntoventalastunas8.setLatitud("21.170906");
                puntoventalastunas8.setLongitud("-76.47359");
                puntoventalastunas8.setHorario("Lunes - Viernes 7:00am - 11:00am y 3:30pm - 6:30pm. Sábados 7:00am - 11:00am");
                puntoventalastunas8.setTelefono("31582756");
                puntoventalastunas8.setNombre("Jesús Menéndez");
                daoSession.insertOrReplace(puntoventalastunas8);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventalastunas9 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventalastunas9.setCodigo("LasTunas9");
                puntoventalastunas9.setMunicipio(municipiopuertopadre);
                puntoventalastunas9.setIdmunicipio(municipiopuertopadre.getIdmunicipio());
                puntoventalastunas9.setDireccion("Angel Almejeira no. 37 e/ Juan G. Gómez y Vicente García");
                puntoventalastunas9.setLatitud("21.196845");
                puntoventalastunas9.setLongitud("-76.599992");
                puntoventalastunas9.setHorario("Lunes - Viernes 7:00am - 11:00am y 3:30pm - 6:30pm. Sábados 7:00am - 11:00am");
                puntoventalastunas9.setTelefono("No Tiene");
                puntoventalastunas9.setNombre("Puerto Padre");
                daoSession.insertOrReplace(puntoventalastunas9);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventalastunas10 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventalastunas10.setCodigo("LasTunas10");
                puntoventalastunas10.setMunicipio(municipiomanati);
                puntoventalastunas10.setIdmunicipio(municipiomanati.getIdmunicipio());
                puntoventalastunas10.setDireccion("Calle 42 no.12  Reparto Politécnico");
                puntoventalastunas10.setLatitud("21.310308");
                puntoventalastunas10.setLongitud("-76.929774");
                puntoventalastunas10.setHorario("Lunes - Viernes 7:00am - 11:00am y 3:30pm - 6:30pm. Sábados 7:00am - 11:00am");
                puntoventalastunas10.setTelefono("31221369");
                puntoventalastunas10.setNombre("Manatí");
                daoSession.insertOrReplace(puntoventalastunas10);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventalastunas11 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventalastunas11.setCodigo("LasTunas11");
                puntoventalastunas11.setMunicipio(municipiomajibacoa);
                puntoventalastunas11.setIdmunicipio(municipiomajibacoa.getIdmunicipio());
                puntoventalastunas11.setDireccion("Calle 4ta no.13 e/ 1ra y 5ta Calixto");
                puntoventalastunas11.setLatitud("20.919027");
                puntoventalastunas11.setLongitud("-76.86834");
                puntoventalastunas11.setHorario("Lunes - Viernes 7:00am - 11:00am y 3:30pm - 6:30pm. Sábados 7:00am - 11:00am");
                puntoventalastunas11.setTelefono("No Tiene");
                puntoventalastunas11.setNombre("Majibacoa");
                daoSession.insertOrReplace(puntoventalastunas11);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventalastunas12 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventalastunas12.setCodigo("LasTunas12");
                puntoventalastunas12.setMunicipio(municipiojobabo);
                puntoventalastunas12.setIdmunicipio(municipiojobabo.getIdmunicipio());
                puntoventalastunas12.setDireccion("Calle 101 no. 26 Esq. 72. Reparto Viet-Nam");
                puntoventalastunas12.setLatitud("20.911034");
                puntoventalastunas12.setLongitud("-77.277137");
                puntoventalastunas12.setHorario("Lunes - Viernes 7:00am - 11:00am y 3:30pm - 6:30pm. Sábados 7:00am - 11:00am");
                puntoventalastunas12.setTelefono("No Tiene");
                puntoventalastunas12.setNombre("Jobabo");
                daoSession.insertOrReplace(puntoventalastunas12);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventalastunas13 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventalastunas13.setCodigo("LasTunas13");
                puntoventalastunas13.setMunicipio(municipiocolombia);
                puntoventalastunas13.setIdmunicipio(municipiocolombia.getIdmunicipio());
                puntoventalastunas13.setDireccion("Calle 11 no. 81 e/ 8 y 10. Reparto Progreso");
                puntoventalastunas13.setLatitud("20.998705");
                puntoventalastunas13.setLongitud("-77.435807");
                puntoventalastunas13.setHorario("Lunes - Viernes 7:00am - 11:00am y 3:30pm - 6:30pm. Sábados 7:00am - 11:00am");
                puntoventalastunas13.setTelefono("No Tiene");
                puntoventalastunas13.setNombre("Colombia");
                daoSession.insertOrReplace(puntoventalastunas13);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventalastunas14 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventalastunas14.setCodigo("LasTunas14");
                puntoventalastunas14.setMunicipio(municipioamancio);
                puntoventalastunas14.setIdmunicipio(municipioamancio.getIdmunicipio());
                puntoventalastunas14.setDireccion("Calle 22 s/n e/ 8 y 10 Rpto. Pueblo Nuevo");
                puntoventalastunas14.setLatitud("20.833427");
                puntoventalastunas14.setLongitud("-77.579552");
                puntoventalastunas14.setHorario("Lunes - Viernes 7:00am - 11:00am y 3:30pm - 6:30pm. Sábados 7:00am - 11:00am");
                puntoventalastunas14.setTelefono("No Tiene");
                puntoventalastunas14.setNombre("Amancio");
                daoSession.insertOrReplace(puntoventalastunas14);

                /*//PROVINCIA HOLGUÍN
                /*Provincia provinciaholguin = new Provincia();
                provinciaholguin.setNombre("Holguín");
                provinciaholguin.setActiva(false);
                daoSession.insertOrReplace(provinciaholguin);*/

                //PROVINCIA GRANMA
                Provincia provinciagranma = new Provincia();
                provinciagranma.setNombre("Granma");
                provinciagranma.setLatitud("20.37343");
                provinciagranma.setLongitud("-76.64921");
                provinciagranma.setActiva(false);
                daoSession.insertOrReplace(provinciagranma);

                Empresacomercializadora empresacomercializadoragranma = new Empresacomercializadora();
                empresacomercializadoragranma.setNombre("División Territorial de Comercialización de Combustible Granma");
                empresacomercializadoragranma.setProvincia(provinciagranma);
                empresacomercializadoragranma.setIdprovincia(provinciagranma.getIdprovincia());
                empresacomercializadoragranma.setDireccion("CARRETERA CENTRAL VIA LAS TUNAS. KM 1");
                empresacomercializadoragranma.setTelefono("23427415");
                empresacomercializadoragranma.setHorario("LUNES A JUEVES DE 7:30AM a 5:00PM. VIERNES DE 7:30AM A 4:00PM");
                empresacomercializadoragranma.setLatitud("20.386092");
                empresacomercializadoragranma.setLongitud("-76.663725");
                daoSession.insertOrReplace(empresacomercializadoragranma);

                Serviciosmecanicos serviciosmecanicosgranma = new Serviciosmecanicos();
                serviciosmecanicosgranma.setNombre("Servicios Mécanicos Granma");
                serviciosmecanicosgranma.setProvincia(provinciagranma);
                serviciosmecanicosgranma.setIdprovincia(provinciagranma.getIdprovincia());
                serviciosmecanicosgranma.setDireccion("CARRETERA CENTRAL VIA LAS TUNAS. KM 1");
                serviciosmecanicosgranma.setTelefono("23448186");
                serviciosmecanicosgranma.setHorario("LUNES A JUEVES DE 7:30AM a 5:00PM. VIERNES DE 7:30AM A 4:00PM");
                serviciosmecanicosgranma.setLatitud("20.387918");
                serviciosmecanicosgranma.setLongitud("-76.66388");
                daoSession.insertOrReplace(serviciosmecanicosgranma);

                Atencionclientes atencionclientesgranma = new Atencionclientes();
                atencionclientesgranma.setNombre("Oficina de Atención al Cliente Granma");
                atencionclientesgranma.setProvincia(provinciagranma);
                atencionclientesgranma.setIdprovincia(provinciagranma.getIdprovincia());
                atencionclientesgranma.setDireccion("CARRETERA CENTRAL VIA LAS TUNAS. KM 1");
                atencionclientesgranma.setTelefono("23448186");
                atencionclientesgranma.setHorario("LUNES A JUEVES DE 7:30AM a 5:00PM. VIERNES DE 7:30AM A 4:00PM");
                atencionclientesgranma.setLatitud("20.387918");
                atencionclientesgranma.setLongitud("-76.66388");
                daoSession.insertOrReplace(atencionclientesgranma);

                //MUNICIPIOS //PROVINCIA GRANMA

                cu.tecnomatica.android.glp.database.greendao.Municipio municipiobayamo = new cu.tecnomatica.android.glp.database.greendao.Municipio();
                municipiobayamo.setNombre("Bayamo");
                municipiobayamo.setProvincia(provinciagranma);
                municipiobayamo.setActivo(true);
                daoSession.insertOrReplace(municipiobayamo);

                cu.tecnomatica.android.glp.database.greendao.Municipio municipiomanzanillo = new cu.tecnomatica.android.glp.database.greendao.Municipio();
                municipiomanzanillo.setNombre("Manzanillo");
                municipiomanzanillo.setProvincia(provinciagranma);
                municipiomanzanillo.setActivo(false);
                daoSession.insertOrReplace(municipiomanzanillo);

                //CASAS COMERCIALES //PROVINCIA GRANMA

                cu.tecnomatica.android.glp.database.greendao.Casacomercial casacomercialbayamo = new cu.tecnomatica.android.glp.database.greendao.Casacomercial();

                casacomercialbayamo.setIdmunicipio(municipiobayamo.getIdmunicipio());
                casacomercialbayamo.setMunicipio(municipiobayamo);
                casacomercialbayamo.setDireccion("CALLE 3 E/ 5 Y 7. REPARTO JESÚS MENÉNDEZ (DETRÁS PLAZA DE LA PATRIA)");
                casacomercialbayamo.setTelefono("23427560 Y 23485038");
                casacomercialbayamo.setHorario("LUNES A JUEVES DE 7:30AM a 5:00PM. VIERNES DE 7:30AM A 4:00PM");
                casacomercialbayamo.setNombre("CC Bayamo");
                casacomercialbayamo.setLatitud("20.3743");
                casacomercialbayamo.setLongitud("-76.641217");
                daoSession.insertOrReplace(casacomercialbayamo);

                cu.tecnomatica.android.glp.database.greendao.Casacomercial casacomercialmanzanillo = new cu.tecnomatica.android.glp.database.greendao.Casacomercial();

                casacomercialjesusmendez.setIdmunicipio(municipiomanzanillo.getIdmunicipio());
                casacomercialjesusmendez.setMunicipio(municipiomanzanillo);
                casacomercialjesusmendez.setDireccion("CALLE 100 E/ AVE. LAS PALMAS Y MIGUEL RAMÍREZ. REPARTO PÉREZ");
                casacomercialjesusmendez.setTelefono("23542375");
                casacomercialjesusmendez.setHorario("LUNES A JUEVES DE 7:30AM a 5:00PM. VIERNES DE 7:30AM A 4:00PM");
                casacomercialjesusmendez.setNombre("CC Manzanillo");
                casacomercialjesusmendez.setLatitud("20.556756");
                casacomercialjesusmendez.setLongitud("-77.245619");
                daoSession.insertOrReplace(casacomercialjesusmendez);

                //PUNTOS DE VENTA //PROVINCIA GRANMA

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventalgranma1 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventalgranma1.setCodigo("Granma1");
                puntoventalgranma1.setMunicipio(municipiobayamo);
                puntoventalgranma1.setIdmunicipio(municipiobayamo.getIdmunicipio());
                puntoventalgranma1.setDireccion("Ave. Francisco Vicente Aguilera s/n, Esquina a 7ma. Rpto. Ojeda");
                puntoventalgranma1.setLatitud("20.386768");
                puntoventalgranma1.setLongitud("-76.650374");
                puntoventalgranma1.setHorario("Lunes a Viernes 7:30AM a 2:00PM y 3:00PM a 8:00PM. Sábado 7:30AM a 11:00AM");
                puntoventalgranma1.setTelefono("23429100");
                puntoventalgranma1.setNombre("Ojeda");
                daoSession.insertOrReplace(puntoventalgranma1);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventalgranma2 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventalgranma2.setCodigo("Granma2");
                puntoventalgranma2.setMunicipio(municipiobayamo);
                puntoventalgranma2.setIdmunicipio(municipiobayamo.getIdmunicipio());
                puntoventalgranma2.setDireccion("Ave. Granma s/n e/ 5 y 9. Rpto. Jesús Menéndez");
                puntoventalgranma2.setLatitud("20.361907");
                puntoventalgranma2.setLongitud("-76.632352");
                puntoventalgranma2.setHorario("Lunes a Viernes de 7:30 AM a 11:00 AM y de 3:00 PM a 6:40 PM");
                puntoventalgranma2.setTelefono("23427465");
                puntoventalgranma2.setNombre("Ave. Granma");
                daoSession.insertOrReplace(puntoventalgranma2);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventalgranma3 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventalgranma3.setCodigo("Granma3");
                puntoventalgranma3.setMunicipio(municipiobayamo);
                puntoventalgranma3.setIdmunicipio(municipiobayamo.getIdmunicipio());
                puntoventalgranma3.setDireccion("Ave. Rafael M. Mendive s/n e/ 4 y 5. Rpto. Antonio Guiteras");
                puntoventalgranma3.setLatitud("20.365968");
                puntoventalgranma3.setLongitud("-76.631068");
                puntoventalgranma3.setHorario("Lunes a Viernes 7:30AM a 2:00PM y 3:00PM a 8:00PM. Sábado 7:30AM a 11:00AM");
                puntoventalgranma3.setTelefono("23427464");
                puntoventalgranma3.setNombre("El Bayam");
                daoSession.insertOrReplace(puntoventalgranma3);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventalgranma4 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventalgranma4.setCodigo("Granma4");
                puntoventalgranma4.setMunicipio(municipiobayamo);
                puntoventalgranma4.setIdmunicipio(municipiobayamo.getIdmunicipio());
                puntoventalgranma4.setDireccion("Calle Linea s/n e/ Parada y Milanés. Rpto. El Cristo");
                puntoventalgranma4.setLatitud("20.378302");
                puntoventalgranma4.setLongitud("-76.646383");
                puntoventalgranma4.setHorario("Lunes a Viernes 7:30AM a 2:00PM y 3:00PM a 8:00PM. Sábado 7:30AM a 11:00AM");
                puntoventalgranma4.setTelefono("23429059");
                puntoventalgranma4.setNombre("Mercado Luis Ramírez");
                daoSession.insertOrReplace(puntoventalgranma4);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventalgranma5 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventalgranma5.setCodigo("Granma5");
                puntoventalgranma5.setMunicipio(municipiobayamo);
                puntoventalgranma5.setIdmunicipio(municipiobayamo.getIdmunicipio());
                puntoventalgranma5.setDireccion("Ctra. Central Vía Holguín e/ Carlos M. Machado y Línea. Rpto. Iser");
                puntoventalgranma5.setLatitud("20.372912");
                puntoventalgranma5.setLongitud("-76.63987");
                puntoventalgranma5.setHorario("Lunes a Viernes 7:30AM a 2:00PM y 3:00PM a 8:00PM. Sábado 7:30AM a 11:00AM");
                puntoventalgranma5.setTelefono("23427562");
                puntoventalgranma5.setNombre("La Redonda");
                daoSession.insertOrReplace(puntoventalgranma5);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventalgranma6 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventalgranma6.setCodigo("Granma6");
                puntoventalgranma6.setMunicipio(municipiobayamo);
                puntoventalgranma6.setIdmunicipio(municipiobayamo.getIdmunicipio());
                puntoventalgranma6.setDireccion("Calle Masó s/n e/ Máximo Gómez y Chapuzón. Rpto. San Juan");
                puntoventalgranma6.setLatitud("20.368747");
                puntoventalgranma6.setLongitud("-76.648287");
                puntoventalgranma6.setHorario("Lunes a Viernes 7:30AM a 2:00PM y 3:00PM a 8:00PM. Sábado 7:30AM a 11:00AM");
                puntoventalgranma6.setTelefono("23427945");
                puntoventalgranma6.setNombre("El Chapuzón");
                daoSession.insertOrReplace(puntoventalgranma6);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventalgranma7 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventalgranma7.setCodigo("Granma7");
                puntoventalgranma7.setMunicipio(municipiobayamo);
                puntoventalgranma7.setIdmunicipio(municipiobayamo.getIdmunicipio());
                puntoventalgranma7.setDireccion("Ave. Francisco Vicente Aguilera s/n, Esquina a 7ma. Rpto. Ojeda");
                puntoventalgranma7.setLatitud("20.373657");
                puntoventalgranma7.setLongitud("-76.625457");
                puntoventalgranma7.setHorario("Ave. Granma s/n. Rpto. Granma");
                puntoventalgranma7.setTelefono("23431912");
                puntoventalgranma7.setNombre("El Polígono");
                daoSession.insertOrReplace(puntoventalgranma7);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventalgranma8 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventalgranma8.setCodigo("Granma8");
                puntoventalgranma8.setMunicipio(municipiobayamo);
                puntoventalgranma8.setIdmunicipio(municipiobayamo.getIdmunicipio());
                puntoventalgranma8.setDireccion("Ave. 1ro de Mayo e/ Astillero y León");
                puntoventalgranma8.setLatitud("20.593356");
                puntoventalgranma8.setLongitud("-77.208153");
                puntoventalgranma8.setHorario("Lunes a Viernes 7:30AM a 2:00PM y 3:00PM a 8:00PM. Sábado 7:30AM a 11:00AM");
                puntoventalgranma8.setTelefono("23577313");
                puntoventalgranma8.setNombre("Ave. 1ro de Mayo");
                daoSession.insertOrReplace(puntoventalgranma8);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventalgranma9 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventalgranma9.setCodigo("Granma9");
                puntoventalgranma9.setMunicipio(municipiomanzanillo);
                puntoventalgranma9.setIdmunicipio(municipiomanzanillo.getIdmunicipio());
                puntoventalgranma9.setDireccion("Calle Merchán #28 e/ Salud y Tomás Barrero");
                puntoventalgranma9.setLatitud("20.573272");
                puntoventalgranma9.setLongitud("-77.213167");
                puntoventalgranma9.setHorario("Lunes a Viernes 7:30AM a 2:00PM y 3:00PM a 8:00PM. Sábado 7:30AM a 11:00AM");
                puntoventalgranma9.setTelefono("23577188");
                puntoventalgranma9.setNombre("Merchán");
                daoSession.insertOrReplace(puntoventalgranma9);

                /*//PROVINCIA SANTIAGO DE CUBA
                /*Provincia provinciasantiago = new Provincia();
                provinciasantiago.setNombre("Santiago de Cuba");
                provinciasantiago.setActiva(false);
                daoSession.insertOrReplace(provinciasantiago);*/

                //PROVINCIA GUANTÁNAMO
                Provincia provinciaguantanamo = new Provincia();
                provinciaguantanamo.setNombre("Guantánamo");
                provinciaguantanamo.setLatitud("20.14318");
                provinciaguantanamo.setLongitud("-75.20350");
                provinciaguantanamo.setActiva(false);
                daoSession.insertOrReplace(provinciaguantanamo);

                Empresacomercializadora empresacomercializadoraguantanamo = new Empresacomercializadora();
                empresacomercializadoraguantanamo.setNombre("División Territorial de Comercialización de Combustible Guantánamo");
                empresacomercializadoraguantanamo.setProvincia(provinciaguantanamo);
                empresacomercializadoraguantanamo.setIdprovincia(provinciaguantanamo.getIdprovincia());
                empresacomercializadoraguantanamo.setDireccion("Carretera El Salvador Km 4 ½, San Idelfonso");
                empresacomercializadoraguantanamo.setTelefono("21394103, 21394104, 21394105, 21394106");
                empresacomercializadoraguantanamo.setHorario("LUNES A JUEVES DE 7:00AM a 12:00PM Y 12:30PM a 4:30PM. VIERNES DE 7:00AM a 12:00PM Y 12:30PM a 3:30PM");
                empresacomercializadoraguantanamo.setLatitud("20.187438");
                empresacomercializadoraguantanamo.setLongitud("-75.223187");
                daoSession.insertOrReplace(empresacomercializadoraguantanamo);

                Serviciosmecanicos serviciosmecanicosguantanamo = new Serviciosmecanicos();
                serviciosmecanicosguantanamo.setNombre("Servicios Mécanicos Guantánamo");
                serviciosmecanicosguantanamo.setProvincia(provinciaguantanamo);
                serviciosmecanicosguantanamo.setIdprovincia(provinciaguantanamo.getIdprovincia());
                serviciosmecanicosguantanamo.setDireccion("Ave. de los Estudiantes #563 e/ Carlos Manuel y Luz Caballero");
                serviciosmecanicosguantanamo.setTelefono("21325898");
                serviciosmecanicosguantanamo.setHorario("LUNES A VIERNES DE 7:00AM a 12:00PM Y 2:00PM a 5:00PM. SÁBADOS DE 8:00AM a 12:00PM");
                serviciosmecanicosguantanamo.setLatitud("20.147195");
                serviciosmecanicosguantanamo.setLongitud("-75.20609");
                daoSession.insertOrReplace(serviciosmecanicosguantanamo);

                Atencionclientes atencionclientesguantanamo = new Atencionclientes();
                atencionclientesgranma.setNombre("Oficina de Atención al Cliente Guantánamo");
                atencionclientesgranma.setProvincia(provinciaguantanamo);
                atencionclientesgranma.setIdprovincia(provinciaguantanamo.getIdprovincia());
                atencionclientesgranma.setDireccion("Carretera El Salvador Km 4 ½, San Idelfonso");
                atencionclientesgranma.setTelefono("21394201");
                atencionclientesgranma.setHorario("LUNES A JUEVES DE 7:00AM a 12:00PM Y 12:30PM a 4:30PM. VIERNES DE 7:00AM a 12:00PM y 12:30AM a 3:30PM");
                atencionclientesgranma.setLatitud("20.387918");
                atencionclientesgranma.setLongitud("-76.66388");
                daoSession.insertOrReplace(atencionclientesgranma);

                //MUNICIPIOS //PROVINCIA GUANTÁNAMO

                cu.tecnomatica.android.glp.database.greendao.Municipio municipioguantanamo = new cu.tecnomatica.android.glp.database.greendao.Municipio();
                municipioguantanamo.setNombre("Guantánamo");
                municipioguantanamo.setProvincia(provinciaguantanamo);
                municipioguantanamo.setActivo(true);
                daoSession.insertOrReplace(municipioguantanamo);

                cu.tecnomatica.android.glp.database.greendao.Municipio municipiocaimanera = new cu.tecnomatica.android.glp.database.greendao.Municipio();
                municipiocaimanera.setNombre("Caimanera");
                municipiocaimanera.setProvincia(provinciaguantanamo);
                municipiocaimanera.setActivo(false);
                daoSession.insertOrReplace(municipiocaimanera);

                //CASAS COMERCIALES //PROVINCIA GUANTÁNAMO

                cu.tecnomatica.android.glp.database.greendao.Casacomercial casacomercialguantanamo = new cu.tecnomatica.android.glp.database.greendao.Casacomercial();

                casacomercialguantanamo.setIdmunicipio(municipioguantanamo.getIdmunicipio());
                casacomercialguantanamo.setMunicipio(municipioguantanamo);
                casacomercialguantanamo.setDireccion("Ave. de los Estudiantes #563/ Carlos Manuel y Luz Caballero");
                casacomercialguantanamo.setTelefono("21325898");
                casacomercialguantanamo.setHorario("LUNES A VIERNES DE 7:00AM a 12:00PM Y 2:00PM a 5:00PM. SÁBADOS DE 8:00AM a 12:00PM");
                casacomercialguantanamo.setNombre("CC GUANTÁNAMO");
                casacomercialguantanamo.setLatitud("20.147195");
                casacomercialguantanamo.setLongitud("-75.20609");
                daoSession.insertOrReplace(casacomercialguantanamo);

                cu.tecnomatica.android.glp.database.greendao.Casacomercial casacomercialcaimanera = new cu.tecnomatica.android.glp.database.greendao.Casacomercial();

                casacomercialcaimanera.setIdmunicipio(municipiocaimanera.getIdmunicipio());
                casacomercialcaimanera.setMunicipio(municipiocaimanera);
                casacomercialcaimanera.setDireccion("Ave. de los Estudiantes #563/ Carlos Manuel y Luz Caballero");
                casacomercialcaimanera.setTelefono("21325898");
                casacomercialcaimanera.setHorario("LUNES A VIERNES DE 7:00AM a 12:00PM Y 2:00PM a 5:00PM. SÁBADOS DE 8:00AM a 12:00PM");
                casacomercialcaimanera.setNombre("CC GUANTÁNAMO");
                casacomercialcaimanera.setLatitud("20.147195");
                casacomercialcaimanera.setLongitud("-75.20609");
                daoSession.insertOrReplace(casacomercialcaimanera);

                //PUNTOS DE VENTA //PROVINCIA GUANTÁNAMO

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventalguantanamo1 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventalguantanamo1.setCodigo("Guantanamo1");
                puntoventalguantanamo1.setMunicipio(municipioguantanamo);
                puntoventalguantanamo1.setIdmunicipio(municipioguantanamo.getIdmunicipio());
                puntoventalguantanamo1.setDireccion("Calle 9 / 5 y 7 Rpto. Mártires de Granada");
                puntoventalguantanamo1.setLatitud("20.156088");
                puntoventalguantanamo1.setLongitud("-75.185545");
                puntoventalguantanamo1.setHorario("Lunes, Martes, Miércoles y Viernes: 7:00am – 12:00m Y 2:00pm - 4:30pm. Jueves: 12:00 m - 7:00pm. Sábado: 8.00am - 11.30am");
                puntoventalguantanamo1.setTelefono("21322505");
                puntoventalguantanamo1.setNombre("Dabul");
                daoSession.insertOrReplace(puntoventalguantanamo1);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventalguantanamo2 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventalguantanamo2.setCodigo("Guantanamo2");
                puntoventalguantanamo2.setMunicipio(municipioguantanamo);
                puntoventalguantanamo2.setIdmunicipio(municipioguantanamo.getIdmunicipio());
                puntoventalguantanamo2.setDireccion("Calle 5 Este esq. Aguilera  San Justo");
                puntoventalguantanamo2.setLatitud("20.14379");
                puntoventalguantanamo2.setLongitud("-75.195615");
                puntoventalguantanamo2.setHorario("Lunes, Martes, Miércoles y Viernes: 7:00am – 12:00m Y 2:00pm - 4:30pm. Jueves: 12:00 m - 7:00pm. Sábado: 8.00am - 11.30am");
                puntoventalguantanamo2.setTelefono("21328094");
                puntoventalguantanamo2.setNombre("San Justo");
                daoSession.insertOrReplace(puntoventalguantanamo2);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventalguantanamo3 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventalguantanamo3.setCodigo("Guantanamo3");
                puntoventalguantanamo3.setMunicipio(municipioguantanamo);
                puntoventalguantanamo3.setIdmunicipio(municipioguantanamo.getIdmunicipio());
                puntoventalguantanamo3.setDireccion("Calle 7 Oeste / Crombet y Aguilera");
                puntoventalguantanamo3.setLatitud("20.14389");
                puntoventalguantanamo3.setLongitud("-75.216702");
                puntoventalguantanamo3.setHorario("Lunes, Martes, Miércoles y Viernes: 7:00am – 12:00m Y 2:00pm - 4:30pm. Jueves: 12:00 m - 7:00pm. Sábado: 8.00am - 11.30am");
                puntoventalguantanamo3.setTelefono("21324229");
                puntoventalguantanamo3.setNombre("7 Oeste");
                daoSession.insertOrReplace(puntoventalguantanamo3);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventalguantanamo4 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventalguantanamo4.setCodigo("Guantanamo4");
                puntoventalguantanamo4.setMunicipio(municipioguantanamo);
                puntoventalguantanamo4.setIdmunicipio(municipioguantanamo.getIdmunicipio());
                puntoventalguantanamo4.setDireccion("Calle 5 Sur / P. A. Perez y Calixto García");
                puntoventalguantanamo4.setLatitud("20.134387");
                puntoventalguantanamo4.setLongitud("-75.203788");
                puntoventalguantanamo4.setHorario("Lunes, Martes, Miércoles y Viernes: 7:00am – 12:00m Y 2:00pm - 4:30pm. Jueves: 12:00 m - 7:00pm. Sábado: 8.00am - 11.30am");
                puntoventalguantanamo4.setTelefono("21328057");
                puntoventalguantanamo4.setNombre("5 Sur");
                daoSession.insertOrReplace(puntoventalguantanamo4);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventalguantanamo5 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventalguantanamo5.setCodigo("Guantanamo5");
                puntoventalguantanamo5.setMunicipio(municipioguantanamo);
                puntoventalguantanamo5.setIdmunicipio(municipioguantanamo.getIdmunicipio());
                puntoventalguantanamo5.setDireccion("Calle 1 Oeste / 5 y 6 Sur");
                puntoventalguantanamo5.setLatitud("20.134847");
                puntoventalguantanamo5.setLongitud("-75.212638");
                puntoventalguantanamo5.setHorario("Lunes, Martes, Miércoles y Viernes: 7:00am – 12:00m Y 2:00pm - 4:30pm. Jueves: 12:00 m - 7:00pm. Sábado: 8.00am - 11.30am");
                puntoventalguantanamo5.setTelefono("21328046");
                puntoventalguantanamo5.setNombre("Materno");
                daoSession.insertOrReplace(puntoventalguantanamo5);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventalguantanamo6 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventalguantanamo6.setCodigo("Guantanamo6");
                puntoventalguantanamo6.setMunicipio(municipioguantanamo);
                puntoventalguantanamo6.setIdmunicipio(municipioguantanamo.getIdmunicipio());
                puntoventalguantanamo6.setDireccion("4 Oeste / 14 y 15 Norte. Rpto Caribe");
                puntoventalguantanamo6.setLatitud("20.160608");
                puntoventalguantanamo6.setLongitud("-75.214197");
                puntoventalguantanamo6.setHorario("Lunes, Martes, Miércoles y Viernes: 7:00am – 12:00m Y 2:00pm - 4:30pm. Jueves: 12:00 m - 7:00pm. Sábado: 8.00am - 11.30am");
                puntoventalguantanamo6.setTelefono("21385467");
                puntoventalguantanamo6.setNombre("Caribe 1");
                daoSession.insertOrReplace(puntoventalguantanamo6);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventalguantanamo7 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventalguantanamo7.setCodigo("Guantanamo7");
                puntoventalguantanamo7.setMunicipio(municipioguantanamo);
                puntoventalguantanamo7.setIdmunicipio(municipioguantanamo.getIdmunicipio());
                puntoventalguantanamo7.setDireccion("San Gregorio /15 y 16 Norte. Rpto Caribe");
                puntoventalguantanamo7.setLatitud("20.161352");
                puntoventalguantanamo7.setLongitud("-75.208785");
                puntoventalguantanamo7.setHorario("Lunes, Martes, Miércoles y Viernes: 7:00am – 12:00m Y 2:00pm - 4:30pm. Jueves: 12:00 m - 7:00pm. Sábado: 8.00am - 11.30am");
                puntoventalguantanamo7.setTelefono("21383526");
                puntoventalguantanamo7.setNombre("Caribe 3");
                daoSession.insertOrReplace(puntoventalguantanamo7);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventalguantanamo8 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventalguantanamo8.setCodigo("Guantanamo8");
                puntoventalguantanamo8.setMunicipio(municipiocaimanera);
                puntoventalguantanamo8.setIdmunicipio(municipiocaimanera.getIdmunicipio());
                puntoventalguantanamo8.setDireccion("Calle # 5ta / Carretera y Carril");
                puntoventalguantanamo8.setLatitud("19.988798");
                puntoventalguantanamo8.setLongitud("-75.154465");
                puntoventalguantanamo8.setHorario("Viernes: 7:00am – 12:00m Y 1:00pm - 4:00pm. Sábado: 7.00am - 11.00am");
                puntoventalguantanamo8.setTelefono("21499206");
                puntoventalguantanamo8.setNombre("Caimanera");
                daoSession.insertOrReplace(puntoventalguantanamo8);

                cu.tecnomatica.android.glp.database.greendao.Puntoventa puntoventalguantanamo9 = new cu.tecnomatica.android.glp.database.greendao.Puntoventa();
                puntoventalguantanamo9.setCodigo("Guantanamo9");
                puntoventalguantanamo9.setMunicipio(municipiocaimanera);
                puntoventalguantanamo9.setIdmunicipio(municipiocaimanera.getIdmunicipio());
                puntoventalguantanamo9.setDireccion("Calle H Boquerón");
                puntoventalguantanamo9.setLatitud("19.985193");
                puntoventalguantanamo9.setLongitud("-75.109982");
                puntoventalguantanamo9.setHorario("Viernes: 7:00am – 12:00m Y 1:00pm - 4:00pm. Sábado: 7.00am - 11.00am");
                puntoventalguantanamo9.setTelefono("23577188");
                puntoventalguantanamo9.setNombre("Boquerón");
                daoSession.insertOrReplace(puntoventalguantanamo9);

                //SERVICIOS

                //TRAMITES

                //CLIENTES
                Cliente cliente_invitado = new Cliente();
                cliente_invitado.setTitular("Invitado");
                cliente_invitado.setNumerocontrato("Sin Número de Contrato");
                cliente_invitado.setContrato(contrato_normado);
                cliente_invitado.setIdcontrato(contrato_normado.getIdcontrato());
                cliente_invitado.setCantidadconsumidores(1);
                cliente_invitado.setMunicipio(municipioarroyo);
                cliente_invitado.setIdmunicipio(municipioarroyo.getIdmunicipio());
                cliente_invitado.setPuntoventa(puntoventa12001);
                cliente_invitado.setIdpuntoventa(puntoventa12001.getIdpuntoventa());
                cliente_invitado.setActivo(true);
                daoSession.insertOrReplace(cliente_invitado);

                //COMPRAS

            }
        }
        catch (Exception e)
        {
            Log.e("Errorrrrrr:", e.getMessage());
        }
    }
}