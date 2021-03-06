package com.lapissea.opengl.rendering.shader.uniforms;

import com.lapissea.opengl.rendering.shader.Shader;
import com.lapissea.opengl.rendering.shader.uniforms.floats.UniformFloat1;

public class UniformBoolean extends UniformFloat1{
	
	public UniformBoolean(Shader shader, int id, String name){
		super(shader, id, name);
	}
	
	public void upload(boolean b){
		upload(b?1:0);
	}
	
}
