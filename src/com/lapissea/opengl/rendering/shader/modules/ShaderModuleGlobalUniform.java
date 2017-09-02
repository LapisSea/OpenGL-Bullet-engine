package com.lapissea.opengl.program.rendering.shader.modules;

import java.util.function.Consumer;

import com.lapissea.opengl.program.rendering.shader.Shader;
import com.lapissea.opengl.program.rendering.shader.modules.ShaderModule.Global;
import com.lapissea.opengl.program.rendering.shader.uniforms.AbstractUniform;

public abstract class ShaderModuleGlobalUniform<T extends AbstractUniform>extends ShaderModule implements Global{
	
	public static class Loader extends ShaderModuleSrcLoader{
		
		protected String type;
		
		public Loader(String type, String name){
			super(name);
			this.type=type;
		}
		
		@Override
		public String load(String extension, String[] args){
			return "\nuniform "+type+" "+name+";\n";
		}
	}
	
	protected T				uniform;
	protected String		name;
	protected Consumer<T>	uploader;
	
	public ShaderModuleGlobalUniform(Shader parent, String name, Consumer<T> uploader){
		super(parent);
		this.name=name;
		this.uploader=uploader;
	}
	
	@Override
	public void setUpUniforms(){
		uniform=getUniform(name);
	}
	
	@Override
	public void uploadUniformsGlobal(){
		if(uniform!=null) upload(uniform);
	}
	
	protected abstract void upload(T unifom);
	
}
