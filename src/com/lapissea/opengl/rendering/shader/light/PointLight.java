package com.lapissea.opengl.rendering.shader.light;

import com.lapissea.opengl.util.math.vec.Vec3f;
import com.lapissea.opengl.window.api.util.color.IColorM;

public class PointLight extends LightSource{
	
	public Vec3f	pos;
	public Vec3f	attenuation;
	
	public PointLight(Vec3f pos, IColorM color, Vec3f attenuation){
		super(color);
		this.pos=pos;
		this.attenuation=attenuation;
	}
	
}
