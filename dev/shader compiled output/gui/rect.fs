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

/*MODULE_START: ScreenSize.smd*/
uniform vec2 screenSize;
/*MODULE_END: ScreenSize.smd*/


/*MODULE_START: MousePosition.smd*/
uniform vec2 mousePos;
/*MODULE_END: MousePosition.smd*/


/*MODULE_START: Texture.smd*/
////////////////////////////////////////////////

uniform sampler2D MDL_TEXTURE_0;
uniform bool MDL_TEXTURE_USED_0;

vec4 screen(vec2 uv){
	if(!MDL_TEXTURE_USED_0)return vec4(1);
	return texture(MDL_TEXTURE_0, uv);
}
////////////////////////////////////////////////

uniform sampler2D MDL_TEXTURE_1;
uniform bool MDL_TEXTURE_USED_1;

vec4 elementTexture(vec2 uv){
	if(!MDL_TEXTURE_USED_1)return vec4(0);
	return texture(MDL_TEXTURE_1, uv);
}
////////////////////////////////////////////////
/*MODULE_END: Texture.smd*/



uniform RenderType background;
uniform RenderType border;
uniform float borderWidth;
uniform float blurDiv;

float ease(float t){ 
	return t<.5 ? 4*t*t*t : (t-1)*(2*t-2)*(2*t-2)+1;
}

vec4 color(RenderType type){
	vec4 color=vec4(0);
	
	float mouseRadAlpha;
	if(type.mouseRad<0)mouseRadAlpha=1;
	else{
		mouseRadAlpha=clamp(1-length(mousePos-screenSize*screenUv)/type.mouseRad,0,1);
		if(mouseRadAlpha<1/256.0)return color;
		mouseRadAlpha=ease(mouseRadAlpha*mouseRadAlpha);
		//mouseRadAlpha=mouseRadAlpha;
		//else mouseRadAlpha*=mouseRadAlpha;
	}
	int rad=int(floor(type.blurRad));
	vec3 blurColor;
	color=type.color;
	if(color.a<1){
		if(rad>0){
			/////////CALC BLUR/////////
			float pxCount=0;
			blurColor=vec3(0,0,0);
			for(int x=-rad;x<rad;x++){
				for(int y=-rad;y<rad;y++){
					float dist=length(vec2(x,y));
					if(dist<=rad){
						vec4 c=screen(vec2(screenUv.x+x/screenSize.x, screenUv.y+y/screenSize.y));
						dist=type.blurRad-dist;
						blurColor+=c.rgb*c.rgb*dist;
						pxCount+=dist;
					}
				}
			}
			blurColor/=pxCount;
			blurColor=sqrt(blurColor);
			
			color=vec4(mix(blurColor,color.rgb,type.color.a),1);
			
		}else{
			color=vec4(mix(screen(screenUv).rgb,color.rgb,type.color.a),1);
		}
	}
	
	color.a*=mouseRadAlpha;
	return color;
}

float borderPercent(){
	vec2 uvPx=elementUv*elementSize;
	float borderCol=0;
	
	borderCol=uvPx.x-borderWidth;
	if(borderCol<0)return 0;
	borderCol=elementSize.x-borderWidth-uvPx.x;
	if(borderCol<0)return 0;
	
	borderCol=uvPx.y-borderWidth;
	if(borderCol<0)return 0;
	borderCol=elementSize.y-borderWidth-uvPx.y;
	if(borderCol<0)return 0;
	
	return min(borderCol,1);
}

void main(void){
	pixelColor=elementTexture(uv);
	if(pixelColor.a>255/256.0)return;
	
	vec4 eCol;
	float borderCol=borderPercent();
	
	/*
	?1-clamp(max(
		max(borderWidth-uvPx.x, uvPx.x-elementSize.x+borderWidth),
		max(borderWidth-uvPx.y, uvPx.y-elementSize.y+borderWidth)
	), 0,1);*/
	if(borderCol<=0)eCol=color(border);
	else if(borderCol>=1)eCol=color(background);
	else{
		vec4 bg=color(background);
		vec4 bd=color(border);
		eCol=mix(bd,bg,bd.a);
	}
	
	
	
	pixelColor=mix(eCol,pixelColor,pixelColor.a);
	if(pixelColor.a<1/256.0)discard;
}