package cu.tecnomatica.android.glp.activities.localizacion;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import cu.tecnomatica.android.glp.R;
import cu.tecnomatica.android.glp.activities.PrincipalActivity;
import cu.tecnomatica.android.glp.activities.cliente.NuevoClienteActivity;

public class DetallesActivity extends AppCompatActivity
{
    TextView textViewNombre;
    TextView textViewDireccion;
    TextView textViewHorario;
    TextView textViewTelefono;
    TextView llamar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        PedirPermiso();

        toolbar.setNavigationOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view)
            {
                finish();
            }
        });

        final Bundle bundle = this.getIntent().getExtras();

        textViewNombre = (TextView)findViewById(R.id.id_texto_nombre_llenar);
        textViewNombre.setText(bundle.getString("Nombre"));

        textViewDireccion = (TextView)findViewById(R.id.id_texto_direccion_llenar);
        textViewDireccion.setText(bundle.getString("Direccion"));

        textViewHorario = (TextView)findViewById(R.id.id_texto_horario_llenar);
        textViewHorario.setText(bundle.getString("Horario"));

        textViewTelefono = (TextView)findViewById(R.id.id_texto_telefono_llenar);
        textViewTelefono.setText(bundle.getString("Telefono"));

        llamar = (TextView) findViewById(R.id.id_texto_llamar);

        llamar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                try
                {
                    String[] numeros = bundle.getString("Telefono").split(" ");
                    String[] llamar = numeros[0].split(",");
                    startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + llamar[0])));
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
    }

    public void PedirPermiso()
    {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 2);
        }
    }

}
