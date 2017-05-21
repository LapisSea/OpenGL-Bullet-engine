package com.lapissea.opengl.program.rendering.gl;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import com.lapissea.opengl.program.core.Game;

public class Fbo{
	
	private int		id,tex,width,height;
	private boolean	loaded,hasTexture;
	
	public void create(){
		
		id=GL30.glGenFramebuffers();
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, id);
		GL11.glDrawBuffer(GL30.GL_COLOR_ATTACHMENT0);
		
		
		if(hasTexture){
			tex=GL11.glGenTextures();
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, tex);
			GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, width, height, 0, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, (ByteBuffer)null);
			
			
			
		}
	}
	
	public void delete(){
		if(loaded) return;
		loaded=true;
		int id=this.id;
		Game.glCtx(()->{
			GL30.glDeleteFramebuffers(id);
		});
	}
	
	@Override
	protected void finalize(){
		delete();
	}
}
