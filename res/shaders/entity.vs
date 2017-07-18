#version 400 core


in vec3 pos;
in vec2 uvIn;
in vec3 normalIn;
in int materialIdIn;

out vec2 uv;
flat out int materialId;


uniform mat4 transformMat;
uniform mat4 projectionMat;
uniform mat4 viewMat;

uniform float fogDensity;
uniform float fogGradient;

#include "Material.smd"
#include "Light.vsmd"
#include "Fog.vsmd"
#include "Time.smd"


void main(void){
	uv=uvIn;
	
	ModelMaterial m=getMaterial(materialId=materialIdIn);
	
	vec3 pos0=pos;
	if(m.jelly>0){
		vec3 v=(transformMat*vec4(pos0,1)).xyz;
		float vt=v.x/5+v.y/2+v.z/5;
		pos0.x+=m.jelly*sin(worldTime+vt);
		pos0.z+=m.jelly*cos(worldTime+vt);
	}
	vec4 worldPos=transformMat*vec4(pos0,1);
	vec4 posRelativeToCam=viewMat*worldPos;
	
	gl_Position=projectionMat*posRelativeToCam;
	
	
	lightingSetUp(transformMat, worldPos, normalIn, (inverse(viewMat)*vec4(0,0,0,1)).xyz);
	
	applyFog(posRelativeToCam.xyz,fogDensity,fogGradient);
}