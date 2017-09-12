#version 400 core

in vec2 uv;

out vec4 pixelColor;

/*MODULE_START: Texture.smd*/
////////////////////////////////////////////////

uniform sampler2D MDL_TEXTURE_0;
uniform bool MDL_TEXTURE_USED_0;

vec4 screen(vec2 uv){
	if(!MDL_TEXTURE_USED_0)return vec4(1);
	return texture(MDL_TEXTURE_0, uv);
}
////////////////////////////////////////////////
/*MODULE_END: Texture.smd*/



void main(void){
	pixelColor=screen(uv);
}