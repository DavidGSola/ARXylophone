#include <jni.h>
#include <android/log.h>
#include <stdio.h>
#include <string.h>
#include <assert.h>
#include <time.h>
#include <map>
#include <vector>

#include <GLES2/gl2.h>
#include <GLES2/gl2ext.h>

#include <QCAR/QCAR.h>
#include <QCAR/CameraDevice.h>
#include <QCAR/Renderer.h>
#include <QCAR/VideoBackgroundConfig.h>
#include <QCAR/Trackable.h>
#include <QCAR/TrackableResult.h>
#include <QCAR/Tool.h>
#include <QCAR/Tracker.h>
#include <QCAR/TrackerManager.h>
#include <QCAR/ImageTracker.h>
#include <QCAR/ImageTarget.h>
#include <QCAR/ImageTargetResult.h>
#include <QCAR/CameraCalibration.h>
#include <QCAR/UpdateCallback.h>
#include <QCAR/VirtualButton.h>
#include <QCAR/VirtualButtonResult.h>
#include <QCAR/DataSet.h>

#include "QCARTexture.h"
#include "CuboShaders.h"
#include "Cubo.h"
#include "HitRenderer.h"

/**
 * Clase Utils dada por Vuforia
 */
#include "QCARUtils.h"

/**
 * Clase Math dada por Vuforia
 */
#include "QCARMath.h"

