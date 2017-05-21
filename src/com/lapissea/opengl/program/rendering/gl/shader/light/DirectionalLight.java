package com.lapissea.opengl.program.rendering.gl.shader.light;

import com.lapissea.opengl.program.util.math.vec.Vec3f;
import com.lapissea.opengl.window.api.util.color.IColorM;

public class DirectionalLight{
	
	public Vec3f	dir;
	public IColorM	color;
	
	public DirectionalLight(Vec3f dir, IColorM color){
		this.dir=dir;
		this.color=color;
	}
	
}
