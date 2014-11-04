package com.CojiSoft.ARXylophone;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

/**
 * Pantalla de Splash
 * @author DavidGSola
 *
 */
public class PantallaSplashScreen extends Activity
{
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.pantalla_splash);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable()
        {
            public void run()
            {
                // Iniciar la Actividad PantallaMenu al pasar dos segundos
                startActivity(new Intent(PantallaSplashScreen.this,
                        PantallaLicencia.class));
            }
        }, 2000L);
    }
}
