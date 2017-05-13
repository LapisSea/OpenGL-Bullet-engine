package com.lapissea.opengl.program.game.entity.entitys;

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import com.bulletphysics.collision.dispatch.CollisionWorld.ClosestRayResultCallback;
import com.bulletphysics.collision.shapes.BvhTriangleMeshShape;
import com.lapissea.opengl.program.game.entity.EntityUpd;
import com.lapissea.opengl.program.game.world.World;
import com.lapissea.opengl.program.opengl.assets.Model;
import com.lapissea.opengl.program.rendering.gl.model.ModelLoader;
import com.lapissea.opengl.program.rendering.gl.model.ObjModelLoader;
import com.lapissea.opengl.program.rendering.gl.model.ObjModelLoader.ModelData;
import com.lapissea.opengl.program.util.MotionStateM;
import com.lapissea.opengl.program.util.RandUtil;
import com.lapissea.opengl.program.util.RigidBodyEntity;
import com.lapissea.opengl.program.util.TransformOgl;
import com.lapissea.opengl.program.util.UtilM;
import com.lapissea.opengl.program.util.math.vec.Vec3f;

public class EntityTree extends EntityUpd{
	
	public static Model		MODEL;
	public static float[]	VERT;
	
	private static Model getModel0(){
		if(MODEL==null){
			ModelData data=ObjModelLoader.load("TreeSmall/2");
			VERT=data.getVert();
			MODEL=ModelLoader.buildModel(data);
		}
		return MODEL;
	}
	
	float				base=5+RandUtil.RF(5);
	final TransformOgl	transform;
	
	public EntityTree(World world, Vec3f pos){
		super(world, getModel0(), pos);
		scale.set(base, base, base);
		ClosestRayResultCallback v=new ClosestRayResultCallback(new Vector3f(pos.x, 50, pos.z), new Vector3f(pos.x, -50, pos.z));
		world.bulletWorld.rayTest(v.rayFromWorld, v.rayToWorld, v);
		this.pos.x=v.hitPointWorld.x;
		this.pos.y=v.hitPointWorld.y;
		this.pos.z=v.hitPointWorld.z;
		transform=new TransformOgl(new Matrix4f(new Quat4f(0, 0, 0, 1), new Vector3f(this.pos.x, this.pos.y, this.pos.z), 1));
		
		BvhTriangleMeshShape shape=new BvhTriangleMeshShape(UtilM.verticesToPhysicsMesh(VERT), true);
		shape.setLocalScaling(new Vector3f(base, base, base));
		
		physicsBody=new RigidBodyEntity(this, 0, new MotionStateM(transform), shape, new Vector3f());
		rot.set(new Vec3f(0,1,0));
		updatePrevs();
	}
	
	@Override
	public void render(){
		model.getMaterial(1).setJelly(0.1F);
		//		rot.set(new Vec3f((float)(world.time()/100D),(float)(world.time()/130D),0));
		//		LogUtil.println(model.getFrustrumShape().withScale(scale).isVisibleAt(pos, getRenderer().frustrum));
		super.render();
	}
	
	@Override
	public void update(){}
}
