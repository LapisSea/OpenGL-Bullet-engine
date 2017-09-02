package com.lapissea.opengl.game.physics;

import com.lapissea.opengl.window.api.util.vec.IVec3fR;
import com.lapissea.opengl.window.api.util.vec.IVec3fW;

public interface IPhysicsWorld<PhysicsObj extends IPhysicsObj>{
	
	void addRigidBody(PhysicsObj obj);
	
	void removeRigidBody(PhysicsObj obj);
	
	default void setGravity(IVec3fR vec){
		setGravity(vec.x(), vec.y(), vec.z());
	}
	
	void setGravity(float x, float y, float z);
	
	void updatePhysics(float time);
	
	PhysicsObj rayTrace(IVec3fR start, IVec3fR end, IVec3fW dest);
}
