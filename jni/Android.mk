# Copyright 2014 David González Sola (hispalos@gmail.com)
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.



# Variable de la ruta. Se utiliza la llamada a my-dir para obtener
# la ruta del directorio actual.
LOCAL_PATH := $(call my-dir)

# Copiar la librería libQCAR.so en el directorio libs/armeabi o libs/armeabi-v7a 
# dependiendo del directorio. Tambíen se setea la ruta donde se encuentra
# los archivos de cabecera .h
include $(CLEAR_VARS)
LOCAL_MODULE := QCAR-prebuilt
LOCAL_SRC_FILES = ../build/lib/$(TARGET_ARCH_ABI)/libVuforia.so
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/../build/include
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

LOCAL_SRC_FILES := ARXylophone.cpp HitRenderer.cpp Utils.cpp  Textura.cpp

# Genera los ficheros .o a instrucciones de 32 bits

LOCAL_ARM_MODE := arm

include $(BUILD_SHARED_LIBRARY)

