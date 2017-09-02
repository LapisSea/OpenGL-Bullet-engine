package com.lapissea.opengl.game.physics;

import com.lapissea.opengl.util.math.vec.Quat4;
import com.lapissea.opengl.window.api.util.vec.IVec3fR;
import com.lapissea.opengl.window.api.util.vec.IVec3fW;

public interface IPhysicsObj{
	
	void hookPos(IVec3fW pos);
	
	void hookRot(Quat4 rot);
	
	default void applyForce(IVec3fR force){
		applyForce(force.x(), force.y(), force.z());
	}
	
	void applyForce(float x, float y, float z);
	
}
