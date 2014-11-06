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
