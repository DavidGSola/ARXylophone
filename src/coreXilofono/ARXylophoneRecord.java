package coreXilofono;

import java.io.IOException;

import Util.Utils;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.CojiSoft.ARXylophone.PantallaSeleccion;
import com.CojiSoft.ARXylophone.R;

/**
 * Clase que hereda de {@link ARXylophoneBase}. Permite crear
 * una canci&oacute;n mientras se van tocando las notas. Esta se 
 * almacenar&aacute; en la memor&iacute;a del tel&eacute;fono para poder ser 
 * utilizada posteriormente por {@link ARXylophoneGuiada}.
 *  
 * @author DavidGSola
 *
 */
public class ARXylophoneRecord extends ARXylophoneBase{
	
	/**
	 * Objeto encargado de almacenar la informaci&oacute;n
	 * necesaria para tener una canci&oacute;n completa
	 */
	CreadorCancion mCreador;

    /**
     * Tiempo inicial de la canci&oacute;n
     */
    private long tiempoInicial;
    
    /**
     * Tiempo actual de la canci&oacute;n
     */
    private long tiempoActual;
    

	/**
	 * Views espec&iacute;ficos del modo RECORD
	 */
    private View mLayoutRecord;
    private TextView mTextoScore;
    private TextView mTextoTime;
    
    /**
     * Animacion simple para utilizar sobre elementos del GUI
     */
    private Animation animBajar;
    
	/**
	 * Sobreescritura del m&eacute;todo {@link ARXylophoneBase#onCreate(Bundle)}
	 * en el que ahora se crea el cerador de la canci&oacute;n
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
		
		mCreador = new CreadorCancion(bundle.getString("infoExtra"), this);
	}

	/**
	 * Implementaci&oacute;n del m&eacute;todo {@link ARXylophoneBase#iniciarModo()}.
	 * Inicia el modo de juego a RECORD
	 */
	@Override
	protected void iniciarModo() 
	{
		setModo(TipoJuego.RECORD.ordinal(), 
				getIntent().getExtras().getInt("mano"));

        Utils.enviarAnalyrics(this, "Pantalla Modo Record");
	}
	
