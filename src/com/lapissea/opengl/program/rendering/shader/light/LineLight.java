package com.lapissea.opengl.program.rendering.shader.light;

import com.lapissea.opengl.program.util.math.vec.Vec3f;
import com.lapissea.opengl.window.api.util.color.ColorM;
import com.lapissea.opengl.window.api.util.color.IColorM;

public class LineLight extends LightSource{
	
	public Vec3f	pos1;
	public Vec3f	pos2;
	public Vec3f	attenuation;
	
	public LineLight(Vec3f pos1, Vec3f pos2, IColorM color, Vec3f attenuation){
		super(color);
		this.pos1=pos1;
		this.pos2=pos2;
		this.attenuation=attenuation;
		this.color=ColorM.toColorM(color);
	}
	
}
