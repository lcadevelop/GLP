package cu.tecnomatica.android.glp.activities.localizacion;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import org.greenrobot.greendao.database.Database;
import org.oscim.android.MapView;
import org.oscim.android.canvas.AndroidGraphics;
import org.oscim.backend.CanvasAdapter;
import org.oscim.core.GeoPoint;
import org.oscim.layers.marker.ItemizedLayer;
import org.oscim.layers.marker.MarkerItem;
import org.oscim.layers.marker.MarkerSymbol;
import org.oscim.layers.tile.buildings.BuildingLayer;
import org.oscim.layers.tile.vector.VectorTileLayer;
import org.oscim.layers.tile.vector.labeling.LabelLayer;
import org.oscim.renderer.GLViewport;
import org.oscim.scalebar.DefaultMapScaleBar;
import org.oscim.scalebar.MapScaleBar;
import org.oscim.scalebar.MapScaleBarLayer;
import org.oscim.theme.VtmThemes;
import org.oscim.tiling.source.mapfile.MapFileTileSource;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import cu.tecnomatica.android.glp.R;
import cu.tecnomatica.android.glp.database.greendao.Atencionclientes;
import cu.tecnomatica.android.glp.database.greendao.AtencionclientesDao;
import cu.tecnomatica.android.glp.database.greendao.Casacomercial;
import cu.tecnomatica.android.glp.database.greendao.DaoMaster;
import cu.tecnomatica.android.glp.database.greendao.DaoSession;
import cu.tecnomatica.android.glp.database.greendao.Empresacomercializadora;
import cu.tecnomatica.android.glp.database.greendao.EmpresacomercializadoraDao;
import cu.tecnomatica.android.glp.database.greendao.Provincia;
import cu.tecnomatica.android.glp.database.greendao.Puntoventa;
import cu.tecnomatica.android.glp.database.greendao.Serviciosmecanicos;
import cu.tecnomatica.android.glp.database.greendao.ServiciosmecanicosDao;

public class MapaActivity extends Activity
{
    private static final String MAP_FILE = "/GLP/cuba.map";
    private static final String DB_FILE = "/GLP/daoglp.db";

    MapView mapView;
    MapScaleBar mapScaleBar;
    String latitud = "";
    String longitud = "";

    ArrayList<MarkerItem> arrayList;

    List<Provincia> listaprovincias;
    List<Casacomercial> casacomerciales;
    List<Puntoventa> puntosdeventas;
    List<Empresacomercializadora>listaempresacomercializadoras;
    List<Atencionclientes>listaatencionclientes;
    List<Serviciosmecanicos>listaserviciosmecanicos;

    Provincia provinciaactiva;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mapView = new MapView(this);
        setContentView(mapView);
        arrayList = new ArrayList<>();

