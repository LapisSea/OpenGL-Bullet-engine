package com.lapissea.opengl.program.rendering.gl;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Objects;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

import com.lapissea.opengl.program.core.Game;
import com.lapissea.opengl.program.rendering.GLUtil;
import com.lapissea.opengl.program.rendering.gl.model.ModelLoader;
import com.lapissea.opengl.program.rendering.gl.shader.Shaders;
import com.lapissea.opengl.window.api.util.IVec2i;
import com.lapissea.opengl.window.assets.IModel;
import com.lapissea.opengl.window.assets.ITexture;
import com.lapissea.opengl.window.impl.assets.BasicTexture;

public class Fbo{
	
	protected int id;
	
	protected ITexture	texture,depth;
	protected int		colorBuffer	=-1,depthBuffer=-1;
	
	protected int		width,height,sample=1;
	protected boolean	loaded,hasTexture,hasDepth,renderBufferType;
	
	public Runnable initHook;
	
	public static final IModel FULL_SCREEN_MODEL=ModelLoader.buildModel("gen_fscren", GL11.GL_TRIANGLE_STRIP, "genNormals", false, "vertices", new float[]{
			-1,-1,0,
			+1,-1,0,
			-1,+1,0,
			+1,+1,0,
	});
	
	public Fbo(){
		this(0, 0);
	}
	
	public Fbo(int width, int height){
		this(width, height, 1);
	}
	
	public Fbo(int width, int height, int sample){
		this(width, height, sample, true, true);
	}
	
	public Fbo(int width, int height, int sample, boolean hasTexture, boolean hasDepth){
		setSize(width, height);
		this.hasDepth=hasDepth;
		this.hasTexture=hasTexture;
	}
	
	/**
	 * 
	 * NEEDS RENDER BUFFER TYPE SET TO TRUE TO BE EFFECTIVE!
	 * 
	 * @param samples
	 * @return
	 */
	public Fbo setSample(int samples){
		if(samples<1) samples=1;
		if(sample==samples) return this;
		sample=samples;
		delete();
		return this;
	}
	
	public Fbo setRenderBufferType(boolean flag){
		if(renderBufferType==flag) return this;
		renderBufferType=flag;
		delete();
		return this;
	}
	
	
	public boolean getRenderBufferType(){
		return renderBufferType;
	}
	
	public int getSample(){
		return sample;
	}
	
	
	public Fbo setDepth(boolean depth){
		if(hasDepth==depth) return this;
		hasDepth=depth;
		delete();
		return this;
	}
	
	public static void bindDefault(){
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
		GLUtil.viewport(Game.win().getSize());
	}
	
