#version 400 core


in vec3 pos;
in vec4 vtColorIn;

out vec4 vtColor;


uniform mat4 transformMat;
uniform mat4 projectionMat;
uniform mat4 viewMat;

void main(void){
	vec4 worldPos=transformMat*vec4(pos,1);
	vec4 posRelativeToCam=viewMat*worldPos;
	
	gl_Position=projectionMat*posRelativeToCam;
	vtColor=vtColorIn;
}