        String dbPath = new File(Environment.getExternalStorageDirectory().getPath() + DB_FILE).getAbsolutePath();
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, dbPath);
        //DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "daoglp.db");
        Database database = helper.getWritableDb();
        DaoSession daoSession = new DaoMaster(database).newSession();

        listaprovincias = daoSession.getProvinciaDao().loadAll();
        provinciaactiva = listaprovincias.get(0);

        for (int i = 0; i < listaprovincias.size(); i++)
        {
            if (listaprovincias.get(i).getActiva())
            {
                provinciaactiva = listaprovincias.get(i);
            }
        }

        casacomerciales = daoSession.getCasacomercialDao().loadAll();
        puntosdeventas = daoSession.getPuntoventaDao().loadAll();
        listaempresacomercializadoras = daoSession.getEmpresacomercializadoraDao().queryBuilder().where(EmpresacomercializadoraDao.Properties.Idprovincia.like(provinciaactiva.getIdprovincia().toString())).list();
        listaatencionclientes = daoSession.getAtencionclientesDao().queryBuilder().where(AtencionclientesDao.Properties.Idprovincia.like(provinciaactiva.getIdprovincia().toString())).list();
        listaserviciosmecanicos = daoSession.getServiciosmecanicosDao().queryBuilder().where(ServiciosmecanicosDao.Properties.Idprovincia.like(provinciaactiva.getIdprovincia().toString())).list();

        try
        {
            Bundle bundle = this.getIntent().getExtras();
            latitud = bundle.getString("Latitud");
            longitud = bundle.getString("Longitud");
        }
        catch (Exception e)
        {
            latitud = "";
            longitud = "";
        }

        MapFileTileSource tileSource = new MapFileTileSource();
        String mapPath = new File(Environment.getExternalStorageDirectory().getPath() + MAP_FILE).getAbsolutePath();
        if (tileSource.setMapFile(mapPath))
        {
            VectorTileLayer tileLayer = mapView.map().setBaseMap(tileSource);

            mapView.map().layers().add(new BuildingLayer(mapView.map(), tileLayer));

            LabelLayer labelLayer = new LabelLayer(mapView.map(),tileLayer);

            mapView.map().layers().add(labelLayer);

            mapView.map().setTheme(VtmThemes.DEFAULT);

            mapScaleBar = new DefaultMapScaleBar(mapView.map());
            MapScaleBarLayer mapScaleBarLayer = new MapScaleBarLayer(mapView.map(), mapScaleBar);
            mapScaleBarLayer.getRenderer().setPosition(GLViewport.Position.BOTTOM_LEFT);
            mapScaleBarLayer.getRenderer().setOffset(5 * CanvasAdapter.getScale(), 0);
            mapView.map().layers().add(mapScaleBarLayer);



            if (latitud != "" || longitud != "")
            {
                CrearMarca();
                double lat = Double.parseDouble(latitud);
                double lon = Double.parseDouble(longitud);
                mapView.map().setMapPosition(lat, lon, 12 << 14);
            }
            else
            {
                double lat = Double.parseDouble(provinciaactiva.getLatitud());
                double lon = Double.parseDouble(provinciaactiva.getLongitud());
                mapView.map().setMapPosition(lat,lon, 3 << 12);
                CrearMarca();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (mapScaleBar != null)
            mapScaleBar.destroy();
        mapView.onDestroy();
        super.onDestroy();
    }

    public static Bitmap textAsBitmap(String text) {
        // adapted from https://stackoverflow.com/a/8799344/1476989
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(20);
        paint.setColor(Color.BLACK);
        paint.setTextAlign(Paint.Align.LEFT);
        float baseline = -paint.ascent(); // ascent() is negative
        int width = (int) (paint.measureText(text) + 0.0f); // round
        int height = (int) (baseline + paint.descent() + 0.0f);

        int trueWidth = width;
        if(width>height)
        {
            height=width;
        }
        else
        {
            width=height;
        }
        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(image);
        canvas.drawText(text, width/2-trueWidth/2, baseline, paint);
        return image;
    }

    public void CrearMarca()
    {
        CrearMarcaCasaComercial();

        //CrearMarcaTextoCasaComercial();

        CrearMarcaPuntosVenta();

        //CrearMarcaTextoPuntosVenta();

        //CrearMarcaEmpresaComercializadora();

        //CrearMarcaTextoEmpresaComercializadora();

        //CrearMarcaAtencionClientes();

        //CrearMarcaServiciosMecanicos();

        if (latitud != "" || longitud != "")
        {
            double lat = Double.parseDouble(latitud);
            double lon = Double.parseDouble(longitud);
            mapView.map().setMapPosition(lat, lon, 12 << 14);
        }
    }

    public void CrearMarcaCasaComercial()
    {
        arrayList = new ArrayList<>();

        org.oscim.backend.canvas.Bitmap bitmapPoicc = AndroidGraphics.drawableToBitmap(getResources().getDrawable(R.drawable.ic_home_green_800_24dp));
        MarkerSymbol symbolcc = new MarkerSymbol(bitmapPoicc, MarkerSymbol.HotspotPlace.BOTTOM_CENTER);

        for (int i = 0; i < casacomerciales.size(); i++)
        {
            double lat = Double.parseDouble(casacomerciales.get(i).getLatitud());
            double lon = Double.parseDouble(casacomerciales.get(i).getLongitud());
            GeoPoint geoPointcc = new GeoPoint(lat, lon);
            MarkerItem markerItemcc = new MarkerItem(casacomerciales.get(i).getNombre(), "Casa Comercial", geoPointcc);
            arrayList.add(markerItemcc);
        }
        ItemizedLayer<MarkerItem> markerItemItemizedLayercc = new ItemizedLayer<MarkerItem>(mapView.map(), arrayList, symbolcc, new ItemizedLayer.OnItemGestureListener<MarkerItem>() {
            @Override
            public boolean onItemSingleTapUp(int index, MarkerItem item)
            {
                Bundle bundle = new Bundle();
                bundle.putString("Nombre", casacomerciales.get(index).getNombre());
                bundle.putString("Direccion", casacomerciales.get(index).getDireccion());
                bundle.putString("Horario", casacomerciales.get(index).getHorario());
                bundle.putString("Telefono", casacomerciales.get(index).getTelefono());

                Intent intent = new Intent(getApplicationContext(), DetallesActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                return false;
            }

            @Override
            public boolean onItemLongPress(int index, MarkerItem item) {
                return false;
            }
        });
        mapView.map().layers().add(markerItemItemizedLayercc);
    }

    public void CrearMarcaTextoCasaComercial()
    {
        arrayList = new ArrayList<>();
        String texto_prueba = "Texto de Prueba";
        Bitmap bitmap = textAsBitmap(texto_prueba);
        org.oscim.backend.canvas.Bitmap bitmapicono = AndroidGraphics.drawableToBitmap(new BitmapDrawable(getResources(), bitmap));
        MarkerSymbol symboltexto = new MarkerSymbol(bitmapicono, MarkerSymbol.HotspotPlace.TOP_CENTER);

        for (int i = 0; i < casacomerciales.size(); i++)
        {
            bitmap = textAsBitmap(casacomerciales.get(i).getNombre());
            bitmapicono = AndroidGraphics.drawableToBitmap(new BitmapDrawable(getResources(), bitmap));
            symboltexto = new MarkerSymbol(bitmapicono, MarkerSymbol.HotspotPlace.CENTER);
            double lat = Double.parseDouble(casacomerciales.get(i).getLatitud());
            double lon = Double.parseDouble(casacomerciales.get(i).getLongitud());
            GeoPoint geoPointtexto = new GeoPoint(lat, lon);

            MarkerItem markerItemcc = new MarkerItem(casacomerciales.get(i).getNombre(), "Casa Comercial", geoPointtexto);
            arrayList.add(markerItemcc);
            ItemizedLayer<MarkerItem> markerItemItemizedLayertexto = new ItemizedLayer<MarkerItem>(mapView.map(), arrayList, symboltexto, null);
            mapView.map().layers().add(markerItemItemizedLayertexto);
            arrayList = new ArrayList<>();
        }
    }

    public void CrearMarcaPuntosVenta()
    {
        arrayList = new ArrayList<>();

        org.oscim.backend.canvas.Bitmap bitmapPoipp = AndroidGraphics.drawableToBitmap(getResources().getDrawable(R.drawable.ic_location_on_red_900_24dp));
        MarkerSymbol symbolpp = new MarkerSymbol(bitmapPoipp, MarkerSymbol.HotspotPlace.CENTER);

        for (int j = 0; j < puntosdeventas.size(); j++)
        {
            double lat = Double.parseDouble(puntosdeventas.get(j).getLatitud());
            double lon = Double.parseDouble(puntosdeventas.get(j).getLongitud());
            GeoPoint geoPointpp = new GeoPoint(lat,lon);
            MarkerItem markerItempp = new MarkerItem(puntosdeventas.get(j).getCodigo(), "Descripción", geoPointpp);
            arrayList.add(markerItempp);
        }

        ItemizedLayer<MarkerItem> markerItemItemizedLayerpp = new ItemizedLayer<MarkerItem>(mapView.map(), arrayList, symbolpp, new ItemizedLayer.OnItemGestureListener<MarkerItem>() {
            @Override
            public boolean onItemSingleTapUp(int index, MarkerItem item)
            {
                Bundle bundle = new Bundle();
                bundle.putString("Nombre", puntosdeventas.get(index).getNombre());
                bundle.putString("Direccion", puntosdeventas.get(index).getDireccion());
                bundle.putString("Horario", puntosdeventas.get(index).getHorario());
                bundle.putString("Telefono", puntosdeventas.get(index).getTelefono());

                Intent intent = new Intent(getApplicationContext(), DetallesActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                return false;
            }

            @Override
            public boolean onItemLongPress(int index, MarkerItem item) {
                return false;
            }
        });
        mapView.map().layers().add(markerItemItemizedLayerpp);
    }

    public void CrearMarcaTextoPuntosVenta()
    {
        arrayList = new ArrayList<>();
        String texto_prueba = "Texto de Prueba";
        Bitmap bitmap = textAsBitmap(texto_prueba);
        org.oscim.backend.canvas.Bitmap bitmapicono = AndroidGraphics.drawableToBitmap(new BitmapDrawable(getResources(), bitmap));
        MarkerSymbol symboltexto = new MarkerSymbol(bitmapicono, MarkerSymbol.HotspotPlace.TOP_CENTER);

        for (int i = 0; i < puntosdeventas.size(); i++)
        {
            bitmap = textAsBitmap("PV " + puntosdeventas.get(i).getNombre());
            bitmapicono = AndroidGraphics.drawableToBitmap(new BitmapDrawable(getResources(), bitmap));
            symboltexto = new MarkerSymbol(bitmapicono, MarkerSymbol.HotspotPlace.CENTER);
            double lat = Double.parseDouble(puntosdeventas.get(i).getLatitud());
            double lon = Double.parseDouble(puntosdeventas.get(i).getLongitud());
            GeoPoint geoPointtexto = new GeoPoint(lat, lon);

            MarkerItem markerItemcc = new MarkerItem(puntosdeventas.get(i).getNombre(), "Punto de Venta", geoPointtexto);
            arrayList.add(markerItemcc);
            ItemizedLayer<MarkerItem> markerItemItemizedLayertexto = new ItemizedLayer<MarkerItem>(mapView.map(), arrayList, symboltexto, null);
            mapView.map().layers().add(markerItemItemizedLayertexto);
            arrayList = new ArrayList<>();
        }
    }

    public void CrearMarcaEmpresaComercializadora()
    {
        arrayList = new ArrayList<>();

        org.oscim.backend.canvas.Bitmap bitmapPoiec = AndroidGraphics.drawableToBitmap(getResources().getDrawable(R.mipmap.ic_glp_empresa_comercial_foreground));
        MarkerSymbol symbolec = new MarkerSymbol(bitmapPoiec, MarkerSymbol.HotspotPlace.BOTTOM_CENTER);

        for (int i = 0; i < listaempresacomercializadoras.size(); i++)
        {
            double lat = Double.parseDouble(listaempresacomercializadoras.get(i).getLatitud());
            double lon = Double.parseDouble(listaempresacomercializadoras.get(i).getLongitud());
            GeoPoint geoPointec = new GeoPoint(lat,lon);
            MarkerItem markerItemec = new MarkerItem(listaempresacomercializadoras.get(i).getNombre(), "Descripción", geoPointec);
            arrayList.add(markerItemec);
        }

        ItemizedLayer<MarkerItem> markerItemItemizedLayerem = new ItemizedLayer<MarkerItem>(mapView.map(), arrayList, symbolec, new ItemizedLayer.OnItemGestureListener<MarkerItem>() {
            @Override
            public boolean onItemSingleTapUp(int index, MarkerItem item)
            {
                Bundle bundle = new Bundle();
                bundle.putString("Nombre", listaempresacomercializadoras.get(index).getNombre());
                bundle.putString("Direccion", listaempresacomercializadoras.get(index).getDireccion());
                bundle.putString("Horario", listaempresacomercializadoras.get(index).getHorario());
                bundle.putString("Telefono", listaempresacomercializadoras.get(index).getTelefono());

                Intent intent = new Intent(getApplicationContext(), DetallesActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                return false;
            }

            @Override
            public boolean onItemLongPress(int index, MarkerItem item) {
                return false;
            }
        });
        mapView.map().layers().add(markerItemItemizedLayerem);
    }

    public void CrearMarcaTextoEmpresaComercializadora()
    {
        arrayList = new ArrayList<>();
        String texto_prueba = "Texto de Prueba";
        Bitmap bitmap = textAsBitmap(texto_prueba);
        org.oscim.backend.canvas.Bitmap bitmapicono = AndroidGraphics.drawableToBitmap(new BitmapDrawable(getResources(), bitmap));
        MarkerSymbol symboltexto = new MarkerSymbol(bitmapicono, MarkerSymbol.HotspotPlace.TOP_CENTER);

        for (int i = 0; i < listaempresacomercializadoras.size(); i++)
        {
            bitmap = textAsBitmap("EC " + listaempresacomercializadoras.get(i).getProvincia().getNombre());
            bitmapicono = AndroidGraphics.drawableToBitmap(new BitmapDrawable(getResources(), bitmap));
            symboltexto = new MarkerSymbol(bitmapicono, MarkerSymbol.HotspotPlace.CENTER);
            double lat = Double.parseDouble(listaempresacomercializadoras.get(i).getLatitud());
            double lon = Double.parseDouble(listaempresacomercializadoras.get(i).getLongitud());
            GeoPoint geoPointtexto = new GeoPoint(lat, lon);

            MarkerItem markerItemcc = new MarkerItem(listaempresacomercializadoras.get(i).getNombre(), "Descripción", geoPointtexto);
            arrayList.add(markerItemcc);
            ItemizedLayer<MarkerItem> markerItemItemizedLayertexto = new ItemizedLayer<MarkerItem>(mapView.map(), arrayList, symboltexto, null);
            mapView.map().layers().add(markerItemItemizedLayertexto);
            arrayList = new ArrayList<>();
        }
    }

    public void CrearMarcaAtencionClientes()
    {
        arrayList = new ArrayList<>();

        org.oscim.backend.canvas.Bitmap bitmapPoiac = AndroidGraphics.drawableToBitmap(getResources().getDrawable(R.mipmap.ic_glp_atencion_cliente_foreground));
        MarkerSymbol symbolac = new MarkerSymbol(bitmapPoiac, MarkerSymbol.HotspotPlace.BOTTOM_CENTER);

        for (int i = 0; i < listaatencionclientes.size(); i++)
        {
            double lat = Double.parseDouble(listaatencionclientes.get(i).getLatitud());
            double lon = Double.parseDouble(listaatencionclientes.get(i).getLongitud());
            GeoPoint geoPointac = new GeoPoint(lat,lon);
            MarkerItem markerItemac = new MarkerItem(listaatencionclientes.get(i).getNombre(), "Descripción", geoPointac);
            arrayList.add(markerItemac);
        }

        ItemizedLayer<MarkerItem> markerItemItemizedLayerac = new ItemizedLayer<MarkerItem>(mapView.map(), arrayList, symbolac, new ItemizedLayer.OnItemGestureListener<MarkerItem>() {
            @Override
            public boolean onItemSingleTapUp(int index, MarkerItem item)
            {
                Bundle bundle = new Bundle();
                bundle.putString("Nombre", listaatencionclientes.get(index).getNombre());
                bundle.putString("Direccion", listaatencionclientes.get(index).getDireccion());
                bundle.putString("Horario", listaatencionclientes.get(index).getHorario());
                bundle.putString("Telefono", listaatencionclientes.get(index).getTelefono());

                Intent intent = new Intent(getApplicationContext(), DetallesActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                return false;
            }

            @Override
            public boolean onItemLongPress(int index, MarkerItem item) {
                return false;
            }
        });
        mapView.map().layers().add(markerItemItemizedLayerac);
    }

    public void CrearMarcaServiciosMecanicos()
    {
        arrayList = new ArrayList<>();

        org.oscim.backend.canvas.Bitmap bitmapPoism = AndroidGraphics.drawableToBitmap(getResources().getDrawable(R.mipmap.ic_glp_servicios_mecanicos_foreground));
        MarkerSymbol symbolsm = new MarkerSymbol(bitmapPoism, MarkerSymbol.HotspotPlace.BOTTOM_CENTER);

        for (int i = 0; i < listaserviciosmecanicos.size(); i++)
        {
            double lat = Double.parseDouble(listaserviciosmecanicos.get(i).getLatitud());
            double lon = Double.parseDouble(listaserviciosmecanicos.get(i).getLongitud());
            GeoPoint geoPointsm = new GeoPoint(lat,lon);
            MarkerItem markerItemsm = new MarkerItem(listaserviciosmecanicos.get(i).getNombre(), "Descripción", geoPointsm);
            arrayList.add(markerItemsm);
        }

        ItemizedLayer<MarkerItem> markerItemItemizedLayersm = new ItemizedLayer<MarkerItem>(mapView.map(), arrayList, symbolsm, new ItemizedLayer.OnItemGestureListener<MarkerItem>() {
            @Override
            public boolean onItemSingleTapUp(int index, MarkerItem item)
            {
                Bundle bundle = new Bundle();
                bundle.putString("Nombre", listaserviciosmecanicos.get(index).getNombre());
                bundle.putString("Direccion", listaserviciosmecanicos.get(index).getDireccion());
                bundle.putString("Horario", listaserviciosmecanicos.get(index).getHorario());
                bundle.putString("Telefono", listaserviciosmecanicos.get(index).getTelefono());

                Intent intent = new Intent(getApplicationContext(), DetallesActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                return false;
            }

            @Override
            public boolean onItemLongPress(int index, MarkerItem item) {
                return false;
            }
        });
        mapView.map().layers().add(markerItemItemizedLayersm);
    }
}