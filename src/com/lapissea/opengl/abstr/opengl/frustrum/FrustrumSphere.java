package com.lapissea.opengl.abstr.opengl.frustrum;

import com.lapissea.opengl.program.util.Quat4M;
import com.lapissea.opengl.program.util.math.vec.Vec3f;

public class FrustrumSphere implements IFrustrumShape{
	
	private final float	radius;
	private float		scale	=1;
	
	public FrustrumSphere(float radius){
		this.radius=radius;
	}
	
	@Override
	public boolean isVisibleAt(Vec3f pos, Frustum frustrum){
		return frustrum.sphere(pos, radius*scale);
	}
	
	@Override
	public boolean isVisibleAt(float x, float y, float z, Frustum frustrum){
		return frustrum.sphere(x, y, z, radius*scale);
	}
	
	@Override
	public IFrustrumShape withTransform(float x, float y, float z, Quat4M q){
		scale=Math.max(x, Math.max(y, z));
		return this;
	}
	
	@Override
	public String toString(){
		return "Sphere{r="+radius+"}";
	}
	
}
