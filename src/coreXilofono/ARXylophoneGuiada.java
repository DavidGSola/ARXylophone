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

package coreXilofono;

import java.io.IOException;

import Util.Utils;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.CojiSoft.ARXylophone.PantallaSeleccion;
import com.CojiSoft.ARXylophone.R;

/**
 * Clase que hereda de {@link ARXylophoneBase}. Permite leer
 * una canci&oacute;n y crear una gu&iacute;a en pantalla para que el usuario
 * pueda intentar tocar las notas de la canci&oacute;n obteniendo
 * una puntuaci&oacute;n.
 *  
 * @author DavidGSola
 *
 */
public class ARXylophoneGuiada extends ARXylophoneBase
{
	/**
	 * Canci&oacute;n a reproducir y sobre la que se debe crear una
	 * gu&iacute;a en la propia pantalla para que el usuario pueda
	 * seguir la canci&oacute;n.
	 */
	Cancion cancion;
	
    /**
     * Puntuaci&oacute;n de la canci&oacute;n
     */
    private int score = 0;	
	
	/**
	 * Views espec&iacute;ficos del modo GUIADO
	 */
    private View mLayoutScore;
    private TextView mTextoScore;
    private TextView mTextoTime;
    
    /**
     * Animacion simple para utilizar sobre elementos del GUI
     */
    private Animation animBajar;
    
    /**
     * Tiempo inicial de la canci&oacute;n
     */
    private long tiempoInicial;
    
    /**
     * Tiempo actual de la canci&oacute;n
     */
    private long tiempoActual;
    
    /**
     * Destruye las animaciones de los Hits en la pantalla
     */
	private native void limpiarAnimaciones();
    
    /** Añade un Hit a la pantalla */
	private native void addHit(char hitTecla, int idHit);
    
    /** Elimina un Hit de la pantalla */
	private native void deleteHit();
	
	/**
	 * Sobreescritura del m&eacute;todo {@link ARXylophoneBase#onCreate(Bundle)}
	 * en el que ahora se crea una canci&oacute;n.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
		
		// Creamos la canción
		try {
			cancion = new Cancion(this, bundle.getString("infoExtra"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Implementaci&oacute;n del m&eacute;todo {@link ARXylophoneBase#iniciarModo()}.
	 * Inicia el modo de juego a GUIADO
	 */
	@Override
	protected void iniciarModo() 
	{
		setModo(TipoJuego.GUIADO.ordinal(), 
					getIntent().getExtras().getInt("mano"));

        Utils.enviarAnalyrics(this, "Pantalla Modo Guiado");
	}
	
