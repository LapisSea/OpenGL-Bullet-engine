#version 400 core

in vec2 uv;

out vec4 pixelColor;

/*MODULE_START: Texture.smd*/
uniform bool MDL_TEXTURE_USED[1];
////////////////////////////////////////////////

uniform sampler2D MDL_TEXTURE0;

vec4 screen(vec2 uv){
	if(!MDL_TEXTURE_USED[0])return vec4(1);
	return texture(MDL_TEXTURE0, uv);
}
////////////////////////////////////////////////
/*MODULE_END: Texture.smd*/



void main(void){
	pixelColor=screen(uv);
}