package com.lapissea.opengl.program.game.physics;

import com.lapissea.opengl.window.api.util.IVec3f;

public interface IPhysicsWorld<PhysicsObj extends IPhysicsObj>{
	
	void addRigidBody(PhysicsObj obj);
	void removeRigidBody(PhysicsObj obj);
	default void setGravity(IVec3f vec) {
		setGravity(vec.x(), vec.y(), vec.z());
	}
	void setGravity(float x, float y, float z);
	
	void updatePhysics(float time);
	
	PhysicsObj rayTrace(IVec3f start,IVec3f end,IVec3f dest);
}
