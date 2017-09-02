package com.lapissea.opengl.rendering.shader.uniforms.ints;

import static org.lwjgl.opengl.GL20.*;

import com.lapissea.opengl.rendering.shader.Shader;
import com.lapissea.opengl.rendering.shader.uniforms.AbstractUniform;

public class UniformInt3 extends AbstractUniform{
	
	protected int prev1,prev2,prev3;
	
	public UniformInt3(Shader shader, int id, String name){
		super(shader, id, name);
	}
	
	public void upload(int i1, int i2, int i3){
		shader.bindingProttect();
		if(i1==prev1&&i2==prev2&&i3==prev3) return;
		prev1=i1;
		prev2=i2;
		prev3=i3;
		glUniform3i(id(), i1, i2, i3);
		checkError();
	}
	@Override
	protected void onErrorSolve(){
		prev1=Integer.MIN_VALUE;
		upload(prev1,prev2,prev3);
	}
	
}
