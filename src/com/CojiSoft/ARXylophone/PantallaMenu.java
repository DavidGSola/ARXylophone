/**
 * Copyright 2014 David González Sola (hispalos@gmail.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.CojiSoft.ARXylophone;

import Util.Utils;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

/**
 * Pantalla d men&uacute;
 * @author DavidGSola
 *
 */
public class PantallaMenu extends Activity implements OnClickListener
{
    private ImageButton mBotonStart;
    private ImageButton mBotonAyuda;
    private ImageButton mBotonSalir;


    /* (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    public void onCreate(Bundle savedInstanceState)
    {	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pantalla_menu);

        mBotonStart = (ImageButton) findViewById(R.id.boton_menu_start);
        mBotonAyuda = (ImageButton) findViewById(R.id.boton_menu_help);
        mBotonSalir = (ImageButton) findViewById(R.id.boton_menu_salir);
        mBotonStart.setOnClickListener(this);
        mBotonAyuda.setOnClickListener(this);
        mBotonSalir.setOnClickListener(this);
        
        Utils.enviarAnalyrics(this, "Pantalla Menu");
    }
    
    /* (non-Javadoc)
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    public void onClick(View v)
    {
        switch (v.getId())
        {
	        case R.id.boton_menu_start:
	            Intent i = new Intent(this, PantallaSeleccion.class);
	            startActivity(i);
	            break;
	        case R.id.boton_menu_help:
	            Intent i2 = new Intent(this, PantallaAyuda.class);
	            startActivity(i2);
	        	break;
	        case R.id.boton_menu_salir:
	        	Intent intent = new Intent(Intent.ACTION_MAIN);
		        intent.addCategory(Intent.CATEGORY_HOME);
		        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		        startActivity(intent);
	        	break;
        }
    }
}
