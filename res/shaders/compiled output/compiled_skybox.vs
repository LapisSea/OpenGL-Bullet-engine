#version 400 core


in vec3 pos;
out vec3 uv;

uniform mat4 projectionMat;
uniform mat4 viewMat;

void main(void){
	gl_Position=projectionMat*viewMat*vec4(pos,1);
	uv=pos;
}
