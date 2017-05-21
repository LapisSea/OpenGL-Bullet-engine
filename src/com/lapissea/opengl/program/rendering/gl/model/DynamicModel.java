package com.lapissea.opengl.program.rendering.gl.model;

import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;

import com.lapissea.opengl.window.api.frustrum.IFrustrumShape;
import com.lapissea.opengl.window.api.util.BufferUtil;
import com.lapissea.opengl.window.api.util.IRotation;
import com.lapissea.opengl.window.api.util.IVec3f;
import com.lapissea.opengl.window.api.util.color.IColorM;
import com.lapissea.opengl.window.assets.IModel;
import com.lapissea.opengl.window.assets.ITexture;
import com.lapissea.opengl.window.assets.ModelAttribute;
import com.lapissea.opengl.window.impl.assets.Model;

import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.floats.FloatList;
import it.unimi.dsi.fastutil.ints.Int2IntArrayMap;
import it.unimi.dsi.fastutil.ints.Int2IntMap;

public class DynamicModel extends Model{
	
	private FloatList	data[];
	private boolean		dirty;
	private Int2IntMap	vtIds	=new Int2IntArrayMap();
	
	public DynamicModel(ITexture...texture){
		super(texture);
	}
	
	public DynamicModel(String name, ITexture...texture){
		super(name, texture);
	}
	
	public DynamicModel(String name){
		super(name);
	}
	
	@Override
	public IModel load(int vao, int vertexCount, boolean usesIndicies, boolean usesQuads, int[] vbos, ModelAttribute[] attributeIds, IFrustrumShape<? extends IVec3f,? extends IRotation> shape){
		if(data==null||data.length!=vbos.length) data=new FloatList[vbos.length];
		vtIds.clear();
		for(int i=0;i<attributeIds.length;i++){
			vtIds.put(attributeIds[i].id, i);
			data[i]=new FloatArrayList();
		}
		return super.load(vao, vertexCount, usesIndicies, usesQuads, vbos, attributeIds, shape);
	}
	
	public DynamicModel pos(IVec3f vec){
		return add(ModelAttribute.VERTEX_ATTR, vec.x(), vec.y(), vec.z());
	}
	
	public DynamicModel pos(float x, float y, float z){
		return add(ModelAttribute.VERTEX_ATTR, x, y, z);
	}
	
	public DynamicModel uv(float u, float v){
		return add(ModelAttribute.UV_ATTR, u, v);
	}
	
	public DynamicModel norm(IVec3f vec){
		return add(ModelAttribute.NORMAL_ATTR, vec.x(), vec.y(), vec.z());
	}
	
	public DynamicModel norm(float x, float y, float z){
		return add(ModelAttribute.NORMAL_ATTR, x, y, z);
	}
	
	public DynamicModel mater(int id){
		return add(ModelAttribute.MAERIAL_ID_ATTR, id);
	}
	
	public DynamicModel coler(IColorM color){
		return add(ModelAttribute.MAERIAL_ID_ATTR, color.r(), color.g(), color.b(), color.a());
	}
	
	public DynamicModel coler(float r, float g, float b, float a){
		return add(ModelAttribute.MAERIAL_ID_ATTR, r, g, b, a);
	}
	
	public DynamicModel add(ModelAttribute type, float f1, float f2, float f3, float f4){
		if(!isLoaded()) return this;
		FloatList buffer=add0(type, 4);
		buffer.add(f1);
		buffer.add(f2);
		buffer.add(f3);
		buffer.add(f4);
		return this;
	}
	
	public DynamicModel add(ModelAttribute type, float f1, float f2, float f3){
		if(!isLoaded()) return this;
		FloatList buffer=add0(type, 3);
		buffer.add(f1);
		buffer.add(f2);
		buffer.add(f3);
		return this;
	}
	
	public DynamicModel add(ModelAttribute type, float f1, float f2){
		if(!isLoaded()) return this;
		FloatList buffer=add0(type, 2);
		buffer.add(f1);
		buffer.add(f2);
		return this;
	}
	
	public DynamicModel add(ModelAttribute type, float f){
		if(!isLoaded()) return this;
		add0(type, 1).add(f);
		return this;
	}
	
	private FloatList add0(ModelAttribute type, int toAdd){
		if(type.size!=toAdd) throw new IllegalAccessError("Bad attibute size!");
		int id=vtIds.get(type.id);
		
		//		ensureSize((buff[id]==null?0:buff[id].capacity())/type.size+1);
		dirty=true;
		return data[id];
	}
	
	@Override
	public IModel drawCall(){
		requireLoaded();
		if(dirty) upload();
		super.drawCall();
		return this;
	}
	
	public void clearIfEmpty(){
		dirty=true;
	}
	
	private void upload(){
		vertexCount=data[0].size()/getAttribute(0).size;
		for(int i=1;i<getAttributeCount();i++){
			if(data[i].size()/getAttribute(i).size!=vertexCount) throw new IllegalStateException("bad size on "+getAttribute(i));
		}
		GL30.glBindVertexArray(vao);
		for(int i=0;i<getAttributeCount();i++){
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbos[i]);
			GL15.glBufferData(GL15.GL_ARRAY_BUFFER, BufferUtil.store(data[i].toFloatArray()), GL15.GL_STREAM_DRAW);
			data[i].clear();
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		}
		dirty=false;
	}
	@Override
	public DynamicModel culface(boolean enabled){
		super.culface(enabled);
		return this;
	}
}
