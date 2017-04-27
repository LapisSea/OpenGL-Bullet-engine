package com.lapissea.opengl.program.game.entity.entitys;

import com.lapissea.opengl.program.game.entity.EntityUpd;
import com.lapissea.opengl.program.game.world.World;
import com.lapissea.opengl.program.rendering.gl.model.Model;
import com.lapissea.opengl.program.rendering.gl.model.ObjModelLoader;
import com.lapissea.opengl.program.util.math.vec.Vec3f;

public class EntityPlayer extends EntityUpd{
	
	private static Model MODEL;
	
	private static Model getModel0(){
		if(MODEL==null){
			MODEL=ObjModelLoader.loadAndBuild("dragon");
		}
		return MODEL;
	}
	
	public EntityPlayer(World world, Vec3f pos){
		super(world, getModel0(), pos);
	}
	
	@Override
	public void update(){
		updatePrevs();
		//this.pos.add(RandUtil.RF(0.1), RandUtil.RF(0.1), RandUtil.RF(0.1));
	}
	@Override
	public void render(){
//		MODEL.defaultMaterial.reflectivity=10;
//		MODEL.defaultMaterial.shineDamper=100;
		super.render();
	}
}
