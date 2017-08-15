package com.lapissea.opengl.program.game.physics;

import com.lapissea.opengl.program.util.math.vec.Quat4;
import com.lapissea.opengl.window.api.util.IVec3f;

public interface IPhysicsObj{
	
	void hookPos(IVec3f pos);
	
	void hookRot(Quat4 rot);
	
	default void applyForce(IVec3f force){
		applyForce(force.x(), force.y(), force.z());
	}
	
	void applyForce(float x, float y, float z);
	
}
