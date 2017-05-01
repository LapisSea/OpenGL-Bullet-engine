#version 400 core

in vec3 uv;

out vec4 pixelColor;


uniform bool MDL_TEXTURE_USED[1];
////////////////////////////////////////////////

uniform samplerCube MDL_TEXTURE0;

vec4 cubeTexture(vec3 uv){
	if(!MDL_TEXTURE_USED[0])return vec4(1);
	return texture(MDL_TEXTURE0, uv);
}
////////////////////////////////////////////////


void main(void){
	pixelColor=cubeTexture(uv);
	//pixelColor.rgb=uv;
}
