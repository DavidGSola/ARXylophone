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

import android.view.GestureDetector;
import android.view.MotionEvent;

/**
 * Clase encargada del reconocimiento de gestos en pantalla, 
 * en este caso particular se encarga del reconocimiento
 * de un toque en la pantalla para enfocar.
 * @author DavidGSola
 *
 */
public class GestorListener extends GestureDetector.SimpleOnGestureListener
{
	/**
	 * Referencia al objeto de la clase {@link ARXylophoneBase}
	 */
	ARXylophoneBase mARX;
	
	/**
	 * Constructor por defecto
	 * @param arx objeto de la clase {@link ARXylophoneBase}
	 */
	public GestorListener(ARXylophoneBase arx)
	{
		mARX = arx;
	}

	/* (non-Javadoc)
	 * @see android.view.GestureDetector.SimpleOnGestureListener#onSingleTapUp(android.view.MotionEvent)
	 */
	public boolean onSingleTapUp(MotionEvent e)
	{
		mARX.enfocar();
		
		return true;
	}
	
	/* (non-Javadoc)
	 * @see android.view.GestureDetector.SimpleOnGestureListener#onDoubleTap(android.view.MotionEvent)
	 */
	@Override
	public boolean onDoubleTap(MotionEvent e) 
	{
		mARX.mostrarDialogoOpciones();
		
		return true;
	}
}