package com.CojiSoft.ARXylophone;

import java.io.IOException;
import java.util.ArrayList;

import Util.Utils;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Pantalla de selecci&oacute; de modo de juego
 * @author DavidGSola
 *
 */
public class PantallaSeleccion extends Activity
{
	private Context ctx;

	private String[] valuesCanciones;
	private String[] valuesCancionesUsuario;
	private String[] valuesFijos;
	
    private ListView listaFija;
    private ListView listaCanciones;


    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        ctx = this;
        
        setContentView(R.layout.select_song);

        iniciarListasCanciones();
		iniciarPlayerPrefs();
		
        CancionesArrayAdapter adapterFijo = new CancionesArrayAdapter(this, valuesFijos);
        CancionesArrayAdapter adapter = new CancionesArrayAdapter(this, valuesCanciones);

        listaFija = (ListView) findViewById(R.id.listaFija);
        listaCanciones = (ListView) findViewById(R.id.listaCanciones);
        
        listaFija.setAdapter(adapterFijo);
        listaCanciones.setAdapter(adapter);
        
        listaFija.setOnItemClickListener(new OnItemClickListener()
        {
           	@Override
          	public void onItemClick(AdapterView<?> adapter, View v, int position,
                 long id) 
           	{
           		String seleccionado = ((TextView) v.findViewById(R.id.textItem)).getText().toString();
           		if(seleccionado.equals(getString(R.string.modo_record)))
           		{
           			AlertDialog.Builder alertDialog = new AlertDialog.Builder(PantallaSeleccion.this);
           			alertDialog.setTitle(getString(R.string.contextual_crear_cancion_titulo));
           			alertDialog.setMessage(getString(R.string.contextual_crear_cancion_mensaje));
           			
	           		final EditText input = new EditText(PantallaSeleccion.this);  
	           		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
	           	                        LinearLayout.LayoutParams.MATCH_PARENT,
	           	                        LinearLayout.LayoutParams.MATCH_PARENT);
	           		input.setLayoutParams(lp);
	           		alertDialog.setView(input);
	           		
	                alertDialog.setPositiveButton(getString(R.string.contextual_aceptar), new DialogInterface.OnClickListener() 
	                {
	                	public void onClick(DialogInterface dialog,int which) 
	                	{
	                		String texto = input.getText().toString();
	                		if(texto.isEmpty())
	                			dialog.cancel();
	                		else
	                		{
	                			Intent i = new Intent(ctx, PantallaSeleccionMano.class);
	                			i.putExtra("modo", "record");
	                    	   	i.putExtra("infoExtra", texto);
	                    	   	startActivity(i);
	                		}
	                	}
	                });
	                
	                alertDialog.setNegativeButton(getString(R.string.contextual_cancelar), new DialogInterface.OnClickListener() 
	                {
	                	public void onClick(DialogInterface dialog,int which) 
	                	{
	                		dialog.cancel();
	                	}
	                });
	
	                alertDialog.show();
	            }
           		else if(seleccionado.equals( getString(R.string.modo_libre) ))
           		{
           			Intent i2 = new Intent(ctx, PantallaSeleccionMano.class);
        			i2.putExtra("modo", "libre");
           			startActivity(i2);
           		}
			}
        });
        
        listaCanciones.setOnItemClickListener(new OnItemClickListener()
        {
           	@Override
          	public void onItemClick(AdapterView<?> adapter, View v, int position,
                 long arg3) 
           	{
        	   	String value = (String)adapter.getItemAtPosition(position); 
        	   	Intent i = new Intent(ctx, PantallaSeleccionMano.class);
    			i.putExtra("modo", "guiado");
        	   	i.putExtra("infoExtra", value);
        	   	startActivity(i);
			}
        });
        
        Utils.enviarAnalyrics(this, "Pantalla Modos");
    }
    
    @Override
    public void onBackPressed()
    {
    	Intent i = new Intent(ctx, PantallaMenu.class);
    	startActivity(i);
    }
    
    /**
     * Inicia los player prefs de las puntuaciones
     */
    private void iniciarPlayerPrefs()
    {
		SharedPreferences prefs = this.getSharedPreferences("puntuaciones", Context.MODE_PRIVATE);
		Editor editor = prefs.edit();
		
		for(String nombreC : valuesCanciones)
			if(prefs.getInt(nombreC, 0) == 0)
				editor.putInt(nombreC, 0);
			
		editor.apply();
    }
    
    /**
     * Inicia las listas de cancones
     */
    private void iniciarListasCanciones()
    {
        valuesFijos = new String[] { getString(R.string.modo_record), getString(R.string.modo_libre)};
		try {
			valuesCanciones = this.getAssets().list("canciones");
			valuesCancionesUsuario = fileList();
			
			ArrayList<String> arrayListTemp = new ArrayList<String>();
			for(int i=0; i<valuesCancionesUsuario.length; i++)
			{
				if(valuesCancionesUsuario[i].startsWith("_"))
					arrayListTemp.add(valuesCancionesUsuario[i]);
			}
			
			valuesCanciones = Utils.concatenarString(valuesCanciones, arrayListTemp.toArray(new String[arrayListTemp.size()]));
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
   
}
