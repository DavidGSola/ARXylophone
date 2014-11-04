package coreXilofono;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.Queue;

import android.content.res.AssetManager;

/**
 * Clase que representa una canci&oacute;n. Posee los m&eacute;todos y variables necesarias
 * para gestionar la canci&oacute;n, permitiendo comprobar si las notas que se van
 * tocando coinciden con las notas actuales de la canci&oacute;n.
 *
 * @author DavidGSola
 *
 */
public class Cancion 
{
	/**
	 * Nombre de la canci&oacute;n
	 */
	private String nombreCancion;
	
	/**
	 * Puntuaci&oacute;n actual de la canci&oacute;n
	 */
	private int score;
	
	/**
	 * Puntuaci&oacute;n m&aacute;xima que se puede obtener en la canci&oacute;n
	 */
	private int maxScore;
	
	/**
	 * Momento actual por el que va la canci&oacute;n
	 */
	private long tiempoActualCancion;
	
	/**
	 * Duraci&oacute;n total de la canci&oacute;n
	 */
	private long duracionCancion;
	
	/**
	 * Referencia a la clase {@link ARXylophoneGuiada}
	 */
	private ARXylophoneGuiada mARX;
	
	/**
	 * Cola con los Hits restantes de la canci&oacute;n
	 */
	private Queue<Hit> notas;
	
	/**
	 * Cola con los Hits que ya han aparecido en pantalla y que deben
	 * ser comprobados cada vez que el usuario toque una tecla
	 */
	private Queue<Hit> notasComprobar;
	
	/**
	 * Constructor por defecto. Se encarga de iniciar las colas, las
	 * variables necesarias de la clase y leer la canci&oacute;n desde el
	 * archivo.
	 * @param arx Referencia al {@link ARXylophoneGuiada}
	 * @param nombreCancion <code>String</code> con el nombre de la canci&oacute;n
	 * @throws IOException fallo en la lectura del archivo
	 */
	public Cancion(ARXylophoneGuiada arx, String nombreCancion) throws IOException
	{
		this.nombreCancion = nombreCancion;
		
		mARX = arx;
		
		notas = new LinkedList<Hit>();
		notasComprobar = new LinkedList<Hit>();
		
		if(nombreCancion.startsWith("_"))
			leerCancionCreada(nombreCancion);
		else
			leerCancionNoCreada("canciones/" + nombreCancion);

		score = 0;
		maxScore = notas.size()*1000;
	}
	
	/**
	 * Devuelve el nombre de la canci&oacute;n
	 * @return {@link Cancion#nombreCancion}
	 */
	public String getNombre()
	{
		return nombreCancion;
	}
	
	/**
	 * Devuelve la m&aacute;xima puntuacion posible de la canci&oacute;n
	 * @return {@link Cancion#maxScore}
	 */
	public int getMaxScore()
	{
		return maxScore;
	}
	
	/**
	 * Devuelve el momento actual de la canci&oacute;n
	 * @return {@link Cancion#tiempoActualCancion}
	 */
	public long getTiempoActual()
	{
		return tiempoActualCancion;
	}
	
	/**
	 * Setea el {@link Cancion#tiempoActualCancion}
	 * @param tiempoActual nuevo tiempo
	 */
	public void setTiempo(long tiempoActual)
	{
		tiempoActualCancion = tiempoActual;
	}
	
	/**
	 * Teniendo el cuenta el tiempoActual modifica sus colas de 
	 * notas y de comprobar y devuelve el Hit que hay que pintar.
	 * 
	 * @return {@link Hit} nuevo a pintar (<code>NULL</code> en caso que
	 * no haya que pintar nada nuevo)
	 */
	public Hit nextHit()
	{
		if(notas.isEmpty()) // Si no quedan notas se sale
			return null;
		else
			if(tiempoActualCancion >= notas.peek().getTiempo() - 3500.0f) // Si la siguiente nota está a menos de 3.5 segundos de aparecer
			{
				// Se añade a la notas de comprobación
				notasComprobar.add(notas.peek());
				
				// Se devuele eliminándola de la cola de notas
				return notas.poll();
			}
			else
				return null;
	}
	
	/**
	 * Comprueba el acierto de un hit si tiempoActual est&aacute; dentro 
	 * de un interval +- respecto al tiempo de comprobaci&oacute;n de dicha nota.
	 * 
	 * @param tiempoActual tiempo actual de la canci&oacute;n
	 * @param nota nota a comprobar
	 * @return	Resultado de la comprobaci&oacute;n.
	 */
	public boolean comprobarHit(long tiempoActual, char nota)
	{
		if(!notasComprobar.isEmpty())
		{
			// Si la siguiente nota a comprobar se encuentra dentro del margen
			if(	tiempoActual <= notasComprobar.peek().getTiempo() && 
				tiempoActual >= notasComprobar.peek().getTiempo() - 2000)
			{
				// Se comprueba la nota
				Hit hitComprobar = notasComprobar.peek();
				boolean result = hitComprobar.comprobarNota(nota);
				
				// Si el resultado es correcto se aumenta el score 
				// y se saca de la cola de comprobar
				if(result)
				{
					notasComprobar.poll();
					aumentarScore();
					
					return true;
				}
			}
		}
		
		return false;
	}
	
