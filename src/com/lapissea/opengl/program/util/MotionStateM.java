package com.lapissea.opengl.program.util;

import com.bulletphysics.linearmath.MotionState;
import com.bulletphysics.linearmath.Transform;

public class MotionStateM extends MotionState{
	
	/** Current interpolated world transform, used to draw object. */
	public final Transform graphicsWorldTrans=new Transform();
	
	/**
	 * Center of mass offset transform, used to adjust graphics world transform.
	 */
	public final Transform centerOfMassOffset=new Transform();
	
	/** Initial world transform. */
	public final Transform startWorldTrans=new Transform();
	
	private final Transform hook;
	
	/**
	 * Creates a new DefaultMotionState with initial world transform and center
	 * of mass offset transform set to identity.
	 */
	public MotionStateM(Transform startTrans){
		this.graphicsWorldTrans.set(startTrans);
		centerOfMassOffset.setIdentity();
		this.startWorldTrans.set(startTrans);
		hook=startTrans;
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
		hook.set(graphicsWorldTrans);
	}
	
}
