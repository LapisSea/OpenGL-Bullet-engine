#version 400 core


in vec3 pos;
in vec2 uvIn;
in vec3 normalIn;
in float materialIdIn;

out vec2 uv;
out float materialId;


uniform mat4 transformMat;
uniform mat4 projectionMat;
uniform mat4 viewMat;

uniform float fogDensity;
uniform float fogGradient;

/*MODULE_START: Material.smd*/
struct ModelMaterial{
	vec3 ambient;
	vec3 diffuse;
	vec3 specular;
	float jelly;
	float shineDamper;
	float reflectivity;
	float lightTroughput;
};

uniform ModelMaterial materials[20];

ModelMaterial getMaterial(int id){
	return materials[id];
}
/*MODULE_END: Material.smd*/


/*MODULE_START: Light.vsmd*/
out vec3 normal;
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


/*MODULE_START: TextureBinary.smd*/
struct BinTextureReader{
	
	int pos;
	int width;
	int height;
	
	float readFloat(sampler2D data){
		return readByte(data, pos++, width, height, 1);
	}
	vec2 readVec2(sampler2D data){
		return vec2(readFloat(data),readFloat(data));
	}
	vec3 readVec3(sampler2D data){
		return vec3(readVec2(data),readFloat(data));
	}
	vec4 readVec4(sampler2D data){
		return vec4(readVec3(data),readFloat(data));
	}
	
};

float readByte(sampler2D data, int pos, int width, int height, int channelCount){
	
	int pixelId=pos/channelCount;
	if(pixelId>width*height)return -1;
	
	int whatInPixel=pos%channelCount;
	
	float x=pixelId%width;
	float y=pixelId/width;
	
	return texture(data, vec2(x/width,y/height))[whatInPixel];
}
/*MODULE_END: TextureBinary.smd*/




void main(void){
	ModelMaterial m=getMaterial(int(round(materialIdIn)));
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
	uv=uvIn;
	materialId=materialIdIn;
	
	
	lightingSetUp(transformMat, worldPos, normalIn, (inverse(viewMat)*vec4(0,0,0,1)).xyz);
	
	applyFog(posRelativeToCam.xyz,fogDensity,fogGradient);
}