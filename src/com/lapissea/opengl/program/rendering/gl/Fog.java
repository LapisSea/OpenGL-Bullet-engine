package com.lapissea.opengl.program.rendering.gl;

import com.lapissea.opengl.window.api.util.color.ColorM;

public class Fog{
	public float gradient=1.5F,density=0.007F;
	public ColorM color=new ColorM();
	
	
	public double calcVisiblityLimit(){
		float cutOff=1F/256;
		return (Math.pow(-Math.log(cutOff), 1/gradient))/density;
	}
	
}
