package com.lapissea.opengl.program.rendering.gl.shader.modules;

import java.util.List;

import org.lwjgl.opengl.GL13;

import com.lapissea.opengl.program.rendering.gl.shader.Shader;
import com.lapissea.opengl.program.rendering.gl.shader.modules.ShaderModule.ModelUniforms;
import com.lapissea.opengl.program.rendering.gl.shader.uniforms.UniformBoolean;
import com.lapissea.opengl.program.rendering.gl.shader.uniforms.ints.UniformInt1;
import com.lapissea.opengl.window.assets.IModel;
import com.lapissea.opengl.window.assets.ITexture;

public class ShaderModuleTexture extends ShaderModule implements ModelUniforms{
	
	public static class Loader extends ShaderModuleSrcLoader{
		
		public Loader(){
			super("Texture");
		}
		
		@Override
		public String load(String extension, String[] args){
			if(args==null) args=new String[]{"texture0"};
			String src0=super.load(extension, args);
			if(src0==null) throw new NullPointerException(name+extension);
			String[] parts=src0.split("<SPLIT>");
			String template=parts[1],templateCube=parts[2];
			
			StringBuilder src=new StringBuilder(parts[0].replace("<COUNT>", String.valueOf(args.length)));
			
			for(int i=0;i<args.length;i++){
				int colSplit=args[i].indexOf('-');
				String name,color;
				if(colSplit==-1){
					name=args[i];
					color="1";
				}
				else{
					name=args[i].substring(0, colSplit);
					color=args[i].substring(colSplit+1);
				}
				
				src.append((args[i].startsWith("cube")?templateCube:template).replace("<NAME>", name).replace("<COL>", color).replaceAll("<NUM>", String.valueOf(i)));
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
	public void uploadUniformsModel(IModel model){
		if(notInit){
			notInit=false;
			if(texturesUsed!=null){
				for(int i=0;i<texturesUsed.length;i++){
					UniformInt1 u=getUniform(UniformInt1.class, "MDL_TEXTURE"+i);
					if(u!=null) u.upload(i);
				}
			}
		}
		
		if(texturesUsed!=null){
			List<ITexture> txts=model.getTextures();
			//			LogUtil.println(txts);
			for(int i=0;i<texturesUsed.length;i++){
				ITexture texture=i<txts.size()?txts.get(i):null;
				boolean valid=texture!=null&&texture.isLoaded();
				
				texturesUsed[i].upload(valid);
				GL13.glActiveTexture(GL13.GL_TEXTURE0+i);
				if(valid) texture.bind();
			}
		}
	}
	
}
