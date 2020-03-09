package cu.tecnomatica.android.glp.activities.cliente;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import androidx.annotation.NonNull;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import org.greenrobot.greendao.database.Database;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import cu.tecnomatica.android.glp.R;
import cu.tecnomatica.android.glp.activities.PrincipalActivity;
import cu.tecnomatica.android.glp.database.greendao.Cliente;
import cu.tecnomatica.android.glp.database.greendao.Contrato;
import cu.tecnomatica.android.glp.database.greendao.DaoMaster;
import cu.tecnomatica.android.glp.database.greendao.DaoSession;
import cu.tecnomatica.android.glp.database.greendao.Municipio;
import cu.tecnomatica.android.glp.database.greendao.MunicipioDao;
import cu.tecnomatica.android.glp.database.greendao.Provincia;
import cu.tecnomatica.android.glp.database.greendao.Puntoventa;
import cu.tecnomatica.android.glp.database.greendao.PuntoventaDao;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class NuevoClienteActivity extends AppCompatActivity implements LoaderCallbacks<Cursor>
{
    private static final String DB_FILE = "/GLP/daoglp.db";

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private Spinner mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private List<Contrato>tiposcontratos;
    private List<Municipio>listamunicipios;
    private List<Puntoventa>listapuntosventa;
    private Spinner combotiposcontratos;
    private Spinner combolistamunicipios;
    private Spinner combopuntosventa;
    private Municipio municipioseleccionado;
    private Puntoventa puntoventaseleccionado;
    private Contrato contratoseleccionado;
    private EditText numerocontrato;
    private EditText cantidadconsumidores;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_cliente);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarnuevousuario);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(NuevoClienteActivity.this, PrincipalActivity.class);
                startActivity(intent);
                finish();
            }
        });

        setupActionBar();
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.idtitular);
        populateAutoComplete();

        mPasswordView = (Spinner) findViewById(R.id.idcombotipocontrato);

        String dbPath = new File(Environment.getExternalStorageDirectory().getPath() + DB_FILE).getAbsolutePath();
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, dbPath);
        //DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "daoglp.db");
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

        tiposcontratos = daoSession.getContratoDao().loadAll();
        listamunicipios = daoSession.getMunicipioDao().queryBuilder().where(MunicipioDao.Properties.Idprovincia.like(provinciaactiva.getIdprovincia().toString())).list();

        combotiposcontratos = (Spinner)findViewById(R.id.idcombotipocontrato);

        ArrayAdapter<String> adaptertipoconrato = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item);
        adaptertipoconrato.add("Tipo de Contrato");

        for (int i = 0; i < tiposcontratos.size(); i++)
        {
            adaptertipoconrato.add(tiposcontratos.get(i).getNombre());
        }
        combotiposcontratos.setAdapter(adaptertipoconrato);

        combotiposcontratos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                if (position != 0)
                {
                    contratoseleccionado = tiposcontratos.get(position-1);
                }
                else
                {
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
            }
        });

        combolistamunicipios = (Spinner)findViewById(R.id.idcombomunicipionuevocliente);
        combopuntosventa = (Spinner)findViewById(R.id.idcombopuntoventanuevocliente);

        final ArrayAdapter<String> adaptermunicipiocliente = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item);
        adaptermunicipiocliente.add("Municipio del Cliente");

        for (int i=0; i<listamunicipios.size(); i++)
        {
            adaptermunicipiocliente.add(listamunicipios.get(i).getNombre());
        }
        combolistamunicipios.setAdapter(adaptermunicipiocliente);
        combopuntosventa.setAdapter(adaptermunicipiocliente);

        combolistamunicipios.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                if (position != 0)
                {
                    municipioseleccionado = listamunicipios.get(position-1);

                    listapuntosventa = daoSession.getPuntoventaDao().queryBuilder().where(PuntoventaDao.Properties.Idmunicipio.like(municipioseleccionado.getIdmunicipio().toString())).list();

                    ArrayAdapter<String> adapterpuntosdeventamunicipio = new ArrayAdapter<>(parent.getContext(), R.layout.support_simple_spinner_dropdown_item);
                    adapterpuntosdeventamunicipio.add("Punto de Venta del Cliente");

                    for (int i = 0; i < listapuntosventa.size(); i++) {
                        adapterpuntosdeventamunicipio.add(listapuntosventa.get(i).getNombre());
                    }
                    combopuntosventa.setAdapter(adapterpuntosdeventamunicipio);
                }
                else
                {
                    ArrayAdapter<String> adapterpuntosdeventamunicipio = new ArrayAdapter<>(parent.getContext(), R.layout.support_simple_spinner_dropdown_item);
                    adapterpuntosdeventamunicipio.add("Punto de Venta del Cliente");
                    combopuntosventa.setAdapter(adapterpuntosdeventamunicipio);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        combopuntosventa.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                if (position != 0)
                {
                    puntoventaseleccionado = listapuntosventa.get(position-1);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
            }
        });

        numerocontrato = (EditText)findViewById(R.id.idnumerocontrato);
        cantidadconsumidores = (EditText)findViewById(R.id.idcantidadconsumidores);

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view)
            {
                RegistarCliente();
                //attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // Show the Up button in the action bar.
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);


        }
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);


        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        //String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        //if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            //mPasswordView.setError(getString(R.string.error_invalid_password));
            //focusView = mPasswordView;
            //cancel = true;
        //}

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            //mEmailView.setError(getString(R.string.error_field_required));
            //focusView = mEmailView;
            //cancel = true;
        } else if (!isEmailValid(email)) {
            //mEmailView.setError(getString(R.string.error_invalid_email));
            //focusView = mEmailView;
            //cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            //mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(NuevoClienteActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }

    public void RegistarCliente()
    {
        String dbPath = new File(Environment.getExternalStorageDirectory().getPath() + DB_FILE).getAbsolutePath();
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, dbPath);
        //DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "daoglp.db");
        Database database = helper.getWritableDb();
        DaoSession daoSession = new DaoMaster(database).newSession();

        List<Cliente> listadesactivar = daoSession.getClienteDao().loadAll();

        Cliente cliente = new Cliente();

        cliente.setActivo(true);
        cliente.setTitular(mEmailView.getText().toString());
        cliente.setContrato(contratoseleccionado);
        cliente.setIdcontrato(contratoseleccionado.getIdcontrato());
        cliente.setMunicipio(municipioseleccionado);
        cliente.setIdmunicipio(municipioseleccionado.getIdmunicipio());
        cliente.setPuntoventa(puntoventaseleccionado);
        cliente.setIdpuntoventa(puntoventaseleccionado.getIdpuntoventa());
        cliente.setNumerocontrato(numerocontrato.getText().toString());
        cliente.setCantidadconsumidores(Integer.parseInt(cantidadconsumidores.getText().toString()));
        daoSession.insert(cliente);

        for (int i = 0; i < listadesactivar.size(); i++)
        {
            cliente = listadesactivar.get(i);
            cliente.setActivo(false);
            daoSession.insertOrReplace(cliente);
        }

        Intent intent = new Intent(NuevoClienteActivity.this, PrincipalActivity.class);
        startActivity(intent);
        finish();
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }

            for (String credential : DUMMY_CREDENTIALS) {
                String[] pieces = credential.split(":");
                if (pieces[0].equals(mEmail)) {
                    // Account exists, return true if the password matches.
                    return pieces[1].equals(mPassword);
                }
            }

            // TODO: register the new account here.
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                finish();
            } else {
                //mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }


        public void onBackPressed()
        {
            Intent intent = new Intent(NuevoClienteActivity.this, PrincipalActivity.class);
            startActivity(intent);
            finish();
        }
    }
}

