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



out vec3 normal;
out vec3 toCamera;

out vec3 vecToPointLight[25];
uniform vec3 pointLightPos[25];
uniform int numberOfPointLights;


void lightingSetUp(mat4 transformMat, vec4 worldPos, vec3 rawNormal, vec3 cameraPos){
	if(rawNormal.x!=0||rawNormal.y!=0||rawNormal.z!=0)normal=(transformMat*vec4(rawNormal,0)).xyz;
	else normal=rawNormal;
	
	for(int i=0;i<numberOfPointLights;i++){
		vecToPointLight[i]=pointLightPos[i]-worldPos.xyz;
	}
	
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
	vec4 worldPos=transformMat*vec4(pos,1);
	vec4 posRelativeToCam=viewMat*worldPos;
	
	gl_Position=projectionMat*posRelativeToCam;
	uv=uvIn;
	
	setupModelMaterial();
	
	lightingSetUp(transformMat, worldPos, normalIn, (inverse(viewMat)*vec4(0,0,0,1)).xyz);
	
	applyFog(posRelativeToCam.xyz);
}
