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

// Include files
#include "Textura.h"

#include <string.h>

Textura::Textura() :
mWidth(0),
mHeight(0),
mChannelCount(0),
mData(0),
mTextureID(0)
{}


Textura::~Textura()
{
    if (mData != 0)
        delete [] mData;
}


Textura*
Textura::create(JNIEnv* env, jobject textureObject)
{

    Textura* newTexture = new Textura();

    // Handle to the Texture class:
    jclass textureClass = env->GetObjectClass(textureObject);

    // Get width:
    jfieldID widthID = env->GetFieldID(textureClass, "ancho", "I");
    if (!widthID)
    {
        delete newTexture;
        return 0;
    }
    newTexture->mWidth = env->GetIntField(textureObject, widthID);

    // Get height:
    jfieldID heightID = env->GetFieldID(textureClass, "alto", "I");
    if (!heightID)
    {
        delete newTexture;
        return 0;
    }
    newTexture->mHeight = env->GetIntField(textureObject, heightID);

    // Always use RGBA channels:
    newTexture->mChannelCount = 4;

    // Get data:
    jmethodID texBufferMethodId = env->GetMethodID(textureClass , "getData", "()[B");
    if (!texBufferMethodId)
    {
        delete newTexture;
        return 0;
    }
    
    jbyteArray pixelBuffer = (jbyteArray)env->CallObjectMethod(textureObject, texBufferMethodId);    
    if (pixelBuffer == NULL)
    {
        delete newTexture;
        return 0;
    }

    jboolean isCopy;
    jbyte* pixels = env->GetByteArrayElements(pixelBuffer, &isCopy);
    if (pixels == NULL)
    {
        env->ReleaseByteArrayElements(pixelBuffer, pixels, 0);
        delete newTexture;
        return 0;
    }

    newTexture->mData = new unsigned char[newTexture->mWidth * newTexture->mHeight * newTexture->mChannelCount];

    unsigned char* textureData = (unsigned char*) pixels;

    int rowSize = newTexture->mWidth * newTexture->mChannelCount;
    for (int r = 0; r < newTexture->mHeight; ++r)
    {
        memcpy(newTexture->mData + rowSize * r, pixels + rowSize * (newTexture->mHeight - 1 - r), newTexture->mWidth * 4);
    }

    // Release:
    env->ReleaseByteArrayElements(pixelBuffer, pixels, 0);
    
    return newTexture;
}

