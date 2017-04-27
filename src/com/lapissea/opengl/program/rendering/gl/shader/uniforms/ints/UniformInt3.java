package com.lapissea.opengl.program.rendering.gl.shader.uniforms.ints;

import org.lwjgl.opengl.GL20;

import com.lapissea.opengl.program.rendering.gl.shader.Shader;
import com.lapissea.opengl.program.rendering.gl.shader.uniforms.AbstractUniform;

public class UniformInt3 extends AbstractUniform{

	protected int prev1,prev2,prev3;
	
	public UniformInt3(Shader shader, int id, String name){
		super(shader, id, name);
	}
	
	public void upload(int i1, int i2, int i3){
		shader.bindingProttect();
		if(i1==prev1&&i2==prev2&&i3==prev3)return;
		prev1=i1;
		prev2=i2;
		prev3=i3;
		GL20.glUniform3i(id(), i1, i2, i3);
		checkError(()->upload(i1, i2, i3));
	}
	
}
