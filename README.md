#ARXylophone

TFG en el Grado de Ingeniería Informática: ARXylophone, un xilófono basado en realidad aumentada

## ¿Qué es ARXylophone?

ARXylophone es un juego de corte infantil cuyo fin es permitir a los más pequeños de la casa tocar un xilófono impreso en un papel como si fuese un xilófono real. Utilizando la cámara del dispositivo Android, la aplicación reconocerá que tecla está siendo tocada en el papel impreso y sonará en el smartphone la nota correspondiente.

## ¿Donde obtener ARXylophone?

La aplicación ha sido subida al mercado de aplicaciones propio del sistema
operativo Android Play Store:

[Enlace a la Play Store](https://play.google.com/store/apps/details?id=com.CojiSoft.ARXylophone)

### Modos de juego

Además, cuenta con tres modos de juego:
- Modo libre.
- Modo guiado, en el que con la ayuda de una guía en pantalla se permite tocar canciones como si se tratase de un Guitar Hero.
- Modo grabación, en el que se permite al usuario almacenar todo lo que toca para, posteriormente, tocar la canción en el modo guiado.

### Plantilla

Para poder utilizar esta aplicación es necesario imprimir (o utilizarlo desde la propia pantalla del PC) la siguiente imagen:

[Plantilla](http://imgur.com/mOPQP3M)

## Desarrollo

ARXylophone ha sido desarrollado para Android utilizando el SDK Vuforia de realidad aumentada. Esta aplicación está desarrollada en Java y C++ utilizado el NDK de Android para la comunicación entre ambos lenguajes. 

En C++ se ha desarrollado el núcleo de la aplicación con las llamadas al motor de realidad aumentada y todo lo relacionado con la parte gráfica, desarrollada utilizando OpenGL. Gracias a esto, se podría exportar está aplicación a iOS teniendo que reprogramar unicamente la parte de interfaz de usuario.
