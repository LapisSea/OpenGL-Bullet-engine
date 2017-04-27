#version 400 core


in vec3 pos;
in vec2 uvIn;
in vec3 normalIn;

out vec2 uv;


uniform mat4 transformMat;
uniform mat4 projectionMat;
uniform mat4 viewMat;

#include "Material"
#include "Light"
#include "Fog"

void main(void){
	vec4 worldPos=transformMat*vec4(pos,1);
	vec4 posRelativeToCam=viewMat*worldPos;
	
	gl_Position=projectionMat*posRelativeToCam;
	uv=uvIn;
	
	setupModelMaterial();
	
	lightingSetUp(transformMat, worldPos, normalIn, (inverse(viewMat)*vec4(0,0,0,1)).xyz);
	
	applyFog(posRelativeToCam.xyz);
}