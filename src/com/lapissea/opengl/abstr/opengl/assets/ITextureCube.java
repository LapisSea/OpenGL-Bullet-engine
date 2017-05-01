package com.lapissea.opengl.abstr.opengl.assets;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

public interface ITextureCube extends ITexture{

	@Override
	default void bind(){
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, getId());
	}
}