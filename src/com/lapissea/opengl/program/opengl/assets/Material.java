package com.lapissea.opengl.program.rendering.gl.shader;

import com.lapissea.opengl.abstr.opengl.assets.IMaterial;
import com.lapissea.opengl.program.util.color.ColorM;

public class Material implements IMaterial{
	
	private static final String NO_NAME="NO_NAME";
	
	private final ColorM	ambient			=new ColorM(0, 0, 0, 0);
	private final ColorM	diffuse			=new ColorM();
	private final ColorM	specular		=new ColorM(0, 0, 0, 0);
	private float			illum			=0;
	private float			jelly			=0;
	private float			shineDamper		=1;
	private float			reflectivity	=0;
	private float			lightTroughput	=0;
	private final String	name;
	private final int		id;

	@Override
	public String toString(){
		return "Material{id="+id+", ambient="+ambient+", diffuse="+diffuse+", specular="+specular+", jelly="+jelly+"}";
	}
	
	public Material(int id){
		this(id, NO_NAME);
	}
	
	public Material(int id, String name){
		this.name=name;
		this.id=id;
	}
	
	@Override
	public ColorM getAmbient(){
		return ambient;
	}
	
	
	@Override
	public ColorM getDiffuse(){
		return diffuse;
	}
	
	@Override
	public ColorM getSpecular(){
		return specular;
	}
	
	
	public float getIllum(){
		return illum;
	}
	
	public void setIllum(float illum){
		this.illum=illum;
	}
	
	@Override
	public float getJelly(){
		return jelly;
	}
	
	@Override
	public void setJelly(float jelly){
		this.jelly=jelly;
	}
	
	@Override
	public float getShineDamper(){
		return shineDamper;
	}
	
	@Override
	public IMaterial setShineDamper(float shineDamper){
		this.shineDamper=shineDamper;
		return this;
	}
	
	@Override
	public float getReflectivity(){
		return reflectivity;
	}
	
	@Override
	public IMaterial setReflectivity(float reflectivity){
		this.reflectivity=reflectivity;
		return this;
	}
	
	@Override
	public float getLightTroughput(){
		return lightTroughput;
	}
	
	@Override
	public IMaterial setLightTroughput(float lightTroughput){
		this.lightTroughput=lightTroughput;
		return this;
	}
	
	@Override
	public String getName(){
		return name;
	}

	@Override
	public int getId(){
		return id;
	}

	@Override
	public IMaterial setAmbient(float r, float g, float b, float a){
		ambient.set(r, g, b, a);
		return this;
	}

	@Override
	public IMaterial setDiffuse(float r, float g, float b, float a){
		diffuse.set(r, g, b, a);
		return this;
	}

	@Override
	public IMaterial setSpecular(float r, float g, float b, float a){
		specular.set(r, g, b, a);
		return this;
	}
	
	
}
