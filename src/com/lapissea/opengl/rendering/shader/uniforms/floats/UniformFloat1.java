package com.lapissea.opengl.rendering.shader.uniforms.floats;

import static org.lwjgl.opengl.GL20.*;

import com.lapissea.opengl.rendering.shader.Shader;
import com.lapissea.opengl.rendering.shader.uniforms.AbstractUniform;

public class UniformFloat1 extends AbstractUniform{
	
	protected float prev;
	
	public UniformFloat1(Shader shader, int id, String name){
		super(shader, id, name);
	}
	
	public void upload(float f){
		shader.bindingProttect();
		if(prev==f) return;
		prev=f;
		glUniform1f(id(), f);
		checkError();
	}
	
	@Override
	protected void onErrorSolve(){
		prev=Float.NaN;
		upload(prev);
	}
	
}
