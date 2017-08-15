package com.lapissea.opengl.program.game.physics.jbullet;

import javax.vecmath.Vector3f;

import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.CollisionConfiguration;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.CollisionWorld.ClosestRayResultCallback;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.constraintsolver.ConstraintSolver;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.lapissea.opengl.program.game.physics.IPhysicsWorld;
import com.lapissea.opengl.window.api.util.IVec3f;

public class PhysicsWorldJbullet implements IPhysicsWorld<PhysicsObjJBullet>{
	
	private DynamicsWorld	bulletWorld;
	private Vector3f		gravSet	=new Vector3f();
	
	public PhysicsWorldJbullet(){
		BroadphaseInterface broadphase=new DbvtBroadphase();
		CollisionConfiguration collisionConfiguration=new DefaultCollisionConfiguration();
		CollisionDispatcher dispatcher=new CollisionDispatcher(collisionConfiguration);
		ConstraintSolver constraintSolver=new SequentialImpulseConstraintSolver();
		
		bulletWorld=new DiscreteDynamicsWorld(dispatcher, broadphase, constraintSolver, collisionConfiguration);
		setGravity(0, -9.81F, 0);
	}
	
	@Override
	public void addRigidBody(PhysicsObjJBullet obj){
		if(obj.body!=null) bulletWorld.addRigidBody(obj.body);
	}
	
	@Override
	public void removeRigidBody(PhysicsObjJBullet obj){
		bulletWorld.removeRigidBody(obj.body);
	}
	
	@Override
	public void setGravity(float x, float y, float z){
		gravSet.set(x, y, z);
		bulletWorld.setGravity(gravSet);
	}
	
	@Override
	public void updatePhysics(float time){
		int steps=5;
		float step=time/steps;
		while(steps-->0)
			bulletWorld.stepSimulation(step, 1, step);
	}
	
	@Override
	public PhysicsObjJBullet rayTrace(IVec3f start, IVec3f end, IVec3f dest){
		ClosestRayResultCallback v=new ClosestRayResultCallback(new Vector3f(start.x(), start.y(), start.z()), new Vector3f(end.x(), end.y(), end.z()));
		bulletWorld.rayTest(v.rayFromWorld, v.rayToWorld, v);
		if(!v.hasHit()) return null;
		
		if(dest!=null){
			dest.x(v.hitPointWorld.x);
			dest.y(v.hitPointWorld.y);
			dest.z(v.hitPointWorld.z);
		}
		
		return (PhysicsObjJBullet)v.collisionObject.getUserPointer();
	}
	
}
