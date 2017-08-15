package com.lapissea.opengl.program.rendering.gl.shader.uniforms.ints;

import static org.lwjgl.opengl.GL20.*;

import com.lapissea.opengl.program.rendering.gl.shader.Shader;
import com.lapissea.opengl.program.rendering.gl.shader.uniforms.AbstractUniform;

public class UniformInt2 extends AbstractUniform{
	
	protected int prev1,prev2;
	
	public UniformInt2(Shader shader, int id, String name){
		super(shader, id, name);
	}
	
	public void upload(int i1, int i2){
		shader.bindingProttect();
		if(i1==prev1&&i2==prev2) return;
		prev1=i1;
		prev2=i2;
		glUniform2i(id(), i1, i2);
		checkError();
	}
	@Override
	protected void onErrorSolve(){
		prev1=Integer.MIN_VALUE;
		upload(prev1,prev2);
	}
	
}
