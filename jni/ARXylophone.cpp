#include <ARXylophone.h>

JNIEXPORT void JNICALL
Java_coreXilofono_ARXylophoneBase_setModo(JNIEnv *, jobject, MODO_JUEGO modo, int mano)
{
	modoJuegoActual = modo;
	manoJuegoActual = mano;
}

JNIEXPORT int JNICALL
Java_coreXilofono_ARXylophoneBase_iniciarTracker(JNIEnv *, jobject)
{
    // Inicializar el tracker
    QCAR::TrackerManager& trackerManager = QCAR::TrackerManager::getInstance();
    QCAR::Tracker* tracker = trackerManager.initTracker(QCAR::Tracker::IMAGE_TRACKER);

    if (tracker == NULL)
    {
        LOG("Fallo inicializando el tracker.");
        return 0;
    }

    return 1;
}

JNIEXPORT void JNICALL
Java_coreXilofono_ARXylophoneBase_destruirTracker(JNIEnv *, jobject)
{
    // Destruir la imagen del tracker
    QCAR::TrackerManager& trackerManager = QCAR::TrackerManager::getInstance();
    trackerManager.deinitTracker(QCAR::Tracker::IMAGE_TRACKER);
}

JNIEXPORT int JNICALL
Java_coreXilofono_ARXylophoneBase_cargarDatosTracker(JNIEnv *, jobject)
{
    QCAR::TrackerManager& trackerManager = QCAR::TrackerManager::getInstance();
    QCAR::ImageTracker* imageTracker = static_cast<QCAR::ImageTracker*>(
                    trackerManager.getTracker(QCAR::Tracker::IMAGE_TRACKER));
    if (imageTracker == NULL)
    {
        LOG("Fallo al cargar los datos del Tracker debido a que no ha sido inicializado.");
        return 0;
    }

    // Crear los datataset del Tracker
    dataSetARXylophone = imageTracker->createDataSet();
    if (dataSetARXylophone == 0)
    {
        LOG("Fallo creando el nuevo datataset.");
        return 0;
    }
    if (!dataSetARXylophone->load("arXylophon.xml", QCAR::DataSet::STORAGE_APPRESOURCE))
    {
        LOG("Fallo al cargar los dataset.");
        return 0;
    }

    // Activar el dataset
    if (!imageTracker->activateDataSet(dataSetARXylophone))
    {
        LOG("Fallo al activar el dataset.");
        return 0;
    }

    return 1;
}

JNIEXPORT int JNICALL
Java_coreXilofono_ARXylophoneBase_destruirDatosTracker(JNIEnv *, jobject)
{
    QCAR::TrackerManager& trackerManager = QCAR::TrackerManager::getInstance();
    QCAR::ImageTracker* imageTracker = static_cast<QCAR::ImageTracker*>(
        trackerManager.getTracker(QCAR::Tracker::IMAGE_TRACKER));

    if (imageTracker == NULL)
    {
        LOG("Fallo al cargar los datos del Tracker debido a que no ha sido inicializado.");
        return 0;
    }
    
    // Si ya ha sido iniciado el dataset
    if (dataSetARXylophone != 0)
    {
    	// Desactivar el dataset en caso de estar activado
        if (imageTracker->getActiveDataSet() == dataSetARXylophone &&
            !imageTracker->deactivateDataSet(dataSetARXylophone))
        {
            LOG("Fallo al desactivar el dataset.");
            return 0;
        }

        // Destruir el dataset
        if (!imageTracker->destroyDataSet(dataSetARXylophone))
        {
            LOG("Fallo al destruir el dataset.");
            return 0;
        }

        dataSetARXylophone = 0;
    }

    return 1;
}

JNIEXPORT void JNICALL
Java_coreXilofono_MyTimerTask_desbloquearTecla(JNIEnv *, jobject, char tecla)
{
	mapVBBloqueados[tecla] = false;
}

