package com.lapissea.opengl.program.util;

import javax.vecmath.Matrix4f;

import com.bulletphysics.linearmath.Transform;
import com.lapissea.opengl.program.util.math.vec.Vec3f;

public class TransformOgl extends Transform{
	
	private final Vec3f origin0=new Vec3f();
	
	public TransformOgl(Matrix4f mat){
		super(mat);
	}
	
	public Vec3f orig(){
		return orig(origin0);
	}
	
	public Vec3f orig(Vec3f vec){
		vec.x=origin.x;
		vec.y=origin.y;
		vec.z=origin.z;
		return vec;
	}
	
}
