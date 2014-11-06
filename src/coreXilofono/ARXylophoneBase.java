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

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.Vector;

import Util.Utils;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.CojiSoft.ARXylophone.PantallaSeleccion;
import com.CojiSoft.ARXylophone.R;
import com.qualcomm.QCAR.QCAR;

/**
 * Clase abstracta que posee todas las funcionalidades b&aacute;sicas para
 * reconocer un Xil&oacute;fono y gestionar las pulsaciones de sus botones
 * virtuales.
 * 
 * @author DavidGSola
 */
public abstract class ARXylophoneBase extends Activity
{
	/**
	 * Nombre de las librer&iacute;as din&aacute;micas que se deben cargar
	 */
    private static final String LIBRERIA_NATIVA = "ARXylophone";
    private static final String LIBRERIA_QCAR = "QCAR";
    
    /**
     * Se cargan las librer&iacute;as din&aacute;micas en el comienzo
     */
    static
    {
        cargarLibreria(LIBRERIA_QCAR);
        cargarLibreria(LIBRERIA_NATIVA);
    }

    /**
     * OpenGL ES View
     */
    private QCARGLView mGlView;

    /**
     * Renderer
     */
    private ARXRenderer mRenderer;

    /**
     * Dimensiones de la pantalla del dispositivo
     */
    private int mScreenWidth, mScreenHeight = 0;

    /**
     * Tarea as&iacute;ncrona para iniciar QCAR
     */
    private IniciarQCARTask mIniciarQCARTask;
    
    /**
     * Tarea as&iacute;ncrona para cargar el Tracker
     */
    private CargarTrackerTask mCargarTrackerTask;

    /** 
     * Se utiliza para almacenar el candado para llevar a cabo la sincronizaci&oacute;n
     * entre las tareas as&iacute;ncronas {@link ARXylophoneBase#mIniciarQCARTask} y  
     * {@link ARXylophoneBase#mCargarTrackerTask} y el
     * m&eacute;todo propio del ciclo de vida de Android {@link ARXylophoneBase#onDestroy(). 
     * Si la actividad es destruida mientras se realizan alguna de las tareas as&iacute;ncronas, la
     * destrucci&oacute;n se detendr&aacute; hasta que finalicen
     * 
     */
    private Object mCerrojo = new Object();

    /**
     * Flags de inicializaci&oacute;n de QCAR
     */
    private int mQCARFlags = 0;
    
    /**
     * Posibles estados de la aplicaci&oacute;n
     */
    private static enum EstadoAPP {	UNINITED,INIT_APP, INIT_QCAR, INIT_TRACKER, 
    								INIT_APP_AR, LOAD_TRACKER, INITED, 
    								CAMERA_STOPPED, CAMERA_RUNNING};

	/**
	 * Actual estado de la APP
	 */
    private EstadoAPP mEstadoActualAPP = EstadoAPP.UNINITED;
    
	/**
	 * Posibles tipos de enfoques
	 */
    private static enum ModoEnfoque {NORMAL, CONTINUO_AUTO };

    /**
     * Actual modo de enfoque
     */
    private ModoEnfoque mModoEnfoqueActual;
    
    /**
     * Posibles tipos de juegos
     */
    protected static enum TipoJuego {LIBRE, GUIADO, RECORD};

    /**
     * Posibles tipos de acciones a llevar a cabo sobre los
     * elementos de la GUI
     */
    protected static enum AccionesGUI{OCULTAR, MOSTRAR};

    /**
     * Views del GUI
     */
    protected RelativeLayout mUILayout;
    protected View mDialogoDetectando;
    protected View mTextoDetectando;

    /**
     * Vector donde se almacenan las texturas cargadas
     */
    private Vector<Texture> mTextures;

    /**
     * Detector de gestos para gestionar el enfoque manual al pulsar
     * una vez sobre la pantalla
     */
    private GestureDetector mDetectorGestos;

    /**
     * C&aacute;mara forntal del dispositivo
     */
    private boolean hasFrontCamera = false;
    
    /**
     * Opciones para la c&aacute;mara:
     */
    private boolean mFlash = false;
    private boolean mContinuoAutoenfoque = false;
    private int mCamaraActiva = 0;
    
    /**
     * Objeto encargado de reproducir los sonidos
     */
    protected SoundPlayer mReproductor;

