package cu.tecnomatica.android.glp.activities.localizacion;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import cu.tecnomatica.android.glp.R;

public class DetallesActivity extends AppCompatActivity
{
    TextView textViewNombre;
    TextView textViewDireccion;
    TextView textViewHorario;
    TextView textViewTelefono;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle bundle = this.getIntent().getExtras();

        textViewNombre = (TextView)findViewById(R.id.id_texto_nombre_llenar);
        textViewNombre.setText(bundle.getString("Nombre"));

        textViewDireccion = (TextView)findViewById(R.id.id_texto_direccion_llenar);
        textViewDireccion.setText(bundle.getString("Direccion"));

        textViewHorario = (TextView)findViewById(R.id.id_texto_horario_llenar);
        textViewHorario.setText(bundle.getString("Horario"));

        textViewTelefono = (TextView)findViewById(R.id.id_texto_telefono_llenar);
        textViewTelefono.setText(bundle.getString("Telefono"));
    }

}
