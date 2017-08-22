package com.lapissea.opengl.program.resources.model;

import com.lapissea.opengl.window.api.frustrum.IFrustrumShape;
import com.lapissea.opengl.window.assets.IMaterial;
import com.lapissea.opengl.window.assets.IModel;
import com.lapissea.opengl.window.assets.ITexture;
import com.lapissea.opengl.window.assets.ModelAttribute;
import com.lapissea.opengl.window.assets.Vbo;
import com.lapissea.opengl.window.impl.assets.Model;

class EmptyModel extends Model{
	
	public EmptyModel(){
		super("EMPTY_MODEL");
	}
	
	@Override
	public IModel load(int vao, int vertexCount, boolean usesIndicies, int format, Vbo[] vbos, ModelAttribute vertexType, ModelAttribute[] attributeIds, IFrustrumShape shape){
		throw new UnsupportedOperationException();
	}
	
	@Override
	public IModel drawCall(){
		throw new UnsupportedOperationException();
	}
	
	@Override
	public IModel enableAttributes(){
		throw new UnsupportedOperationException();
	}
	
	@Override
	public IModel disableAttributes(){
		throw new UnsupportedOperationException();
	}
	
	@Override
	public boolean isLoaded(){
		return false;
	}
	
	@Override
	public IModel bindVao(){
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void addMaterial(IMaterial material){
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void addTexture(ITexture texture){
		throw new UnsupportedOperationException();
	}
	
	@Override
	public IMaterial createMaterial(){
		throw new UnsupportedOperationException();
	}
	
	@Override
	public IMaterial createMaterial(String name){
		throw new UnsupportedOperationException();
	}
	
}
