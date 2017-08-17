package com.lapissea.opengl.program.resources.model;

import com.lapissea.opengl.window.assets.IMaterial;

public class ModelData{
	
	public String name;
	
	public float[]		vertecies,uvs,normals;
	public int[]		materialIds;
	public IMaterial[]	materials;
	
	public int format;
	
	public ModelData(){}
	
	public ModelData(String name){
		this.name=name;
	}
}
