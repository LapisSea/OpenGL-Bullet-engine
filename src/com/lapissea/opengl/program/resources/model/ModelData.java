package com.lapissea.opengl.program.resources.model;

import com.lapissea.opengl.window.assets.IMaterial;

public class ModelData{
	
	public float[]	vertecies;
	public float[]	uvs;
	public float[]	normals;
	public int[]	materialIds;
	
	public int[] indices;
	
	public IMaterial[]	materials;
	public String		name;
	
	public int format;
	
	public ModelData(){}
	
	public ModelData(String name){
		this.name=name;
	}
}
