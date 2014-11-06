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

import coreXilofono.ARXylophoneGuiada;
import coreXilofono.ARXylophoneLibre;
import coreXilofono.ARXylophoneRecord;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

/**
 * Pantalla de selecci&oacute;n de la mano para jugar
 * @author DavidGSola
 *
 */
public class PantallaSeleccionMano extends Activity implements OnClickListener
{
    private ImageButton mBotonManoIzq;
    private ImageButton mBotonManoDer;
    
    private String siguientePantalla;
    private String informacionExtra;
    
    public void onCreate(Bundle savedInstanceState)
    {	
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        
        siguientePantalla = bundle.getString("modo");
        informacionExtra  =  bundle.getString("infoExtra");
        
        setContentView(R.layout.pantalla_seleccion_mano);

        mBotonManoIzq = (ImageButton) findViewById(R.id.boton_mano_izquierda);
        mBotonManoDer = (ImageButton) findViewById(R.id.boton_mano_derecha);
        mBotonManoIzq.setOnClickListener(this);
        mBotonManoDer.setOnClickListener(this);
    }
    
    /**
     * Listener para los botones
     */
    public void onClick(View v)
    {
        switch (v.getId())
        {
	        case R.id.boton_mano_izquierda:
	        	iniciarJuego(0);
	            break;
	        case R.id.boton_mano_derecha:
	        	iniciarJuego(1);
	        	break;
        }
    }
    
    /**
     * Inicia el juego correspondiente con la mano seleccionada
     * @param mano
     */
    private void iniciarJuego(int mano)
    {
    	Intent i;
    	if(siguientePantalla.equals("libre"))
    	{
            i = new Intent(this, ARXylophoneLibre.class);
            i.putExtra("mano", mano);
            startActivity(i);
    	}else if(siguientePantalla.equals("guiado"))
    	{
            i = new Intent(this, ARXylophoneGuiada.class);
            i.putExtra("mano", mano);
            i.putExtra("infoExtra", informacionExtra);
            startActivity(i);
    	}else if(siguientePantalla.equals("record"))
    	{
            i = new Intent(this, ARXylophoneRecord.class);
            i.putExtra("mano", mano);
            i.putExtra("infoExtra", informacionExtra);
            startActivity(i);
    	}
    }
}
