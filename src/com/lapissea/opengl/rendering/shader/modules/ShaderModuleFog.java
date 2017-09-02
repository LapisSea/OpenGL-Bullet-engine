package com.lapissea.opengl.rendering.shader.modules;

import com.lapissea.opengl.core.Game;
import com.lapissea.opengl.rendering.Fog;
import com.lapissea.opengl.rendering.Renderer;
import com.lapissea.opengl.rendering.shader.Shader;
import com.lapissea.opengl.rendering.shader.modules.ShaderModule.Global;
import com.lapissea.opengl.rendering.shader.uniforms.floats.UniformFloat1;
import com.lapissea.opengl.rendering.shader.uniforms.floats.UniformFloat3;

public class ShaderModuleFog extends ShaderModule implements Global{
	
	protected UniformFloat3	skyColor;
	protected UniformFloat1	fogDensity;
	protected UniformFloat1	fogGradient;
	
	public ShaderModuleFog(Shader parent){
		super(parent);
	}
	
	@Override
	public void setUpUniforms(){
		//VS
		fogDensity=getUniform("fogDensity");
		fogGradient=getUniform("fogGradient");
		
		//FS
		skyColor=getUniform("skyColor");
	}
	
	@Override
	public void uploadUniformsGlobal(){
		Renderer r=Game.get().renderer;
		
		if(r==null){
			fogDensity.upload(1.5F);
			fogGradient.upload(0.007F);
			skyColor.upload(1, 0, 1);
		}else{
			Fog fog=Game.get().world.fog;
			fogDensity.upload(fog.getDensity());
			fogGradient.upload(fog.getGradient());
			if(skyColor!=null) skyColor.upload(fog.color);
		}
	}
	
}
