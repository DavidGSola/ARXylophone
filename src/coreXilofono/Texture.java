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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Clase encargada de cargar una  textura de desde el APK y
 * almacenar la informaci&oacute;n de la textura.
 */
public class Texture
{
	/**
	 * Ancho de la textura
	 */
    public int ancho;
   
    /**
     * Alto de la textura
     */
    public int alto;
    
    /**
     * N&uacute;mero de canales de la textura
     */
    public int numeroCanales;
    
    /**
     * Datos para cada pixel
     */
    public byte[] datos;

    /**
     * @return {@link Texture#datos}
     */
    public byte[] getData()
    {
        return datos;
    }

    public Texture(String nombreTextura, AssetManager assets) throws IOException
    {
    	// Se carga la textura desde los assets y se añade a un InputStream
        InputStream inputStream = null;
        inputStream = assets.open(nombreTextura, AssetManager.ACCESS_BUFFER);

        // Se crea un BufferedInputStream, usando el InputStream anterior,
        // para leer el archivo
        BufferedInputStream bufferedStream = new BufferedInputStream(inputStream);
        
        // Se almacena en un Bitmap el archivo decodificado
        Bitmap bitMap = BitmapFactory.decodeStream(bufferedStream);

        // Creamos un Array de Enteros con tamaño: ancho * alto
        int[] datos = new int[bitMap.getWidth() * bitMap.getHeight()];
        
        // Se almacena en el Array datos una copia del bitmap
        bitMap.getPixels(datos, 0, bitMap.getWidth(), 0, 0,  bitMap.getWidth(), bitMap.getHeight());

        // Se lee la textura (el color de cada pixel)
        byte[] datosBytes = new byte[bitMap.getWidth() *  bitMap.getHeight() * 4];
        for (int p = 0; p < bitMap.getWidth() * bitMap.getHeight(); p++)
        {
            int color = datos[p];
            datosBytes[p * 4]        = (byte)(color >>> 16);    // R
            datosBytes[p * 4 + 1]    = (byte)(color >>> 8);     // G
            datosBytes[p * 4 + 2]    = (byte) color;            // B
            datosBytes[p * 4 + 3]    = (byte)(color >>> 24);    // A
        }

        // Se crea la textura y se inicializan sus variables
        this.ancho 	= bitMap.getWidth();
        this.alto	= bitMap.getHeight();
        this.numeroCanales	= 4;
        this.datos		= datosBytes;

    }
}
