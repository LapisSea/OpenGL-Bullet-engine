package com.lapissea.opengl.program.rendering.shader.uniforms.ints;

import static org.lwjgl.opengl.GL20.*;

import com.lapissea.opengl.program.rendering.shader.Shader;
import com.lapissea.opengl.program.rendering.shader.uniforms.AbstractUniform;

public class UniformInt1 extends AbstractUniform{
	
	protected int prev;
	
	public UniformInt1(Shader shader, int id, String name){
		super(shader, id, name);
	}
	
	public void upload(int i){
		shader.bindingProttect();
		if(prev==i) return;
		prev=i;
		glUniform1i(id(), i);
		checkError();
	}
	@Override
	protected void onErrorSolve(){
		prev=Integer.MIN_VALUE;
		upload(prev);
	}
	
}
