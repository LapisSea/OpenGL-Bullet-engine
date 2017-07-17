package com.lapissea.opengl.program.rendering.gl.shader.uniforms.floats;

import static org.lwjgl.opengl.GL20.*;

import com.lapissea.opengl.program.rendering.gl.shader.Shader;
import com.lapissea.opengl.program.rendering.gl.shader.uniforms.AbstractUniform;
import com.lapissea.opengl.window.api.util.color.IColorM;

public class UniformFloat4 extends AbstractUniform{
	
	protected float prev1,prev2,prev3,prev4;
	
	public UniformFloat4(Shader shader, int id, String name){
		super(shader, id, name);
	}
	
	public void upload(IColorM color){
		upload(color.r(), color.g(), color.b(), color.a());
	}
	
	public void upload(float f1, float f2, float f3, float f4){
		shader.bindingProttect();
		if(f1==prev1&&f2==prev2&&f3==prev3&&f4==prev4) return;
		prev1=f1;
		prev2=f2;
		prev3=f3;
		prev4=f4;
		glUniform4f(id(), f1, f2, f3, f4);
		checkError(()->upload(f1, f2, f3, f4));
	}
	
}
