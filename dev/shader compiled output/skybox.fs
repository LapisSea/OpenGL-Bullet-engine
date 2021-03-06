#version 400 core

in vec3 uv;

out vec4 pixelColor;

uniform vec3 sunPos;
uniform float eyeHeight;
uniform float skyClarity;
uniform float viewFarPlane;

/*MODULE_START: atmosphere.fsmd*/
//==============================================================================
// ALL CREDIT FOR THIS GOES TO https://github.com/wwwtyro/glsl-atmosphere ======
//==============================================================================

#ifndef PI
#define PI 3.141592
#endif

#define iSteps 16
#define jSteps 8

float rsi(vec3 r0, vec3 rd, float sr) {
    // Simplified ray-sphere intersection that assumes
    // the ray starts inside the sphere and that the
    // sphere is centered at the origin. Always intersects.
    float a = dot(rd, rd);
    float b = 2.0 * dot(rd, r0);
    float c = dot(r0, r0) - (sr * sr);
    return (-b + sqrt((b*b) - 4.0*a*c))/(2.0*a);
}

vec3 atmosphere(vec3 r, vec3 r0, vec3 pSun, float iSun, float rPlanet, float rAtmos, vec3 kRlh, float kMie, float shRlh, float shMie, float g) {

    // Calculate the step size of the primary ray.
    float iStepSize = rsi(r0, r, rAtmos) / float(iSteps);

    // Initialize the primary ray time.
    float iTime = 0.0;

    // Initialize accumulators for Rayleight and Mie scattering.
    vec3 totalRlh = vec3(0,0,0);
    vec3 totalMie = vec3(0,0,0);

    // Initialize optical depth accumulators for the primary ray.
    float iOdRlh = 0.0;
    float iOdMie = 0.0;

    // Calculate the Rayleigh and Mie phases.
    float mu = dot(r, pSun);
    float mumu = mu * mu;
    float gg = g * g;
    float pRlh = 3.0 / (16.0 * PI) * (1.0 + mumu);
    float pMie = 3.0 / (8.0 * PI) * ((1.0 - gg) * (mumu + 1.0)) / (pow(1.0 + gg - 2.0 * mu * g, 1.5) * (2.0 + gg));

    // Sample the primary ray.
    for (int i = 0; i < iSteps; i++) {

        // Calculate the primary ray sample position.
        vec3 iPos = r0 + r * (iTime + iStepSize * 0.5);

        // Calculate the height of the sample.
        float iHeight = length(iPos) - rPlanet;

        // Calculate the optical depth of the Rayleigh and Mie scattering for this step.
        float odStepRlh = exp(-iHeight / shRlh) * iStepSize;
        float odStepMie = exp(-iHeight / shMie) * iStepSize;

        // Accumulate optical depth.
        iOdRlh += odStepRlh;
        iOdMie += odStepMie;

        // Calculate the step size of the secondary ray.
        float jStepSize = rsi(iPos, pSun, rAtmos) / float(jSteps);

        // Initialize the secondary ray time.
        float jTime = 0.0;

        // Initialize optical depth accumulators for the secondary ray.
        float jOdRlh = 0.0;
        float jOdMie = 0.0;

        // Sample the secondary ray.
        for (int j = 0; j < jSteps; j++) {

            // Calculate the secondary ray sample position.
            vec3 jPos = iPos + pSun * (jTime + jStepSize * 0.5);

            // Calculate the height of the sample.
            float jHeight = length(jPos) - rPlanet;

            // Accumulate the optical depth.
            jOdRlh += exp(-jHeight / shRlh) * jStepSize;
            jOdMie += exp(-jHeight / shMie) * jStepSize;

            // Increment the secondary ray time.
            jTime += jStepSize;
        }

        // Calculate attenuation.
        vec3 attn = exp(-(kMie * (iOdMie + jOdMie) + kRlh * (iOdRlh + jOdRlh)));

        // Accumulate scattering.
        totalRlh += odStepRlh * attn;
        totalMie += odStepMie * attn;

        // Increment the primary ray time.
        iTime += iStepSize;

    }

    // Calculate and return the final color.
    return iSun * (pRlh * kRlh * totalRlh + pMie * kMie * totalMie);
}
/*MODULE_END: atmosphere.fsmd*/


/*MODULE_START: Texture.smd*/
////////////////////////////////////////////////

uniform samplerCube MDL_TEXTURE_0;
uniform bool MDL_TEXTURE_USED_0;

vec4 cubeMainTexture(vec3 uv){
	if(!MDL_TEXTURE_USED_0)return vec4(1);
	return texture(MDL_TEXTURE_0, uv);
}
////////////////////////////////////////////////
/*MODULE_END: Texture.smd*/


/*MODULE_START: Screen.smd*/
float screen(float a, float b){
	return 1-(1-a)*(1-b);
}

vec2 screen(vec2 a, vec2 b){
	return 1-(1-a)*(1-b);
}
vec3 screen(vec3 a, vec3 b){
	return 1-(1-a)*(1-b);
}
vec4 screen(vec4 a, vec4 b){
	return 1-(1-a)*(1-b);
}

vec2 screen(vec2 a, float b){
	return screen(a, vec2(b));
}
vec3 screen(vec3 a, float b){
	return screen(a, vec3(b));
}
vec4 screen(vec4 a, float b){
	return screen(a, vec4(b));
}

vec2 screen(float a, vec2 b){
	return screen(vec2(a), b);
}
vec3 screen(float a, vec3 b){
	return screen(vec3(a), b);
}
vec4 screen(float a, vec4 b){
	return screen(vec4(a), b);
}
/*MODULE_END: Screen.smd*/



void main(void){
	vec3 uvUnit=normalize(uv);
	float eh=eyeHeight;
	if(eh<0)eh=0;
	float div=max(1,1+eh*eh/100);
	float sunset=1-pow(1-abs(sunPos.y),4);
	
	float worldSiz=20000*viewFarPlane/div;
	pixelColor = vec4(
		1.0 - exp(-1.0 * 
			atmosphere(
				uvUnit,	   		 				// normalized ray direction
				vec3(0,worldSiz,0),				// ray origin
				sunPos,							// position of the sun
				sunset*20+30,					// intensity of the sun
				worldSiz,					// radius of the planet in meters
				worldSiz+100e4/div,			// radius of the atmosphere in meters
				vec3(5.5e-6, 13.0e-6, 22.4e-6), // Rayleigh scattering coefficient
				21e-6,						 	// Mie scattering coefficient
				7e3,							// Rayleigh scale height
				1.2e3,							// Mie scale height
				0.997+sunset*0.001				// Mie preferred scattering direction
			)
		),
	1);
	
	float dot=dot(sunPos, uvUnit),sub=0.2;
	if(dot<sub)dot=0;
	else{
		dot=pow(dot-sub,4)*(1-sunset)*1.2;
		//if(sunPos.y<0)dot*=1-pow(-sunPos.y,4);
	}
	float bright=(pixelColor.r+pixelColor.g+pixelColor.b)/3;
	bright=clamp((bright+0.5),0,1);
	bright=clamp(bright+pow(1-uvUnit.y,3),0,1);
	pixelColor=mix(cubeMainTexture(uv),pixelColor,pow(bright,0.5));
	pixelColor.rgb=screen(vec3(1.5,0.5,0)*dot,pixelColor.rgb);
}