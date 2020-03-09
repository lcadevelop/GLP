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
import android.widget.TextView;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import cu.tecnomatica.android.glp.BuildConfig;
import cu.tecnomatica.android.glp.R;

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

    String version = "";
    private final int duracion = 2000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_portada);
        //mContentView = findViewById(R.id.id_texto_portada);
        version = BuildConfig.VERSION_NAME;
        TextView texto_vesion = findViewById(R.id.id_texto_version);
        texto_vesion.setText("Versión: " + version);

        AssetManager assetManager = getAssets();

        try
        {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE))
                {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                }
                else
                {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                }
            }
            else
            {
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

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(PortadaActivity.this, PrincipalActivity.class);
                        startActivity(intent);
                        finish();
                    };
                }, duracion);
            }

        }
        catch (Exception exc)
        {
            Log.e("Error de Copia", exc.getMessage());
        }
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        AssetManager assetManager = getAssets();

        switch (requestCode)
        {
            case 1:
            {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    try
                    {
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

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(PortadaActivity.this, PrincipalActivity.class);
                                startActivity(intent);
                                finish();
                            };
                        }, duracion);
                    }
                    catch (Exception e)
                    {
                    }
                }
                else
                {
                    finish();
                }
            }
            return;
        }
    }
}