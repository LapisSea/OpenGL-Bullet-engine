package com.lapissea.opengl.program.rendering.gl.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;

import com.lapissea.opengl.abstr.opengl.assets.IMaterial;
import com.lapissea.opengl.abstr.opengl.assets.IModel;
import com.lapissea.opengl.abstr.opengl.assets.ITexture;
import com.lapissea.opengl.abstr.opengl.assets.ModelAttribute;
import com.lapissea.opengl.abstr.opengl.frustrum.IFrustrumShape;
import com.lapissea.opengl.program.interfaces.IntObjConsumer;
import com.lapissea.opengl.program.rendering.gl.shader.Material;

public class Model implements IModel{
	
	protected int				vao;
	protected int				vertexCount;
	protected int[]				vbos;
	protected int				glDrawId;
	private ModelAttribute		attributes[];
	private boolean				loaded;
	private boolean				usesIndicies;
	private IFrustrumShape		shape;
	private final String		name;
	private Consumer<IModel>	onload	=null;
	
	private final List<IMaterial>	materials	=new ArrayList<>();
	private final List<ITexture>	textures	=new ArrayList<>();
	
	public Model(ITexture...texture){
		this("NO_NAME");
		for(ITexture t:texture){
			this.textures.add(t);
		}
		
	}
	
	public Model(String name, ITexture...texture){
		this(name);
		for(ITexture t:texture){
			this.textures.add(t);
		}
	}
	
	public Model(String name){
		this.name=name;
	}
	
	@Override
	public IModel load(int vao, int vertexCount, boolean usesIndicies, boolean usesQuads, int[] vbos, ModelAttribute[] attributeIds, IFrustrumShape shape){
		requireNotLoaded();
		this.vao=vao;
		this.vertexCount=vertexCount;
		this.usesIndicies=usesIndicies;
		this.vbos=vbos;
		this.shape=shape;
		glDrawId=usesQuads?GL11.GL_QUADS:GL11.GL_TRIANGLES;
		this.attributes=attributeIds;
		loaded=true;
		if(onload!=null) onload.accept(this);
		return this;
	}
	
	@Override
	public int hashCode(){
		return vao;
	}
	
	@Override
	public IModel enableAttributes(){
		requireLoaded();
		for(ModelAttribute attr:attributes){
			attr.enable();
		}
		return this;
	}
	
	@Override
	public IModel disableAttributes(){
		requireLoaded();
		for(ModelAttribute attr:attributes){
			attr.disable();
		}
		return this;
	}
	
	@Override
	public IModel drawCall(){
		requireLoaded();
		if(usesIndicies) GL11.glDrawElements(glDrawId, vertexCount, GL11.GL_UNSIGNED_INT, 0);
		else GL11.glDrawArrays(glDrawId, 0, vertexCount);
		return this;
	}
	
	@Override
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
		return "Model{name=\""+name+'"'+(loaded?", vao="+vao+", shape="+shape+", attribs: "+Arrays.toString(attributes).replaceAll("_ATTR", "")+", type="+type+" indexed="+usesIndicies:", not_loaded")+"}";
	}
	
	@Override
	public void delete(){
		GL30.glDeleteVertexArrays(vao);
		for(int vbo:vbos){
			GL15.glDeleteBuffers(vbo);
		}
		vao=glDrawId=vertexCount=0;
		attributes=null;
		vbos=null;
		usesIndicies=loaded=false;
	}
	
	@Override
	public List<ITexture> getTextures(){
		return textures;
	}
	
	public void addTexture(ITexture texture){
		if(textures.contains(texture)) return;
		textures.add(texture);
	}
	
	@Override
	public String getName(){
		return name;
	}
	
	@Override
	public IModel setOnLoad(Consumer<IModel> hook){
		onload=hook;
		return this;
	}
	
	@Override
	public IFrustrumShape getFrustrumShape(){
		return shape;
	}
	
	@Override
	public IMaterial getMaterial(int i){
		if(i==0&&materials.isEmpty()) return null;
		return materials.get(i);
	}
	
	@Override
	public IMaterial removeMaterial(int i){
		return materials.remove(i);
	}
	
	@Override
	public IMaterial createMaterial(){
		return createMaterial("NO_NAME");
	}
	
	@Override
	public IMaterial createMaterial(String name){
		int id=materials.size();
		for(int i=0;i<materials.size();i++){
			final int j=i;
			if(materials.stream().noneMatch(m->m.getId()==j)){
				id=i;
				break;
			}
		}
		IMaterial material=new Material(id, name);
		materials.add(material);
		return material;
	}
	
	@Override
	public int getMaterialCount(){
		return materials.size();
	}
	
	@Override
	public Model iterateMaterials(IntObjConsumer<IMaterial> consummer){
		if(materials.isEmpty()) return this;
		Iterator<IMaterial> iter=materials.iterator();
		int i=0;
		while(iter.hasNext()){
			consummer.accept(i++, iter.next());
		}
		return this;
	}
	
	@Override
	public int getVertexCount(){
		return vertexCount;
	}
	
	@Override
	public ModelAttribute getAttribute(int i){
		return attributes[i];
	}
	
	@Override
	public int getAttributeCount(){
		return attributes.length;
	}
	
	protected void requireLoaded(){
		if(!isLoaded()) throw new IllegalStateException("Model should be loaded!");
	}
	
	protected void requireNotLoaded(){
		if(isLoaded()) throw new IllegalStateException("Model should not be loaded!");
	}
	
	@Override
	public IModel bindVao(){
		GL30.glBindVertexArray(vao);
		return this;
	}
	
	@Override
	public void addMaterial(IMaterial material){
		materials.add(material);
	}
}
