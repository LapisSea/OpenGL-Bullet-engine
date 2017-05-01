package com.lapissea.opengl.program.game.entity.entitys;

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import com.bulletphysics.collision.dispatch.CollisionWorld.ClosestRayResultCallback;
import com.bulletphysics.collision.shapes.BvhTriangleMeshShape;
import com.lapissea.opengl.program.game.entity.EntityUpd;
import com.lapissea.opengl.program.game.world.World;
import com.lapissea.opengl.program.rendering.gl.model.Model;
import com.lapissea.opengl.program.rendering.gl.model.ModelLoader;
import com.lapissea.opengl.program.rendering.gl.model.ObjModelLoader;
import com.lapissea.opengl.program.rendering.gl.model.ObjModelLoader.ModelData;
import com.lapissea.opengl.program.util.MotionStateM;
import com.lapissea.opengl.program.util.Quat4M;
import com.lapissea.opengl.program.util.RandUtil;
import com.lapissea.opengl.program.util.RigidBodyEntity;
import com.lapissea.opengl.program.util.TransformOgl;
import com.lapissea.opengl.program.util.UtilM;
import com.lapissea.opengl.program.util.math.vec.Vec3f;

public class EntityTree extends EntityUpd{
	
	public static Model						MODEL;
	public static float[] VERT;
	
	private static Model getModel0(){
		if(MODEL==null){
			ModelData data=ObjModelLoader.load("TreeSmall/2");
//			for(int i=0;i<data.vertecies.size();i++){
//				data.vertecies.get(i).mul(5);
//			}
			VERT=data.getVert();
			MODEL=ModelLoader.buildModel(data);
			MODEL.createMaterial().getDiffuse().set(0x452319);
			MODEL.getMaterial(0).getDiffuse().set(0x0aCA0c);
			
//			for(int i=0;i<data.vertecies.size();i++){
//				data.vertecies.get(i).mul(1/5F);
//			}
		}
		return MODEL;
	}
	
	public final Quat4M rotSpeed=new Quat4M();
	
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
		transform=new TransformOgl(new Matrix4f(new Quat4f(0, 0, 0, 1), new Vector3f(pos.x, pos.y, pos.z), 1));
		BvhTriangleMeshShape shape=new BvhTriangleMeshShape(UtilM.verticesToPhysicsMesh(VERT), true);
		shape.setLocalScaling(new Vector3f(base,base,base));
		
		physicsBody=new RigidBodyEntity(this,0, new MotionStateM(transform), shape, new Vector3f());
	}
	
	@Override
	public void update(){
		updatePrevs();
//		rotSpeed.x+=RandUtil.CRF(0.01);
//		rotSpeed.y+=RandUtil.CRF(0.01);
//		rotSpeed.z+=RandUtil.CRF(0.01);
//		rotSpeed.w+=RandUtil.CRF(0.01);
//		rotSpeed.mul(0.7F);
		
//		transform.getRotation(rot);
//		rot.add(rotSpeed);
		//rot.mul(0.6F);
//		transform.setRotation(rot);
//		physicsBody.activate();
//		if(RandUtil.RB(0.1)){
//			scale.set(base+RandUtil.RF(), base+RandUtil.RF(), base+RandUtil.RF());
//			physicsBody.getCollisionShape().setLocalScaling(new Vector3f(scale.x,scale.y,scale.z));
//		}
		
	}

	
}
