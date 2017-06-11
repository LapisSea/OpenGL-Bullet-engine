package com.lapissea.opengl.program.rendering.gl.shader.modules;

import com.lapissea.opengl.program.core.Game;
import com.lapissea.opengl.program.rendering.gl.Renderer;
import com.lapissea.opengl.program.rendering.gl.shader.Shader;
import com.lapissea.opengl.program.rendering.gl.shader.modules.ShaderModule.Global;
import com.lapissea.opengl.program.rendering.gl.shader.uniforms.floats.UniformFloat1;
import com.lapissea.opengl.program.rendering.gl.shader.uniforms.floats.UniformFloat3;

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
		fogDensity=getUniform(UniformFloat1.class, "fogDensity");
		fogGradient=getUniform(UniformFloat1.class, "fogGradient");
		
		//FS
		skyColor=getUniform(UniformFloat3.class, "skyColor");
	}
	
	@Override
	public void uploadUniformsGlobal(){
		Renderer r=Game.get().renderer;
		if(r==null){
			fogDensity.upload(1.5F);
			fogGradient.upload(0.007F);
			skyColor.upload(1,0,1);
		}else{
			fogDensity.upload(r.worldFog.density);
			fogGradient.upload(r.worldFog.gradient);
			skyColor.upload(r.worldFog.color);
		}
	}
	
}
