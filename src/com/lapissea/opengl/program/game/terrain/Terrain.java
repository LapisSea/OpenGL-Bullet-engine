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
import com.lapissea.opengl.program.rendering.ModelInWorld;
import com.lapissea.opengl.program.rendering.gl.model.Model;
import com.lapissea.opengl.program.rendering.gl.model.ModelLoader;
import com.lapissea.opengl.program.rendering.gl.texture.ITexture;
import com.lapissea.opengl.program.util.Quat4M;
import com.lapissea.opengl.program.util.UtilM;
import com.lapissea.opengl.program.util.color.ColorM;
import com.lapissea.opengl.program.util.math.SimplexNoise;
import com.lapissea.opengl.program.util.math.vec.Vec3f;

import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.floats.FloatList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;

public class Terrain implements ModelInWorld{
	
	protected static final Matrix4f	TRANSFORM	=new Matrix4f();
	protected static final Vec3f	POS			=new Vec3f();
	
	private static final int SIZE=32,RESOLUTION=32;
	
	public final float	x,z;
	public Model		model;
	public RigidBody	chunkBody;
	
	protected final Matrix4f mat=new Matrix4f();
	
	public final double seed=Math.random();
	
	public Terrain(int gridX, int gridZ, ITexture texture){
		
		x=gridX*SIZE;
		z=gridZ*SIZE;
		mat.translate(new Vec3f(x, -2, z));
		model=generateModel(gridX, gridZ, texture);
		model.defaultMaterial.shineDamper=50;
		model.defaultMaterial.reflectivity=0.2F;
	}
	
	private Model generateModel(int gridX, int gridZ, ITexture texture){
		int r1=RESOLUTION+1;
		
		FloatList vertices=new FloatArrayList(),uvs=new FloatArrayList();
		
		IntList indices=new IntArrayList();
		
		float mul=SIZE/(float)RESOLUTION;
		double div=SIZE;
		for(int z=0;z<r1;z++){
			for(int x=0;x<r1;x++){
				vertices.add(x*mul);
				float h=(float)SimplexNoise.noise((x+gridX*RESOLUTION)/div, (z+gridZ*RESOLUTION)/div);
				h-=0.5;
				vertices.add(h*4+(float)(SimplexNoise.noise(x+gridX*RESOLUTION, z+gridZ*RESOLUTION)*0.1));
				vertices.add(z*mul);
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
		
		BvhTriangleMeshShape trimeshshape=new BvhTriangleMeshShape(UtilM.verticesToPhysicsMesh(vts,ids), true);
		MotionState ballMotionState=new DefaultMotionState(new Transform(new javax.vecmath.Matrix4f(new Quat4f(0, 0, 0, 1), new Vector3f(x, 0, z), 1)));
		RigidBodyConstructionInfo ballConstructionInfo=new RigidBodyConstructionInfo(0, ballMotionState, trimeshshape, new Vector3f());
		ballConstructionInfo.restitution=0F;
		chunkBody=new RigidBody(ballConstructionInfo);
		chunkBody.setUserPointer(this);
		
		return ModelLoader.buildModel("Gen_Floor-"+gridX+"_"+gridZ, false, "vertices", vts, "uvs", uvs.toFloatArray(), "indices", ids, "genNormals", true, "textures", texture);
	}
	
	@Override
	public Model getModel(){
		return model;
	}
	
	@Override
	public Matrix4f getTransform(){
		
		model.defaultMaterial.reflectivity=1;
		model.defaultMaterial.diffuse=new ColorM(0.2,1,0.25);
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
