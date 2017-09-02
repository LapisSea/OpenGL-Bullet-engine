package com.lapissea.opengl.program.rendering.shader.uniforms.floats;

import static org.lwjgl.opengl.GL20.*;

import com.lapissea.opengl.program.rendering.shader.Shader;
import com.lapissea.opengl.program.rendering.shader.uniforms.AbstractUniform;
import com.lapissea.opengl.program.util.math.vec.Vec3f;
import com.lapissea.opengl.window.api.util.color.IColorM;

public class UniformFloat3 extends AbstractUniform{
	
	protected float prev1,prev2,prev3;
	
	public UniformFloat3(Shader shader, int id, String name){
		super(shader, id, name);
	}
	
	public void upload(IColorM noAlphaColor){
		float a=noAlphaColor.a();
		upload(noAlphaColor.r()*a, noAlphaColor.g()*a, noAlphaColor.b()*a);
	}
	
	public void upload(Vec3f vec){
		upload(vec.x(), vec.y(), vec.z());
	}
	
	public void upload(float f1, float f2, float f3){
		shader.bindingProttect();
		if(f1==prev1&&f2==prev2&&f3==prev3) return;
		prev1=f1;
		prev2=f2;
		prev3=f3;
		glUniform3f(id(), f1, f2, f3);
		checkError();
	}
	@Override
	protected void onErrorSolve(){
		prev1=Float.NaN;
		upload(prev1,prev2,prev3);
	}
	
}
