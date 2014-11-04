
# Variable de la ruta. Se utiliza la llamada a my-dir para obtener
# la ruta del directorio actual.
LOCAL_PATH := $(call my-dir)

# Copiar la librería libQCAR.so en el directorio libs/armeabi o libs/armeabi-v7a 
# dependiendo del directorio. Tambíen se setea la ruta donde se encuentra
# los archivos de cabecera .h
include $(CLEAR_VARS)
LOCAL_MODULE := QCAR-prebuilt
LOCAL_SRC_FILES = ../../../build/lib/$(TARGET_ARCH_ABI)/libQCAR.so
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/../../../build/include
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)

# Se define el nombre del módulo
LOCAL_MODULE := ARXylophone

# Setear la configuración de OpenGL ES 2.0
OPENGLES_LIB  := -lGLESv2
OPENGLES_DEF  := -DUSE_OPENGL_ES_2_0

LOCAL_CFLAGS := -Wno-write-strings -Wno-psabi $(OPENGLES_DEF)

LOCAL_LDLIBS := \
    -llog $(OPENGLES_LIB)



# Librerias que necesita el programa en tiempo de compliación.
LOCAL_SHARED_LIBRARIES := QCAR-prebuilt

# Nombre de los ficheros con el código fuente a compliar

LOCAL_SRC_FILES := ARXylophone.cpp HitRenderer.cpp QCARUtils.cpp QCARMath.cpp QCARTexture.cpp

# Genera los ficheros .o a instrucciones de 32 bits

LOCAL_ARM_MODE := arm

include $(BUILD_SHARED_LIBRARY)

