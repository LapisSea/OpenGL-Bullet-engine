package com.lapissea.opengl.program.game.physics.jbullet;

import javax.vecmath.Vector3f;

import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.Transform;
import com.lapissea.opengl.program.game.physics.IPhysicsObj;
import com.lapissea.opengl.program.util.math.vec.Quat4;
import com.lapissea.opengl.window.api.util.IVec3f;

public class PhysicsObjJBullet implements IPhysicsObj{
	
	public RigidBody	body;
	IVec3f				pos;
	Quat4				rot;
	private Vector3f	force	=new Vector3f();
	
	public PhysicsObjJBullet(){}
	
	public PhysicsObjJBullet(float mass, Transform initalTransform, CollisionShape collisionShape, IVec3f localInertia){
		init(mass, initalTransform, collisionShape, localInertia);
	}
	
	public void init(float mass, Transform initalTransform, CollisionShape collisionShape, IVec3f localInertia){
		body=new RigidBody(mass, new MotionStateM(this, initalTransform), collisionShape, new Vector3f(localInertia.x(), localInertia.y(), localInertia.z()));
		body.setUserPointer(this);
	}
	
	@Override
	public void hookPos(IVec3f pos){
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