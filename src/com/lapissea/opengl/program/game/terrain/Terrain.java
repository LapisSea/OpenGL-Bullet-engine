package com.lapissea.opengl.program.game.terrain;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import org.lwjgl.util.vector.Matrix4f;

import com.bulletphysics.collision.shapes.BvhTriangleMeshShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.MotionState;
import com.bulletphysics.linearmath.Transform;
import com.lapissea.opengl.abstr.opengl.assets.IModel;
import com.lapissea.opengl.program.rendering.ModelInWorld;
import com.lapissea.opengl.program.rendering.gl.model.ModelLoader;
import com.lapissea.opengl.program.util.Quat4M;
import com.lapissea.opengl.program.util.UtilM;
import com.lapissea.opengl.program.util.math.SimplexNoise;
import com.lapissea.opengl.program.util.math.vec.Vec3f;

import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.floats.FloatList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;

public class Terrain implements ModelInWorld{
	
	protected static final Matrix4f	TRANSFORM	=new Matrix4f();
	protected static final Vec3f	POS			=new Vec3f();
	
	public static final int		SIZE	=32,RESOLUTION=16;
	public static final float	WORLD_H	=5;
	
	public final float	x,z;
	public IModel		model	=ModelLoader.EMPTY_MODEL;
	public RigidBody	chunkBody;
	
	protected final Matrix4f mat=new Matrix4f();
	
	public final double seed=Math.random();
	
	public Terrain(int gridX, int gridZ){
		
		x=gridX*SIZE;
		z=gridZ*SIZE;
		mat.translate(new Vec3f(x, -2, z));
		model=generateModel(gridX, gridZ);
		model.getMaterial(0).setShineDamper(50).setReflectivity(0.2F).setDiffuse(0.2F, 1, 0.25F, 1).setSpecular(1, 1, 1, 1);
	}
	
	private IModel generateModel(int gridX, int gridZ){
		int r1=RESOLUTION+1;
		
		FloatList vertices=new FloatArrayList(),uvs=new FloatArrayList();
		
		IntList indices=new IntArrayList();
		
		float mul=SIZE/(float)RESOLUTION;
		for(int z=0;z<r1;z++){
			for(int x=0;x<r1;x++){
				double h=SimplexNoise.noise((x/(float)RESOLUTION+gridX), (z/(float)RESOLUTION+gridZ));
				h-=0.5;
				
				h+=(SimplexNoise.noise(x/(float)RESOLUTION+gridX, z/(float)RESOLUTION+gridZ)-0.5)/10;

				vertices.add(x*mul);
				vertices.add((float)(h*WORLD_H));
				vertices.add(z*mul-SIZE/2F);
				
				uvs.add(x/(float)RESOLUTION);
				uvs.add(z/(float)RESOLUTION);
			}
		}
		
		for(int z=0;z<RESOLUTION;z++){
			for(int x=0;x<RESOLUTION;x++){
				
				int x0=x,z0=z,x1=x+1,z1=z+1;
				
				int p0=x0+z0*r1;
				int p1=x1+z0*r1;
				int p2=x0+z1*r1;
				int p3=x1+z1*r1;
				
				indices.add(p3);
				indices.add(p1);
				indices.add(p0);
				
				indices.add(p0);
				indices.add(p2);
				indices.add(p3);
			}
		}
		
		float[] vts=vertices.toFloatArray();
		int[] ids=indices.toIntArray();
		
		BvhTriangleMeshShape trimeshshape=new BvhTriangleMeshShape(UtilM.verticesToPhysicsMesh(vts, ids), true);
		MotionState ballMotionState=new DefaultMotionState(new Transform(new javax.vecmath.Matrix4f(new Quat4f(0, 0, 0, 1), new Vector3f(x, 0, z), 1)));
		RigidBodyConstructionInfo ballConstructionInfo=new RigidBodyConstructionInfo(0, ballMotionState, trimeshshape, new Vector3f());
		ballConstructionInfo.restitution=0F;
		chunkBody=new RigidBody(ballConstructionInfo);
		chunkBody.setUserPointer(this);
		
		return ModelLoader.buildModel("Gen_Floor-"+gridX+"_"+gridZ, false, "vertices", vts, "uvs", uvs.toFloatArray(), "indices", ids, "genNormals", true);
	}
	
	@Override
	public IModel getModel(){
		return model;
	}
	
	@Override
	public Matrix4f getTransform(){
		model.getMaterial(0).setShineDamper(100).setReflectivity(2F);
		TRANSFORM.setIdentity();
		POS.x=x;
		POS.y=0;
		POS.z=z;
		TRANSFORM.translate(POS);
		return TRANSFORM;
	}
	
	@Override
	public Vec3f getModelScale(){
		throw new UnsupportedOperationException();
	}
	
	@Override
	public Quat4M getModelRot(){
		throw new UnsupportedOperationException();
	}
	
	@Override
	public Vec3f getModelPos(){
		throw new UnsupportedOperationException();
	}
}
