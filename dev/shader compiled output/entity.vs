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

/*MODULE_START: Material.smd*/
struct Material{
	vec3 ambient;
	vec3 diffuse;
	vec3 specular;
	vec3 emission;
	float jelly;
	float shineDamper;
	float lightTroughput;
};

uniform Material materials[20];

Material getMaterial(int id){
	return materials[id];
}
/*MODULE_END: Material.smd*/


/*MODULE_START: Light.vsmd*/
flat out vec3 normal;
out vec3 toCamera;
out vec3 wPos;

void lightingSetUp(mat4 transformMat, vec4 worldPos, vec3 rawNormal, vec3 cameraPos){
	if(rawNormal.x!=0||rawNormal.y!=0||rawNormal.z!=0)normal=(transformMat*vec4(rawNormal,0)).xyz;
	else normal=rawNormal;
	wPos=worldPos.xyz;
	toCamera=cameraPos - worldPos.xyz;
	
}
/*MODULE_END: Light.vsmd*/


/*MODULE_START: Fog.vsmd*/
out float fogVisibility;

void applyFog(vec3 worldViewPos, float fogDensity, float fogGradient){
	
	float dist=length(worldViewPos);
	fogVisibility=clamp(exp(-pow((dist*fogDensity),fogGradient)),0,1);
}
/*MODULE_END: Fog.vsmd*/


/*MODULE_START: Time.smd*/
uniform float worldTime;
uniform float systemTime;
/*MODULE_END: Time.smd*/




void main(void){
	uv=uvIn;
	
	Material m=getMaterial(materialId=materialIdIn);
	
	vec3 pos0=pos;
	float jelly=m.jelly;
	if(jelly>0){
		vec3 v=(transformMat*vec4(pos0,1)).xyz;
		float vt=gl_VertexID+v.x/5+v.y/2+v.z/5;
		pos0.x+=jelly*sin(worldTime*10+vt);
		pos0.z+=jelly*cos(worldTime*10+vt);
	}
	vec4 worldPos=transformMat*vec4(pos0,1);
	vec4 posRelativeToCam=viewMat*worldPos;
	
	gl_Position=projectionMat*posRelativeToCam;
	
	
	lightingSetUp(transformMat, worldPos, normalIn, (inverse(viewMat)*vec4(0,0,0,1)).xyz);
	
	applyFog(posRelativeToCam.xyz,fogDensity,fogGradient);
}