#version 400 core

struct RenderType{
	float blurRad;
	vec4 color;
	float mouseRad;
};

in vec2 elementSize;
in vec2 screenUv;
in vec2 elementUv;
in vec2 uv;

out vec4 pixelColor;

#include "ScreenSize"
#include "MousePosition"
#include "Texture: screen;; elementTexture-0"

uniform RenderType background;
uniform RenderType border;
uniform float borderWidth;
uniform float blurDiv;
uniform vec4 parentBg;


vec4 color(RenderType type){
	vec4 color=vec4(0);
	
	float mouseRadAlpha;
	if(type.mouseRad<0)mouseRadAlpha=1;
	else{
		mouseRadAlpha=clamp(1-length(mousePos-screenSize*screenUv)/type.mouseRad,0,1);
		if(mouseRadAlpha<1/256.0)return color;
		//mouseRadAlpha=mouseRadAlpha;
		//else mouseRadAlpha*=mouseRadAlpha;
	}
	
	color=type.color;
	int rad=int(floor(type.blurRad));
	if(rad>0&&color.a<1){
		/////////CALC BLUR/////////
		float pxCount=0;
		vec3 blurColor=vec3(0,0,0);
		for(int x=-rad;x<rad;x++){
			for(int y=-rad;y<rad;y++){
				if(length(vec2(x,y))<=rad){
					vec4 c=screen(vec2(screenUv.x+x/screenSize.x, screenUv.y+y/screenSize.y));
					c*=c;
					blurColor+=c.rgb;
					pxCount+=c.a;
				}
			}
		}
		blurColor/=pxCount;
		
		color=vec4(mix(mix(sqrt(blurColor),parentBg.rgb,parentBg.a),color.rgb,color.a),1);
	}
	color.a*=mouseRadAlpha;
	
	return color;
}

void main(void){
	pixelColor=elementTexture(uv);
	if(pixelColor.a>255/256.0)return;
	
	vec2 uvPx=elementUv*elementSize;
	bool isPxBorder=borderWidth>=1&&(uvPx.x<borderWidth||uvPx.x>elementSize.x-borderWidth||uvPx.y<borderWidth||uvPx.y>elementSize.y-borderWidth);
	
	pixelColor=mix(color(isPxBorder?border:background),pixelColor,pixelColor.a);
	if(pixelColor.a<1/256.0)discard;
}