#ifdef __cplusplus
extern "C"
{
#endif

/**
 * Referencia a la máquina virtual de Java
 */
JavaVM* javaVM = NULL;

/**
 * Referencia a la clase que posee
 * los métodos Java que habrá que llamar
 * desde C++
 */
jclass activityClase;

/**
 * Referencia al objeto Java cuyos
 * métodos hay que llamar desde C++
 */
jobject activityObj;

/**
 * ID del método Java para tocar una tecla
 */
jmethodID metodoPlayKey;

/**
 * ID del método Java para obtener
 * el número de texturas
 */
jmethodID metodoGetNumeroTexturas;

/**
 * ID del método Java para obtener
 * una textura
 */
jmethodID metodoGetTextura;

/**
 * ID del método Java para actualizar
 * un frame
 */
jmethodID metodoActualizarFrame;

/**
 * ID del método Java para eliminar
 * un Hit
 */
jmethodID metodoEliminarHit;

/**
 * Número de texturas
 */
int numTexturas                = 0;

/**
 * Array de punteros a las texturas
 */
QCARTexture** texturas              = 0;

/**
 * ID del programa del Shader
 */
unsigned int shaderProgramID    = 0;

/**
 * Localizaciones de los diferentes atributos
 * y variables uniformes que se encuentran
 * en el shader.
 */
GLint vertexHandle              = 0;
GLint normalHandle              = 0;
GLint textureCoordHandle        = 0;
GLint mvpMatrixHandle           = 0;
GLint tex2DHandle        		= 0;
GLint opacidad		            = 0;

// Screen dimensions:
unsigned int screenWidth        = 0;
unsigned int screenHeight       = 0;

/**
 * Matriz de proyección
 */
QCAR::Matrix44F projectionMatrix;

/**
 * Matriz model view
 */
QCAR::Matrix44F modelViewMatrix;

/**
 * Enumerado con los distintos tipos
 * de juegos validos.
 */
enum MODO_JUEGO
{
	LIBRE	= 0,
	GUIADO	= 1,
	RECORD	= 2
};

/**
 * Modo de Juego actual
 */
int modoJuegoActual = -1;

/**
 * Mano con la que se está jugando actualmente
 */
int manoJuegoActual = -1;

/**
 * Nombre de los diferentes VirtualButtons
 */
const char* nombreVB[] = {"C", "D", "E", "F", "G", "A", "B", "c"};

/**
 * Número de VirtualButtons que se van a tener.
 * En este caso 8.
 */
const int NUM_VB = 8;

/**
 * DataSet del Tracker
 */
QCAR::DataSet* dataSetARXylophone = 0;

/**
 * Mapa que permite bloquear las teclas
 * asociando a cada tecla (dado por su nombre)
 * un bool (true=bloqueado, false=no_bloqueado).
 */
std::map <char, bool> mapVBBloqueados;

/**
 * Vector de Hits que se muestran por
 * pantalla pero aún no han sido tocados
 */
std::vector<HitRenderer*> vectorHit;

/**
 * Vector de Hits que si han sido tocados
 * y se encuentran realizando su animación
 * para desaparecer.
 */
std::vector<HitRenderer*> vectorHitDone;

/**
 * Tiempos del frame anterior y del actual
 */
struct timeval tiempoAntiguo, tiempoNuevo;

/**
 * Primera detección del Tracker
 */
bool primeraDeteccion = true;

/**
 * Setea el modo de juego y la mano con la que se
 * va a jugar.
 */
JNIEXPORT void JNICALL
Java_coreXilofono_ARXylophoneBase_setModo(JNIEnv *, jobject, MODO_JUEGO modo, int mano);

/**
 * Inicaliza el Tracker como IMAGE_TRACKER
 *
 * Return: 0 fallo, 1 ok
 */
JNIEXPORT int JNICALL
Java_coreXilofono_ARXylophoneBase_iniciarTracker(JNIEnv *, jobject);

/**
 * Destruye el Tracker
 */
JNIEXPORT void JNICALL
Java_coreXilofono_ARXylophoneBase_destruirTracker(JNIEnv *, jobject);

/**
 * Carga los datos del Tracker a través del XML de configuración
 * del Tracker.
 *
 * Return: 0 fallo, 1 ok
 */
JNIEXPORT int JNICALL
Java_coreXilofono_ARXylophoneBase_cargarDatosTracker(JNIEnv *, jobject);

/**
 * Destruye los datos del Tracker.
 *
 * Return: 0 fallo, 1 ok
 */
JNIEXPORT int JNICALL
Java_coreXilofono_ARXylophoneBase_destruirDatosTracker(JNIEnv *, jobject);

/**
 * Desbloque una tecla para que puedea ser tocada
 * de nuevo.
 */
JNIEXPORT void JNICALL
Java_coreXilofono_MyTimerTask_desbloquearTecla(JNIEnv *, jobject , char tecla);

/**
 * Limpia los vectores de Hits
 */
JNIEXPORT void JNICALL
Java_coreXilofono_ARXylophoneGuiada_limpiarAnimaciones(JNIEnv *, jobject);

/**
 * Se añade un Hit al vector de Hits con un id y una tecla
 * pasados como parámetros.
 */
JNIEXPORT void JNICALL
Java_coreXilofono_ARXylophoneGuiada_addHit(JNIEnv *, jobject, char teclaHit, int id);

/**
 * Elimina el primer Hit del vector de Hits a tocar
 * y se mueve al vector de Hits tocados
 */
JNIEXPORT void JNICALL
Java_coreXilofono_ARXylophoneGuiada_deleteHit(JNIEnv *, jobject);

/**
 * Renderiza un frame completo:
 * 1) Comprueba si hay detectado un Target
 * 2) Comprueba si hay algún botón virtual pulsado
 * 3) Lleva a cabo las animaciones de los Hits
 */
JNIEXPORT void JNICALL
Java_coreXilofono_ARXRenderer_renderizarFrame(JNIEnv *env, jobject obj);

/**
 * Inicializa todas las variables necesarias en la
 * aplicación nativa. Se deben pasar las dimensiones
 * de la pantalla por parámetros.
 */
JNIEXPORT void JNICALL
Java_coreXilofono_ARXylophoneBase_iniciarAplicacionNativa(JNIEnv* env, jobject obj, jint ancho, jint alto);
							
/**
 * Destruye las variables de la aplicación nativa.
 * En este caso solo es necesario destruir las texturas
 * y vaciar los vectores de Hits.
 */
JNIEXPORT void JNICALL
Java_coreXilofono_ARXylophoneBase_destruirAplicacionNativa(JNIEnv* env, jobject obj);

/**
 * Inicia la cámara: id==0 cámara trasera.
 * 					 id==1 cámara frontal.
 */
JNIEXPORT void JNICALL
Java_coreXilofono_ARXylophoneBase_iniciarCamara(JNIEnv *, jobject, jint idCamara);

/**
 * Para la cámara que se encuentre activa en el dispositivo
 */
JNIEXPORT void JNICALL
Java_coreXilofono_ARXylophoneBase_pararCamara(JNIEnv *, jobject);

/**
 * Activa/desactiva el flash dependiendo de un boolean
 * pasado por parámetro.
 * Devuelve: true en caso de modificar correctamente
 * 			 false en cualquier otro caso
 */
JNIEXPORT jboolean JNICALL
Java_coreXilofono_ARXylophoneBase_setFlash(JNIEnv*, jobject, jboolean activar);

/**
 * Activa el autoenfoque.
 * Devuelve: true en caso de activarse correctamente
 * 			 false en cualquier otro caso
 */
JNIEXPORT jboolean JNICALL
Java_coreXilofono_ARXylophoneBase_activarAutoEnfoque(JNIEnv*, jobject);

/**
 * Activa el modode  enfoque a un modo pasado por parámetro.
 * Devuelve: true en caso de activarse correctamente
 * 			 false en cualquier otro caso
 */
JNIEXPORT jboolean JNICALL
Java_coreXilofono_ARXylophoneBase_setModoEnfoque(JNIEnv*, jobject, jint modo);

/**
 * Inicializa todas los atributos de OpenGL y crea los shaders
 * para que se pueda llevar a cabo el renderizado
 */
JNIEXPORT void JNICALL
Java_coreXilofono_ARXRenderer_iniciarRenderizado(JNIEnv* env, jobject obj);

/**
 * Reconfigura el renderizado con las nuevas dimensiones
 * de la pantalla
 */
JNIEXPORT void JNICALL
Java_coreXilofono_ARXRenderer_reconfigurarRenderizado(JNIEnv* env, jobject obj, jint width, jint height);

/**
 * Devuelve el entorno de la maquina virtual de Java
 */
JNIEnv* getJNIEnv();

/**
 * Configura el video background teniendo en cuenta
 * las dimensiones de la pantalla y el modo de vídeo
 */
void configureVideoBackground();

/**
 * Procesa cada VirtualButton comprobando si ha sido pulsado.
 */
void procesarVirtualButtons(const QCAR::TrackableResult* trackableResult);

/**
 * Actualiza las animaciones de los Hits que aún no han sido
 * tocados y de los que si han sido tocados y están desapareciendo
 */
void procesarAnimaciones(double diferenciaMs);

/**
 * Renderiza un cubo dado una matriz de transformación
 * y un valor de opacidad
 */
void renderizarCubo(float* transform, float valorOpacidad);

/**
 * Devuelve el tiempo transcurrido entre el frame anterior
 * y el actual en milisegundos
 */
double calcularDiferenciaMs();

#ifdef __cplusplus
}
#endif
