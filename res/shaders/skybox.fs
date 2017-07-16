#version 400 core

in vec3 uv;

out vec4 pixelColor;

uniform vec3 sunPos;
uniform float eyeHeight;
uniform float skyClarity;
uniform float viewFarPlane;

#include "atmosphere.fsmd"
#include "Texture.smd: cubeMainTexture"
#include "Screen.smd"

void main(void){
	vec3 uvUnit=normalize(uv);
	float eh=eyeHeight;
	if(eh<0)eh=0;
	float div=max(1,1+eh*eh/100);
	float sunset=1-pow(1-abs(sunPos.y),4);
	
	float worldSiz=20000*viewFarPlane/div;
	pixelColor = vec4(
		1.0 - exp(-1.0 * 
			atmosphere(
				uvUnit,	   		 				// normalized ray direction
				vec3(0,worldSiz,0),				// ray origin
				sunPos,							// position of the sun
				sunset*20+30,					// intensity of the sun
				worldSiz,					// radius of the planet in meters
				worldSiz+100e4/div,			// radius of the atmosphere in meters
				vec3(5.5e-6, 13.0e-6, 22.4e-6), // Rayleigh scattering coefficient
				21e-6,						 	// Mie scattering coefficient
				7e3,							// Rayleigh scale height
				1.2e3,							// Mie scale height
				0.997+sunset*0.001				// Mie preferred scattering direction
			)
		),
	1);
	
	float dot=dot(sunPos, uvUnit),sub=0.2;
	if(dot<sub)dot=0;
	else{
		dot=pow(dot-sub,4)*(1-sunset)*1.2;
		//if(sunPos.y<0)dot*=1-pow(-sunPos.y,4);
	}
	float bright=(pixelColor.r+pixelColor.g+pixelColor.b)/3;
	bright=clamp((bright+0.5),0,1);
	bright=clamp(bright+pow(1-uvUnit.y,3),0,1);
	pixelColor=mix(cubeMainTexture(uv),pixelColor,pow(bright,0.5));
	pixelColor.rgb=screen(vec3(1.5,0.5,0)*dot,pixelColor.rgb);
}
