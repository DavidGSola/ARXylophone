package coreXilofono;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Clase que representa un Hit de una canción.
 * @author DavidGSola
 *
 */
public class Hit
{
	/**
	 * Array con las notas v&aacute;lidas del xilófono
	 */
	private static Character[] notasV = {'C','D','E','F','G','A','B','c'};
	
	/**
	 * ArrayList con las notas v&aacute;lidas del xilófono
	 */
	private static ArrayList<Character> notasValidas = new ArrayList<Character>(Arrays.asList(notasV));
	
	/**
	 * Variable de clase que representa el n&uacute;mero total
	 * de Hits que se han creado (en una canción)
	 */
	private static int numeroTotalHits = 0;
	
	/**
	 * Nota del Hit
	 */
	private char nota;
	
	/**
	 * Id del Hit
	 */
	private int id;
	
	/**
	 * Tiempo en el que debe ser tocado un Hit
	 */
	private long tiempo;
	
	/**
	 * Constructor por defecto que crea un Hit con las notas del
	 * vector n.
	 * @param n Vector de char con las notas a añadir.
	 */
	public Hit(String n, long tiempo)
	{
		id = numeroTotalHits;
		this.tiempo = tiempo;
		
		// Aumentamos el número total de Hits
		numeroTotalHits++;
		
		if(notasValidas.contains(n.charAt(0)))
			nota = n.charAt(0);
	}
	
	/**
	 * Devuelve las notas que componen el Hit.
	 * @return Lista de notas que componen el Hit.
	 */
	public char getNota()
	{
		return nota;
	}

	/**
	 * Devuelve {@link Hit#id}
	 * @return
	 */
	public int getId()
	{
		return id;
	}
	
	/**
	 * Devuelve {@link Hit#tiempo}
	 * @return
	 */
	public long getTiempo()
	{
		return tiempo;
	}

	/**
	 * Comprueba el resultado de tocar una nota respecto a este Hit.
	 * @param nota nombre de la nota a comprobar.
	 * @return Resultado de la comprobación.
	 */
	public boolean comprobarNota(char notaComprobar)
	{		
		return nota == notaComprobar;
	}
}