package com.lapissea.opengl.program.rendering.gl.texture;

import org.lwjgl.opengl.GL11;

public interface ITexture{
	
	int getId();
	
	int getWidth();

	int getHeight();

	boolean isLoaded();

	boolean isLoading();
	
	Object notifyLoading();
	
	void load(int id, int width, int height);
	
	String getPath();
	
	default void bind(){
		if(isLoaded())GL11.glBindTexture(GL11.GL_TEXTURE_2D, getId());
		else TextureLoader.NO_TEXTURE.bind();
	}
	
	default void delete(){
		if(isLoaded())GL11.glDeleteTextures(getId());
	}

	int[] params();
	void params(int id, int value);

	Object loadingKey();
	
}
