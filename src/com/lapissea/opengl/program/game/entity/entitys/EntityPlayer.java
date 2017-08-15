package com.lapissea.opengl.program.game.entity.entitys;

import org.lwjgl.util.vector.Matrix4f;

import com.lapissea.opengl.program.core.Game;
import com.lapissea.opengl.program.game.entity.EntityUpd;
import com.lapissea.opengl.program.game.world.World;
import com.lapissea.opengl.program.rendering.frustrum.FrustrumCube;
import com.lapissea.opengl.program.rendering.gl.model.ObjModelLoader;
import com.lapissea.opengl.program.util.math.MatrixUtil;
import com.lapissea.opengl.program.util.math.vec.Quat4;
import com.lapissea.opengl.program.util.math.vec.Vec3f;
import com.lapissea.opengl.window.assets.IModel;

public class EntityPlayer extends EntityUpd{
	
	private static IModel MODEL;
	
	private static IModel getModel0(){
		if(MODEL==null){
			MODEL=ObjModelLoader.loadAndBuild("fox");
		}
		return MODEL;
	}
	
	public EntityPlayer(World world, Vec3f pos){
		super(world, getModel0(), pos);
		//pos.set(1, 0, 0);
//		scale.set(0.3F, 0.3F, 0.3F);
	}
	
	@Override
	public void update(){
		updatePrevs();
		pos.y(65);
		if(model.isLoaded()&&!model.getFrustrumShape().withTransform(scale, rot).isVisibleAt(pos, Game.get().renderer.frustrum)){
			Vec3f vec=new Vec3f(0, -((FrustrumCube)model.getFrustrumShape()).getSizeY()*scale.y()*1.4F, 0);
			rot.rotate(vec);
			
			vec.add(Game.get().renderer.getCamera().pos).sub(pos).normalize().toAngular();
			Matrix4f mat=new Matrix4f();
			MatrixUtil.rotateX(mat, vec.x());
			MatrixUtil.rotateY(mat, vec.y());
			Quat4.interpolate(rot, rot, new Quat4().fromMatrix(mat), 0.2F);
		}
		//		rot.set(Game.get().renderer.getCamera().activeRotQuat);
		//this.pos.add(RandUtil.RF(0.1), RandUtil.RF(0.1), RandUtil.RF(0.1));
	}
	
	@Override
	public void render(){
		//		MODEL.defaultMaterial.reflectivity=10;
		//		MODEL.defaultMaterial.shineDamper=100;
		super.render();
	}
}