JNIEXPORT void JNICALL
Java_coreXilofono_ARXylophoneGuiada_limpiarAnimaciones(JNIEnv *, jobject)
{
	// Se vacian ambos vectores de animación
	vectorHit.clear();
	vectorHitDone.clear();
}

JNIEXPORT void JNICALL
Java_coreXilofono_ARXylophoneGuiada_addHit(JNIEnv *, jobject, char hitTecla, int id)
{
	// Crear el nuevo hit
	HitRenderer* hit = new HitRenderer();
	hit->setNota(hitTecla);
	hit->setId(id);

	// Asignar la posición y la textura dependiendo de la
	// tecla que sea
	switch(hit->getNota())
	{
		case('C'):
			hit->setPosicion(-0.8,0);
			hit->setTextura(texturas[0]);
			break;
		case('D'):
			hit->setPosicion(-0.57,0);
			hit->setTextura(texturas[1]);
			break;
		case('E'):
			hit->setPosicion(-0.34,0);
			hit->setTextura(texturas[2]);
			break;
		case('F'):
			hit->setPosicion(-0.11,0);
			hit->setTextura(texturas[3]);
			break;
		case('G'):
			hit->setPosicion(0.12,0);
			hit->setTextura(texturas[4]);
			break;
		case('A'):
			hit->setPosicion(0.35,0);
			hit->setTextura(texturas[5]);
			break;
		case('B'):
			hit->setPosicion(0.58,0);
			hit->setTextura(texturas[6]);
			break;
		case('c'):
			hit->setPosicion(0.81,0);
			hit->setTextura(texturas[7]);
			break;
	}

	// Añadir al vector en el último lugar
	vectorHit.push_back(hit);
}

JNIEXPORT void JNICALL
Java_coreXilofono_ARXylophoneGuiada_deleteHit(JNIEnv *,jobject)
{
	// Siempre se elimina el primero

	// Cambiar el estado para especificar
	// que está desapareciendo
	vectorHit[0]->setEstado(1);

	// Añadir al vector de Hits realizados
	vectorHitDone.push_back(vectorHit[0]);

	// Eliminar del vector de Hits a realizar
	vectorHit.erase(vectorHit.begin());
}

