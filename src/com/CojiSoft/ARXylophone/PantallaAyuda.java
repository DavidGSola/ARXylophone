package com.CojiSoft.ARXylophone;

import Util.Utils;
import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

/**
 * Pantalla de ayuda.
 * @author DavidGSola
 *
 */
public class PantallaAyuda extends Activity
{
    /* (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Sets the Splash Screen Layout
        setContentView(R.layout.pantalla_ayuda);
        
        TextView test1 = (TextView)findViewById(R.id.actividad_ayuda_enlace);
        Spanned spanned = Html.fromHtml(getString(R.string.ayuda_enlace));
        test1.setMovementMethod(LinkMovementMethod.getInstance());
        test1.setText(spanned);

        Utils.enviarAnalyrics(this, "Pantalla Ayuda");
    }
}
