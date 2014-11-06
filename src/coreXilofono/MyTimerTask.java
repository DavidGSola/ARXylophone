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