package com.lapissea.opengl.program.rendering.gl.shader.uniforms.floats;

import org.lwjgl.opengl.GL20;

import com.lapissea.opengl.program.rendering.gl.shader.Shader;
import com.lapissea.opengl.program.rendering.gl.shader.uniforms.AbstractUniform;

public class UniformFloat1 extends AbstractUniform{

	protected float prev;

	public UniformFloat1(Shader shader, int id, String name){
		super(shader, id, name);
	}

	public void upload(float f){
		shader.bindingProttect();
		if(prev==f)return;
		prev=f;
		GL20.glUniform1f(id(), f);
		checkError(()->upload(f));
	}
	
}
