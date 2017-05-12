package com.lapissea.opengl.abstr.opengl.frustrum;

import com.lapissea.opengl.program.util.Quat4M;
import com.lapissea.opengl.program.util.math.vec.Vec3f;

public interface IFrustrumShape{
	
	boolean isVisibleAt(Vec3f pos, Frustum frustum);
	
	boolean isVisibleAt(float x, float y, float z, Frustum frustrum);
	
	default IFrustrumShape withTransform(Vec3f scale, Quat4M rot){
		return withTransform(scale.x(), scale.z(), scale.y(), rot);
	}
	
	IFrustrumShape withTransform(float x, float y, float z, Quat4M rot);
	
}
