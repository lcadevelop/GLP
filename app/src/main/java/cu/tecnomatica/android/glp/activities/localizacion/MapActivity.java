package cu.tecnomatica.android.glp.activities.localizacion;

import android.app.Activity;
import android.os.Bundle;
import org.oscim.android.MapPreferences;
import org.oscim.android.MapView;
import org.oscim.map.Map;

import cu.tecnomatica.android.glp.R;

public class MapActivity extends Activity
{
    MapView mMapView;
    Map mMap;
    MapPreferences mPrefs;

    protected final int mContentView;

    public MapActivity(int contentView)
    {
        mContentView = contentView;
    }

    public MapActivity()
    {
        this(R.layout.activity_mapa);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(mContentView);

        setTitle(getClass().getSimpleName());

        mMapView = (MapView) findViewById(R.id.idmapa);
        mMap = mMapView.map();
        mPrefs = new MapPreferences(MapActivity.class.getName(), this);
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        mPrefs.load(mMapView.map());
        mMapView.onResume();
    }

    @Override
    protected void onPause()
    {
        mPrefs.save(mMapView.map());
        mMapView.onPause();

        super.onPause();
    }

    @Override
    protected void onDestroy()
    {
        mMapView.onDestroy();

        super.onDestroy();
    }
}
