#version 400 core

in vec2 uv;

out vec4 pixelColor;

#include "Texture: screen"

void main(void){
	pixelColor=screen(uv);
}
