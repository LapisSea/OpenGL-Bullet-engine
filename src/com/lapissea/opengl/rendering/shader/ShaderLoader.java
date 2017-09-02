package com.lapissea.opengl.rendering.shader;

import java.util.Collection;

import com.lapissea.opengl.rendering.shader.modules.ShaderModule;
import com.lapissea.opengl.util.PairM;

/**
 * return null or empty for no shader
 */
public abstract class ShaderLoader{
	
	protected Shader shader;
	
	public void setShader(Shader shader){
		this.shader=shader;
	}
	
	public abstract PairM<String,Collection<ShaderModule>> getVertex();
	
	public abstract PairM<String,Collection<ShaderModule>> getGeometry();
	
	public abstract PairM<String,Collection<ShaderModule>> getFragment();
	
}
