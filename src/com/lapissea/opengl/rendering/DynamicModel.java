package com.lapissea.opengl.rendering;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

import java.nio.FloatBuffer;
import java.util.function.Consumer;

import org.lwjgl.BufferUtils;

import com.lapissea.opengl.resources.model.ModelLoader;
import com.lapissea.opengl.window.api.frustrum.IFrustrumShape;
import com.lapissea.opengl.window.api.util.BufferUtil;
import com.lapissea.opengl.window.api.util.color.IColorM;
import com.lapissea.opengl.window.api.util.vec.IVec3fR;
import com.lapissea.opengl.window.assets.IModel;
import com.lapissea.opengl.window.assets.ModelAttribute;
import com.lapissea.opengl.window.assets.Vbo;
import com.lapissea.opengl.window.impl.assets.Model;

import it.unimi.dsi.fastutil.ints.Int2IntArrayMap;
import it.unimi.dsi.fastutil.ints.Int2IntMap;

public class DynamicModel extends Model{
	
	private FloatBuffer	data[];
	private boolean		dirty;
	private Int2IntMap	vtIds	=new Int2IntArrayMap();
	
	public DynamicModel(String name){
		super(name);
	}
	
	@Override
	public IModel load(int vao, int vertexCount, boolean usesIndicies, int format, Vbo[] vbos, ModelAttribute vertexType, ModelAttribute[] attributes, IFrustrumShape shape){
		if(onload!=null){
			Consumer<IModel> onload0=onload;
			onload=null;
			super.load(vao, vertexCount, usesIndicies, format, vbos, vertexType, attributes, shape);
			onload=onload0;
		}else super.load(vao, vertexCount, usesIndicies, format, vbos, vertexType, attributes, shape);
		
		if(data==null||data.length!=vbos.length) data=new FloatBuffer[vbos.length];
		vtIds.clear();
		for(int i=0;i<this.attributes.length;i++){
			ModelAttribute att=this.attributes[i];
			vtIds.put(att.id, i);
			buf(i, att.size*3);
		}
		
		if(onload!=null) onload.accept(this);
		return this;
	}
	
	private void buf(int i, int size){
		data[i]=BufferUtils.createFloatBuffer(size);
	}
	
	public DynamicModel pos(IVec3fR vec){
		return add(ModelAttribute.VERTEX_ATTR_3D, vec.x(), vec.y(), vec.z());
	}
	
	public DynamicModel pos(float x, float y, float z){
		return add(ModelAttribute.VERTEX_ATTR_3D, x, y, z);
	}
	
	public DynamicModel uv(float u, float v){
		return add(ModelAttribute.UV_ATTR, u, v);
	}
	
	public DynamicModel norm(IVec3fR vec){
		return add(ModelAttribute.NORMAL_ATTR, vec.x(), vec.y(), vec.z());
	}
	
	public DynamicModel norm(float x, float y, float z){
		return add(ModelAttribute.NORMAL_ATTR, x, y, z);
	}
	
	public DynamicModel mater(int id){
		return add(ModelAttribute.MATERIAL_ID_ATTR, id);
	}
	
	public DynamicModel coler(IColorM color){
		return add(ModelAttribute.MATERIAL_ID_ATTR, color.r(), color.g(), color.b(), color.a());
	}
	
	public DynamicModel coler(float r, float g, float b, float a){
		return add(ModelAttribute.MATERIAL_ID_ATTR, r, g, b, a);
	}
	
	public DynamicModel add(ModelAttribute type, float f1, float f2, float f3, float f4){
		if(!isLoaded()) return this;
		add0(type, 4).put(f1).put(f2).put(f3).put(f4);
		return this;
	}
	
	public DynamicModel add(ModelAttribute type, float f1, float f2, float f3){
		if(!isLoaded()) return this;
		add0(type, 3).put(f1).put(f2).put(f3);
		return this;
	}
	
	public DynamicModel add(ModelAttribute type, float f1, float f2){
		if(!isLoaded()) return this;
		add0(type, 2).put(f1).put(f2);
		return this;
	}
	
	public DynamicModel add(ModelAttribute type, float f){
		if(!isLoaded()) return this;
		add0(type, 1).put(f);
		return this;
	}
	
	private FloatBuffer add0(ModelAttribute type, int toAdd){
		if(type.size!=toAdd) throw new IllegalAccessError("Bad attibute size!"+type.size+"/"+toAdd);
		int id=vtIds.get(type.id);
		FloatBuffer b=data[id];
		if(b.limit()<=b.position()+toAdd) b=data[id]=BufferUtil.expand(b, (int)((b.position()+toAdd)*1.5));
		dirty=true;
		return b;
	}
	
	@Override
	public DynamicModel drawCall(){
		requireLoaded();
		if(dirty) upload();
		super.drawCall();
		return this;
	}
	
	public void clearIfEmpty(){
		dirty=true;
	}
	
	private void upload(){
		vertexCount=data[0].position()/getAttribute(0).size;
		
		if(glDrawId!=GL_LINES){
			data[0].flip();
			shape=ModelLoader.calcShape(data[0], getVertexType().size);
			data[0].position(data[0].limit());
		}
		
		for(int i=1;i<getAttributeCount();i++){
			int size=data[i].position()/getAttribute(i).size;
			
			if(size!=vertexCount){
				StringBuilder b=new StringBuilder("bad size ").append(size).append('/').append(vertexCount).append(" on ").append(getAttribute(i)).append("\nList of all attributes:\n");
				for(int j=0;j<getAttributeCount();j++){
					b.append('\t').append(getAttribute(j)).append(": \t").append(data[j].position()).append(" - ").append(data[j].position()/(float)getAttribute(j).size).append('\n');
				}
				throw new IllegalStateException(b.toString());
			}
		}
		
		glBindVertexArray(vao);
		for(int i=0;i<getAttributeCount();i++){
			FloatBuffer f=data[i];
			f.flip();
			Vbo vbo=vbos[i];
			vbo.bind();
			vbo.storeData(f);
			vbo.unbind();
		}
		clear();
		dirty=false;
	}
	
	@Override
	public DynamicModel culface(boolean enabled){
		super.culface(enabled);
		return this;
	}
	
	public void clear(){
		for(int i=0;i<getAttributeCount();i++){
			data[i].clear();
		}
	}
}
