package coreXilofono;

import Util.Utils;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import com.CojiSoft.ARXylophone.R;

/**
 * Clase que hereda de {@link ARXylophoneBase}. Permite tocar
 * un xil&oacute;fono al antojo del usuario.s
 *  
 * @author DavidGSola
 *
 */
public class ARXylophoneLibre extends ARXylophoneBase{
	
	/**
	 * Implementaci&oacute;n del m&eacute;todo {@link ARXylophoneBase#iniciarModo()}.
	 * Inicia el modo de juego a LIBRE
	 */
	@Override
	protected void iniciarModo() 
	{
		setModo(TipoJuego.LIBRE.ordinal(), 
				getIntent().getExtras().getInt("mano"));

        Utils.enviarAnalyrics(this, "Pantalla Modo Libre");
	}
	
	/**
	 * Implementaci&oacute;n del m&eacute;todo {@link ARXylophoneBase#iniciarLayout()}.
	 * Carga el <code>Layout</code> que corresponde con este tipo de juego
	 */
	@Override
	protected void iniciarLayout()
	{
        LayoutInflater inflater = LayoutInflater.from(this);
		mUILayout = (RelativeLayout) inflater.inflate(R.layout.camara_overlay_libre, null, false);
	}

	/**
	 * Implementaci&oacute;n del m&eacute;todo {@link ARXylophoneBase#actualizarFrame(boolean, boolean)}.
     * Se encarga de modificar la GUI para notificar al usuario que el Target se ha 
     * detectado 
	 */
	@Override
	public void actualizarFrame(boolean primeraDeteccion, boolean detectado) 
	{
		// Si es la primera vez que se detecta:
    	if(primeraDeteccion)
    	{
    		loadingDialogHandler.sendEmptyMessage(AccionesGUI.OCULTAR.ordinal());
    		textoDetectandoHandler.sendEmptyMessage(AccionesGUI.OCULTAR.ordinal());
    	}
    	else	// No es la primera vez que se detecta
    		if(detectado)
	    	{
    			// Se ocultan los elementos del GUI para que el usuario sepa
        		// que se ha detectado el Target
        		loadingDialogHandler.sendEmptyMessage(AccionesGUI.OCULTAR.ordinal());
        		textoDetectandoHandler.sendEmptyMessage(AccionesGUI.OCULTAR.ordinal());
	    	}
    		else
    		{
		    	// Se muestran los elementos del GUI para que el usuario sepa
        		// que no está detectado el Target
        		loadingDialogHandler.sendEmptyMessage(AccionesGUI.MOSTRAR.ordinal());
        		textoDetectandoHandler.sendEmptyMessage(AccionesGUI.MOSTRAR.ordinal());
    		}
	}
}
