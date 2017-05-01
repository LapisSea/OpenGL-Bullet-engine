package com.lapissea.opengl.abstr.opengl.frustrum;

import com.lapissea.opengl.program.util.math.vec.Vec3f;

public interface IFrustrumShape{
	boolean isVisibleAt(Vec3f pos, Frustum frustum);
	boolean isVisibleAt(float x, float y, float z, Frustum frustrum);

	IFrustrumShape withScale(Vec3f scale);
	IFrustrumShape withScale(float x, float y, float z);

}