    /**
     * Handler que se encarga de mostrar u ocultar el dialogo detectando.
     */
    static class DialogoDetectadoHandler extends Handler
    {
    	// Se utiliza una referencia débil para que el recolector de basura 
    	// pueda actuar sobre el como el quiera.
        private final WeakReference<ARXylophoneBase> mARXylophone;

        DialogoDetectadoHandler(ARXylophoneBase arx)
        {
            mARXylophone = new WeakReference<ARXylophoneBase>(arx);
        }

        public void handleMessage(Message msg)
        {
            ARXylophoneBase arx = mARXylophone.get();
            if (arx == null)
                return;
                
            if (msg.what == AccionesGUI.MOSTRAR.ordinal())
                arx.mDialogoDetectando.setVisibility(View.VISIBLE);
            else if (msg.what == AccionesGUI.OCULTAR.ordinal())
                arx.mDialogoDetectando.setVisibility(View.GONE);
        }
    }
    
    /**
     * Handler que se encarga de mostrar u ocultar el texto detectando.
     */
    static class TextoDetectandoHandler extends Handler
    {
    	// Se utiliza una referencia débil para que el recolector de basura 
    	// pueda actuar sobre el como el quiera.
        private final WeakReference<ARXylophoneBase> mARXylophone;

        TextoDetectandoHandler(ARXylophoneBase arx)
        {
            mARXylophone = new WeakReference<ARXylophoneBase>(arx);
        }
        
        public void handleMessage(Message msg)
        {
            ARXylophoneBase arx = mARXylophone.get();
            if (arx == null)
                return;

            if (msg.what == AccionesGUI.MOSTRAR.ordinal())
                arx.mTextoDetectando.setVisibility(View.VISIBLE);
            else if (msg.what == AccionesGUI.OCULTAR.ordinal())
                arx.mTextoDetectando.setVisibility(View.GONE);
        }
    }

    // Handlers para los dos elementos de la GUI
    protected Handler loadingDialogHandler = new DialogoDetectadoHandler(this);
    protected Handler textoDetectandoHandler = new TextoDetectandoHandler(this);
    
    /**
     * Tarea as&iacute;ncrona para inicializar QCAR
     */
    private class IniciarQCARTask extends AsyncTask<Void, Integer, Boolean>
    {
        private int mProgreso = -1;

        protected Boolean doInBackground(Void... params)
        {
        	// Cerrojo para evitar que el método onDestroy() se solape 
        	// a esta tarea
            synchronized (mCerrojo)
            {
                QCAR.setInitParameters(ARXylophoneBase.this, mQCARFlags);

                do
                {
                	// Inicializa QCAR
                    mProgreso = QCAR.init();

                    // Publicar el progreso actual para utilizarlo en
                    // el método onProgressUpdate
                    publishProgress(mProgreso);
                } while (!isCancelled() && mProgreso >= 0 && mProgreso < 100);

                return (mProgreso > 0);
            }
        }

        protected void onProgressUpdate(Integer... values)
        {
        	
        }

        protected void onPostExecute(Boolean result)
        {
        	if (result)
                actualizarEstadoAplicacion(EstadoAPP.INIT_TRACKER);
            else
            {
            	AlertDialog dialogoError = mProgreso ==  QCAR.INIT_DEVICE_NOT_SUPPORTED ? 
            			Utils.crearDialogoError("Fallo al inicializar QCAR, el dispositivo no es soportado.", ARXylophoneBase.this) : 
            			Utils.crearDialogoError("Fallo al inicializar QCAR.", ARXylophoneBase.this);
                   
            	dialogoError.show();
            }
        }
    }

    /**
     * Tarea as&iacute;ncrona para cargar el Tracker
     *
     */
    private class CargarTrackerTask extends AsyncTask<Void, Integer, Boolean>
    {
        protected Boolean doInBackground(Void... params)
        {
        	// Cerrojo para evitar que el método onDestroy() se solape 
        	// a esta tarea
            synchronized (mCerrojo)
            {
                return (cargarDatosTracker() > 0);
            }
        }

        protected void onPostExecute(Boolean result)
        {
            if (result)
                actualizarEstadoAplicacion(EstadoAPP.INITED);
            else
            {
            	AlertDialog dialogoError = Utils.crearDialogoError("Fallo al cargar el Tracker.", 
            		ARXylophoneBase.this);

            	dialogoError.show();
            }
        }
    }

