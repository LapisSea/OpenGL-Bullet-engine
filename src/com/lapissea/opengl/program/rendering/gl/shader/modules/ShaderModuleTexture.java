package com.lapissea.opengl.program.rendering.gl.shader.modules;

import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import com.lapissea.opengl.program.rendering.gl.model.Model;
import com.lapissea.opengl.program.rendering.gl.shader.Shader;
import com.lapissea.opengl.program.rendering.gl.shader.modules.ShaderModule.ModelUniforms;
import com.lapissea.opengl.program.rendering.gl.shader.uniforms.UniformBoolean;
import com.lapissea.opengl.program.rendering.gl.shader.uniforms.ints.UniformInt1;
import com.lapissea.opengl.program.rendering.gl.texture.ITexture;

public class ShaderModuleTexture extends ShaderModule implements ModelUniforms{
	
	public static class Loader extends ShaderModuleSrcLoader{
		
		public Loader(){
			super("Texture");
		}
		
		@Override
		public String load(boolean isFragment, String[] args){
			if(args==null) args=new String[]{"texture0"};
			String src0=super.load(isFragment, args);
			
			String template=src0.substring(src0.indexOf("<TEMPLATE>")+10);
			
			StringBuilder src=new StringBuilder(src0.substring(0, src0.indexOf("<TEMPLATE>")).replace("<COUNT>", String.valueOf(args.length)));
			
			for(int i=0;i<args.length;i++){
				src.append(template.replace("<NAME>", args[i]).replaceAll("<NUM>", String.valueOf(i)));
			}
			
			return src.toString();
		}
		
	}
	
	protected UniformBoolean[]	texturesUsed;
	private boolean				notInit	=true;
	
	public ShaderModuleTexture(Shader parent){
		super(parent);
	}
	
	@Override
	public void setUpUniforms(){
		texturesUsed=getUniformArray(UniformBoolean.class, "MDL_TEXTURE_USED");
	}
	
	@Override
	public void uploadUniformsModel(Model model){
		if(notInit){
			notInit=false;
			for(int i=0;i<texturesUsed.length;i++){
				getUniform(UniformInt1.class, "MDL_TEXTURE"+i).upload(i);
			}
		}
		
		List<ITexture> txts=model.getTextures();
		
		for(int i=0;i<texturesUsed.length;i++){
			ITexture texture=i<txts.size()?txts.get(i):null;
			boolean valid=texture!=null&&texture.isLoaded();
			
			texturesUsed[i].upload(valid);
			GL13.glActiveTexture(GL13.GL_TEXTURE0+i);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, valid?texture.getId():0);
		}
	}
	
}
