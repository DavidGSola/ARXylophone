package coreXilofono;
import java.util.TimerTask;

/**
 * Clase para desbloquear un bot&oacute;n pulsado despu&eacute;s de un tiempo
 * predeterminado, para ello es necesario heredar de la clase
 * TimerTask.
 * 
 * @author DavidGSola
 * @version 1.0
 */
public class MyTimerTask extends TimerTask{

	/**
	 * Nombre de la tecla a desbloquear
	 */
	String mTecla;

	/**
	 * Desbloquea una tecla para poder ser utilizada
	 * de nuevo
	 * @param tecla nombre de la tecla a desbloquear.
	 */
    public native void desbloquearTecla(char tecla);
	
	/**
	 * Constructor por defecto.
	 * @param tecla Tecla a desbloquear.
	 */
	public MyTimerTask(String tecla) 
	{
		this.mTecla = tecla;
	}
	
	/**
	 * M&eacute;todo heredado de TimerTask que se ejecutar&aacute; cuando
	 * se llame a un objeto de la clase MyTimerTask ejecutado
	 * mediante un Timer.
	 */
	@Override
	public void run() 
	{
		desbloquearTecla(mTecla.charAt(0));
	}
}