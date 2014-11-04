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