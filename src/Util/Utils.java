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

package Util;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

import com.CojiSoft.ARXylophone.GlobalState;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.Tracker;

/**
 * Clase de utilidades con diversas funciones que pueden
 * ser de utilidad durante el desarrollo de la aplicaci&oacute;n
 * 
 * @author DavidGSola
 *
 */
public class Utils {
	
	 /**
     * Concatena 2 <code>Arrays</code> de String
     * @param a Primer <code>Array</code> de String
     * @param b Segundo <code>Array</code> de String
     * @return Nuevo <code>Array</code> formado por la concatenaci&oacute;n de a y b
     */
    public static String[] concatenarString(String[] a, String[] b)
    {
    	String[] temp = (String[]) Array.newInstance(String.class,  a.length + b.length);
		System.arraycopy(a, 0, temp, 0, a.length);
		System.arraycopy(b, 0, temp, a.length, b.length);
		
    	return temp;
    }
    
    /**
     * Formatea un float a un formato de fecha <code>("m:ss:SSS")</code>
     * @param tiempo en ms
     * @return un <code>String</code> con el float formateado
     */
	public static String convertirFloatToTime(float tiempo)
    {
    	Date date = new Date((long)tiempo);
    	SimpleDateFormat formatter = new SimpleDateFormat("m:ss:SSS");
    	String dateFormatted = formatter.format(date);
    	
    	return dateFormatted;
    }
	
	/**
	 * Crea un <code>AlertDialog</code> con un mensaje y en un contexto
	 * espec&iacute;fico. Este AlertDialog tiene un solo bot&oacute;n para cerrarlo
	 * @param mensaje <code>String</code> con el mensaje a mostrar en la alerta
	 * @param ctx Contexto donde se va a crear la alerta
	 * @return AlertDialog creado
	 */
	public static AlertDialog crearDialogoError(String mensaje, Context ctx)
	{
		AlertDialog dialogoError = new AlertDialog.Builder(ctx).create();
		dialogoError.setButton
        (
        	DialogInterface.BUTTON_POSITIVE,
            "Cerrar",
            new DialogInterface.OnClickListener()
            {
            	public void onClick(DialogInterface dialog, int which)
            	{
            		System.exit(1);
                }
            }
        );

        dialogoError.setMessage(mensaje);
        dialogoError.show();
                
        return dialogoError;
	}
	
	/**
	 * Crea un mensaje de tipo <code>Toast</code> con un mensaje y 
	 * en un contexto espec&iacute;fico
	 * @param mensaje <code>String</code> con el mensaje a mostrar en la alerta
	 * @param ctx Contexto donde se va a crear la alerta
	 */
	public static void crearToast(String mensaje, Context ctx)
	{
		Toast.makeText
        (
        	ctx,
            mensaje,
            Toast.LENGTH_SHORT
        ).show();
	}
	
	/**
	 * Devuelve la hora actual en ms
	 * @return <code>long</code> con la hora actual en ms
	 */
	public static long obtenerHoraActual()
	{
    	Calendar c = Calendar.getInstance();
    	return c.getTimeInMillis();
	}
	
	public static void enviarAnalyrics(Activity activity, String nombrePantalla)
	{
		// Obtener Tracker
        Tracker t = ((GlobalState) activity.getApplication()).getTracker();
        
        // Setear la pantalla
        t.set(Fields.SCREEN_NAME, nombrePantalla);

        // Enviar
        t.send(MapBuilder.createAppView().build());
	}
}