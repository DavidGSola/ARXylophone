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

/**
 * Define los Shader para dibujar cubos como char*
 * para que posteriormente se puedan interpretar como
 * programas.
 */

#ifndef _QCAR_CUBO_SHADERS_H_
#define _QCAR_CUBO_SHADERS_H_

/**
 * Vertex Shader para un cubo
 */
static const char* cubeMeshVertexShader = " \
 \
attribute vec4 vertexPosicion; \
attribute vec4 vertexNormal; \
attribute vec2 vertexTexCoord; \
\
varying vec2 texCoord; \
varying vec4 normal; \
\
uniform mat4 modelViewProjectionMatrix; \
\
void main() \
{ \
  gl_Position = modelViewProjectionMatrix * vertexPosicion; \
  normal = vertexNormal; \
  texCoord = vertexTexCoord; \
} \
";

/**
 * Fragment Shader para un cubo. Posee un atributo opacidad
 * para poder modificar en tiempo de ejecución la transparencía
 * de una coordenada del cubo.
 */
static const char* cubeFragmentShader = " \
\
precision mediump float; \
\
varying vec2 texCoord; \
varying vec4 normal; \
uniform sampler2D texSampler2D; \
uniform float opacidad; \
\
void main() \
{ \
  gl_FragColor = texture2D(texSampler2D, texCoord); \
  gl_FragColor.a *= opacidad; \
} \
";

#endif // _QCAR_CUBO_SHADERS_H_
