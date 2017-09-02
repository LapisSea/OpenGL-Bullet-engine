package com.lapissea.opengl.rendering.shader.uniforms;

import static org.lwjgl.opengl.GL20.*;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Matrix4f;

import com.lapissea.opengl.rendering.shader.Shader;
import com.lapissea.opengl.util.math.MatrixUtil;

public class UniformMat4 extends AbstractUniform{
	
	protected static final FloatBuffer BUFF=BufferUtils.createFloatBuffer(16);
	
	protected Matrix4f prev=Matrix4f.setZero(new Matrix4f());
	
	public UniformMat4(Shader shader, int id, String name){
		super(shader, id, name);
		prev.m00=Float.NaN;
	}
	
	public void upload(Matrix4f mat){
		shader.bindingProttect();
		
		if(MatrixUtil.equals(mat, prev)) return;
		prev.load(mat);
		
		mat.store(BUFF);
		BUFF.flip();
		glUniformMatrix4(id(), false, BUFF);
		checkError();
	}
	
	@Override
	protected void onErrorSolve(){
		prev.m00=Float.NaN;
		upload(prev);
	}
	
	public Matrix4f getLastKnown(Matrix4f mat){
		mat.load(prev);
		return mat;
	}
	
}
