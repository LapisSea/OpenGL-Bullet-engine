#version 400 core


in vec3 pos;


uniform mat4 transformMat;
uniform float z;
uniform vec2 screenSize;

void main(void){
	vec3 p=pos;
	p.z=z;
	vec4 screenPos=transformMat*vec4(p,1);
	screenPos.xy/=screenSize/2;
	screenPos.x-=1;
	screenPos.y=1-screenPos.y;
	gl_Position=screenPos;
}
