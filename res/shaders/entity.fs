#version 400 core

in vec2 uv;

out vec4 pixelColor;

#include "Texture.smd: mainTexture"
#include "Material.smd"
#include "Light.fsmd"
#include "Fog.fsmd"

void main(void){
	initFog(wPos.xz);
	pixelColor=mainTexture(uv);
	if(pixelColor.a==0)discard;
	pixelColor=applyLighting(pixelColor, getMaterial());
	pixelColor=applyFog(pixelColor);
	
}
