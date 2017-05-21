#version 400 core

out vec4 pixelColor;

void main(void){
	pixelColor=vec4(gl_FragCoord.xy/100,1,1);
}