	/**
	 * Sobreescritura del m&eacute;todo {@link ARXylophoneBase#iniciarGUI()} en el que ahora se crea
	 * otro layout propio de este modo y la animaci&oacute;n para la aparici&oacute;n del mismo.
	 */
	@Override
	protected void iniciarGUI()
	{
		super.iniciarGUI();
        mLayoutRecord = mUILayout.findViewById(R.id.layoutRecord);
        mTextoScore = (TextView) mUILayout.findViewById(R.id.textoNumNotas);
        mTextoTime = (TextView) mUILayout.findViewById(R.id.textoTime);

        animBajar = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.bajar);  
	}
	
	/**
	 * Implementaci&oacute;n del m&eacute;todo {@link ARXylophoneBase#iniciarLayout()}.
	 * Carga el <code>Layout</code> que corresponde con este tipo de juego
	 */
	@Override
	protected void iniciarLayout()
	{
        LayoutInflater inflater = LayoutInflater.from(this);
		mUILayout = (RelativeLayout) inflater.inflate(R.layout.camara_overlay_record, null, false);
	}

	/**
	 * Sobreescritura del m&eacute;todo {@link ARXylophoneBase#playKey(String)}
	 * en el que se produce el sonido y adem&aacute;s se lleva
	 * almacena la nota y el tiempo haciendo uso de {@link ARXylophoneRecord#mCreador}
	 */
	@Override
	public void playKey(String key) 
	{
		super.playKey(key);
    	
    	mCreador.addNota(key);
    	actualizarTextViewNumNotas();
	}

	/**
	 * Implementaci&oacute;n del m&eacute;todo {@link ARXylophoneBase#actualizarFrame(boolean, boolean)}.
     * Actualiza cada frame teniendo en cuenta el tiempo actual e inicial.
     * Se encarga de notificar de delegar sobre m&eacute;todos para añadir nuevos 
     * hits en pantalla y de modificar la GUI en funci&oacute;n de si se ha detectado o no el Target.
	 */
	@Override
	public void actualizarFrame(boolean primeraDeteccion, boolean detectado) {
		// Si es la primera vez que se detecta:
    	if(primeraDeteccion)
    	{
    		tiempoInicial = Utils.obtenerHoraActual();
    		loadingDialogHandler.sendEmptyMessage(AccionesGUI.OCULTAR.ordinal());
    		textoDetectandoHandler.sendEmptyMessage(AccionesGUI.OCULTAR.ordinal());
    		
    		// Se muesta el cartel con el tiempo y el número de notas utilizando una animación
    		runOnUiThread(new Runnable() 
            {
    	        public void run() 
    	        {
    	        	mLayoutRecord.startAnimation(animBajar);
    	        	mLayoutRecord.setVisibility(View.VISIBLE);
    	        }
            });
    	}
    	else
    		if(detectado)
	    	{
    			// Actualiamos el tiempo actual de la canción
			    tiempoActual = Utils.obtenerHoraActual();
			    mCreador.setTiempo(tiempoActual-tiempoInicial);
			    
			    // Se ocultan los elementos del GUI para que el usuario sepa
        		// que se ha detectado el Target
        		loadingDialogHandler.sendEmptyMessage(AccionesGUI.OCULTAR.ordinal());
        		textoDetectandoHandler.sendEmptyMessage(AccionesGUI.OCULTAR.ordinal());
        		
        		actualizarTextViewTime();
    		}
    		else // Actualizamos tiempoInicial para que la canción no continue
    		{
		    	tiempoActual = Utils.obtenerHoraActual();
    			tiempoInicial = tiempoActual - (long) mCreador.getTiempo();
    			
		    	// Se muestran los elementos del GUI para que el usuario sepa
        		// que no está detectado el Target
        		loadingDialogHandler.sendEmptyMessage(AccionesGUI.MOSTRAR.ordinal());
        		textoDetectandoHandler.sendEmptyMessage(AccionesGUI.MOSTRAR.ordinal());
    		}
	}
	
	/**
	 * Sobreescritura del m&eacute;todo {@link ARXylophoneBase#finalizar()}
	 * En este caso se debe crear un dialogo salir o guardar dependiendo
	 * si se ha llegado a guardar alguna nota en la canci&oacute;n creada
	 */
    @Override
    protected void finalizar()
    {
    	AlertDialog dialog;
    	
    	if(mCreador.isEmpty())
    		dialog = crearDialogoSalir();
    	else
    		dialog = crearDialogoGuardar();
   
    	dialog.show();
    }
    
    /**
     * M&eacute;todo para actualizar la GUI del n&uacute;mero de notas.
     */
    private void actualizarTextViewNumNotas()
    {
    	runOnUiThread(new Runnable() 
        {
	        public void run() 
	        {
	        	mTextoScore.setText(String.valueOf(mCreador.getNumNotas()));
	        }
        });
    }
    
    /**
     * M&eacute;todo para actualizar la GUI del tiempo de canci&oacute;n.
     */
    private void actualizarTextViewTime()
    {
    	runOnUiThread(new Runnable() 
        {
	        public void run() 
	        {
	        	mTextoTime.setText(Utils.convertirFloatToTime(mCreador.getTiempo()));
	        }
        });
    }
	
    
    /**
     * Crea un <code>AlertDialog</code> de salir. Pulsando sobre si se navegar&aacute; hacia la actividad
     * de selecci&oacute;n de canciones.
     * @return <code>AlertDialog</code> creado
     */
    private AlertDialog crearDialogoSalir()
    {
    	AlertDialog.Builder builder = new AlertDialog.Builder(ARXylophoneRecord.this);
    	
     	builder.setMessage(getString(R.string.salir_cancion));
		
     	builder.setPositiveButton(getString(R.string.contextual_aceptar), new DialogInterface.OnClickListener() {
     		public void onClick(DialogInterface dialog, int id) 
     		{
     			finish();
     	    	
     	    	Intent i = new Intent(ARXylophoneRecord.this, PantallaSeleccion.class);
     		   	startActivity(i);
	        }
     	});
     	builder.setNegativeButton(getString(R.string.contextual_cancelar), new DialogInterface.OnClickListener() {
	    	public void onClick(DialogInterface dialog, int id) 
	    	{
	    		
	        }
     	});

		return builder.create();
    }

    /**
     * Crea un <code>AlertDialog</code> para guardar la canci&oacute;n o no.
     * El bot&oacute;n de Si guardar&aacute; la canci&oacute;n y pasar&aacute; a la siguiente actividad.
     * El bot&oacute;n de No pasar&aacute; a la siguiente actividad directamente
     * @return <code>AlertDialog</code> creado
     */
    private AlertDialog crearDialogoGuardar()
    {
    	AlertDialog.Builder builder = new AlertDialog.Builder(ARXylophoneRecord.this);
    	
     	builder.setMessage(getString(R.string.guardar_cancion));
		
     	builder.setPositiveButton(getString(R.string.contextual_aceptar), new DialogInterface.OnClickListener() {
     		public void onClick(DialogInterface dialog, int id) 
     		{
     			// Se guarda la canción
     			try {
					mCreador.guardarCancion();
				} catch (IOException e) {
					e.printStackTrace();
				}
     			finish();
     	    	
     	    	Intent i = new Intent(ARXylophoneRecord.this, PantallaSeleccion.class);
     		   	startActivity(i);	
	        }
     	});
     	builder.setNegativeButton(getString(R.string.contextual_cancelar), new DialogInterface.OnClickListener() {
	    	public void onClick(DialogInterface dialog, int id) 
	    	{
     			finish();
     	    	
     	    	Intent i = new Intent(ARXylophoneRecord.this, PantallaSeleccion.class);
     		   	startActivity(i);
	        }
     	});

		return builder.create();
    }
}
