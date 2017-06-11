package com.lapissea.opengl.program.game.entity.entitys;

import com.lapissea.opengl.program.game.entity.Entity;
import com.lapissea.opengl.program.game.world.World;
import com.lapissea.opengl.program.util.math.vec.Vec3f;
import com.lapissea.opengl.window.assets.IModel;

public class EntityStatic extends Entity{

	public EntityStatic(World world,IModel model, Vec3f pos){
		super(world,model,pos);
	}
}
