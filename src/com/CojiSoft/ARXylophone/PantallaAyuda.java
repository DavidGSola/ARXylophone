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
