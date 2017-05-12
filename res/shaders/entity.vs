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
	setupModelMaterial();
	ModelMaterial m=getMaterial();
	vec3 pos0=pos;
	if(m.jelly>0){
		float vt=length((transformMat*vec4(pos0,1)).xyz);
		pos0.x+=m.jelly*sin(tim+vt);
		pos0.z+=m.jelly*cos(tim+vt);
	}
	vec4 worldPos=transformMat*vec4(pos0,1);
	vec4 posRelativeToCam=viewMat*worldPos;
	
	gl_Position=projectionMat*posRelativeToCam;
	uv=uvIn;
	
	
	
	lightingSetUp(transformMat, worldPos, normalIn, (inverse(viewMat)*vec4(0,0,0,1)).xyz);
	
	applyFog(posRelativeToCam.xyz);
}