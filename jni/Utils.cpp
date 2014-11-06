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

#include "Utils.h"

#include <math.h>
#include <stdlib.h>

#ifdef USE_OPENGL_ES_1_1
#include <GLES/gl.h>
#include <GLES/glext.h>
#else
#include <GLES2/gl2.h>
#include <GLES2/gl2ext.h>
#endif


void
Utils::checkGlError(const char* operation)
{ 
    for (GLint error = glGetError(); error; error = glGetError())
        LOG("after %s() glError (0x%x)", operation, error);
}


void
Utils::translatePoseMatrix(float x, float y, float z, float* matrix)
{
    // Sanity check
    if (!matrix)
        return;

    // matrix * translate_matrix
    matrix[12] += 
        (matrix[0] * x + matrix[4] * y + matrix[8]  * z);
        
    matrix[13] += 
        (matrix[1] * x + matrix[5] * y + matrix[9]  * z);
        
    matrix[14] += 
        (matrix[2] * x + matrix[6] * y + matrix[10] * z);
        
    matrix[15] += 
        (matrix[3] * x + matrix[7] * y + matrix[11] * z);
}

void
Utils::scalePoseMatrix(float x, float y, float z, float* matrix)
{
    // Sanity check
    if (!matrix)
        return;

    // matrix * scale_matrix
    matrix[0]  *= x;
    matrix[1]  *= x;
    matrix[2]  *= x;
    matrix[3]  *= x;
                     
    matrix[4]  *= y;
    matrix[5]  *= y;
    matrix[6]  *= y;
    matrix[7]  *= y;
                     
    matrix[8]  *= z;
    matrix[9]  *= z;
    matrix[10] *= z;
    matrix[11] *= z;
}


void
Utils::multiplyMatrix(float *matrixA, float *matrixB, float *matrixC)
{
    int i, j, k;
    float aTmp[16];

    for (i = 0; i < 4; i++)
    {
        for (j = 0; j < 4; j++)
        {
            aTmp[j * 4 + i] = 0.0;

            for (k = 0; k < 4; k++)
                aTmp[j * 4 + i] += matrixA[k * 4 + i] * matrixB[j * 4 + k];
        }
    }

    for (i = 0; i < 16; i++)
        matrixC[i] = aTmp[i];
}


unsigned int
Utils::initShader(unsigned int shaderType, const char* source)
{
#ifdef USE_OPENGL_ES_2_0    
    GLuint shader = glCreateShader((GLenum)shaderType);
    if (shader)
    {
        glShaderSource(shader, 1, &source, NULL);
        glCompileShader(shader);
        GLint compiled = 0;
        glGetShaderiv(shader, GL_COMPILE_STATUS, &compiled);
    
        if (!compiled)
        {
            GLint infoLen = 0;
            glGetShaderiv(shader, GL_INFO_LOG_LENGTH, &infoLen);
            if (infoLen)
            {
                char* buf = (char*) malloc(infoLen);
                if (buf)
                {
                    glGetShaderInfoLog(shader, infoLen, NULL, buf);
                    LOG("Could not compile shader %d: %s", 
                        shaderType, buf);
                    free(buf);
                }
                glDeleteShader(shader);
                shader = 0;
            }
        }
    }
    return shader;
#else
    return 0;
#endif
}


unsigned int
Utils::createProgramFromBuffer(const char* vertexShaderBuffer,
                                     const char* fragmentShaderBuffer)
{
#ifdef USE_OPENGL_ES_2_0    

    GLuint vertexShader = initShader(GL_VERTEX_SHADER, vertexShaderBuffer);
    if (!vertexShader)
        return 0;    

    GLuint fragmentShader = initShader(GL_FRAGMENT_SHADER,
                                        fragmentShaderBuffer);
    if (!fragmentShader)
        return 0;

    GLuint program = glCreateProgram();
    if (program)
    {
        glAttachShader(program, vertexShader);
        checkGlError("glAttachShader");
        
        glAttachShader(program, fragmentShader);
        checkGlError("glAttachShader");
        
        glLinkProgram(program);
        GLint linkStatus = GL_FALSE;
        glGetProgramiv(program, GL_LINK_STATUS, &linkStatus);
        
        if (linkStatus != GL_TRUE)
        {
            GLint bufLength = 0;
            glGetProgramiv(program, GL_INFO_LOG_LENGTH, &bufLength);
            if (bufLength)
            {
                char* buf = (char*) malloc(bufLength);
                if (buf)
                {
                    glGetProgramInfoLog(program, bufLength, NULL, buf);
                    LOG("Could not link program: %s", buf);
                    free(buf);
                }
            }
            glDeleteProgram(program);
            program = 0;
        }
    }
    return program;
#else
    return 0;
#endif
}


QCAR::Matrix44F
Utils::Matrix44FIdentity()
{
    QCAR::Matrix44F r;
    
    for (int i = 0; i < 16; i++)
        r.data[i] = 0.0f;
    
    r.data[0] = 1.0f;
    r.data[5] = 1.0f;
    r.data[10] = 1.0f;
    r.data[15] = 1.0f;
    
    return r;
}