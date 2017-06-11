package com.lapissea.opengl.program.rendering.gl;

import com.lapissea.opengl.window.assets.ITexture;

public class FboRboTextured extends Fbo{
	
	private final Fbo textureReference=new Fbo();
	
	public FboRboTextured(){
		textureReference.setDepth(false);
	}
	
	@Override
	public ITexture getTexture(){
		if(renderBufferType) return textureReference.getTexture();
		return super.getTexture();
	}
	
	@Override
	public ITexture getDepth(){
		if(renderBufferType) return textureReference.getDepth();
		return super.getDepth();
	}
	
	public void process(){
		if(renderBufferType){
			textureReference.setSize(width, height);
			copyColorTo(textureReference);
		}
		else textureReference.delete();
	}
}
