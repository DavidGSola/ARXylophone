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

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLSurfaceView;

import com.qualcomm.QCAR.QCAR;

/**
 * Clase renderer que se encarga de todo lo relativo al
 * renderizado de la imagen haciendo uso de OpenGL ES
 * @author DavidGSola
 *
 */
public class ARXRenderer implements GLSurfaceView.Renderer
{
	/**
	 * Booleano para determinar si el renderizado
	 * se ha activado
	 */
    private boolean mActivo = false;
    
    /**
     * Referencia al objeto de la clase {@link ARXylophoneBase}
     */
    private ARXylophoneBase mARX;

    /**
     * Constructor por defecto
     * @param arx referencia al objeto de la clase {@link ARXylophoneBase}
     */
    public ARXRenderer(ARXylophoneBase arx)
    {
    	mARX = arx;
    }
    
    /**
     * Inicializa el renderizado
     */
    public native void iniciarRenderizado();
    
    /**
     * Actualiza el renderizado
     * @param width ancho de la pantalla donde se llevar&aacute 
     * 				 a cabo el renderizado
     * @param height alto de la pantalla donde se llevar&aacute 
     * 				 a cabo el renderizado
     */
    public native void reconfigurarRenderizado(int width, int height);
    
    /**
     * Se llama cuando la superficie es creada o se ha reiniciado.
     * Se encarga de inicializar el Rendering.
     */
    public void onSurfaceCreated(GL10 gl, EGLConfig config)
    {   
        iniciarRenderizado();

        // Llamada a QCAR para crear la superficie de renderizado
        QCAR.onSurfaceCreated();
    }

    /**
     * Se llama cuando el tamaño de la superficie de renderizado
     * ha cambiado. Actualiza el renderizado con los nuevos valores
     * para el ancho y el alto de la pantalla.
     */
    public void onSurfaceChanged(GL10 gl, int ancho, int alto)
    {
    	// Actualiza el renderizado con el nuevo ancho y alto de la pantalla
        reconfigurarRenderizado(ancho, alto);

        // Llamada a QCAR para modificar la superficie de renderizado
        QCAR.onSurfaceChanged(ancho, alto);
    }

    /**
     * Activa el renderizado
     */
    public void activarRenderizado()
    {
    	mActivo = true;
    }

    /**
     * Renderiza un frame. En esta funci&oacute;n se lleva a cabo el reconocimiento
     * del Target, el reconocimiento de la pulsaci&oacute;n de los botones virutales 
     * y el dibujado de las notas en pantalla en caso de encontrarse
     * en el modo GUIADO.
     */
    public native void renderizarFrame();

    /**
     * Dibuja un frame en caso de estar activo el renderizado
     */
    public void onDrawFrame(GL10 gl)
    {
    	// Si aún no se ha activado el renderizado no 
    	// se dibuja nada
        if (!mActivo)
            return;

        renderizarFrame();
    }
}
