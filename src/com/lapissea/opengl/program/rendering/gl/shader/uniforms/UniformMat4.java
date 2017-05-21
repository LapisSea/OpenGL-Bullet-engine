package com.lapissea.opengl.program.rendering.gl.shader.uniforms;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix4f;

import com.lapissea.opengl.program.rendering.gl.shader.Shader;
import com.lapissea.opengl.program.util.math.MatrixUtil;

public class UniformMat4 extends AbstractUniform{
	
	protected static final FloatBuffer BUFF=BufferUtils.createFloatBuffer(16);
	
	protected Matrix4f prev=new Matrix4f();
	
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
		GL20.glUniformMatrix4(id(), false, BUFF);
		checkError(this::retryUpload);
	}
	
	protected void retryUpload(){
		prev.m00=Float.NaN;
		upload(prev);
	}
	
}
