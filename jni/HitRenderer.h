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

#include <android/log.h>
#include <stdio.h>
#include <string.h>

#include <QCAR/QCAR.h>
#include <QCAR/Tool.h>

#include "Textura.h"

/**
 * Clase que representa un Hit dibujado en pantalla.
 * Posee métoodos para modificar la posición, cambiar
 * la textura y para animar
 */
class HitRenderer
{
	public:
		/**
		 * Constructor por defecto
		 */
		HitRenderer();

		/**
		 * Destructor
		 */
		~HitRenderer();

		/**
		 * Devuelve la posición en X
		 */
		float getPosicionX();

		/**
		 * Devuelve la posición en Y
		 */
		float getPosicionY();

		/**
		 * Devuelve la opacidad actual
		 * del Hit.
		 */
		float getOpacidad();

		/**
		 * Devuelve la escala
		 */
		float getEscala();

		/**
		 * Devuelve la nota del Hit
		 */
		char getNota();

		/**
		 * Devuelve el ID del Hit
		 */
		int getId();

		/**
		 * Devuelve la textura
		 */
		Textura* getTextura();

		/**
		 * Setea una nueva posición
		 */
		void setPosicion(float x, float y);

		/**
		 * Cambia el estado del Hit
		 */
		void setEstado(int newEstado);

		/**
		 * Setea la nota del Hit
		 */
		void setNota(char newNota);

		/**
		 * Setea el ID del Hit
		 */
		void setId(int id);

		/**
		 * Asocia una texture al Hit
		 */
		void setTextura(Textura* t);

		/**
		 * Realiza la animación del Hit
		 * dependiendo del Estado actual
		 * del Hit. Modifica la posición
		 * y la opacidad del Hit.
		 */
		void animation(double ms);

		/**
		 * Devuelve true si el Hit debe
		 * seguir animandose o False
		 * si ya ha terminado su animación
		 */
		bool isAlive();

	private:
		/**
		 * Duración de la animación en Milisegundos
		 */
		const static float DURACION_ANIMACION_MS;

		/**
		 * Nota del Hit
		 */
		char nota;

		/**
		 * Estado del Hit:
		 * 		0 = no tocado
		 * 		1 = tocado
		 */
		int estado;

		/**
		 * ID del hit
		 */
		int id;

		/**
		 * Opacidad del Hit
		 */
		float opacidad;

		/**
		 * Escala del Hit
		 */
		float escala;

		/**
		 * Textura del Hit
		 */
		Textura* textura;

		/**
		 * Posición(x,y) del Hit
		 */
		QCAR::Vec2F posicion;
};
