package com.lapissea.opengl.program.rendering.gl.gui;

import com.lapissea.opengl.window.api.util.color.ColorM;

public class GuiElementMaterial{
	
	public static final float INHERIT=-100;
	
	public float	blurRad	=-1;
	public ColorM	color	=new ColorM(0,0,0,0);
	public float	mouseRad=-1;
	
	public GuiElementMaterial(){
		this(false);
	}
	
	public GuiElementMaterial(boolean inherit){
		if(inherit){
			this.blurRad=INHERIT;
			this.color=null;
			this.mouseRad=INHERIT;
		}
	}
	
	public GuiElementMaterial(float blurRad, ColorM color, float mouseRad){
		this.blurRad=blurRad;
		this.color=color;
		this.mouseRad=mouseRad;
	}
	
	public GuiElementMaterial set(GuiElementMaterial src){
		if(src.blurRad!=INHERIT)blurRad=src.blurRad;
		if(src.color!=null)color.set(src.color);
		if(src.mouseRad!=INHERIT)mouseRad=src.mouseRad;
		return this;
	}
	
	public GuiElementMaterial interpolate(GuiElementMaterial v, float percent){
		return interpolate(this, this, v, percent);
	}
	
	public GuiElementMaterial interpolate(GuiElementMaterial v1, GuiElementMaterial v2, float percent){
		return interpolate(this, v1, v2, percent);
	}
	
	public static GuiElementMaterial interpolate(GuiElementMaterial dest, GuiElementMaterial v1, GuiElementMaterial v2, float percent){
		if(percent==0) return dest.set(v1);
		else if(percent==1) return dest.set(v2);
		float f1,f2;
		
		f1=v1.blurRad;
		if(f1==INHERIT) f1=dest.blurRad;
		f2=v2.blurRad;
		if(f2==INHERIT) f2=dest.blurRad;
		dest.blurRad=f1+(f2-f1)*percent;
		
		dest.color.interpolate(v1.color==null?dest.color:v1.color, v2.color==null?dest.color:v2.color, percent);
		
		f1=v1.mouseRad;
		if(f1==INHERIT) f1=dest.mouseRad;
		f2=v2.mouseRad;
		if(f2==INHERIT) f2=dest.mouseRad;
		dest.mouseRad=f1+(f2-f1)*percent;
		
		return dest;
	}
}