	public void bind(){
		create();
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, id);
		GLUtil.viewport(getWidth(), getHeight());
	}
	
	public Fbo create(){
		if(loaded||getWidth()<=0||getHeight()<=0) return this;
		loaded=true;
		
		id=GL30.glGenFramebuffers();
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, id);
		GL11.glDrawBuffer(GL30.GL_COLOR_ATTACHMENT0);
		
		if(hasTexture) createTexture();
		if(hasDepth) createDepth();
		
		if(initHook!=null) initHook.run();
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
		return this;
	}
	
	protected void createTexture(){
		if(renderBufferType){
			colorBuffer=GL30.glGenRenderbuffers();
			GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, colorBuffer);
			if(sample>1) GL30.glRenderbufferStorageMultisample(GL30.GL_RENDERBUFFER, sample, GL11.GL_RGBA8, getWidth(), getHeight());
			else GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, GL11.GL_RGBA8, getWidth(), getHeight());
			GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL30.GL_RENDERBUFFER, colorBuffer);
		}
		else{
			int id=GL11.glGenTextures();
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
			GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, getWidth(), getHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, (ByteBuffer)null);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
			GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, id, 0);
			
			texture=new BasicTexture(null);
			texture.load(id, getWidth(), getHeight());
		}
	}
	
	protected void createDepth(){
		if(renderBufferType){
			depthBuffer=GL30.glGenRenderbuffers();
			GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, depthBuffer);
			if(sample>1) GL30.glRenderbufferStorageMultisample(GL30.GL_RENDERBUFFER, sample, GL14.GL_DEPTH_COMPONENT24, getWidth(), getHeight());
			else GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, GL14.GL_DEPTH_COMPONENT24, getWidth(), getHeight());
			GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL30.GL_RENDERBUFFER, depthBuffer);
		}
		else{
			int id=GL11.glGenTextures();
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
			GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL14.GL_DEPTH_COMPONENT24, getWidth(), getHeight(), 0, GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, (ByteBuffer)null);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
			GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL11.GL_TEXTURE_2D, id, 0);
			
			depth=new BasicTexture(null);
			depth.load(id, getWidth(), getHeight());
		}
	}
	
	public void setSize(IVec2i size){
		setSize(size.x(), size.y());
	}
	
	public void setSize(Fbo fbo){
		setSize(fbo.getWidth(), fbo.getHeight());
	}
	
	public void setSize(int width, int height){
		if(width==this.getWidth()&&height==this.getHeight()) return;
		delete();
		this.width=width;
		this.height=height;
	}
	
	public void delete(){
		if(!loaded) return;
		loaded=false;
		GL30.glDeleteFramebuffers(id);
		
		if(texture!=null){
			texture.delete();
			texture=null;
		}
		if(colorBuffer!=-1){
			GL30.glDeleteRenderbuffers(colorBuffer);
			colorBuffer=-1;
		}
		
		if(depth!=null){
			depth.delete();
			depth=null;
		}
		if(depthBuffer!=-1){
			GL30.glDeleteRenderbuffers(depthBuffer);
			depthBuffer=-1;
		}
	}
	
	
	@Override
	protected void finalize(){
		delete();
	}
	
	Matrix4f zero=new Matrix4f();
	
	public void copyColorTo(Fbo dest){
		copyTo(dest, GL11.GL_COLOR_BUFFER_BIT);
	}
	
	public void copyTo(Fbo dest, int what){
		copyTo(dest, what, GL11.GL_NEAREST);
	}
	
	public void copyTo(Fbo dest, int what, int how){
		create();
		dest.create();
		GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, id);
		GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, dest.id);
		GL30.glBlitFramebuffer(0, 0, getWidth(), getHeight(), 0, 0, dest.getWidth(), dest.getHeight(), what, how);
		dest.bind();
	}
	
	public void copyColorToScreen(){
		copyToScreen(GL11.GL_COLOR_BUFFER_BIT);
	}
	
	public void copyToScreen(int what){
		copyToScreen(what, GL11.GL_NEAREST);
	}
	
	public void copyToScreen(int what, int how){
		create();
		GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, id);
		GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, 0);
		GL30.glBlitFramebuffer(0, 0, getWidth(), getHeight(), 0, 0, Display.getWidth(), Display.getHeight(), what, how);
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
	}
	
	public void drawImg(){
		Objects.requireNonNull(getTexture());
		List<ITexture> tx=FULL_SCREEN_MODEL.getTextures();
		if(tx.size()==0) tx.add(getTexture());
		
		tx.set(0, getTexture());
		
		GLUtil.DEPTH_TEST.set(false);
		Shaders.POST_COPY.renderSingle(zero, FULL_SCREEN_MODEL);
		GLUtil.DEPTH_TEST.set(true);
	}
	
	public ITexture getTexture(){
		return texture;
	}
	
	public ITexture getDepth(){
		return depth;
	}
	
	public int getWidth(){
		return width;
	}
	
	public int getHeight(){
		return height;
	}
	
	public boolean isLoaded(){
		return loaded;
	}
	
	public boolean hasTexture(){
		return hasTexture;
	}
	
	public boolean hasDepth(){
		return hasDepth;
	}
}
