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

import java.util.HashMap;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import com.CojiSoft.ARXylophone.R;

/**
 * Clase para reproducir los sonidos del xi&oacute;fono.
 * Se encarga de cargar los sonidos y reproducirlos
 * @author DavidGSola
 */
public class SoundPlayer
{
	/**
	 * HashMap que mantiene en el primer elemento el nombre
	 * de la nota y en el segundo elemento un <code>Integer</code>
	 * como referencia al archivo de sonido
	 */
	HashMap<String, Integer> mSonidos;
	
	/**
	 * Objeto encargado de almacenar y reproducir los sonidos
	 */
	SoundPool mReproductor;
	
	/**
	 * Contexto de reproducci&oacute;n de los sonidos
	 */
	Context mContext;
	
	/**
	 * Constructor por defecto. Crea un HashMap para cargar los sonidos
	 * y tenerlos almacenados para su posterior reproducci&oacute;n
	 * @param ctx contexto donde se reproducir&aacute;n los sonidos
	 */
	public SoundPlayer(Context ctx)
	{
		mContext = ctx;
		mReproductor = new SoundPool(8, AudioManager.STREAM_MUSIC, 100);
		cargarSonidos();
	}
	
	/**
	 * Almacena los diferentes sonidos en el HashMap para su posterior
	 * reproducci&oacute;n. Los sonidos se encuentra guardados en R.raw
	 */
	private void cargarSonidos()
	{
		mSonidos = new HashMap<String, Integer>();
		mSonidos.put("C", mReproductor.load(mContext, R.raw.c, 1));
		mSonidos.put("D", mReproductor.load(mContext, R.raw.d, 1));
		mSonidos.put("E", mReproductor.load(mContext, R.raw.e, 1));
		mSonidos.put("F", mReproductor.load(mContext, R.raw.f, 1));
		mSonidos.put("G", mReproductor.load(mContext, R.raw.g, 1));
		mSonidos.put("A", mReproductor.load(mContext, R.raw.a, 1));
		mSonidos.put("B", mReproductor.load(mContext, R.raw.b, 1));
		mSonidos.put("c", mReproductor.load(mContext, R.raw.c1, 1));
	}

	/**
	 * Reproduce un sonido espec&iacute;fico teniendo en cuenta el volumen 
	 * actual del dispositivo
	 * @param nota nota asociada al sonido que se ha de reproducir
	 */
	public void reproducirSonido(String nota) 
	{
		AudioManager mgr = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
		int volumen = mgr.getStreamVolume(AudioManager.STREAM_MUSIC);
		
		// Obtenemos el sonido haciendo uso del HashMap
		mReproductor.play(mSonidos.get(nota), volumen, volumen, 1, 0, 1.0f);
	}
	
	/**
	 * Libera toda la memoria y los recuros nativos que utiliza 
	 * el {@link #mReproductor} para evitar problemas de memoria
	 */
	public void limpiar()
	{
		mReproductor.release();
	}
}
