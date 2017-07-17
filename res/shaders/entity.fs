#version 400 core

in vec2 uv;
in float materialId;

out vec4 pixelColor;

#include "Texture.smd: mainTexture"
#include "Material.smd"
#include "Light.fsmd"
#include "Fog.fsmd"
#include "ScreenSize.smd"

uniform sampler2D skyBuffer;


uniform vec3 skyColor;
uniform float minBrightness;

uniform ListPointLight       pointLights;
uniform ListLineLight        lineLights;
uniform ListDirectionalLight dirLights;


void main(void){
	initFog(wPos);
	pixelColor=mainTexture(uv);
	if(pixelColor.a==0)discard;
	
	ModelMaterial m=getMaterial(int(round(materialId)));
	
	calculateLighting(m, pointLights, lineLights, dirLights);
	pixelColor=applyLighting(pixelColor, minBrightness, m);
	
	vec2 screenPos=(gl_FragCoord.xy/screenSize);
	vec4 fog=applyFog(pixelColor, texture(skyBuffer, screenPos).rgb);
	pixelColor=mix(pixelColor,fog,1-fogCalculated);
	
}
