#version 400 core

in vec2 uv;
in float materialIdIn;

out vec4 pixelColor;

#include "Texture.smd: mainTexture"
#include "Material.smd"
#include "Light.fsmd"
#include "Fog.fsmd"


uniform vec3 skyColor;
uniform float minBrightness;

uniform ListPointLight       pointLights;
uniform ListLineLight        lineLights;
uniform ListDirectionalLight dirLights;


void main(void){
	initFog(wPos.xz);
	pixelColor=mainTexture(uv);
	if(pixelColor.a==0)discard;
	pixelColor=applyLighting(pixelColor, minBrightness, getMaterial(int(round(materialIdIn))), pointLights, lineLights, dirLights);
	pixelColor=applyFog(pixelColor, skyColor);
	pixelColor+=1;
}
