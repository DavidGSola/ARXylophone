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
