package com.lapissea.opengl.program.rendering.gl;

import com.lapissea.opengl.window.api.util.color.ColorM;

public class Fog{
	
	private float	gradient=1.5F,density=0.007F;
	public ColorM	color	=new ColorM();
	private double	maxDistance;
	
	public Fog(){
		calc();
	}
	
	public float getGradient(){
		return gradient;
	}
	
	public void setGradient(float gradient){
		if(this.gradient==gradient) return;
		this.gradient=gradient;
		calc();
	}
	
	public float getDensity(){
		return density;
	}
	
	public void setDensity(float density){
		if(this.density==density) return;
		this.density=density;
		calc();
	}
	
	private void calc(){
		float cutOff=1F/256;
		maxDistance=Math.pow(-Math.log(cutOff), 1/gradient)/density;
	}
	
	public double getMaxDistance(){
		return maxDistance;
	}
}
