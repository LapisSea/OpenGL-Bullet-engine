#version 400 core


in vec3 pos;

out vec2 uv;

void main(void){
	uv=pos.xy/2+0.5;
	gl_Position=vec4(pos,1);
}
