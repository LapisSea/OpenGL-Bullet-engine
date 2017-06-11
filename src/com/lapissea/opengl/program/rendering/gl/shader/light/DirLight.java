package com.lapissea.opengl.program.rendering.gl.shader.light;

import com.lapissea.opengl.program.util.math.vec.Vec3f;
import com.lapissea.opengl.window.api.util.color.IColorM;

public class DirLight extends LightSource{
	
	public Vec3f dir;
	
	public DirLight(Vec3f dir, IColorM color){
		super(color);
		this.dir=dir;
	}
	
}
