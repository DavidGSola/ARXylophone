package com.CojiSoft.ARXylophone;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * Pantalla d men&uacute;
 * @author DavidGSola
 *
 */
public class PantallaLicencia extends Activity implements OnClickListener
{
    private Button mBotonContinuar;

    /* (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    public void onCreate(Bundle savedInstanceState)
    {	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pantalla_licencia);

        mBotonContinuar = (Button) findViewById(R.id.boton_continuar);
        mBotonContinuar.setOnClickListener(this);
    }
    
    /* (non-Javadoc)
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    public void onClick(View v)
    {
        switch (v.getId())
        {
	        case R.id.boton_continuar:
	            Intent i = new Intent(this, PantallaMenu.class);
	            startActivity(i);
	            break;
        }
    }
}
