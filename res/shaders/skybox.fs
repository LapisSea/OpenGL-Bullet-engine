#version 400 core

in vec3 uv;

out vec4 pixelColor;

uniform vec3 sunPos;
uniform float eyeHeight;
uniform float skyClarity;

#include "atmosphere"

void main(void){
	float div=max(1,1+eyeHeight*eyeHeight/100);
	
	float worldSiz=6371e3/div;
	pixelColor = vec4(
		1.0 - exp(-1.0 * 
			atmosphere(
				normalize(uv),	   		 		// normalized ray direction
				vec3(0,worldSiz,0),				// ray origin
				sunPos,							// position of the sun
				50.0,							// intensity of the sun
				worldSiz,						// radius of the planet in meters
				worldSiz+100e3/div,				// radius of the atmosphere in meters
				vec3(5.5e-6, 13.0e-6, 22.4e-6), // Rayleigh scattering coefficient
				21e-6,						 	// Mie scattering coefficient
				7e3,							// Rayleigh scale height
				1.2e3,							// Mie scale height
				0.998							// Mie preferred scattering direction
			)
		),
	1);
}
