package com.lapissea.opengl.game.physics.jbullet;

import javax.vecmath.Vector3f;

import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.Transform;
import com.lapissea.opengl.game.physics.IPhysicsObj;
import com.lapissea.opengl.util.math.vec.Quat4;
import com.lapissea.opengl.window.api.util.vec.IVec3fR;
import com.lapissea.opengl.window.api.util.vec.IVec3fW;

public class PhysicsObjJBullet implements IPhysicsObj{
	
	public RigidBody	body;
	IVec3fW				pos;
	Quat4				rot;
	private Vector3f	force	=new Vector3f();
	
	public PhysicsObjJBullet(){}
	
	public PhysicsObjJBullet(float mass, Transform initalTransform, CollisionShape collisionShape, IVec3fR localInertia){
		init(mass, initalTransform, collisionShape, localInertia);
	}
	
	public void init(float mass, Transform initalTransform, CollisionShape collisionShape, IVec3fR localInertia){
		body=new RigidBody(mass, new MotionStateM(this, initalTransform), collisionShape, new Vector3f(localInertia.x(), localInertia.y(), localInertia.z()));
		body.setUserPointer(this);
	}
	
	@Override
	public void hookPos(IVec3fW pos){
		this.pos=pos;
	}
	
	@Override
	public void hookRot(Quat4 rot){
		this.rot=rot;
	}
	
	@Override
	public void applyForce(float x, float y, float z){
		force.set(x, y, z);
		body.activate();
		body.applyCentralForce(force);
	}
	
}
