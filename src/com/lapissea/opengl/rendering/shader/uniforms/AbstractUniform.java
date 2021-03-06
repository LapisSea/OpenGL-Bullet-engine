package com.lapissea.opengl.rendering.shader.uniforms;

import static org.lwjgl.opengl.GL20.*;

import org.lwjgl.opengl.OpenGLException;

import com.lapissea.opengl.rendering.GLUtil;
import com.lapissea.opengl.rendering.shader.Shader;
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
	
	protected void checkError(){
		try{
			GLUtil.checkError();
		}catch(Throwable e){
			id=glGetUniformLocation(shader.program, name);
			String realName=glGetActiveUniform(shader.program, id, 128);
			if(realName.isEmpty()){
				throw new OpenGLException("Non existant uniform "+name, e);
			}else if(!realName.equals(name)){
				LogUtil.printlnEr("correct name:", realName, "false name:", name);
				throw e;
			}
			onErrorSolve();
		}
	}
	
	protected void onErrorSolve(){}
	
	@Override
	public String toString(){
		return getClass().getSimpleName()+"{name="+name()+", id="+id()+" parent="+shader.name+"}";
	}
}
