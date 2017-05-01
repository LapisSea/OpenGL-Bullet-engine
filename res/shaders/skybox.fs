#version 400 core

in vec3 uv;

out vec4 pixelColor;

#include "Texture: cubeTexture"

void main(void){
	pixelColor=cubeTexture(uv);
	//pixelColor.rgb=uv;
}
