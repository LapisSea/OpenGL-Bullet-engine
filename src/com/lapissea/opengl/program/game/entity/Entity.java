package com.lapissea.opengl.program.game.entity;

import com.lapissea.opengl.abstr.opengl.assets.IModel;
import com.lapissea.opengl.abstr.opengl.events.Renderable;
import com.lapissea.opengl.program.game.world.World;
import com.lapissea.opengl.program.rendering.ModelInWorld;
import com.lapissea.opengl.program.rendering.gl.Renderer;
import com.lapissea.opengl.program.rendering.gl.shader.Shaders;
import com.lapissea.opengl.program.util.Quat4M;
import com.lapissea.opengl.program.util.math.vec.Vec3f;

public abstract class Entity implements Renderable,ModelInWorld{

	protected static final Vec3f POS=new Vec3f(),SCAL=new Vec3f();
	protected static final Quat4M ROT=new Quat4M();
	
	public IModel		model;
	public final Vec3f	pos,scale;
	public final Quat4M rot;
	private boolean		dead;
	public final World	world;
	
	public Entity(World world, IModel model, Vec3f pos){
		this.model=model;
		this.pos=initPos(pos);
		this.world=world;
		rot=initRot();
		scale=initScale();
	}
	
	
	protected Vec3f initPos(Vec3f pos){
		return pos;
	}
	protected Quat4M initRot(){
		return new Quat4M();
	}
	protected Vec3f initScale(){
		return new Vec3f(1,1,1);
	}
	
	public void kill(){
		dead=true;
		world.markDeadDirty();
	}
	
	public boolean isDead(){
		return dead;
	}
	
	@Override
	public Quat4M getModelRot(){
		ROT.set(rot);
		return ROT;
	}
	
	@Override
	public Vec3f getModelPos(){
		return POS.set(pos);
	}
	
	@Override
	public Vec3f getModelScale(){
		return SCAL.set(scale);
	}
	
	@Override
	public IModel getModel(){
		return model;
	}
	
	@Override
	public void render(){
		Renderer r=getRenderer();
		
		r.notifyEntityRender();
		if(!model.isLoaded()||!model.getFrustrumShape().withScale(scale).isVisibleAt(pos, r.frustrum)) return;
		r.notifyEntityActualRender();
		
		Shaders.ENTITY.renderSingle(this);
	}
}
