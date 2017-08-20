package com.lapissea.opengl.program.rendering.frustrum;

import com.lapissea.opengl.window.api.frustrum.Frustum;
import com.lapissea.opengl.window.api.frustrum.IFrustrumShape;
import com.lapissea.opengl.window.api.util.vec.IRotation;
import com.lapissea.opengl.window.api.util.vec.IVec3fR;

public class FrustrumSphere implements IFrustrumShape{
	
	private final float	radius;
	private float		scale	=1;
	
	public FrustrumSphere(float radius){
		this.radius=radius;
	}
	
	@Override
	public boolean isVisibleAt(IVec3fR pos, Frustum frustrum){
		return frustrum.sphere(pos, radius*scale);
	}
	
	@Override
	public boolean isVisibleAt(float x, float y, float z, Frustum frustrum){
		return frustrum.sphere(x, y, z, radius*scale);
	}
	
	@Override
	public IFrustrumShape withTransform(float x, float y, float z, IRotation q){
		scale=Math.max(x, Math.max(y, z));
		return this;
	}
	
	@Override
	public String toString(){
		return "Sphere{r="+radius+"}";
	}
	
}