    /**
     * Inicializa todas las variables necesarias del objeto.
     * Se llama cuando la actividad se inicia o el usuario navega
     * de vuelta a la actividad.
     */
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        // Carga las texturas necesarias
        mTextures = new Vector<Texture>();
        cargarTexturas();

        // Inicializa la flag de la versión de OpenGL ES
        mQCARFlags = QCAR.GL_20;
        //getFlagsOpenGLES();

        // Crea el detector de gestos para procesar el doble click
        mDetectorGestos = new GestureDetector(this, new GestorListener(this));

        // Actualiza el estado de la aplicación al inicial
        actualizarEstadoAplicacion(EstadoAPP.INIT_APP);
        
        // Creamos el SoundPlayer para la reproducción de sonidos
        mReproductor = new SoundPlayer(this);
        
        // Comprobamos si tiene cámara frontal el dispositivo
        CameraInfo ci = new CameraInfo();
         for (int i = 0; i < Camera.getNumberOfCameras(); i++)
        {
            Camera.getCameraInfo(i, ci);
            if (ci.facing == CameraInfo.CAMERA_FACING_FRONT)
                hasFrontCamera = true;
        }
    }


    /**
     * Almacena las dimensiones de la pantalla.
     */
    private void setDimensionesPantalla()
    {
        DisplayMetrics metrics = new DisplayMetrics();
        
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        mScreenWidth = metrics.widthPixels;
        mScreenHeight = metrics.heightPixels;
    }

    /**
     * Carga las texturas desde APK.
     */
    private void cargarTexturas()
    {
    	try
    	{/*
	        mTextures.add(Texture.cargarTextura("texturaTeclaC.png",
	                getAssets()));
	        mTextures.add(Texture.cargarTextura("texturaTeclaD.png",
	                getAssets()));
	        mTextures.add(Texture.cargarTextura("texturaTeclaE.png",
	                getAssets()));
	        mTextures.add(Texture.cargarTextura("texturaTeclaF.png",
	                getAssets()));
	        mTextures.add(Texture.cargarTextura("texturaTeclaG.png",
	                getAssets()));
	        mTextures.add(Texture.cargarTextura("texturaTeclaA.png",
	                getAssets()));
	        mTextures.add(Texture.cargarTextura("texturaTeclaB.png",
	                getAssets()));
	        mTextures.add(Texture.cargarTextura("texturaTeclaC1.png",
	                getAssets()));
	        */
    		mTextures.add(new Texture("texturaTeclaC.png", getAssets()) );
    		mTextures.add(new Texture("texturaTeclaD.png", getAssets()) );
    		mTextures.add(new Texture("texturaTeclaE.png", getAssets()) );
    		mTextures.add(new Texture("texturaTeclaF.png", getAssets()) );
    		mTextures.add(new Texture("texturaTeclaG.png", getAssets()) );
    		mTextures.add(new Texture("texturaTeclaA.png", getAssets()) );
    		mTextures.add(new Texture("texturaTeclaB.png", getAssets()) );
    		mTextures.add(new Texture("texturaTeclaC1.png", getAssets()) );
    	} catch(IOException e)
    	{
    		Log.v("ELIMINAR", "Fallo en la carga de texturas");
    		e.printStackTrace();
    	}
    }

    /**
     * M&eacute;todo nativo para inicializar el Tracker
     * @return	<code>1</code> inicializado correctamente
     * 			<code>0</code> fallo al inicializar
     */
    public native int iniciarTracker();
    
    /**
     * M&eacute;todo nativo que destruye el Tracker
     */
    public native void destruirTracker();

    /**
     * M&eacute;todo nativo que carga los datos del Tracker
     * @return	<code>1</code> cargado correctamente
     * 			<code>0</code> fallo al cargar el Tracker
     */
    public native int cargarDatosTracker();
    
    /**
     * M&eacute;todo nativo que destruye los datos del Tracker
     */
    public native void destruirDatosTracker();
    
    /**
     * M&eacute;todo nativo que inicia la c&aacute;mara
     * @param idCamera id de la c&aacute;mara a iniciar.
     */
    private native void iniciarCamara(int idCamera);
    
    /**
     * M&eacute;todo nativo que para la c&aacute;mara activa
     */
    private native void pararCamara();

    /**
     * Reanuda la actividad: reactiva la c&aacute;mara y reanuda el View.
     * Es llamada cuando la actividad vuelve a iniciase tras una pausa
     * o un reinicio de la misma.
     */
    protected void onResume()
    {
        super.onResume();

        // Resumen específico de QCAR
        QCAR.onResume();

        // Si la cámara ya se ha iniciado alguna vez la volvemos a iniciar
        if (mEstadoActualAPP == EstadoAPP.CAMERA_STOPPED)
        {
            actualizarEstadoAplicacion(EstadoAPP.CAMERA_RUNNING);
        }

        // Resumen específico del OpenGL ES View
        if (mGlView != null)
        {
            mGlView.setVisibility(View.VISIBLE);
            mGlView.onResume();
        }
    }

    /**
     * Pausa la actividad: pausa el OpenGL ES view, modifica el estado actual
     * de la aplicaci&oacute;n y desactiva el flash en caso de estar activo.
     * 
     * Se llama cuando se cambia a otra actividad y se deja la actual
     * en segundo plano.
     */
    protected void onPause()
    {
        super.onPause();

        if (mGlView != null)
        {
            mGlView.setVisibility(View.INVISIBLE);
            mGlView.onPause();
        }

        if (mEstadoActualAPP == EstadoAPP.CAMERA_RUNNING)
        {
            actualizarEstadoAplicacion(EstadoAPP.CAMERA_STOPPED);
        }

        if (mFlash)
        {
            mFlash = false;
            setFlash(mFlash);
        }

        QCAR.onPause();
    }
    
    /**
     * M&eacute;todo nativo para limpiar la aplicaci&oacute;n
     */
    private native void destruirAplicacionNativa();

    /**
     * Destruye la actividad: limpia y cancela todas las operaciones
     * que se hayan podido quedar activadas.
     * 
     * Es lo &uacute;ltimo que se ejecuta antes de ser destruida la aplicaci&oacute;n
     */
    protected void onDestroy()
    {
        super.onDestroy();

        // Cancelar las tareas asíncronas que pueden estar en segundo plano:
        // inicializar QCAR y cargar los Trackers
        if (mIniciarQCARTask != null &&
            mIniciarQCARTask.getStatus() != IniciarQCARTask.Status.FINISHED)
        {
            mIniciarQCARTask.cancel(true);
            mIniciarQCARTask = null;
        }

        if (mCargarTrackerTask != null &&
            mCargarTrackerTask.getStatus() != CargarTrackerTask.Status.FINISHED)
        {
            mCargarTrackerTask.cancel(true);
            mCargarTrackerTask = null;
        }

        // Se utiliza este cerrojo para asegurar que no se están ejecutando
        // aún las dos tareas asíncronas para inicizalizar QCAR y para
        // cargar los Trackers.
        synchronized (mCerrojo) 
        {
            destruirAplicacionNativa();
            
            mTextures.clear();
            mTextures = null;

            destruirDatosTracker();
            destruirTracker();

            QCAR.deinit();
        }
        
        // Se limpia la memoría generada por el soundPool
        if(mReproductor != null)
        	mReproductor.limpiar();
        
        System.gc();
    }

    /**
     * M&eacute;todo sincronizado para actualizar el estado actual de la Aplicaci&oacute;n.
     * Debe ser synchronized debido a que es posible que sea ejecutado simultaneamente
     * por el m&eacute;todo propio del ciclo de vida de Android onResume() y por el m&eacute;todo
     * onPostExecute() de la tarea as&iacute;ncrona IniciarQCARTask.
     * 
     * @param nuevoEstado estado al que va a pasar la aplicaci&oacute;n
     */
    private synchronized void actualizarEstadoAplicacion(EstadoAPP nuevoEstado)
    {
        if (mEstadoActualAPP == nuevoEstado)
            return;

        mEstadoActualAPP = nuevoEstado;

        switch (mEstadoActualAPP)
        {
            case INIT_APP:
            	// Inicializar la parte de la aplicación independiente de QCAR
                iniciarAplicacion();

                // Pasar al siguiente estado
                actualizarEstadoAplicacion(EstadoAPP.INIT_QCAR);
                break;

            case INIT_QCAR:
            	// Lanzar la tarea asíncrona que se encarga de inicializar QCAR.
            	try
                {
                    mIniciarQCARTask = new IniciarQCARTask();
                    mIniciarQCARTask.execute();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                break;

            case INIT_TRACKER:
            	// Inicializar el Tracker
                if (iniciarTracker() > 0)
                {
                    // Pasar al siguiente estado
                    actualizarEstadoAplicacion(EstadoAPP.INIT_APP_AR);
                }
                break;

            case INIT_APP_AR:
            	// Inicializar la parte específica de Realidad Aumentada que 
            	// necesita que QCAR haya sido inicializado.
                iniciarAplicacionAR();

                // Pasar al siguiente estado
                actualizarEstadoAplicacion(EstadoAPP.LOAD_TRACKER);
                break;

            case LOAD_TRACKER:
            	// Lanzar la tarea asíncrona que se encarg&iacute; de cargar el Tracker
                try
                {
                    mCargarTrackerTask = new CargarTrackerTask();
                    mCargarTrackerTask.execute();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                break;

            case INITED:	 	
                // Llamamos al Garbage Collector para que libere
            	// la memoria de las dos tareas asíncronas
            	System.gc();

                // Activar el renderizado
                mRenderer.activarRenderizado();
                
                // Añadir el OpenGL ES View a la vista
                addContentView(mGlView, new LayoutParams(LayoutParams.MATCH_PARENT,
                	LayoutParams.MATCH_PARENT));

                // Mover el layout de UI al frente
                mUILayout.bringToFront();

                // Pasar al siguiente estado
                actualizarEstadoAplicacion(EstadoAPP.CAMERA_RUNNING);

                break;

            case CAMERA_STOPPED:
                pararCamara();
                break;

            case CAMERA_RUNNING:
                iniciarCamara(mCamaraActiva);

                // Setear el fondo del layout UI a transparente para poder ver
                // lo que va capturando la cámara
                mUILayout.setBackgroundColor(Color.TRANSPARENT);
                
                // Activar el modo auto enfoque continuo si es posible
                if (!setModoEnfoque(ModoEnfoque.CONTINUO_AUTO.ordinal()))
                {
                    mContinuoAutoenfoque = false;
                    setModoEnfoque(ModoEnfoque.NORMAL.ordinal());
                }
                else
                    mContinuoAutoenfoque = true;
                break;

            default:
                throw new RuntimeException("Estado de la aplicación invalido.");
        }
    }
    
    /**
     *  Inicializa el modo de juego y la mano con la que
     *  se va a jugar
     */
    protected native void setModo(int modoJuego, int mano);

    /**
     * Inicializar aquellos elementos independientes de QCAR
     */
    protected void iniciarAplicacion()
    {
    	iniciarModo();
    	iniciarGUI();

    	// Se fuerza la orientación de la pantalla a Landscape
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    
        setDimensionesPantalla();

        // Mantener la pantalla activa
        getWindow().setFlags( 	WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
        						WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    /**
     * M&eacute;todo nativo para inicializar la parte nativa
     * @param ancho ancho de la pantalla
     * @param height alto de la pantalla
     */
    private native void iniciarAplicacionNativa(int ancho, int height);
    
    /**
     * Inicializa todo aquello relacionado con la parte de realidad aumentada
     */
    private void iniciarAplicacionAR()
    {
        iniciarAplicacionNativa(mScreenWidth, mScreenHeight);

        // Crear el OpenGL ES View
        int depthSize = 16;
        int stencilSize = 0;
        boolean translucent = QCAR.requiresAlpha();

        mGlView = new QCARGLView(this);
        mGlView.init(mQCARFlags, translucent, depthSize, stencilSize);

        mRenderer = new ARXRenderer(this);
        mGlView.setRenderer(mRenderer);

        // Mostrar "Detectando" y la barra de progreso en pantalla
        loadingDialogHandler.sendEmptyMessage(AccionesGUI.MOSTRAR.ordinal());
        textoDetectandoHandler.sendEmptyMessage(AccionesGUI.MOSTRAR.ordinal());

        // Añadir el layout de UI a la vista
        addContentView(mUILayout, new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));
    }
    
    /**
     * Iniciar los componentes del GUI
     */
    protected void iniciarGUI()
    {
    	iniciarLayout();

        mUILayout.setVisibility(View.VISIBLE);
        mUILayout.setBackgroundColor(Color.BLACK);

        mDialogoDetectando = mUILayout.findViewById(R.id.loading_indicator);
        mTextoDetectando = mUILayout.findViewById(R.id.textoDetectando);
    }

    /**
     * Iniciar el modo de juego
     */
    protected abstract void iniciarModo();
    
    /**
     * Iniciar el Layout del UI
     */
    protected abstract void iniciarLayout();
    
    /**
     * Enfocar por petici&oacute;n explicita del usuario 
     */
    public void enfocar()
    {
    	if(!activarAutoEnfoque())
    		Utils.crearToast(getString(R.string.toast_enfoque_manual_fallo), this);
    	// Desactivamos el enfoque automático continuo 
    	mContinuoAutoenfoque = false;
    }
    
    /**
     * Activar auto enfoque continuo
     * 
     * @return 	<code>true</code>: activado con exito
     * 			<code>false</code>: fallo al activar
     */
    private native boolean activarAutoEnfoque();
    
    /**
     * Setear el tipo de enfoque
     * @param mode TipoFocus tipo de enfoque
     * 
     * @return 	<code>true</code>: activado con exito
     * 			<code>false</code>: fallo al activar
     */
    private native boolean setModoEnfoque(int modo);
    
    /**
     * Activar el Flash
     * @param activar <code>true</code> para activarlo
     * 				<code>false</code> para desactivarlo
     * 
     * @return 	<code>true</code>: cambio realizado con exito
     * 			<code>false</code>: fallo al realizar el cambio
     */
    private native boolean setFlash(boolean activar);


    /**
     * Devuelve el n&uacute;mero de texturas cargadas. Aunque es un
     * m&eacute;todo muy simple es necesario ya que se llama desde c&oacute;digo nativo.
     * 
     * @return n&uacute;mero de texturas cargadas
     */
    public int getNumeroTexturas()
    {
        return mTextures.size();
    }

    /**
     * Devuelve el n&uacute;mero la textura que se encuentra en una posici&oacute;n
     * espec&iacute;fica. Aunque es un m&eacute;todo muy simple es necesario ya que 
     * se llama desde c&oacute;digo nativo.
     * 
     * @param i posici&oacute;n requerida
     * @return Textura de la posici&oacute;n i
     */
    public Texture getTexture(int i)
    {
        return mTextures.elementAt(i);
    }

    /**
     * Carga una librer&iacute;a pasado por par&aacute;metro que se encuentr en "libs/armeabi*"
     * @param nombreLibreria nombre de la librer&iacute;a
     * @return	<code>true</code> cargada con exito
     * 			<code>false</code> fallo al cargar la librer&iacute;a
     */
    public static boolean cargarLibreria(String nombreLibreria)
    {
        try
        {
            System.loadLibrary(nombreLibreria);
            return true;
        }
        catch (Exception e)
        {
        	e.printStackTrace();
        }
        
        return false;
    }

    /**
     * Muestra las opciones de la c&aacute;mara cuando se pulsa el bot&oacute;n de Menu
     */
    public boolean onKeyUp(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_MENU)
        {
            mostrarDialogoOpciones();
            return true;
        }

        return super.onKeyUp(keyCode, event);
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onTouchEvent(android.view.MotionEvent)
     */
    public boolean onTouchEvent(MotionEvent event)
    {
    	return mDetectorGestos.onTouchEvent(event);
    }
    
    /**
     * Muestra un Di&aacute;logo con las opciones dispoibles para la c&aacute;mara
     */
    public void mostrarDialogoOpciones()
    {
        // Solo se muestran las opciones si la APP ya ha sido inicializada
        if (mEstadoActualAPP != EstadoAPP.CAMERA_RUNNING && mEstadoActualAPP != EstadoAPP.CAMERA_STOPPED)
        {
            return;
        }

        final int itemCameraFlash = 0;
        final int itemAutofocusIndex = 1;
        final int itemCameraActive = 2;

        AlertDialog dialogoOpciones = null;

        CharSequence[] items = {	getString(R.string.menu_flash_on),
                					getString(R.string.menu_contAutofocus_off), 
                					getString(R.string.menu_contCameraBack)};

        // Actualizar los títulos de la lista de acuerdo al estado actual
        // de las opciones
        if (mFlash)
            items[itemCameraFlash] = (getString(R.string.menu_flash_off));
        else
            items[itemCameraFlash] = (getString(R.string.menu_flash_on));

        if (mContinuoAutoenfoque)
            items[itemAutofocusIndex] = (getString(R.string.menu_contAutofocus_off));
        else
            items[itemAutofocusIndex] = (getString(R.string.menu_contAutofocus_on));
        
        if(mCamaraActiva == 0)
        	items[itemCameraActive] = (getString(R.string.menu_contCameraFront));
        else
        	items[itemCameraActive] = (getString(R.string.menu_contCameraBack));


        AlertDialog.Builder cameraOptionsDialogBuilder = new AlertDialog.Builder(ARXylophoneBase.this);
       
        cameraOptionsDialogBuilder.setTitle(getString(R.string.menu_camera_title));
        
        cameraOptionsDialogBuilder.setItems(items,
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int item)
                    {
                    	switch(item)
                    	{
                    		case(itemCameraFlash):
                    			if (setFlash(!mFlash))
                                {
                                    mFlash = !mFlash;
                                    if(mFlash)
                                    	Utils.crearToast(getString(R.string.toast_flash_on), ARXylophoneBase.this);
                                    else
                                    	Utils.crearToast(getString(R.string.toast_flash_off), ARXylophoneBase.this);
                                }
                    			else
                    				Utils.crearToast(getString(R.string.toast_flash_fallo), ARXylophoneBase.this);

                            	dialog.dismiss();
                    			break;
                    			
                    		case(itemAutofocusIndex):
                    			if (mContinuoAutoenfoque)
                                {
                    				if (setModoEnfoque(ModoEnfoque.NORMAL.ordinal()))
                                    {
                    					mContinuoAutoenfoque = false;
                        				Utils.crearToast(getString(R.string.toast_enfoque_on), ARXylophoneBase.this);
                                    }
                    				else
                        				Utils.crearToast(getString(R.string.toast_enfoque_fallo), ARXylophoneBase.this);
                                }
                    			else
                                {
                    				if (setModoEnfoque(ModoEnfoque.CONTINUO_AUTO.ordinal()))
                    				{
                    					mContinuoAutoenfoque = true;
                        				Utils.crearToast(getString(R.string.toast_autoenfoque_on), ARXylophoneBase.this);
                    				}
                    				else
                    					Utils.crearToast(getString(R.string.toast_autoenfoque_fallo), ARXylophoneBase.this);
                                }
                    		
                            	dialog.dismiss();
                    			break;
                    			
                    		case(itemCameraActive):
                            	if(hasFrontCamera)
                            	{
    	                        	mCamaraActiva = mCamaraActiva == 0 ? 1 : 0;
    	                        	iniciarCamara(mCamaraActiva);
                            	}
                            	else
                    				Utils.crearToast(getString(R.string.toast_camara_fallo), ARXylophoneBase.this);
                    		
	                            dialog.dismiss();
	                    		break;
                    	}
                    }
                });

        dialogoOpciones = cameraOptionsDialogBuilder.create();
        dialogoOpciones.show();
    }
  
    /**
     * M&eacute;todo para reproducir un sonido y desbloquear el bot&oacute;n
     * que ha sido pulsado.
     * @param key Tecla/bot&oacute;n del xil&oacute;fono.
     */
    public void playKey(String key)
    {
    	mReproductor.reproducirSonido(key);
    	desbloquearTecla(key, 450);
    }

    /**
     * Actualiza cada frame. 
     * @param primeraDeteccion true: primera vez que se detecta el Target.
     * @param detectado true: se encuentra detectado en este frame.
     */
    public abstract void actualizarFrame(boolean primeraDeteccion, boolean detectado);
 
    /**
     * Desbloquea un bot&oacute;n pasado unos ms.
     * @param key Tecla/bot&oacute;n a desbloquear.
     * @param tiempoEspera Tiempo (ms) a esperar antes de desbloquear el bot&oacute;n 
     */
    protected void desbloquearTecla(String key, int tiempoEspera)
    {
    	Timer timer;
    	timer = new Timer();
    	
    	// TimerTask para lanzar la tarea pasado un tiempo
    	MyTimerTask timerTask = new MyTimerTask(key);
    	timer.schedule(timerTask, tiempoEspera);
    }

    /**
     * M&eacute;todo para finalizar el Activity y volver a la pantalla
     * de selecci&oacute;n.
     */
    protected void finalizar()
    {
    	finish();
    	Intent i = new Intent(this, PantallaSeleccion.class);
	   	startActivity(i);
    }
    
    /**
     * M&eacute;todo para salir. Se llama desde un bot&oacute;n del GUI.
     * @param view
     */
    public void botonSalir(View view)
    {
    	finalizar();
    }
    
    /**
     * M&eacute;todo para mostrar las opciones. Se llama desde un
     * bot&oacute;n del GUI.
     * @param view
     */
    public void botonOpciones(View view)
    {
    	mostrarDialogoOpciones();
    }
}
