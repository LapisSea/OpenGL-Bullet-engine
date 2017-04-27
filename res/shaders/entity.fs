#version 400 core

in vec2 uv;

out vec4 pixelColor;

#include "Texture: mainTexture"
#include "Material"
#include "Light"
#include "Fog"

void main(void){
	checkFogVisibility();
	pixelColor=mainTexture(uv);
	if(pixelColor.a<1/256.0)discard;
	pixelColor=applyFog(applyLighting(pixelColor, getMaterial()));
	
}
