package com.lapissea.opengl.program.opengl.assets;

import com.lapissea.opengl.abstr.opengl.assets.ITexture;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;

public class BasicTexture implements ITexture{
	
	private int					id,width,height;
	private Object				loading;
	private boolean				loaded;
	private final String		path;
	private final Int2IntMap	params	=new Int2IntOpenHashMap();
	
	public BasicTexture(String path){
		this.path=path;
	}
	
	@Override
	public int getId(){
		if(!isLoaded()) return -1;
		return id;
	}
	
	@Override
	public int getWidth(){
		if(!isLoaded()) return 3;
		return width;
	}
	
	@Override
	public int getHeight(){
		if(!isLoaded()) return 3;
		return height;
	}
	
	@Override
	public void delete(){
		if(!isLoaded()) return;
		ITexture.super.delete();
		loaded=false;
	}
	
	@Override
	public void finalize(){
		delete();
	}
	
	@Override
	public String getPath(){
		return path;
	}
	
	@Override
	public boolean isLoaded(){
		return loaded;
	}
	
	@Override
	public void load(int id, int width, int height){
		if(isLoaded()) throw new IllegalStateException("Image already loaded!");
		this.width=width;
		this.height=height;
		this.id=id;
		loaded=true;
		loading=null;
	}
	
	@Override
	public String toString(){
		return new StringBuilder(getClass().getSimpleName()).append("{path=").append(getPath()).append(isLoaded()?"":isLoading()?", LOADING":", NOT_LOADED=").append(", id=").append(getId()).append(", width=").append(getWidth()).append(", height=").append(getHeight()).append("}").toString();
	}
	
	@Override
	public boolean equals(Object obj){
		if(!(obj instanceof ITexture)) return false;
		
		ITexture texture=(ITexture)obj;
		return texture.getId()==getId()&&
				texture.getWidth()==getWidth()&&
				texture.getHeight()==getHeight()&&
				texture.isLoaded()==isLoaded();
	}
	
	@Override
	public int hashCode(){
		if(!isLoaded()) return -1;
		int hash=getId();
		hash*=1024;
		hash+=getWidth();
		hash*=1024;
		hash+=getHeight();
		return hash;
	}
	
	@Override
	public boolean isLoading(){
		return loading!=null;
	}
	
	@Override
	public Object notifyLoading(){
		if(isLoading()) throw new IllegalStateException("Image \""+path+"\" already loading!");
		return loading=new Object();
	}
	
	@Override
	public int[] params(){
		int[] arr=new int[params.size()*2];
		int i=0;
		for(int id:params.keySet()){
			arr[i++]=id;
			arr[i++]=params.get(id);
		}
		return arr;
	}
	
	@Override
	public void params(int id, int value){
		params.put(id, value);
	}
	
	@Override
	public Object loadingKey(){
		return loading;
	}
}
