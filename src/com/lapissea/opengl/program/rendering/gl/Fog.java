package com.lapissea.opengl.program.rendering.gl;

public class Fog{
	public float gradient=1.5F,density=0.007F;
	
	
	public double calcVisiblityLimit(){
		float cutOff=1F/256;
		return (Math.pow(-Math.log(cutOff), 1/gradient))/density;
	}
	
}
