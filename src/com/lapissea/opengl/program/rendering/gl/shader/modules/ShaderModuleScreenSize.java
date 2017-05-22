package com.lapissea.opengl.program.rendering.gl.shader.modules;

import com.lapissea.opengl.program.core.Game;
import com.lapissea.opengl.program.rendering.gl.shader.Shader;
import com.lapissea.opengl.program.rendering.gl.shader.modules.ShaderModule.Global;
import com.lapissea.opengl.program.rendering.gl.shader.uniforms.floats.UniformFloat2;

public class ShaderModuleScreenSize extends ShaderModule implements Global{
	
	protected UniformFloat2 screenSize;
	
	public ShaderModuleScreenSize(Shader parent){
		super(parent);
	}
	
	@Override
	public void setUpUniforms(){
		screenSize=getUniform(UniformFloat2.class, "screenSize");
	}
	
	@Override
	public void uploadUniformsGlobal(){
		if(screenSize!=null) screenSize.upload(Game.win().getSize());
	}
	
}
