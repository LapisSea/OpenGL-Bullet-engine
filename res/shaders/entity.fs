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
	initFog(wPos.xz);
	pixelColor=mainTexture(uv);
	if(pixelColor.a==0)discard;
	
	ModelMaterial m=getMaterial(int(round(materialId)));
	
	calculateLighting(m, pointLights, lineLights, dirLights);
	pixelColor=applyLighting(pixelColor, minBrightness, m);
	
	vec2 screenPos=(gl_FragCoord.xy/screenSize);
	//screenPos.y=1-screenPos.y;
	vec3 bg=texture(skyBuffer, screenPos).rgb;
	//bg=applyLighting(vec4(bg,1), 0, m).rgb;
	//bg=mix(bg,applyLighting(vec4(bg,1), 0, m).rgb, fogCalculated*fogCalculated);
//	float b=(bg.r+bg.g+bg.b)/3;
//	bg=mix(skyColor,bg,b);
	vec4 fog=applyFog(pixelColor, bg);
	pixelColor=mix(pixelColor,fog,1-fogCalculated);
	
}
