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

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Clase adaptador para los elementos de una lista de canciones
 * y los distintos modos de juego que tambi&eacute;n forman parte de la
 * lista.
 * @author DavidGSola
 *
 */
public class CancionesArrayAdapter extends ArrayAdapter<String>
{
	/**
	 * Contexto donde se utilizar&aacute; el adaptador
	 */
	private final Context context;
	
	/**
	 * Valores de la lista
	 */
	private final String[] valores;
	
	/**
	 * Constructor por defecto. Asocia el contexto y almacena
	 * los valores de la lista.
	 * 
	 * @param context contexto del adaptador
	 * @param valores <code>String</code> con los valores de la lista
	 */
	public CancionesArrayAdapter(Context context, String[] valores) 
	{
		// Se utiliza el layout R.layout.item_lista para los elementos de la lista
		super(context, R.layout.item_lista, valores);
		this.context = context;
		this.valores = valores;
	}

	/**
	 * Formatea los distintos campos de un elemento de la lista
	 * dependiendo del valor de la lista para esa posici&oacute;n
	 * 
	 * @param position actual posici&oacute;n de la lista a adaptar
	 * @param convertView 
	 * @param parent
	 * 
	 * @return vista del elemento de la vista adaptado correctamente
	 */
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) 
	{	
		// Se "infla" el layout correspondiente
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.item_lista, parent, false);
		
		// Se obtienen referencias a los distintos campos del layout
		View botonAmplView = rowView.findViewById(R.id.botonImagen);
		ImageView imageView = (ImageView) rowView.findViewById(R.id.iconItem);
		TextView textView = (TextView) rowView.findViewById(R.id.textItem);
		TextView scoreView = (TextView) rowView.findViewById(R.id.scoreItem);

		// Se obtiene el valor de la lista para la posición actual
		String s = valores[position];
	    
		if (s.equals( context.getString(R.string.modo_record)) ) 	// Si es el modo "Nueva Canción" se añade la imagen R.drawable.plus
	    	imageView.setImageResource(R.drawable.plus);
	    else if(s.equals( context.getString(R.string.modo_libre)))	// Si es el modo "Libre" se añade la imagen R.drawable.song
	    	imageView.setImageResource(R.drawable.song);
	    else 						// En cualquiero otro caso el elemento de la lista es una canción
	    {
	    	// Obtenemos las Prefs del usuario para conseguir la puntuación que ha obtenido el usuario
	    	// en esta canción
		    SharedPreferences prefs = context.getSharedPreferences("puntuaciones", Context.MODE_PRIVATE);
		    scoreView.setText(String.valueOf(prefs.getInt(s, 0)));
	 
		    // Eliminamos el ".txt" que forma parte del nombre de la canción
		    // NOTA: solo se elimina para mostrarse por pantalla, internamente
		    // el valor del elemento sigue manteniendo el ".txt"
	    	s = s.substring(0, s.length()-4);
	    	if(s.startsWith("_")) // Si comienza por "_" la canción ha sido creada por el usuario
	    	{
	    		// Se añade la imagen R.drawable.eliminar y se habilita la posibilidad
	    		// de eliminar la canción
	    		imageView.setImageResource(R.drawable.eliminar);
	    		botonAmplView.setOnClickListener(new View.OnClickListener() 
	    		{
                    public void onClick(View v) 
                    {
                    	AlertDialog.Builder builder = new AlertDialog.Builder(context);
                     	builder.setMessage(context.getString(R.string.contextual_borrar_cancion_mensaje) + " " + 
                     			valores[position].substring(1, valores[position].length()-4) + "?");
                		
                     	builder.setPositiveButton(context.getString(R.string.contextual_aceptar), new DialogInterface.OnClickListener() {
                     		public void onClick(DialogInterface dialog, int id) 
                     		{
                     			// En caso de desear borrar la canción se elimina de los
                     			// archivos
                            	context.deleteFile(valores[position]); 
                            	
                            	// Borramos la puntuación
                            	SharedPreferences prefs = context.getSharedPreferences("puntuaciones", Context.MODE_PRIVATE);
                        		
                            	Editor editor = prefs.edit();
                        		editor.putInt(valores[position], 0);
                        		editor.apply();
                            	
                        		// Recargamos la actividad para refrescar la lista
                            	Intent i = new Intent(context, PantallaSeleccion.class);
                            	context.startActivity(i);
                	        }
                     	});
                     	builder.setNegativeButton(context.getString(R.string.contextual_cancelar), new DialogInterface.OnClickListener() {
                	    	public void onClick(DialogInterface dialog, int id) 
                	    	{
                	    		
                	        }
                     	});

                		builder.create().show();
                    }
                });
	    		// Eliminamos el "_" de la canción
			    // NOTA: solo se elimina para mostrarse por pantalla, internamente
			    // el valor del elemento sigue manteniendo el ".txt"
	    		s = s.substring(1);
	    	}
	    }
	    
		// Asociamos al textView del elemento de la lista
		// el valor de s formateado
	    textView.setText(s);
	    
	    return rowView;
	}
}