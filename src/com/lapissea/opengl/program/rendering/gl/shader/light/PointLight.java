package com.lapissea.opengl.program.rendering.gl.shader.light;

import com.lapissea.opengl.program.util.math.vec.Vec3f;
import com.lapissea.opengl.window.api.util.color.IColorM;

public class PointLight{
	
	public Vec3f	pos,attenuation;
	public IColorM	color;
	public float radiusSize;
	
	public PointLight(Vec3f pos, IColorM color, Vec3f attenuation){
		this.pos=pos;
		this.color=color;
		this.attenuation=attenuation;
	}
	
}
