#version 400 core


in vec3 pos;
in vec2 uvIn;

out vec2 uv;

uniform mat4 transformMat;

/*MODULE_START: ScreenSize.smd*/
uniform vec2 screenSize;
/*MODULE_END: ScreenSize.smd*/



void main(void){
	vec4 trans=transformMat*vec4(pos,1);
	trans.xy/=screenSize;
	gl_Position=trans;
	uv=uvIn;

}