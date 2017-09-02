package com.lapissea.opengl.program.rendering.shader.uniforms.ints;

import static org.lwjgl.opengl.GL20.*;

import com.lapissea.opengl.program.rendering.shader.Shader;
import com.lapissea.opengl.program.rendering.shader.uniforms.AbstractUniform;

public class UniformInt4 extends AbstractUniform{
	
	protected int prev1,prev2,prev3,prev4;
	
	public UniformInt4(Shader shader, int id, String name){
		super(shader, id, name);
	}
	
	public void upload(int i1, int i2, int i3, int i4){
		shader.bindingProttect();
		if(i1==prev1&&i2==prev2&&i3==prev3&&i4==prev4) return;
		prev1=i1;
		prev2=i2;
		prev3=i3;
		prev4=i4;
		glUniform4i(id(), i1, i2, i3, i4);
		checkError();
	}
	@Override
	protected void onErrorSolve(){
		prev1=Integer.MIN_VALUE;
		upload(prev1,prev2,prev3,prev4);
	}
	
}
