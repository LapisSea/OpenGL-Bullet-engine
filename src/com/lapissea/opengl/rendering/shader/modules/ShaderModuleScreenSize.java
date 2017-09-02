package com.lapissea.opengl.rendering.shader.modules;

import com.lapissea.opengl.rendering.GLUtil;
import com.lapissea.opengl.rendering.shader.Shader;
import com.lapissea.opengl.rendering.shader.modules.ShaderModule.Global;
import com.lapissea.opengl.rendering.shader.uniforms.floats.UniformFloat2;

public class ShaderModuleScreenSize extends ShaderModule implements Global{
	
	protected UniformFloat2 mousePos;
	
	public ShaderModuleScreenSize(Shader parent){
		super(parent);
	}
	
	@Override
	public void setUpUniforms(){
		mousePos=getUniform("screenSize");
	}
	
	@Override
	public void uploadUniformsGlobal(){
		if(mousePos!=null) mousePos.upload(GLUtil.getViewportWidth(), GLUtil.getViewportHeight());
	}
	
}
