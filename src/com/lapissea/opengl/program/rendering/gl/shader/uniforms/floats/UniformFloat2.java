package com.lapissea.opengl.program.rendering.gl.shader.uniforms.floats;

import static org.lwjgl.opengl.GL20.*;

import com.lapissea.opengl.program.rendering.gl.shader.Shader;
import com.lapissea.opengl.program.rendering.gl.shader.uniforms.AbstractUniform;
import com.lapissea.opengl.program.util.math.vec.Vec2f;
import com.lapissea.opengl.window.api.util.IVec2i;

public class UniformFloat2 extends AbstractUniform{
	
	protected float prev1,prev2;
	
	public UniformFloat2(Shader shader, int id, String name){
		super(shader, id, name);
	}
	
	public void upload(Vec2f vec){
		upload(vec.x(), vec.y());
	}
	
	public void upload(float f1, float f2){
		shader.bindingProttect();
		if(f1==prev1&&f2==prev2) return;
		prev1=f1;
		prev2=f2;
		glUniform2f(id(), f1, f2);
		checkError(()->upload(f1, f2));
	}
	
	public void upload(IVec2i size){
		upload(size.x(), size.y());
	}
	
}
