package com.lapissea.opengl.program.rendering.gl.gui;

import com.lapissea.opengl.window.api.util.color.ColorM;

public class GuiElementMaterial{
	
	public float	blurRad	=-1;
	public ColorM	color	=new ColorM(0, 0, 0, 0);
	public float	mouseRad=-1;
	
	public GuiElementMaterial(){
		
	}
	
	public GuiElementMaterial(float blurRad, ColorM color, float mouseRad){
		this.blurRad=blurRad;
		this.color=color;
		this.mouseRad=mouseRad;
	}
}