	/**
	 * Sobreescritura del m&eacute;todo {@link ARXylophoneBase#iniciarGUI()} en el que ahora se crean los 
	 * layouts del score y la animaci&oacute;n para la aparici&oacute;n del Layout.
	 */
	@Override
	protected void iniciarGUI()
	{
		super.iniciarGUI();
        mLayoutScore = mUILayout.findViewById(R.id.layoutScore);
        mTextoScore = (TextView) mUILayout.findViewById(R.id.textoScore);
        mTextoTime = (TextView) mUILayout.findViewById(R.id.textoTime);

        animBajar = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.bajar);  
	}
	
	/**
	 * Implementaci&oacute;n del m&eacute;todo {@link ARXylophoneBase#iniciarLayout()}.
	 * Carga el <code>Layout</code> que corresponde con este tipo de juego
	 */
	@Override
	protected void iniciarLayout() 
	{
    	LayoutInflater inflater = LayoutInflater.from(this);
        mUILayout = (RelativeLayout) inflater.inflate(R.layout.camara_overlay_guiada, null, false);
	}
	
	/**
	 * Sobreescritura del m&eacute;todo {@link ARXylophoneBase#playKey(String)}
	 * en el que se produce el sonido y adem&aacute;s se lleva
	 * a cabo una comprobaci&oacute;n de dicha nota en la canci&oacute;n
	 */
	@Override
	public void playKey(String key) 
	{
		super.playKey(key);
		
		boolean result = cancion.comprobarHit(tiempoActual-tiempoInicial, key.charAt(0));

    	if(result)
    		deleteHit();
	}

	/**
	 * Implementaci&oacute;n del m&eacute;todo {@link ARXylophoneBase#actualizarFrame(boolean, boolean)}.
     * Actualiza cada frame teniendo en cuenta el tiempo actual e inicial.
     * Se encarga de notificar de delegar sobre m&eacute;todos para añadir nuevos 
     * hits en pantalla y de modificar la GUI en funci&oacute;n de si se ha detectado o no el Target.
	 */
	@Override
	public void actualizarFrame(boolean primeraDeteccion, boolean detectado) {
		
		// Si es la primera vez que se detecta:
    	if(primeraDeteccion)
    	{
    		// Se inicia el tiempo
    		tiempoInicial = Utils.obtenerHoraActual();
    		
    		// Se ocultan los elementos del GUI para que el usuario sepa
    		// que se ha detectado el Target
    		loadingDialogHandler.sendEmptyMessage(AccionesGUI.OCULTAR.ordinal());
    		textoDetectandoHandler.sendEmptyMessage(AccionesGUI.OCULTAR.ordinal());
    		// Se muesta el cartel de score utilizando una animación
    		runOnUiThread(new Runnable() 
            {
    	        public void run() 
    	        {
    	        	mLayoutScore.startAnimation(animBajar);
    	        	mLayoutScore.setVisibility(View.VISIBLE);
    	        }
            });
    		
    	}
    	else	// No es la primera vez que se detecta
    	{
    		if(detectado)
	    	{
    			// Se ocultan los elementos del GUI para que el usuario sepa
        		// que se ha detectado el Target
        		loadingDialogHandler.sendEmptyMessage(AccionesGUI.OCULTAR.ordinal());
        		textoDetectandoHandler.sendEmptyMessage(AccionesGUI.OCULTAR.ordinal());
        		
    			cancion.setTiempo(tiempoActual-tiempoInicial);
    			if(!cancion.isFin())
    			{
			    	tiempoActual = Utils.obtenerHoraActual();
			    	
			    	// Se comprueba si es necesario añadir un nuevo hit
			    	Hit hitActual = cancion.nextHit();
			    	if(hitActual != null)
			    		addHit(hitActual.getNota(), hitActual.getId());
			    	
			    	actualizarTextViewTime();
    			}
    			else // Fin de la canción
    			{
    				finalizar();
    			}
	    	}
    		else // Se actualiza tiempoInicial para que la canción no continue
    		{
		    	tiempoActual = Utils.obtenerHoraActual();
    			tiempoInicial = tiempoActual - cancion.getTiempoActual();
    			
    			// Se muestran los elementos del GUI para que el usuario sepa
        		// que no está detectado el Target
        		loadingDialogHandler.sendEmptyMessage(AccionesGUI.MOSTRAR.ordinal());
        		textoDetectandoHandler.sendEmptyMessage(AccionesGUI.MOSTRAR.ordinal());
    		}
    	}
	}
	
	/**
	 * Notifica a la Canci&oacute;n que hay que eliminar el 
     * ultimo Hit pues ya ha pasado el tiempo.
	 * @param id ID del hit a eliminar
	 */
    public void eliminarHit(int id)
    {
    	cancion.eliminarHit(id);
    }
    
    /**
     * M&eacute;todo para reiniciar la canci&oacute;n. Se llama desde un
     * bot&oacute;n del GUI.
     * @param view
     */
    public void reiniciarCancion(View view)
    {
    	tiempoInicial = Utils.obtenerHoraActual();
		
    	// Llamada nativa para limpiar la pantalla
    	limpiarAnimaciones();
    	
    	try {
			cancion.reiniciar();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    	actualizarTextViewScore();
    	actualizarTextViewTime();
    }

    /**
     * M&eacute;todo para actualizar el score y la GUI
     * @param newScore nueva puntuaci&oacute;n
     */
    public void actualizarScore(int newScore)
    {
    	score = newScore;
    	actualizarTextViewScore();
    }
    
    /**
     * M&eacute;todo para actualizar la GUI del score.
     */
    private void actualizarTextViewScore()
    {
    	runOnUiThread(new Runnable() 
        {
	        public void run() 
	        {
	        	mTextoScore.setText(String.valueOf(score));
	        }
        });
    }
    
    /**
     * M&eacute;todo para actualizar la GUI del tiempo de canci&oacute;n.
     */
    private void actualizarTextViewTime()
    {
    	runOnUiThread(new Runnable() 
        {
	        public void run() 
	        {
	        	mTextoTime.setText(Utils.convertirFloatToTime(cancion.getTiempoActual()));
	        }
        });
    }
	
	/**
	 * Sobreescritura del m&eacute;todo {@link ARXylophoneBase#onDestroy()}
	 * en el que se limpian las animaciones antes de finalizar
	 * la actividad.
	 */
	@Override
	protected void onDestroy()
	{
		limpiarAnimaciones();
		super.onDestroy();
	}
    
	/**
	 * Sobreescritura del m&eacute;todo {@link ARXylophoneBase#finalizar()}
	 * En este caso se deben actualizar la m&aacute;ximas puntuaci&oacute;n de la
	 * canci&oacute;n dentro de los Prefs del usuario y añadir unos valores
	 * al <code>Intent</cdeo> para mostrar en la siguiente Actividad.
	 */
    @Override
    protected void finalizar()
    {
    	finish();
    	
    	// Sobreescribimos la máxima puntuación de la canción en caso
    	// de haber obtenido una puntuación mayor
    	SharedPreferences prefs = this.getSharedPreferences("puntuaciones", Context.MODE_PRIVATE);
		Editor editor = prefs.edit();
		
		if(prefs.getInt(cancion.getNombre(), 0) < score)
			editor.putInt(cancion.getNombre(),score);
			
		editor.apply();
		
    	Intent i = new Intent(this, PantallaSeleccion.class);
    	
	   	i.putExtra("nombreCancion", cancion.getNombre());
	   	i.putExtra("score", score);
	   	i.putExtra("maxScore", cancion.getMaxScore());
	   	startActivity(i);
    }
}