JNIEXPORT void JNICALL
Java_coreXilofono_ARXRenderer_renderizarFrame(JNIEnv *env, jobject obj)
{
	// Calcular el tiempo transcurrido entre el frame
	// anterior y el actual
	double diferenciaMs = calcularDiferenciaMs();

    // Limpiar el buffer de color y de profundidad
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

    // Se obtiene el estado de QCAR y se marca el inicio de la sección de renderizado
    QCAR::State estadoQCAR = QCAR::Renderer::getInstance().begin();
    
    // Dibujar el video background
    QCAR::Renderer::getInstance().drawVideoBackground();

    // Activar el el GL_DEPTH_TEST
    glEnable(GL_DEPTH_TEST);

    // Activar el CULLING para sacrificar aquellos vértices ocultos
    glEnable(GL_CULL_FACE);
    glCullFace(GL_BACK);

    // Si el reflejo está activado quiere decir que nos encontramos en la cámara frontal.
    // Si nos encontramos en la cámara frontal definimos como caras frontales aquellos polígonos
    // dados en orden de las agujas del reloj. En caso de encontrarnos en la cámara
    // trasera definimos como caras frontales aquellos polígonos dados en orden inverso
    // a las agujas del reloj.

    // Esto se hace debido a que al tener activado el modo reflejo Vuforia actualiza automáticamente
    // la matriz de proyección y es necesario modificar la definición de las caras frontales
    if(QCAR::Renderer::getInstance().getVideoBackgroundConfig().mReflection == QCAR::VIDEO_BACKGROUND_REFLECTION_ON)
        glFrontFace(GL_CW);		// Cámara frontal
    else
        glFrontFace(GL_CCW);  	// Cámara trasera

    // Comprobar si se ha detectado el Trackable en este frame
    if (estadoQCAR.getNumTrackableResults() > 0)
    {
    	// Llamar al método java ActualizarFrame
    	getJNIEnv()->CallVoidMethod(activityObj, metodoActualizarFrame, primeraDeteccion, true);
    	// La primera vez que se detecte se pondrá a false la variable
    	// primeraDeteccion
    	primeraDeteccion = primeraDeteccion ? false : false;

    	// Se obtiene el trackable
        const QCAR::TrackableResult* trackableResult = estadoQCAR.getTrackableResult(0);
        const QCAR::Trackable& trackable = trackableResult->getTrackable();

        // Se inicializa la matriz Model View
        modelViewMatrix = QCARMath::Matrix44FIdentity();
			
		// Seteamos la matriz Model View Projection como resultado
        // de multiplicar la matriz de Proyección y la Model View
        QCAR::Matrix44F modelViewProjection;
        QCARUtils::multiplyMatrix(&projectionMatrix.data[0],
                                    &modelViewMatrix.data[0],
                                    &modelViewProjection.data[0]);

		// Abortamos en caso de que el Tracable obetenido no sea
        // de tipo IMAGE_TARGET_RESULT
        assert(trackableResult->getType() == QCAR::TrackableResult::IMAGE_TARGET_RESULT);

        // Procesar los virtual buttons
        procesarVirtualButtons(trackableResult);

        // Especificar el shader
        glUseProgram(shaderProgramID);

        // Activar los arrays donde se encuentran los atributos de los vertex
        glEnableVertexAttribArray(vertexHandle);
        glEnableVertexAttribArray(normalHandle);
        glEnableVertexAttribArray(textureCoordHandle);

        // Especificar la localicación y el formato de cada array anterior
        glVertexAttribPointer(vertexHandle, 3, GL_FLOAT, GL_FALSE, 0, (const GLvoid*) &cuboVertices[0]);
        glVertexAttribPointer(normalHandle, 3, GL_FLOAT, GL_FALSE, 0, (const GLvoid*) &cuboNormales[0]);
        glVertexAttribPointer(textureCoordHandle, 2, GL_FLOAT, GL_FALSE, 0, (const GLvoid*) &cuboTexCoords[0]);

        // Activar la primera textura
        glActiveTexture(GL_TEXTURE0);

        // Activar el blend y permitimos transparencias.
        // Es necesario porque algunas de las animaciones contienen
        // transparencias
        glEnable (GL_BLEND);
        glBlendFunc (GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        // Actualizar las animaciones en caso de encontrarnos en modo GUIADO
        if(modoJuegoActual == GUIADO)
        	procesarAnimaciones(diferenciaMs);

        // Desactivar el blend
        glDisable (GL_BLEND);
    }
    else // Si no se ha detectado ningún Target
    	getJNIEnv()->CallVoidMethod(activityObj, metodoActualizarFrame, false, false);

    glDisable(GL_DEPTH_TEST);

    // Marcamos el final de la sección de renderizado
    QCAR::Renderer::getInstance().end();
}

JNIEXPORT void JNICALL
Java_coreXilofono_ARXylophoneBase_iniciarAplicacionNativa( JNIEnv* env, jobject obj, jint ancho, jint alto)
{
    
    // Inicialiar el map usado para bloquear los botones
	for(int i=0; i<NUM_VB; i++)
		mapVBBloqueados[nombreVB[i][0]] = false;

	primeraDeteccion = true;

	// Iniciar el tiempo
	gettimeofday(&tiempoAntiguo, NULL);

	// Obtener una referenciaa la maquina virtual de java,
	// a la clase Java a la que llamar y al objeto para
	// posteriomente poder obtener referencias a los métodos
	env->GetJavaVM(&javaVM);
	jclass cls = env->GetObjectClass(obj);
	activityClase = (jclass) env->NewGlobalRef(cls);
	activityObj = env->NewGlobalRef(obj);

	// Obtener las referencias a los métodos Java que habrá que llamar posteriormente
	metodoPlayKey = env->GetMethodID(activityClase, "playKey", "(Ljava/lang/String;)V");
	metodoGetNumeroTexturas = env->GetMethodID(activityClase, "getNumeroTexturas", "()I");
	metodoGetTextura = env->GetMethodID(activityClase, "getTexture", "(I)LcoreXilofono/Texture;");
	metodoActualizarFrame = env->GetMethodID(activityClase, "actualizarFrame", "(ZZ)V");

	if(modoJuegoActual == GUIADO)
		metodoEliminarHit = env->GetMethodID(activityClase, "eliminarHit", "(I)V");

    // Almacenar las dimensiones de la pantalla
    screenWidth = ancho;
    screenHeight = alto;

    // Obtener el número de texturas llamando al método Java
    numTexturas = env->CallIntMethod(obj, metodoGetNumeroTexturas);
    texturas = new QCARTexture*[numTexturas];

    // Crear las texturas
    for (int i=0; i<numTexturas; i++)
    {
        jobject textureObject = env->CallObjectMethod(obj, metodoGetTextura, i);
        if (textureObject == NULL)
        {
            LOG("Fallo al obtener la textura.");
            return;
        }

        texturas[i] = QCARTexture::create(env, textureObject);
    }
}

JNIEXPORT void JNICALL
Java_coreXilofono_ARXylophoneBase_destruirAplicacionNativa(JNIEnv* env, jobject obj)
{
    // Eliminar las texturas
    if (texturas != 0)
    {    
        for (int i=0; i<numTexturas; i++)
        {
            delete texturas[i];
            texturas[i] = NULL;
        }
    
        delete[]texturas;
        texturas = NULL;
        
        numTexturas = 0;
    }

    // Eliminar los vectores
    vectorHit.clear(),
    vectorHitDone.clear();
}

JNIEXPORT void JNICALL
Java_coreXilofono_ARXylophoneBase_iniciarCamara(JNIEnv *,jobject, jint idCamara)
{
	// Activar la cámara
	QCAR::CameraDevice::CAMERA camara;
	if(idCamara == 0)
		camara = QCAR::CameraDevice::CAMERA_DEFAULT;
	else if(idCamara == 1)
		camara = QCAR::CameraDevice::CAMERA_FRONT;

	// Inicializar la cámara
    if (!QCAR::CameraDevice::getInstance().init(camara))
        return;

    // Configurar el video background
    configureVideoBackground();

    // Seleccionar el modo por defecto
    if (!QCAR::CameraDevice::getInstance().selectVideoMode(QCAR::CameraDevice::MODE_DEFAULT))
        return;

    // Iniciar la cámara
    if (!QCAR::CameraDevice::getInstance().start())
        return;

    // Iniciar el Tracker
    QCAR::TrackerManager& trackerManager = QCAR::TrackerManager::getInstance();
    QCAR::Tracker* imageTracker = trackerManager.getTracker(QCAR::Tracker::IMAGE_TRACKER);

    if(imageTracker != 0)
        imageTracker->start();
}

JNIEXPORT void JNICALL
Java_coreXilofono_ARXylophoneBase_pararCamara(JNIEnv *, jobject)
{
    // Parar el Tracker:
    QCAR::TrackerManager& trackerManager = QCAR::TrackerManager::getInstance();
    QCAR::Tracker* imageTracker = trackerManager.getTracker(QCAR::Tracker::IMAGE_TRACKER);

    if(imageTracker != 0)
        imageTracker->stop();
    
    // Parar y destruir la cámara
    QCAR::CameraDevice::getInstance().stop();
    QCAR::CameraDevice::getInstance().deinit();
}

JNIEXPORT jboolean JNICALL
Java_coreXilofono_ARXylophoneBase_setFlash(JNIEnv*, jobject, jboolean activar)
{
	// Activar o desactivar el flash
    return QCAR::CameraDevice::getInstance().setFlashTorchMode((activar==JNI_TRUE)) ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT jboolean JNICALL
Java_coreXilofono_ARXylophoneBase_activarAutoEnfoque(JNIEnv*, jobject)
{
	// Activar o desactivar el modo auto enfoque
    return QCAR::CameraDevice::getInstance().setFocusMode(QCAR::CameraDevice::FOCUS_MODE_TRIGGERAUTO) ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT jboolean JNICALL
Java_coreXilofono_ARXylophoneBase_setModoEnfoque(JNIEnv*, jobject, jint modo)
{
    int QCARModoEnfoque;

    switch((int)modo)
    {
        case 0:
        	QCARModoEnfoque = QCAR::CameraDevice::FOCUS_MODE_NORMAL;
            break;
        case 1:
        	QCARModoEnfoque = QCAR::CameraDevice::FOCUS_MODE_CONTINUOUSAUTO;
            break;
        case 2:
        	QCARModoEnfoque = QCAR::CameraDevice::FOCUS_MODE_INFINITY;
            break;
        case 3:
        	QCARModoEnfoque = QCAR::CameraDevice::FOCUS_MODE_MACRO;
            break;
        default:
            return JNI_FALSE;
    }
    
    return QCAR::CameraDevice::getInstance().setFocusMode(QCARModoEnfoque) ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT void JNICALL
Java_coreXilofono_ARXRenderer_iniciarRenderizado(JNIEnv* env, jobject obj)
{
	// Se inicia la matriz de proyección a la matriz identidad
	// para que todo lo que se dibuje sea relativo a la pantalla
	projectionMatrix = QCARMath::Matrix44FIdentity();

    // Se define el ClearColor de OpenGL
    glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

    // Generar las texturas de OpenGL
    for (int i=0; i<numTexturas; i++)
    {
    	glGenTextures(1, &(texturas[i]->mTextureID));
    	glBindTexture(GL_TEXTURE_2D, texturas[i]->mTextureID);

    	// Filtro Minification
    	glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);

    	// Filtro Magnification
    	glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

    	// Leer la imagen
    	glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, texturas[i]->mWidth,
                texturas[i]->mHeight, 0, GL_RGBA, GL_UNSIGNED_BYTE,
                (GLvoid*)  texturas[i]->mData);
    }

    // Crear el Shader
    shaderProgramID     = QCARUtils::createProgramFromBuffer(cubeMeshVertexShader, cubeFragmentShader);

    // Obtener las localizaciones de los atributos que se encuentran en el programa del Shader
    vertexHandle        = glGetAttribLocation(shaderProgramID, "vertexPosicion");

    normalHandle        = glGetAttribLocation(shaderProgramID, "vertexNormal");
    textureCoordHandle  = glGetAttribLocation(shaderProgramID, "vertexTexCoord");
    // Obtener las localizaciones de las variables uniformes que se encuentran en el programa del Shader
    mvpMatrixHandle     = glGetUniformLocation(shaderProgramID, "modelViewProjectionMatrix");
    tex2DHandle  = glGetUniformLocation(shaderProgramID, "texSampler2D");
    opacidad  			= glGetUniformLocation(shaderProgramID, "opacidad");
}

JNIEXPORT void JNICALL
Java_coreXilofono_ARXRenderer_reconfigurarRenderizado(JNIEnv* env, jobject obj, jint width, jint height)
{
    // Actualizar las dimensiones de la pantalla
    screenWidth = width;
    screenHeight = height;

    // Reconfigurar el video background con los nuevos valores
    configureVideoBackground();
}


JNIEnv* getJNIEnv()
{
    JNIEnv* env;
    if (javaVM == NULL || javaVM->GetEnv((void**)&env, JNI_VERSION_1_4) != JNI_OK)
    {
        return NULL;
    }

    return env;
}

void configureVideoBackground()
{
    // Obtener el modo de video a través de la cámara
    QCAR::CameraDevice& cameraDevice = QCAR::CameraDevice::getInstance();
    QCAR::VideoMode videoMode = cameraDevice.getVideoMode(QCAR::CameraDevice::MODE_DEFAULT);

    // Configurar el video background
    QCAR::VideoBackgroundConfig config;

    // Activar el rendering del video background
    config.mEnabled = true;

    // Activar la sincronización entre el render y el frame de la cámara
    config.mSynchronous = true;

    // Centrar el video background
    config.mPosition.data[0] = 0.0f;
    config.mPosition.data[1] = 0.0f;

    // Setear el ancho del video background al ancho de la pantalla
    config.mSize.data[0] = screenWidth;

    // Setear el alto del video background alto * relación entre el ancho de la pantalla
    // y el ancho del frame
    config.mSize.data[1] = videoMode.mHeight * (screenWidth / (float)videoMode.mWidth);

    // Si es menor que el alto de la pantalla
    if(config.mSize.data[1] < screenHeight)
    {
		LOG("Corrigiendo tamaño de la video background para manejar la discordancia entre el ratio de la pantalla y del vídeo.");
		config.mSize.data[0] = screenHeight * (videoMode.mWidth / (float)videoMode.mHeight);
		config.mSize.data[1] = screenHeight;
    }

   // Seteamos la configuración a la que hemos creado
    QCAR::Renderer::getInstance().setVideoBackgroundConfig(config);
}

void procesarVirtualButtons(const QCAR::TrackableResult* trackableResult)
{
	const QCAR::ImageTargetResult* imageTargetResult = static_cast<const QCAR::ImageTargetResult*>(trackableResult);

	if(manoJuegoActual == 0)
	{
		bool teclaPulsadaEncontrada = false;
		for (int i=imageTargetResult->getNumVirtualButtons()-1; i>=0 && !teclaPulsadaEncontrada; i--)
		{
			const QCAR::VirtualButtonResult* vbResultado = imageTargetResult->getVirtualButtonResult(i);
			const QCAR::VirtualButton& vb = vbResultado->getVirtualButton();

			// Si el VirtualButton está pulsado este frame hemos encontrado ya la tecla
			if(vbResultado->isPressed())
			{
				teclaPulsadaEncontrada = true;

				// Si no está bloqueada la hacemos sonar
				if(!(mapVBBloqueados[vb.getName()[0]]) )
				{
					// Se bloquea el botón virtual
					mapVBBloqueados[vb.getName()[0]] = true;
					// Se llama al método Java para notificar que se ha pulsado una tecla
					jstring nombreVB = getJNIEnv()->NewStringUTF( vb.getName());
					getJNIEnv()->CallVoidMethod(activityObj, metodoPlayKey, nombreVB);
					teclaPulsadaEncontrada = true;
				}
			}
		}
	}else
	{
		bool teclaPulsadaEncontrada = false;
		for (int i=0; i<imageTargetResult->getNumVirtualButtons() && !teclaPulsadaEncontrada; i++)
		{
			const QCAR::VirtualButtonResult* vbResultado = imageTargetResult->getVirtualButtonResult(i);
			const QCAR::VirtualButton& vb = vbResultado->getVirtualButton();

			// Si el VirtualButton está pulsado este frame hemos encontrado ya la tecla
			if(vbResultado->isPressed())
			{
				teclaPulsadaEncontrada = true;

				// Si no está bloqueada la hacemos sonar
				if(!(mapVBBloqueados[vb.getName()[0]]) )
				{
					// Se bloquea el botón virtual
					mapVBBloqueados[vb.getName()[0]] = true;
					// Se llama al método Java para notificar que se ha pulsado una tecla
					jstring nombreVB = getJNIEnv()->NewStringUTF( vb.getName());
					getJNIEnv()->CallVoidMethod(activityObj, metodoPlayKey, nombreVB);
				}
			}
		}
	}
}

void renderizarCubo(float* transform, float valorOpacidad)
{
	// Obtenemos la matriz de proyección Model View (pasar a coordenadas de pantalla)
    QCAR::Matrix44F modelViewProjection, objectMatrix;
    QCARUtils::multiplyMatrix(&modelViewMatrix.data[0], transform, &objectMatrix.data[0]);
    QCARUtils::multiplyMatrix(&projectionMatrix.data[0], &objectMatrix.data[0], &modelViewProjection.data[0]);

    // Modificamos el valor de las variables uniformes
    glUniformMatrix4fv(mvpMatrixHandle, 1, GL_FALSE, (GLfloat*)&modelViewProjection.data[0]);
    glUniform1i(tex2DHandle, 0);
    glUniform1f(opacidad, valorOpacidad);

    // Dibujamos el cubo
    glDrawElements(GL_TRIANGLES, NUM_CUBO_INDEX, GL_UNSIGNED_SHORT, (const GLvoid*) &cuboIndices[0]);
}

void procesarAnimaciones(double diferenciaMs)
{
	// Iterar sobre los Hits no tocados
	for(int i=0; i<vectorHit.size(); i++)
	{
		QCAR::Matrix44F transform = QCARMath::Matrix44FIdentity();
		float* transformPtr = &transform.data[0];

		// Si el Hit no ha terminado su animación
		if(vectorHit[i]->isAlive())
		{
			// Actualizar los valores internos del Hit
			vectorHit[i]->animation(diferenciaMs);

			// Setear la posición
			QCARUtils::translatePoseMatrix(vectorHit[i]->getPosicionX(),vectorHit[i]->getPosicionY(), 0.0f, transformPtr);

			// Setear la escala
			QCARUtils::scalePoseMatrix(0.11f, 0.11f, 0.0f, transformPtr);

			// Setear la textura a usar
			glBindTexture(GL_TEXTURE_2D, vectorHit[i]->getTextura()->mTextureID);

			// Dibujar el cubo
			renderizarCubo(transformPtr, 1.0f);
		}
		else
		{
			// Llamar al método Java para notificar que el Hit ha desaparecido
			// sin ser tocado
			getJNIEnv()->CallVoidMethod(activityObj, metodoEliminarHit, vectorHit[i]->getId());
			vectorHit.erase(vectorHit.begin()+i-1);
		}
	}

	// Iterar sobre los Hits tocados
	for(int i=0; i<vectorHitDone.size(); i++)
	{
		QCAR::Matrix44F transform = QCARMath::Matrix44FIdentity();
		float* transformPtr = &transform.data[0];

		// Si el Hit no ha terminado su animación
		if(vectorHitDone[i]->isAlive())
		{
			// Actualizar los valores internos del Hit
			vectorHitDone[i]->animation(diferenciaMs);

			// Setear la posición
			QCARUtils::translatePoseMatrix(vectorHitDone[i]->getPosicionX(),vectorHitDone[i]->getPosicionY(), 0.0f, transformPtr);

			// Setear la escala
			QCARUtils::scalePoseMatrix(vectorHitDone[i]->getEscala(), vectorHitDone[i]->getEscala(), 0.0f, transformPtr);

			// Setear la textura a usar
			glBindTexture(GL_TEXTURE_2D, vectorHitDone[i]->getTextura()->mTextureID);

			// Dibujar el cubo
			renderizarCubo(transformPtr, vectorHitDone[i]->getOpacidad());
		}
	}
}

double calcularDiferenciaMs()
{
	// Obtener la hora actual
	gettimeofday(&tiempoNuevo, NULL);

	// Calcular la diferencia
	double ms = (tiempoNuevo.tv_sec - tiempoAntiguo.tv_sec) * 1000 + (tiempoNuevo.tv_usec - tiempoAntiguo.tv_usec)/1000;

	// Actualizar el tiempo antiguo
	tiempoAntiguo = tiempoNuevo;

	return ms;
}
