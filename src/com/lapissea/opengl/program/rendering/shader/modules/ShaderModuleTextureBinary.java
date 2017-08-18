package com.lapissea.opengl.program.rendering.shader.modules;

import com.lapissea.opengl.program.rendering.shader.Shader;
import com.lapissea.opengl.program.rendering.shader.uniforms.ints.UniformInt2;
import com.lapissea.opengl.window.assets.ITexture;

public class ShaderModuleTextureBinary extends ShaderModule{
	
	public ShaderModuleTextureBinary(Shader parent){
		super(parent);
	}
	
	public static class UniformTextureBinary{
		
		protected UniformInt2 size;
		
		public UniformTextureBinary(Shader shader, String name){
			size=shader.getUniform(name+".size");
		}
		
		public void upload(ITexture f){
			size.upload(f.getWidth(), f.getHeight());
			
			
		}
		
	}
	
}
