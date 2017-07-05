#version 400 core

in vec2 uv;
in float materialIdIn;

out vec4 pixelColor;

/* MODULE_:Texture.smd:_MODULE*/
uniform bool MDL_TEXTURE_USED[1];
////////////////////////////////////////////////

uniform sampler2D MDL_TEXTURE0;

vec4 mainTexture(vec2 uv){
	if(!MDL_TEXTURE_USED[0])return vec4(1);
	return texture(MDL_TEXTURE0, uv);
}
////////////////////////////////////////////////

/* MODULE_:Material.smd:_MODULE*/
struct ModelMaterial{
	vec3 ambient;
	vec3 diffuse;
	vec3 specular;
	float jelly;
	float shineDamper;
	float reflectivity;
	float lightTroughput;
};

uniform ModelMaterial materials["<MATERIAL_MAX_COUNT>"];

ModelMaterial getMaterial(int id){
	return materials[id];
}

/* MODULE_:Light.fsmd:_MODULE*/
/*SKIPPED DUPLICATE "Material.smd" */


struct PointLight{
	vec4 color;
	vec3 attenuation;
	vec3 pos;
};
struct LineLight{
	vec4 color;
	vec3 attenuation;
	vec3 pos1;
	vec3 pos2;
};
struct DirectionalLight{
	vec4 color;
	vec3 direction;
};


vec4 light_diffuseTotal=vec4(0,0,0,1);
vec4 light_specularTotal=vec4(0,0,0,0);

#define EPSILON 2.220446049250313e-16

vec3 getClosetPoint(vec3 vA, vec3 vB, vec3 vPoint)
{
    vec3 vVector1 = vPoint - vA;
    vec3 vVector2 = normalize(vB - vA);
 
    float d = length(vA-vB);
    float t = dot(vVector2, vVector1);
 
    if (t <= 0)return vA;
 
    if (t >= d)return vB;
 
    vec3 vVector3 = vVector2 * t;
	
    vec3 vClosestPoint = vA + vVector3;
 
    return vClosestPoint;
}

float lengthSqr(vec3 vec){
	return vec.x*vec.x+vec.y*vec.y+vec.z*vec.z;
}

vec3 calculateLineLineIntersection(vec3 line1Point1, vec3 line1Point2, vec3 line2Point1, vec3 line2Point2){
 
	vec3 p1 = line1Point1;
	vec3 p2 = line1Point2;
	vec3 p3 = line2Point1;
	vec3 p4 = line2Point2;
	vec3 p13 = p1 - p3;
	vec3 p43 = p4 - p3;
 
	if (lengthSqr(p43) < EPSILON)return vec3(0);
	vec3 p21 = p2 - p1;
	if (lengthSqr(p21) < EPSILON)return vec3(0);
 
	float d1343 = p13.x * p43.x + p13.y * p43.y + p13.z * p43.z;
	float d4321 = p43.x * p21.x + p43.y * p21.y + p43.z * p21.z;
	float d1321 = p13.x * p21.x + p13.y * p21.y + p13.z * p21.z;
	float d4343 = p43.x * p43.x + p43.y * p43.y + p43.z * p43.z;
	float d2121 = p21.x * p21.x + p21.y * p21.y + p21.z * p21.z;
 
	float denom = d2121 * d4343 - d4321 * d4321;
	if (abs(denom) < EPSILON)return vec3(0);
	float numer = d1343 * d4321 - d1321 * d4343;
 
	float mua = numer / denom;
	float mub = (d1343 + d4321 * (mua)) / d4343;
	vec3 resultSegmentPoint=vec3(0);
	resultSegmentPoint.x = (p1.x + mua * p21.x);
	resultSegmentPoint.y = (p1.y + mua * p21.y);
	resultSegmentPoint.z = (p1.z + mua * p21.z);
	/*
	resultSegmentPoint.x = (p3.x + mub * p43.x);
	resultSegmentPoint.y = (p3.y + mub * p43.y);
	resultSegmentPoint.z = (p3.z + mub * p43.z);
	*/
	return resultSegmentPoint;
}


