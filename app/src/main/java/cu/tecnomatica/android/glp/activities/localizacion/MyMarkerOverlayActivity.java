package cu.tecnomatica.android.glp.activities.localizacion;


import android.os.Bundle;
import android.widget.Toast;
import org.oscim.event.Gesture;
import org.oscim.event.GestureListener;
import org.oscim.event.MotionEvent;
import org.oscim.layers.Layer;
import org.oscim.layers.marker.ItemizedLayer;
import org.oscim.layers.marker.MarkerItem;
import org.oscim.layers.marker.MarkerSymbol;
import org.oscim.layers.tile.bitmap.BitmapTileLayer;
import org.oscim.map.Map;
import org.oscim.tiling.TileSource;
import org.oscim.tiling.source.UrlTileSource;
import org.oscim.tiling.source.bitmap.DefaultSources;
import org.oscim.tiling.source.HttpEngine;

public class MyMarkerOverlayActivity extends MapActivity implements ItemizedLayer.OnItemGestureListener<MarkerItem>
{
    static final boolean BILLBOARDS = true;
    MarkerSymbol mFocusMarker;
    ItemizedLayer<MarkerItem> mMarkerLayer;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        createLayers();
    }

    void createLayers()
    {
        mMap.layers().add(new MapEventsReceiver(mMap));

        TileSource tileSource = DefaultSources.OPENSTREETMAP.httpFactory(new HttpEngine.Factory() {
            @Override
            public HttpEngine create(UrlTileSource tileSource) {
                return null;
            }
        }).build();
        mMap.layers().add(new BitmapTileLayer(mMap, tileSource));

    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    @Override
    public boolean onItemSingleTapUp(int index, MarkerItem item)
    {
        if (item.getMarker() == null)
            item.setMarker(mFocusMarker);
        else
            item.setMarker(null);

        Toast.makeText(this, "Marker tap\n" + item.getTitle(), Toast.LENGTH_SHORT).show();
        return true;
    }

    @Override
    public boolean onItemLongPress(int index, MarkerItem item)
    {
        if (item.getMarker() == null)
            item.setMarker(mFocusMarker);
        else
            item.setMarker(null);

        Toast.makeText(this, "Marker long press\n" + item.getTitle(), Toast.LENGTH_SHORT).show();
        return true;
    }

    class MapEventsReceiver extends Layer implements GestureListener
    {
        MapEventsReceiver(Map map)
        {
            super(map);
        }

        @Override
        public boolean onGesture(Gesture g, MotionEvent e)
        {
            return false;
        }
    }
}
