package com.lapissea.opengl.program.game.entity.entitys;

import com.lapissea.opengl.program.game.entity.Entity;
import com.lapissea.opengl.program.game.world.World;
import com.lapissea.opengl.program.rendering.gl.model.Model;
import com.lapissea.opengl.program.util.math.vec.Vec3f;

public class EntityStatic extends Entity{

	public EntityStatic(World world,Model model, Vec3f pos){
		super(world,model,pos);
	}
}
