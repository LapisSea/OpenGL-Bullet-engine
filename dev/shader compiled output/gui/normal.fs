#version 400 core

in vec2 uv;

out vec4 pixelColor;

void main(void){
	pixelColor=vec4(uv.x,uv.y,0,1);
}