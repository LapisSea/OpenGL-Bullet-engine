#version 400 core


in vec3 pos;
in vec2 uvIn;

out vec2 uv;
out vec2 elementSize;
out vec2 screenUv;
out vec2 elementUv;

uniform mat4 transformMat;
uniform vec2 screenSize;
uniform vec2 size;

void main(void){
	uv=uvIn;
	vec2 ss2=screenSize/2;
	vec3 p=pos;
	
	elementUv=p.xy;
	p.xy*=size;
	vec4 screenPos=transformMat*vec4(p,1);
	elementSize=size;
	
	screenPos.xy/=ss2;
	screenPos.x-=1;
	screenPos.y=1-screenPos.y;
	
	screenUv=vec2(screenPos.x+1,screenPos.y+1)/2;
	
	gl_Position=screenPos;
}