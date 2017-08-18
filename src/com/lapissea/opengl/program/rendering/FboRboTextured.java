package com.lapissea.opengl.program.rendering;

import com.lapissea.opengl.window.assets.ITexture;

public class FboRboTextured extends Fbo{
	
	private final Fbo textureReference=new Fbo(Fbo.TEXTURE);
	
	public FboRboTextured(){
		super();
	}
	
	public FboRboTextured(int width, int height, int sample, int args){
		super(width, height, sample, args);
	}
	
	public FboRboTextured(int width, int height, int sample){
		super(width, height, sample);
	}
	
	public FboRboTextured(int width, int height){
		super(width, height);
	}
	
	public FboRboTextured(int args){
		super(args);
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
		}else textureReference.delete();
	}
}
