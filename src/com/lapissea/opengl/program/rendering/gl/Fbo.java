package com.lapissea.opengl.program.rendering.gl;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL14.*;
import static org.lwjgl.opengl.GL30.*;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Objects;

import org.lwjgl.opengl.Display;
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
	
	public static final IModel FULL_SCREEN_MODEL=ModelLoader.buildModel("gen_fscren", GL_TRIANGLE_STRIP, "genNormals", false, "vertices", new float[]{
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
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
		GLUtil.viewport(Game.win().getSize());
	}
	
	public void bind(){
		create();
		glBindFramebuffer(GL_FRAMEBUFFER, id);
		GLUtil.viewport(getWidth(), getHeight());
	}
	
	public Fbo create(){
		if(loaded||getWidth()<=0||getHeight()<=0) return this;
		loaded=true;
		
		id=glGenFramebuffers();
		glBindFramebuffer(GL_FRAMEBUFFER, id);
		glDrawBuffer(GL_COLOR_ATTACHMENT0);
		
		if(hasTexture) createTexture();
		if(hasDepth) createDepth();
		
		if(initHook!=null) initHook.run();
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
		return this;
	}
	
	protected void createTexture(){
		if(renderBufferType){
			colorBuffer=glGenRenderbuffers();
			glBindRenderbuffer(GL_RENDERBUFFER, colorBuffer);
			if(sample>1) glRenderbufferStorageMultisample(GL_RENDERBUFFER, sample, GL_RGBA8, getWidth(), getHeight());
			else glRenderbufferStorage(GL_RENDERBUFFER, GL_RGBA8, getWidth(), getHeight());
			glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_RENDERBUFFER, colorBuffer);
		}else{
			int id=glGenTextures();
			glBindTexture(GL_TEXTURE_2D, id);
			glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, getWidth(), getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, (ByteBuffer)null);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
			glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, id, 0);
			
			texture=new BasicTexture(null);
			texture.load(id, getWidth(), getHeight());
		}
	}
	
	protected void createDepth(){
		if(renderBufferType){
			depthBuffer=glGenRenderbuffers();
			glBindRenderbuffer(GL_RENDERBUFFER, depthBuffer);
			if(sample>1) glRenderbufferStorageMultisample(GL_RENDERBUFFER, sample, GL_DEPTH_COMPONENT24, getWidth(), getHeight());
			else glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT24, getWidth(), getHeight());
			glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, depthBuffer);
		}else{
			int id=glGenTextures();
			glBindTexture(GL_TEXTURE_2D, id);
			glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT24, getWidth(), getHeight(), 0, GL_DEPTH_COMPONENT, GL_FLOAT, (ByteBuffer)null);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
			glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, id, 0);
			
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
		if(width==getWidth()&&height==getHeight()) return;
		delete();
		this.width=width;
		this.height=height;
	}
	
	public void delete(){
		if(!loaded) return;
		loaded=false;
		glDeleteFramebuffers(id);
		
		if(texture!=null){
			texture.delete();
			texture=null;
		}
		if(colorBuffer!=-1){
			glDeleteRenderbuffers(colorBuffer);
			colorBuffer=-1;
		}
		
		if(depth!=null){
			depth.delete();
			depth=null;
		}
		if(depthBuffer!=-1){
			glDeleteRenderbuffers(depthBuffer);
			depthBuffer=-1;
		}
	}
	
	@Override
	protected void finalize(){
		delete();
	}
	
	Matrix4f zero=new Matrix4f();
	
	public void copyColorTo(Fbo dest){
		copyTo(dest, GL_COLOR_BUFFER_BIT);
	}
	
	public void copyTo(Fbo dest, int what){
		copyTo(dest, what, GL_NEAREST);
	}
	
	public void copyTo(Fbo dest, int what, int how){
		create();
		dest.create();
		glBindFramebuffer(GL_READ_FRAMEBUFFER, id);
		glBindFramebuffer(GL_DRAW_FRAMEBUFFER, dest.id);
		glBlitFramebuffer(0, 0, getWidth(), getHeight(), 0, 0, dest.getWidth(), dest.getHeight(), what, how);
		dest.bind();
	}
	
	public void copyColorToScreen(){
		copyToScreen(GL_COLOR_BUFFER_BIT);
	}
	
	public void copyToScreen(int what){
		copyToScreen(what, GL_NEAREST);
	}
	
	public void copyToScreen(int what, int how){
		create();
		glBindFramebuffer(GL_READ_FRAMEBUFFER, id);
		glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0);
		glBlitFramebuffer(0, 0, getWidth(), getHeight(), 0, 0, Display.getWidth(), Display.getHeight(), what, how);
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
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
