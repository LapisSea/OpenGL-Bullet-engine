package com.lapissea.opengl.resources.model;

import java.util.ArrayList;
import java.util.List;

import com.lapissea.opengl.util.math.vec.Vec2f;
import com.lapissea.opengl.util.math.vec.Vec3f;
import com.lapissea.opengl.window.assets.IMaterial;
import com.lapissea.opengl.window.impl.assets.Material;

import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.floats.FloatList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;

public class ModelDataBuilder{
	
	public FloatList	vertecies	=new FloatArrayList();
	public FloatList	uvs			=new FloatArrayList();
	public FloatList	normals		=new FloatArrayList();
	public IntList		materials	=new IntArrayList();
	
	public IntList indices=new IntArrayList();
	
	public List<IMaterial>	materialDefs=new ArrayList<>();
	public String			name;
	
	public int format;
	
	public ModelDataBuilder(String name){
		this.name=name;
	}
	
	public void addVertex(Vec3f vec){
		vertecies.add(vec.x());
		vertecies.add(vec.y());
		vertecies.add(vec.z());
	}
	
	public void addNormal(Vec3f vec){
		normals.add(vec.x());
		normals.add(vec.y());
		normals.add(vec.z());
	}
	
	public void addUv(Vec2f vec){
		uvs.add(vec.x());
		uvs.add(vec.y());
	}
	
	public ModelBuilder compile(){
		
		ModelBuilder compiled=new ModelBuilder().withName(name).withVertecies(vertecies).withFormat(format);
		
		if(!normals.isEmpty()) compiled.withNormals(normals);
		if(!uvs.isEmpty()) compiled.withUvs(uvs);
		if(!indices.isEmpty()) compiled.withIndices(indices);
		
		if(!materialDefs.isEmpty()) compiled.withMaterials(materials);
		else materialDefs.add(new Material(0));
		
		compiled.withMaterialDefs(materialDefs);
		
		return compiled;
	}
}
