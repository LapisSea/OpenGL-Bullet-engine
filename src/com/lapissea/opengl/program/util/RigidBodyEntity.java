package com.lapissea.opengl.program.util;

import javax.vecmath.Vector3f;

import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.MotionState;
import com.bulletphysics.linearmath.Transform;
import com.lapissea.opengl.program.game.entity.Entity;

public class RigidBodyEntity extends RigidBody{
	
	private static final Vector3f	NO_INERT=new Vector3f(),AABB_MIN=new Vector3f(),AABB_MAX=new Vector3f();
	private static final Transform	NO_TRANS=new Transform();
	
	private static Vector3f calcInert(float mass, CollisionShape collisionShape, Vector3f localInertia){
		if(localInertia!=null) return localInertia;
		Vector3f inert=new Vector3f();
		collisionShape.calculateLocalInertia(mass, inert);
		return inert;
	}
	
	public RigidBodyEntity antiTunnel(){
		NO_TRANS.setIdentity();
		getCollisionShape().getAabb(NO_TRANS, AABB_MIN, AABB_MAX);
		AABB_MAX.sub(AABB_MIN);
		setCcdMotionThreshold(AABB_MAX.length()/3);
		return this;
	}
	
	public RigidBodyEntity(Entity parent, RigidBodyConstructionInfo constructionInfo){
		super(constructionInfo);
		setUserPointer(parent);
	}
	
	public RigidBodyEntity(Entity parent, float mass, MotionState motionState, CollisionShape collisionShape, Vector3f localInertia, float friction, float restitution){
		this(parent, mass, motionState, collisionShape, localInertia);
		setFriction(friction);
		setRestitution(restitution);
		
	}
	
	public RigidBodyEntity(Entity parent, float mass, MotionState motionState, CollisionShape collisionShape, Vector3f localInertia){
		super(mass, motionState, collisionShape, calcInert(mass, collisionShape, localInertia));
		setUserPointer(parent);
	}
	
	public RigidBodyEntity(Entity parent, float mass, MotionState motionState, CollisionShape collisionShape){
		super(mass, motionState, collisionShape, NO_INERT);
		setUserPointer(parent);
	}
	
	@Override
	@Deprecated
	public void setUserPointer(Object userObjectPointer){
		super.setUserPointer(userObjectPointer);
	}
	
	public void setUserPointer(Entity userObjectPointer){
		super.setUserPointer(userObjectPointer);
	}
	
	@Override
	public Entity getUserPointer(){
		return (Entity)super.getUserPointer();
	}
}
