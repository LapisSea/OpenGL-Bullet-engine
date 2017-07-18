package com.lapissea.opengl.program.rendering.gl.shader.modules;

import com.lapissea.opengl.program.core.Game;
import com.lapissea.opengl.program.rendering.gl.Fog;
import com.lapissea.opengl.program.rendering.gl.Renderer;
import com.lapissea.opengl.program.rendering.gl.shader.Shader;
import com.lapissea.opengl.program.rendering.gl.shader.modules.ShaderModule.Global;
import com.lapissea.opengl.program.rendering.gl.shader.uniforms.floats.UniformFloat1;
import com.lapissea.opengl.program.rendering.gl.shader.uniforms.floats.UniformFloat3;

public class ShaderModuleArmature extends ShaderModule implements Global{
	
	protected UniformFloat3	skyColor;
	protected UniformFloat1	fogDensity;
	protected UniformFloat1	fogGradient;
	
	public ShaderModuleArmature(Shader parent){
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
