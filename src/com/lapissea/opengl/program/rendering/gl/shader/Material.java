package com.lapissea.opengl.program.rendering.gl.shader;

import com.lapissea.opengl.program.util.color.ColorM;

public class Material{
	
	public ColorM		ambient			=new ColorM(0, 0, 0, 0);
	public ColorM		diffuse			=new ColorM();
	public ColorM		specular		=new ColorM();
	public float		illum			=0;
	public float		jelly			=0;
	public float		shineDamper		=1;
	public float		reflectivity	=0;
	public float		lightTroughput	=0;
	public final String	name;
	public int			id;
	
	public Material(){
		this("");
	}
	
	public Material(String name){
		this.name=name;
	}
}