void calcGenericLight(vec3 unitToCamera, vec3 unitNormal, float attFact, vec3 toLight, vec3 lightColor, ModelMaterial material){
	vec3 unitToLight=normalize(toLight);
	float cutOff=6/256.0;
	attFact-=cutOff;
	if(attFact<0)return;
	attFact*=1+cutOff;
	
	float brightness=1;
	if(abs(unitNormal.x)+abs(unitNormal.y)+abs(unitNormal.z)>0)brightness=mix(dot(unitNormal,unitToLight), 1 ,material.lightTroughput);
	
	if(brightness<0)return;
	vec3 col=lightColor*attFact;
	
	light_diffuseTotal.rgb+=brightness*col;
	
	if(material.reflectivity>0){
		vec3 camreaReflect=reflect(unitToCamera,unitNormal);
		
		
		vec3 lightDir=-unitToLight; 
		
		vec3 reflectedDir=reflect(lightDir,unitNormal);
		
		float sepcularFact=max(0, dot(reflectedDir, unitToCamera));
		float dampedSpecular=pow(sepcularFact,material.shineDamper);
		
		light_specularTotal.rgb+=dampedSpecular*brightness*material.reflectivity*col;
	}
}
void calcPointLightColor(vec3 unitToCamera, vec3 unitNormal, PointLight light, ModelMaterial material, vec3 wPos){
	vec3 toLight=light.pos-wPos;
	float dist=length(toLight);
	float attFact= 
		 light.attenuation.x+
		(light.attenuation.y*dist)+
		(light.attenuation.z * dist*dist)
	;
	calcGenericLight(unitToCamera, unitNormal, 1/attFact, toLight, light.color.rgb*light.color.a,material);
}
void calcLineLightColor(vec3 unitToCamera, vec3 unitNormal, LineLight light, ModelMaterial material, vec3 wPos){
	vec3 toLight=getClosetPoint(light.pos1,light.pos2, wPos)-wPos;
	
	float dist=length(toLight);
	float attFact= 
		 light.attenuation.x+
		(light.attenuation.y*dist)+
		(light.attenuation.z * dist*dist)
	;
	attFact=1/attFact;
	vec3 lightColor=light.color.rgb*light.color.a;
	
	vec3 unitToLight=normalize(toLight);
	
	float cutOff=6/256.0;
	attFact-=cutOff;
	if(attFact<0)return;
	attFact*=1+cutOff;
	
	float brightness=1;
	if(abs(unitNormal.x)+abs(unitNormal.y)+abs(unitNormal.z)>0)brightness=mix(dot(unitNormal,unitToLight), 1 ,material.lightTroughput);
	
	if(brightness<0)return;
	vec3 col=lightColor*attFact;
	
	light_diffuseTotal.rgb+=brightness*col;
	
	if(material.reflectivity>0){
		vec3 onLightPoint=light.pos1;
		if(light.pos1!=light.pos2){
			vec3 reflectedCam=reflect(unitToCamera,unitNormal);
			onLightPoint=calculateLineLineIntersection(light.pos1,light.pos2, wPos, wPos+reflectedCam*100);
			int axis=0;
			if(light.pos1[axis]==light.pos2[axis])axis++;
			if(light.pos1[axis]==light.pos2[axis])axis++;
			bool bigger1=light.pos1[axis]>light.pos2[axis];
			
			if((bigger1?light.pos2[axis]:light.pos1[axis])>onLightPoint[axis])onLightPoint=bigger1?light.pos2:light.pos1;
			else if(onLightPoint[axis]>(bigger1?light.pos1[axis]:light.pos2[axis]))onLightPoint=bigger1?light.pos1:light.pos2;
		}
		
		vec3 lightDir=-normalize(onLightPoint-wPos); 
		
		vec3 reflectedDir=reflect(lightDir,unitNormal);
		
		float sepcularFact=max(0, dot(reflectedDir, unitToCamera));
		float dampedSpecular=pow(sepcularFact,material.shineDamper);
		
		light_specularTotal.rgb+=dampedSpecular*brightness*material.reflectivity*col;
	}
}

void calcDirLightColor(vec3 unitToCamera, vec3 unitNormal, DirectionalLight light, ModelMaterial material){
	calcGenericLight(unitToCamera, unitNormal, 1, light.direction, light.color.rgb*light.color.a,material);
}


/* MODULE_:ArrayList.smd:_MODULE*/
struct ArrayList<TYPE>{
	
	<TYPE> data[<MAX_SIZE>];
	
	int size;
	
	<TYPE> get(int id){
		return data[id];
	}
	
};


vec4 applyLighting(vec4 baseColor, float minBrightness, ModelMaterial material, vec3 normal, vec3 toCamera, vec3 wPos, ArrayListPointLight pointLights, ArrayListLineLight lineLights, ArrayListDirectionalLight dirLights){
	
	vec3 unitToCamera=normalize(toCamera);
	vec3 unitNormal=normalize(normal);
	if(!gl_FrontFacing)unitNormal*=-1;
	
	int i;
	for(i=0;i<pointLights.size;i++){
		calcPointLightColor(unitToCamera, unitNormal, pointLights.get(i), material, wPos);
	}
	for(i=0;i<lineLights.size;i++){
		calcLineLightColor(unitToCamera,unitNormal,lineLights.get(i),material, wPos);
	}
	for(i=0;i<dirLights.size;i++){
		calcDirLightColor(unitToCamera,unitNormal,dirLights.get(i), material);
	}
	
	light_diffuseTotal.rgb*=material.diffuse;
	light_diffuseTotal.rgb+=material.ambient;
	light_specularTotal.rgb*=material.specular*baseColor.a;
	
	return max(vec4(minBrightness),light_diffuseTotal)*baseColor+light_specularTotal;
}

