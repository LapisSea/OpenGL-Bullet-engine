package com.lapissea.opengl.program.rendering.gl.shader.modules;

import org.lwjgl.input.Mouse;

import com.lapissea.opengl.program.rendering.gl.shader.Shader;
import com.lapissea.opengl.program.rendering.gl.shader.modules.ShaderModule.Global;
import com.lapissea.opengl.program.rendering.gl.shader.uniforms.floats.UniformFloat2;

public class ShaderModuleMousePosition extends ShaderModule implements Global{
	
	protected UniformFloat2 mousePos;
	
	public ShaderModuleMousePosition(Shader parent){
		super(parent);
	}
	
	@Override
	public void setUpUniforms(){
		mousePos=getUniform("mousePos");
	}
	
	@Override
	public void uploadUniformsGlobal(){
		if(mousePos!=null) mousePos.upload(Mouse.getX(), Mouse.getY());
	}
	
}
