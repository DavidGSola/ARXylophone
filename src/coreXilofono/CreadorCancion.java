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

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.LinkedList;
import java.util.Queue;

import android.content.Context;

/**
 * Clase encargada de crear una canci&oacute;n y almacenar
 * la informaci&oacute;n necesaria en una estructura consistente
 * para poder ser almacenada la canci&oacute;n en formato de
 * archivo de texto. Esto permite que el usuario posteriormente
 * pueda "jugar" estas canciones como si fuesen canciones
 * que vienen de serie por la aplicaci&oacute;n.
 * 
 * @author DavidGSola
 *
 */
public class CreadorCancion 
{
	/**
	 * Cola de notas que forman parte de la canci&oacute;n creada.
	 * Cada elemento de la cola posee la nota a tocar representado
	 * mediante un {@link Hit}}.
	 */
	private Queue<Hit> notas;
	
	/**
	 * Referencia al objeto {@link ARXylophoneRecord}
	 */
	private ARXylophoneRecord mARX;
	
	/**
	 * Nombre de la canci&oacute;n
	 */
	private String nombre;
	
	/**
	 * Momento actual de la canci&oacute;n
	 */
	private long tiempoActual;
	
	/**
	 * Constructor por defecto. Inicializa la cola de notas
	 * @param nombre <code>String</code> con el nombre de la canci&oacute;n
	 * @param arx referencia al objeto {@link ARXylophoneRecord}
	 */
	public CreadorCancion(String nombre, ARXylophoneRecord arx)
	{
		this.mARX = arx;
		this.nombre = nombre;
		notas = new LinkedList<Hit>();
	}
	
	/**
	 * Devuelve el {@link CreadorCancion#tiempoActual}
	 * @return {@link CreadorCancion#tiempoActual}
	 */
	public float getTiempo()
	{
		return tiempoActual;
	}
	
	/**
	 * Setea el {@link CreadorCancion#tiempoActual}
	 * @param t nuevo Tiempo
	 */
	public void setTiempo(long t)
	{
		tiempoActual = t;
	}
	
	/**
	 * Añade una nota a la cola {@link CreadorCancion#notas}
	 * @param nota Nota a añadir
	 */
	public void addNota(String nota)
	{
		notas.add(new Hit(nota, tiempoActual));
	}
	
	/**
	 * Devuelve si la canci&oacute;n est&aacute; vac&iacute;a
	 * @return 	<code>True</code> la cola est&aacute; vac&iacute;a
	 *			<code>False</code> la cola no est&aacute; vac&iacute;a
	 */
	public boolean isEmpty()
	{
		return notas.isEmpty();
	}
	
	/**
	 * Devuelve el n&uacute;mero de notas que se han añadido
	 * hasta ahora
	 * @return tamaño de la Queue {@link CreadorCancion#notas}
	 */
	public int getNumNotas()
	{
		return notas.size();
	}
	
	/**
	 * Guarda la canci&oacute;n siguiendo en un archivo .txt el mismo formato 
	 * que las canciones que trae de base la aplicaci&oacute;n. Adem&aacute;s, añade 
	 * una "_" al principio del nombre del archivo para poder ser
	 * diferenciada posteriormente de las canciones que trae de base la
	 * aplicaci&oacute;n.
	 * @throws IOException fallo en la escritura del archivo
	 */
	public void guardarCancion() throws IOException
	{
		// Se añade el "_" al principio del archivo y el ".txt" al final
		OutputStreamWriter  fos = new OutputStreamWriter( mARX.openFileOutput("_"+nombre+".txt",Context.MODE_PRIVATE));
		BufferedWriter mbw = new BufferedWriter(fos);
		while(!notas.isEmpty())
		{
			// Las líneas impares son las notas
			Hit hit= notas.poll();
			mbw.write(String.valueOf(hit.getNota()));
			
			// Salto de línea
			mbw.newLine();
			
			// Las líneas pares son los tiempos de cada nota
			mbw.write(String.valueOf(hit.getTiempo()+3500));
			
			// Salto de l&oacute;nea
			mbw.newLine();
		}
		
		// Se cierra el archivo
		mbw.close();
	}
}
