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

#ifndef _QCAR_CUBO_H_
#define _QCAR_CUBO_H_

#define NUM_CUBO_VERTEX 24
#define NUM_CUBO_INDEX 36

/**
 * Vértices de un cubo
 */
static const float cuboVertices[NUM_CUBO_VERTEX * 3] =
{
    -1.00f,  -1.00f,   1.00f, // front
     1.00f,  -1.00f,   1.00f,
     1.00f,   1.00f,   1.00f,
    -1.00f,   1.00f,   1.00f,

    -1.00f,  -1.00f,  -1.00f, // back
     1.00f,  -1.00f,  -1.00f,
     1.00f,   1.00f,  -1.00f,
    -1.00f,   1.00f,  -1.00f,

    -1.00f,  -1.00f,  -1.00f, // left
    -1.00f,  -1.00f,   1.00f,
    -1.00f,   1.00f,   1.00f,
    -1.00f,   1.00f,  -1.00f,

     1.00f,  -1.00f,  -1.00f, // right
     1.00f,  -1.00f,   1.00f,
     1.00f,   1.00f,   1.00f,
     1.00f,   1.00f,  -1.00f,

    -1.00f,   1.00f,   1.00f, // top
     1.00f,   1.00f,   1.00f,
     1.00f,   1.00f,  -1.00f,
    -1.00f,   1.00f,  -1.00f,

    -1.00f,  -1.00f,   1.00f, // bottom
     1.00f,  -1.00f,   1.00f,
     1.00f,  -1.00f,  -1.00f,
    -1.00f,  -1.00f,  -1.00f
};

static const float cuboTexCoords[NUM_CUBO_VERTEX * 2] =
{
    0, 0,
    0.5, 0,
    0.5, 1,
    0, 1,

    0, 0,
    0.5, 0,
    0.5, 1,
    0, 1,

    0, 0,
    0.5, 0,
    0.5, 1,
    0, 1,

    1, 1,
    1, 0,
    0.5, 0,
    0.5, 1,

    0, 0,
    0.5, 0,
    0.5, 1,
    0, 1,

    0, 0,
    0.5, 0,
    0.5, 1,
    0, 1
};

static const float cuboNormales[NUM_CUBO_VERTEX * 3] =
{
    0, 0, 1,
    0, 0, 1,
    0, 0, 1,
    0, 0, 1,

    0, 0, -1,
    0, 0, -1,
    0, 0, -1,
    0, 0, -1,

    0, -1, 0,
    0, -1, 0,
    0, -1, 0,
    0, -1, 0,

    0, 1, 0,
    0, 1, 0,
    0, 1, 0,
    0, 1, 0,

    1, 0, 0,
    1, 0, 0,
    1, 0, 0,
    1, 0, 0,

    -1, 0, 0,
    -1, 0, 0,
    -1, 0, 0,
    -1, 0, 0
};

static const unsigned short cuboIndices[NUM_CUBO_INDEX] =
{
     0,  1,  2,  0,  2,  3, // front
     4,  6,  5,  4,  7,  6, // back
     8,  9, 10,  8, 10, 11, // left
    12, 14, 13, 12, 15, 14, // right
    16, 17, 18, 16, 18, 19, // top
    20, 22, 21, 20, 23, 22  // bottom
};


#endif // _QC_AR_CUBO_H_