	/**
	 * Elimina el primer Hit de la Queue si coincide
	 * con la ID a eliminar
	 * @param id ID del Hit a eliminar
	 */
	public void eliminarHit(int id)
	{
		if(!notasComprobar.isEmpty())
			if(notasComprobar.peek().getId() == id)
				notasComprobar.poll();
	}
	
	/**
	 * Reinicia la canci&oacute;n: limpia todas las colas, lee de nuevo
	 * la canci&oacute;n y avisa al {@link ARXylophoneGuiada} para 
	 * volver a actualizar el score
	 * @throws IOException fallo en la lectura del archivo
	 */
	public void reiniciar() throws IOException
	{
		notas.clear();
		notasComprobar.clear();
		
		// Leer el archivo
		if(nombreCancion.startsWith("_"))
			leerCancionCreada(nombreCancion);
		else
			leerCancionNoCreada("canciones/" + nombreCancion);
		
		score = 0;
		
		// Notificar de la nueva puntuación
		mARX.actualizarScore(score);
	}
	
	/**
	 * Comprueba si ya se ha finalizado la canci&oacute;n
	 * @return 	<code>true</code> canci&oacute;n finalizada
	 * 			<code>false</code> canci&oacute;n no finalizada
	 */
	public boolean isFin()
	{
		
		if(tiempoActualCancion >= duracionCancion)
			return true;
		else
			return false;
	}
	
	/**
	 * Aumenta la puntuaci&oacute;n de la canci&oacute;n y notifica
	 * al {@link ARXylophoneGuiada} para actualizar
	 * su puntuaci&oacute;n
	 */
	private void aumentarScore()
	{
		score += 1000;
		
		mARX.actualizarScore(score);
	}
	
	/**
	 * Lee una canci&oacute;n que no haya sido creada por el usuario.
	 * @param nombre <code>String</code> con el nombre de la canci&oacute;n a leer
	 * @throws IOException fallo en la lectura de la canci&oacute;n
	 */
	private void leerCancionNoCreada(String nombre) throws IOException
	{
		BufferedReader mbr = abrirArchivoAsset(nombre);
		leerArchivo(mbr);
	}
	
	/**
	 * Lee una canci&oacute;n que haya sido creada por el usuario.
	 * @param nombre <code>String</code> con el nombre de la canci&oacute;n a leer
	 * @throws IOException fallo en la lectura de la canci&oacute;n
	 */
	private void leerCancionCreada(String nombre) throws IOException
	{   
		// Creamos el BufferedReader
		FileInputStream mInputStream = mARX.getApplicationContext().openFileInput(nombre);
        InputStreamReader mReader = new InputStreamReader(mInputStream);
        BufferedReader mbr = new BufferedReader(mReader);
		
        // Leemos el archivo a través del BufferedReader
		leerArchivo(mbr);
	}
	
	/**
	 * Abre un archivo de los assets que coincida con el nombre
	 * especificado.
	 * @param nombre <code>String</code> con el nombre de la canci&oacute;n a leer
	 * @return <code>BufferedReader</code> con el archivo
	 * @throws IOException fallo en la lectura de la canci&oacute;n
	 */
	private BufferedReader abrirArchivoAsset(String nombre) throws IOException
	{
		AssetManager mAssetManager = mARX.getApplicationContext().getAssets();
		InputStream mInputStream = mAssetManager.open(nombre);
		InputStreamReader mReader = new InputStreamReader(mInputStream);
		BufferedReader mbr = new BufferedReader(mReader);
			
		return mbr;		
	}
	
	/**
	 * Lee un archivo a trav&eacute;s de un BufferedReader
	 * @param mbr <code>BufferedReader</code> con el archivo
	 * @throws IOException fallo en la lectura de la canci&oacute;n
	 */
	private void leerArchivo(BufferedReader mbr) throws IOException
	{
		String linea, linea2;
		
		while((linea = mbr.readLine()) != null && (linea2 = mbr.readLine()) != null)
		{
			// Las líneas impares son las notas de los Hits
			// Las líneas pares son los tiempos de comproboación
			// de dichos hits
			long tiempo = Long.parseLong(linea2);
			Hit h = new Hit(linea, tiempo);

			notas.add(h);
			duracionCancion = tiempo + 3000;
		}
	}
}
