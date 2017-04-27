#version 400 core


in vec3 pos;
in vec3 normalIn;


uniform mat4 transformMat;
uniform mat4 projectionMat;
uniform mat4 viewMat;
uniform float tim;

void main(void){
	vec3 pos0=pos;
	float tm=(pos.x*40+pos.y*5+pos.z*10)*15+tim;
	pos0.x+=sin(tm)/500;
	pos0.y+=cos(tm)/500;
	vec4 worldPos=transformMat*vec4(pos0,1);
	vec4 posRelativeToCam=viewMat*worldPos;
	
	gl_Position=projectionMat*posRelativeToCam;
}
