package com.lapissea.opengl.program.game.entity.entitys;

import javax.vecmath.Vector3f;

import com.bulletphysics.collision.dispatch.CollisionWorld.ClosestRayResultCallback;
import com.lapissea.opengl.abstr.opengl.assets.IModel;
import com.lapissea.opengl.program.game.entity.Entity;
import com.lapissea.opengl.program.game.world.World;
import com.lapissea.opengl.program.rendering.GLUtil;
import com.lapissea.opengl.program.rendering.gl.model.Model;
import com.lapissea.opengl.program.rendering.gl.model.ObjModelLoader;
import com.lapissea.opengl.program.util.RandUtil;
import com.lapissea.opengl.program.util.math.vec.Vec3f;

public class EntityGrass extends Entity{
	
	public static Model[] MODELS;
	
	private static class MDL extends Model{
		public MDL(String name){
			super(name);
		}
		@Override
		public IModel drawCall(){
			GLUtil.CULL_FACE.set(false);
			return super.drawCall();
		}
	}
	
	private static Model getModel0(int i){
		if(MODELS==null){
			MODELS=ObjModelLoader.loadAndBuildArr(MDL.class,"Grass");
		}
		return MODELS[i];
	}
	
	public EntityGrass(World world, Vec3f pos){
		super(world, getModel0(RandUtil.RI(3)), pos);
		ClosestRayResultCallback v=new ClosestRayResultCallback(new Vector3f(pos.x, 50, pos.z), new Vector3f(pos.x, -50, pos.z));
		world.bulletWorld.rayTest(v.rayFromWorld, v.rayToWorld, v);
		if(v.hasHit()){
			this.pos.x=v.hitPointWorld.x;
			this.pos.y=v.hitPointWorld.y;
			this.pos.z=v.hitPointWorld.z;
		}
		rot.set(new Vec3f(0, RandUtil.RF(Math.PI*2), RandUtil.CRF(0.2)));
		float s=0.4F+RandUtil.RF(0.3);
		scale.set(s, s, s);
	}
	
	@Override
	public void render(){
//		rot.set(new Vec3f(0F,0,0.2F));
		//model.getMaterial(0).setAmbient(1, 1, 1, 1);
		model.getMaterial(0).setDiffuse(0.2F, 1, 0.25F, 1);
		model.getMaterial(0).setLightTroughput(0.5F);
		model.getMaterial(0).setJelly(0);
		super.render();
	}
}
