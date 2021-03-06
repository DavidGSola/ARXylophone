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

#include "HitRenderer.h"

const float HitRenderer::DURACION_ANIMACION_MS = 2000;

HitRenderer::HitRenderer()
{
	opacidad = 1.0f;
	estado = 0;
	escala = 0.11f;
}

HitRenderer::~HitRenderer()
{
	delete [] textura;
}

float HitRenderer::getPosicionX()
{
	return posicion.data[0];
}

float HitRenderer::getPosicionY()
{
	return posicion.data[1];
}

float HitRenderer::getOpacidad()
{
	return opacidad;
}

float HitRenderer::getEscala()
{
	return escala;
}

char HitRenderer::getNota()
{
	return nota;
}

int HitRenderer::getId()
{
	return id;
}

Textura* HitRenderer::getTextura()
{
	return textura;
}

void HitRenderer::setEstado(int newEstado)
{
	estado = newEstado;
}

void HitRenderer::setNota(char newNota)
{
	nota = newNota;
}

void HitRenderer::setPosicion(float x, float y)
{
	posicion.data[0] = x;
	posicion.data[1] = y+0.75;
}

void HitRenderer::setId(int newId)
{
	id = newId;
}

void HitRenderer::setTextura(Textura* t)
{
	textura = t;
}

void HitRenderer::animation(double ms)
{
	if(estado == 0)
		posicion.data[1] = posicion.data[1]-(ms/DURACION_ANIMACION_MS);
	else if(estado == 1)
	{
		opacidad = opacidad - ((ms*0.5f)/500.0f);
		escala	= escala + ((ms*0.2f)/500.0f);
	}
}

bool HitRenderer::isAlive()
{
	if(estado == 0)
		if(posicion.data[1] < -1)
			return estado;
		else
			return true;
	else
		if(opacidad < 0.50f)
			return false;
		else
			return true;
}
