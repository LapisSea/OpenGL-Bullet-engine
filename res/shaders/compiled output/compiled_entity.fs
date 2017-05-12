#version 400 core

in vec2 uv;

out vec4 pixelColor;


uniform bool MDL_TEXTURE_USED[1];
////////////////////////////////////////////////

uniform sampler2D MDL_TEXTURE0;

vec4 mainTexture(vec2 uv){
	if(!MDL_TEXTURE_USED[0])return vec4(1);
	return texture(MDL_TEXTURE0, uv);
}

////////////////////////////////////////////////


in float materialId;

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

ModelMaterial getMaterial(){
	return materials[int(ceil(materialId))];
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

struct PointLight{
	vec4 color;
	vec3 attenuation;
};
struct DirectionalLight{
	vec4 color;
	vec3 direction;
};

in vec3 normal;
in vec3 toCamera;

uniform PointLight pointLights[25];
in vec3 vecToPointLight[25];
uniform int numberOfPointLights;

uniform DirectionalLight dirLights[2];
uniform int numberOfDirLights;

uniform float minBrightness;


vec4 light_diffuseTotal=vec4(0,0,0,1);
vec4 light_specularTotal=vec4(0,0,0,0);


void calcGenericLight(vec3 unitToCamera, vec3 unitNormal, float attFact, vec3 unitToLight, vec3 lightColor, ModelMaterial material){
	float cutOff=6/256.0;
	attFact-=cutOff;
	if(attFact<0)return;
	attFact*=1+cutOff;
	
	float brightness=1;
	if(abs(unitNormal.x)+abs(unitNormal.y)+abs(unitNormal.z)>0)brightness=mix(dot(unitNormal,unitToLight), 1 ,material.lightTroughput);
	
	if(brightness<0)return;
	//if(attFact<0.0001)light_diffuseTotal.rgb+=1;
	vec3 col=lightColor*attFact;
	
	light_diffuseTotal.rgb+=brightness*col;
	
	if(material.reflectivity>0){
		vec3 lightDir=-unitToLight; 
		vec3 reflectedDir=reflect(lightDir,unitNormal);
		
		float sepcularFact=max(0, dot(reflectedDir, unitToCamera));
		float dampedSpecular=pow(sepcularFact,material.shineDamper);
		
		light_specularTotal.rgb+=dampedSpecular*brightness*material.reflectivity*col;
	}
}

void calcPointLightColor(vec3 unitToCamera, vec3 unitNormal, vec3 toLightVec, PointLight light, ModelMaterial material){
	
	float dist=length(toLightVec);
	float attFact= 
		 light.attenuation.x+
		(light.attenuation.y*dist)+
		(light.attenuation.z * dist*dist)
	;
	if(attFact>0)calcGenericLight(unitToCamera, unitNormal, 1/attFact, normalize(toLightVec), light.color.rgb*light.color.a,material);
}

void calcDirLightColor(vec3 unitToCamera, vec3 unitNormal, DirectionalLight light, ModelMaterial material){
	calcGenericLight(unitToCamera, unitNormal, 1, light.direction, light.color.rgb*light.color.a,material);
}

vec4 applyLighting(vec4 baseColor, ModelMaterial material){
	
	vec3 unitToCamera=normalize(toCamera);
	vec3 unitNormal=normalize(normal);
	if(!gl_FrontFacing)unitNormal*=-1;
	
	for(int i=0;i<numberOfPointLights;i++){
		calcPointLightColor(unitToCamera,unitNormal,vecToPointLight[i],pointLights[i],material);
	}
	for(int i=0;i<numberOfDirLights;i++){
		calcDirLightColor(unitToCamera,unitNormal,dirLights[i],material);
	}
	
	light_diffuseTotal.rgb*=material.diffuse;
	light_diffuseTotal.rgb+=material.ambient;
	light_specularTotal.rgb*=material.specular*baseColor.a;
	
	vec4 result=max(vec4(minBrightness),light_diffuseTotal)*baseColor+light_specularTotal;
	return result;
}


in float visibility;

uniform vec3 skyColor;

void checkFogVisibility(){
	if(visibility<1/255.0){
		discard;
	}
}

vec4 applyFog(vec4 initalColor){
	return vec4(mix(skyColor,initalColor.rgb, visibility),initalColor.a);
}


void main(void){
	checkFogVisibility();
	pixelColor=mainTexture(uv);
	if(pixelColor.a<1/256.0)discard;
	pixelColor=applyLighting(pixelColor, getMaterial());
	pixelColor=applyFog(pixelColor);
	
}
