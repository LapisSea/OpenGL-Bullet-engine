package com.lapissea.opengl.program.rendering.gl;

import java.nio.ByteBuffer;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32;
import org.lwjgl.util.vector.Matrix4f;

import com.lapissea.opengl.program.core.Game;
import com.lapissea.opengl.program.rendering.GLUtil;
import com.lapissea.opengl.program.rendering.gl.model.ModelLoader;
import com.lapissea.opengl.program.rendering.gl.shader.Shaders;
import com.lapissea.opengl.program.rendering.gl.texture.TextureLoader;
import com.lapissea.opengl.window.assets.IModel;
import com.lapissea.opengl.window.assets.ITexture;
import com.lapissea.opengl.window.impl.assets.BasicTexture;

public class Fbo{
	
	public int id;
	
	public ITexture tex;
	
	public int depth;
	
	private int		width,height;
	private boolean	loaded;
	public boolean	hasTexture	=true,hasDepth=false;
	
	public static final IModel FULL_SCREEN_MODEL=ModelLoader.buildModel("gen_fscren", false, "genNormals", false, "vertices", new float[]{
			-1.0f,-1.0f,0.0f,
			1.0f,-1.0f,0.0f,
			-1.0f,1.0f,0.0f,
			-1.0f,1.0f,0.0f,
			1.0f,-1.0f,0.0f,
			1.0f,1.0f,0.0f,
	});
	
	public Fbo(int width, int height){
		this.width=width;
		this.height=height;
	}
	
	public static void bindDefault(){
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
		GLUtil.viewport(Game.win().getSize());
	}
	
	public void bind(){
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, id);
		GLUtil.viewport(width, height);
	}
	
	public Fbo create(){
		if(loaded) return this;
		loaded=true;
		id=GL30.glGenFramebuffers();
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, id);
		GL11.glDrawBuffer(GL30.GL_COLOR_ATTACHMENT0);
		
		
		if(hasTexture){
			tex=TextureLoader.alocate("gen_frbuf", BasicTexture.class);
			int id=GL11.glGenTextures();
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
			GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, (ByteBuffer)null);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
			GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, id, 0);
			tex.load(id, width, height);
		}
		if(hasDepth){
			depth=GL11.glGenTextures();
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, depth);
			GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL14.GL_DEPTH_COMPONENT32, width, height, 0, GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, (ByteBuffer)null);
			GL32.glFramebufferTexture(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, depth, 0);
		}
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
		return this;
	}
	
	public void delete(){
		if(!loaded) return;
		loaded=false;
		if(Game.get().glCtx.isGlThread()) delete0();
		else Game.glCtx(this::delete0);
	}
	
	private void delete0(){
		GL30.glDeleteFramebuffers(id);
		tex.delete();
	}
	
	@Override
	protected void finalize(){
		delete();
	}
	
	Matrix4f zero=new Matrix4f();
	
	public void drawImg(){
		List<ITexture> tx=FULL_SCREEN_MODEL.getTextures();
		if(tx.size()==0) tx.add(tex);
		
		tx.set(0, tex);
		
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
		GLUtil.DEPTH_TEST.set(false);
		Shaders.POST.renderSingle(zero, FULL_SCREEN_MODEL);
		GLUtil.DEPTH_TEST.set(true);
	}
}