/* MODULE_:Fog.fsmd:_MODULE*/
/* MODULE_:Noise2D.smd:_MODULE*/
//
// Description : Array and textureless GLSL 2D simplex noise function.
//      Author : Ian McEwan, Ashima Arts.
//  Maintainer : stegu
//     Lastmod : 20110822 (ijm)
//     License : Copyright (C) 2011 Ashima Arts. All rights reserved.
//               Distributed under the MIT License. See LICENSE file.
//               https://github.com/ashima/webgl-noise
//               https://github.com/stegu/webgl-noise
// 

vec3 mod289(vec3 x) {
  return x - floor(x * (1.0 / 289.0)) * 289.0;
}

vec2 mod289(vec2 x) {
  return x - floor(x * (1.0 / 289.0)) * 289.0;
}

vec3 permute(vec3 x) {
  return mod289(((x*34.0)+1.0)*x);
}

float snoise(vec2 v)
  {
  const vec4 C = vec4(0.211324865405187,  // (3.0-sqrt(3.0))/6.0
                      0.366025403784439,  // 0.5*(sqrt(3.0)-1.0)
                     -0.577350269189626,  // -1.0 + 2.0 * C.x
                      0.024390243902439); // 1.0 / 41.0
// First corner
  vec2 i  = floor(v + dot(v, C.yy) );
  vec2 x0 = v -   i + dot(i, C.xx);

// Other corners
  vec2 i1;
  //i1.x = step( x0.y, x0.x ); // x0.x > x0.y ? 1.0 : 0.0
  //i1.y = 1.0 - i1.x;
  i1 = (x0.x > x0.y) ? vec2(1.0, 0.0) : vec2(0.0, 1.0);
  // x0 = x0 - 0.0 + 0.0 * C.xx ;
  // x1 = x0 - i1 + 1.0 * C.xx ;
  // x2 = x0 - 1.0 + 2.0 * C.xx ;
  vec4 x12 = x0.xyxy + C.xxzz;
  x12.xy -= i1;

// Permutations
  i = mod289(i); // Avoid truncation effects in permutation
  vec3 p = permute( permute( i.y + vec3(0.0, i1.y, 1.0 ))
		+ i.x + vec3(0.0, i1.x, 1.0 ));

  vec3 m = max(0.5 - vec3(dot(x0,x0), dot(x12.xy,x12.xy), dot(x12.zw,x12.zw)), 0.0);
  m = m*m ;
  m = m*m ;

// Gradients: 41 points uniformly over a line, mapped onto a diamond.
// The ring size 17*17 = 289 is close to a multiple of 41 (41*7 = 287)

  vec3 x = 2.0 * fract(p * C.www) - 1.0;
  vec3 h = abs(x) - 0.5;
  vec3 ox = floor(x + 0.5);
  vec3 a0 = x - ox;

// Normalise gradients implicitly by scaling m
// Approximation of: m *= inversesqrt( a0*a0 + h*h );
  m *= 1.79284291400159 - 0.85373472095314 * ( a0*a0 + h*h );

// Compute final noise value at P
  vec3 g;
  g.x  = a0.x  * x0.x  + h.x  * x0.y;
  g.yz = a0.yz * x12.xz + h.yz * x12.yw;
  return 130.0 * dot(m, g);
}


in float fogVisibility;

float fogCalculated;

void initFog(vec2 wPos, vec3 skyColor){
	float n=(1+snoise(wPos/200))/2;
	n*=n;
	fogCalculated=fogVisibility-n/5;
	if(fogCalculated<1/255.0){
		fogCalculated=0;
		discard;
	}
}

vec4 applyFog(vec4 initalColor){
	return vec4(mix(skyColor,initalColor.rgb, fogCalculated),initalColor.a);
}


void main(void){
	initFog(wPos.xz);
	pixelColor=mainTexture(uv);
	if(pixelColor.a==0)discard;
	pixelColor=applyLighting(pixelColor, getMaterial(int(round(materialIdIn))));
	pixelColor=applyFog(pixelColor);
	
}