package com.lapissea.opengl.program.rendering.gl.shader.uniforms;

import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.OpenGLException;

import com.lapissea.opengl.program.rendering.GLUtil;
import com.lapissea.opengl.program.rendering.gl.shader.Shader;
import com.lapissea.util.LogUtil;

public abstract class AbstractUniform{
	
	private int				id;
	private String			name;
	protected final Shader	shader;
	
	public AbstractUniform(Shader shader, int id, String name){
		this.id=id;
		this.name=name;
		this.shader=shader;
	}
	
	public String name(){
		return name;
	}
	
	protected int id(){
		return id;
	}
	
	//	protected void checkError(){
	//		checkError(()->{});
	//	}
	protected void checkError(Runnable onSolve){
		try{
			GLUtil.checkError();
		}catch(Throwable e){
			id=GL20.glGetUniformLocation(shader.program, name);
			String realName=GL20.glGetActiveUniform(shader.program, id, 128);
			if(realName.isEmpty()){
				throw new OpenGLException("Non existant uniform "+name, e);
			}
			else if(!realName.equals(name)){
				LogUtil.printlnEr("correct name:",realName, "false name:",name);
				throw e;
			}
			onSolve.run();
		}
	}
	
	@Override
	public String toString(){
		return getClass().getSimpleName()+"{name="+name()+", id="+id()+" parent="+shader.name+"}";
	}
}
