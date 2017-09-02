package com.lapissea.opengl.rendering.shader.light;

import com.lapissea.opengl.window.api.util.color.ColorM;
import com.lapissea.opengl.window.api.util.color.IColorM;

public class LightSource{
	
	public ColorM color;
	
	public LightSource(IColorM color){
		this.color=ColorM.toColorM(color);
	}
	
	public LightSource(){}
	
}
