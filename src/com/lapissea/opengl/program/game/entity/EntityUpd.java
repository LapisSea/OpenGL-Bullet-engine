package com.lapissea.opengl.program.game.entity;

import com.bulletphysics.dynamics.RigidBody;
import com.lapissea.opengl.program.game.world.World;
import com.lapissea.opengl.program.interfaces.Updateable;
import com.lapissea.opengl.program.rendering.gl.Renderer;
import com.lapissea.opengl.program.rendering.gl.model.Model;
import com.lapissea.opengl.program.rendering.gl.shader.Shaders;
import com.lapissea.opengl.program.util.Quat4M;
import com.lapissea.opengl.program.util.RigidBodyEntity;
import com.lapissea.opengl.program.util.math.PartialTick;
import com.lapissea.opengl.program.util.math.vec.Vec3f;

public abstract class EntityUpd extends Entity implements Updateable{
	
	public final Vec3f prevPos=new Vec3f(),prevScale=new Vec3f(1, 1, 1);
	public final Quat4M prevRot=new Quat4M();
	
	protected RigidBodyEntity physicsBody;
	
	public EntityUpd(World world, Model model, Vec3f pos){
		super(world, model, pos);
	}
	
	public void updatePrevs(){
		prevPos.set(pos);
		prevRot.set(rot);
		prevScale.set(scale);
	}
	
	@Override
	public Quat4M getModelRot(){
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
	public void render(){
		Renderer r=getRenderer();
		
		r.notifyEntityRender();
		if(!r.frustrum.sphere(pos, model.rad()*getModelScale().max())) return;
		r.notifyEntityActualRender();
		
		Shaders.ENTITY.renderBatched(this);
	}
	
	public RigidBody getPhysicsBody(){
		return physicsBody;
	}
}
