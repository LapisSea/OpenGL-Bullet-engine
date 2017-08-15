package com.lapissea.opengl.program.game.physics.jbullet;

import com.bulletphysics.linearmath.MotionState;
import com.bulletphysics.linearmath.Transform;

class MotionStateM extends MotionState{
	
	/** Current interpolated world transform, used to draw object. */
	public final Transform graphicsWorldTrans;
	
	/**
	 * Center of mass offset transform, used to adjust graphics world
	 * transform.
	 */
	public final Transform centerOfMassOffset=new Transform();
	
	public final Transform startWorldTrans=new Transform();
	
	
	/**
	 * Creates a new DefaultMotionState with initial world transform and
	 * center
	 * of mass offset transform set to identity.
	 */
	public MotionStateM(PhysicsObjJBullet parent,Transform startTrans){
		graphicsWorldTrans=new TransformM(parent);
		graphicsWorldTrans.set(startTrans);
		centerOfMassOffset.setIdentity();
		startWorldTrans.set(startTrans);
	}
	
	@Override
	public Transform getWorldTransform(Transform out){
		out.inverse(centerOfMassOffset);
		out.mul(graphicsWorldTrans);
		return out;
	}
	
	@Override
	public void setWorldTransform(Transform centerOfMassWorldTrans){
		graphicsWorldTrans.set(centerOfMassWorldTrans);
		graphicsWorldTrans.mul(centerOfMassOffset);
	}
	
}