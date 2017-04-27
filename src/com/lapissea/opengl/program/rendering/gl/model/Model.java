package com.lapissea.opengl.program.rendering.gl.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;

import com.lapissea.opengl.program.rendering.GLUtil;
import com.lapissea.opengl.program.rendering.gl.shader.Material;
import com.lapissea.opengl.program.rendering.gl.texture.ITexture;

public class Model{
	
	protected int				vao;
	protected int				vertexCount;
	protected int[]				vbos;
	protected int				glDrawId;
	protected ModelAttribute	attributeIds[];
	protected boolean			loaded;
	protected boolean			usesIndicies;
	protected float				rad;
	public String				name;
	public Runnable				onload	=()->{};
	
	public final Material			defaultMaterial	=new Material("default");
	public final List<Material>		materials		=new ArrayList<>();
	protected final List<ITexture>	textures		=new ArrayList<>();
	
	public Model(ITexture...texture){
		for(ITexture t:texture){
			this.textures.add(t);
		}
	}
	
	public Model(String name, ITexture...texture){
		this.name=name;
		for(ITexture t:texture){
			this.textures.add(t);
		}
	}
	
	public Model(String name){
		this.name=name;
	}
	
	public void load(int vao, int vertexCount, boolean usesIndicies, boolean usesQuads, int[] vbos, ModelAttribute[] attributeIds, float rad){
		if(isLoaded()) return;
		this.vao=vao;
		this.vertexCount=vertexCount;
		this.usesIndicies=usesIndicies;
		this.vbos=vbos;
		this.rad=rad;
		glDrawId=usesQuads?GL11.GL_QUADS:GL11.GL_TRIANGLES;
		this.attributeIds=attributeIds;
		loaded=true;
		onload.run();
	}
	
	@Override
	public int hashCode(){
		return vao();
	}
	
	@Override
	public boolean equals(Object obj){
		if(obj==this) return true;
		
		return obj instanceof Model&&vao()==((Model)obj).vao();
	}
	
	public void enableAttributes(){
		if(!isLoaded()) return;
		GLUtil.checkError();
		for(ModelAttribute attr:attributeIds){
			attr.enable();
		}
		GLUtil.checkError();
	}
	
	public void disableAttributes(){
		if(!isLoaded()) return;
		GLUtil.checkError();
		for(ModelAttribute attr:attributeIds){
			attr.disable();
		}
		GLUtil.checkError();
	}
	
	public void drawCall(){
		if(!isLoaded()) return;
		GLUtil.checkError();
		if(usesIndicies) GL11.glDrawElements(glDrawId, vertexCount, GL11.GL_UNSIGNED_INT, 0);
		else GL11.glDrawArrays(glDrawId, 0, vertexCount);
		GLUtil.checkError();
	}
	
	public int vao(){
		return vao;
	}
	
	public float rad(){
		return rad;
	}
	
	public boolean isLoaded(){
		return loaded;
	}
	
	@Override
	public String toString(){
		String type;
		switch(glDrawId){
		case GL11.GL_TRIANGLES:
			type="Triangle";
		break;
		case GL11.GL_QUADS:
			type="Quad";
		break;
		default:
			type=Integer.toString(glDrawId);
		break;
		}
		return "Model{name=\""+name+'"'+(loaded?", vao="+vao()+", rad="+Math.round(rad()*100)/100F+", attribs: "+Arrays.toString(attributeIds)+", type="+type+" indexed="+usesIndicies:", not_loaded")+"}";
	}
	
	public void delete(){
		GL30.glDeleteVertexArrays(vao);
		for(int vbo:vbos){
			GL15.glDeleteBuffers(vbo);
		}
		vao=glDrawId=vertexCount=0;
		attributeIds=null;
		vbos=null;
		usesIndicies=loaded=false;
	}
	
	public List<ITexture> getTextures(){
		return textures;
	}
	
	public void addTexture(ITexture texture){
		if(textures.contains(texture)) return;
		textures.add(texture);
	}
	
}
