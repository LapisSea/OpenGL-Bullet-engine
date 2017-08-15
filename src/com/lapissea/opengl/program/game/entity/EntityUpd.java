package com.lapissea.opengl.program.game.entity;

import com.lapissea.opengl.program.game.events.Updateable;
import com.lapissea.opengl.program.game.physics.IPhysicsObjPointer;
import com.lapissea.opengl.program.game.physics.jbullet.PhysicsObjJBullet;
import com.lapissea.opengl.program.game.world.World;
import com.lapissea.opengl.program.util.math.PartialTick;
import com.lapissea.opengl.program.util.math.vec.Quat4;
import com.lapissea.opengl.program.util.math.vec.Vec3f;
import com.lapissea.opengl.window.assets.IModel;

public abstract class EntityUpd extends Entity implements Updateable,IPhysicsObjPointer<PhysicsObjJBullet>{
	
	public final Vec3f				prevPos		=new Vec3f(),prevScale=new Vec3f(1, 1, 1);
	public final Quat4				prevRot		=new Quat4();
	private final PhysicsObjJBullet	physicsObj	=new PhysicsObjJBullet();
	
	public EntityUpd(World world, IModel model, Vec3f pos){
		super(world, model, pos);
	}
	
	public void updatePrevs(){
		prevPos.set(pos);
		prevRot.set(rot);
		prevScale.set(scale);
	}
	
	@Override
	public Quat4 getModelRot(){
		return PartialTick.calc(ROT, prevRot, rot);
	}
	
	@Override
	public Vec3f getModelPos(){
		return PartialTick.calc(POS, prevPos, pos);
	}
	
	@Override
	public Vec3f getModelScale(){
		return PartialTick.calc(SCAL, prevScale, scale);
	}
	
	@Override
	public PhysicsObjJBullet getPhysicsObj(){
		return physicsObj;
	}
}
