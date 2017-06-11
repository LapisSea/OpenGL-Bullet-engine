#version 400 core


in vec3 pos;
in vec2 uvIn;
in vec3 normalIn;

out vec2 uv;


uniform mat4 transformMat;
uniform mat4 projectionMat;
uniform mat4 viewMat;


in float materialIdIn;

out float materialId;

void setupModelMaterial(){
	materialId=materialIdIn;
}

#ifndef ModelMaterial_STR
#define ModelMaterial_STR

struct ModelMaterial{
	vec3 ambient;
	vec3 diffuse;
	vec3 specular;
	float jelly;
	float shineDamper;
	float reflectivity;
	float lightTroughput;
};

#endif

uniform ModelMaterial materials[20];
uniform float tim;

ModelMaterial getMaterial(){
	return materials[int(ceil(materialIdIn))];
}



out vec3 normal;
out vec3 toCamera;
out vec3 wPos;



void lightingSetUp(mat4 transformMat, vec4 worldPos, vec3 rawNormal, vec3 cameraPos){
	if(rawNormal.x!=0||rawNormal.y!=0||rawNormal.z!=0)normal=(transformMat*vec4(rawNormal,0)).xyz;
	else normal=rawNormal;
	wPos=worldPos.xyz;
	toCamera=cameraPos - worldPos.xyz;
	
}


out float visibility;

uniform float fogDensity;
uniform float fogGradient;

void applyFog(vec3 worldViewPos){
	
	float dist= length(worldViewPos);
	
	visibility=clamp(exp(-pow((dist*fogDensity),fogGradient)),0,1);
}


void main(void){
	setupModelMaterial();
	ModelMaterial m=getMaterial();
	vec3 pos0=pos;
	if(m.jelly>0){
		vec3 v=(transformMat*vec4(pos0,1)).xyz;
		float vt=v.x/5+v.y/2+v.z/5;
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