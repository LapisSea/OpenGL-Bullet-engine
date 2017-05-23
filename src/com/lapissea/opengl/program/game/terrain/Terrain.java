package com.lapissea.opengl.program.game.terrain;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;

import com.bulletphysics.collision.shapes.BvhTriangleMeshShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.MotionState;
import com.bulletphysics.linearmath.Transform;
import com.lapissea.opengl.program.rendering.ModelTransformed;
import com.lapissea.opengl.program.rendering.gl.model.ModelLoader;
import com.lapissea.opengl.program.rendering.gl.model.ObjModelLoader;
import com.lapissea.opengl.program.rendering.gl.model.ObjModelLoader.ModelData;
import com.lapissea.opengl.program.util.Quat4M;
import com.lapissea.opengl.program.util.RandUtil;
import com.lapissea.opengl.program.util.UtilM;
import com.lapissea.opengl.program.util.math.SimplexNoise;
import com.lapissea.opengl.program.util.math.vec.Vec3f;
import com.lapissea.opengl.window.assets.IMaterial;
import com.lapissea.opengl.window.assets.IModel;

import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.floats.FloatList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;

public class Terrain implements ModelTransformed{
	
	protected static final Matrix4f	TRANSFORM	=new Matrix4f();
	protected static final Vec3f	POS			=new Vec3f();
	
	public static ModelData[] grass=ObjModelLoader.loadArr("Grass");
	
	public static final int		SIZE	=32,RESOLUTION=6;
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
		
		FloatList vertices=new FloatArrayList(),mats=new FloatArrayList();
		
		IntList indices=new IntArrayList();
		
		float mul=SIZE/(float)RESOLUTION;
		for(int z=0;z<r1;z++){
			for(int x=0;x<r1;x++){
				double h=SimplexNoise.noise((x/(float)RESOLUTION+gridX), (z/(float)RESOLUTION+gridZ));
				h-=0.5;
				
				h+=(SimplexNoise.noise(x/(float)RESOLUTION+gridX, z/(float)RESOLUTION+gridZ)-0.5)/10;
				
				vertices.add(x*mul);
				vertices.add((float)(h*WORLD_H));
				vertices.add(z*mul);
				mats.add(0);
				//				uvs.add(x/(float)RESOLUTION);
				//				uvs.add(z/(float)RESOLUTION);
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
		BvhTriangleMeshShape trimeshshape=new BvhTriangleMeshShape(UtilM.verticesToPhysicsMesh(vertices.toFloatArray(), indices.toIntArray()), true);
		MotionState ballMotionState=new DefaultMotionState(new Transform(new javax.vecmath.Matrix4f(new Quat4f(0, 0, 0, 1), new Vector3f(x, 0, z), 1)));
		RigidBodyConstructionInfo ballConstructionInfo=new RigidBodyConstructionInfo(0, ballMotionState, trimeshshape, new Vector3f());
		ballConstructionInfo.restitution=0F;
		chunkBody=new RigidBody(ballConstructionInfo);
		chunkBody.setUserPointer(this);
		
		Quat4M q=new Quat4M();
		Vec3f rot=new Vec3f(),vRot=new Vec3f();
		for(int i=0, j=100+RandUtil.RI(200);i<j;i++){
			float x=RandUtil.RF(mul*RESOLUTION);
			float z=RandUtil.RF(mul*RESOLUTION);
			float y;
			float scale=0.7F+RandUtil.RF(0.3);
			int xi=(int)Math.floor(x/mul);
			int zi=(int)Math.floor(z/mul);
			
			float y_00=vertices.getFloat((xi+zi*r1)*3+1);
			float y_10=vertices.getFloat((xi+1+zi*r1)*3+1);
			float y_01=vertices.getFloat((xi+(zi+1)*r1)*3+1);
			float y_11=vertices.getFloat((xi+1+(zi+1)*r1)*3+1);
			
			float xPerc=(x%mul)/mul;
			float zPerc=(z%mul)/mul;
			
			
			if(1-xPerc+zPerc>1){
				float y_0=y_00+(y_01-y_00)*zPerc;
				y=y_0+(y_11-y_0)*xPerc;
			}
			else{
				float y_1=y_10+(y_11-y_10)*zPerc;
				y=y_00+(y_1-y_00)*xPerc;
			}
			
			
			q.set(rot.setThis(0, RandUtil.RF(Math.PI*2), RandUtil.CRF(0.2)));
			
			for(Vec3f v:grass[RandUtil.RI(grass.length)].vertecies){
				q.rotate(vRot.set(v));
				indices.add(vertices.size()/3);
				vertices.add(vRot.x()*scale+x);
				vertices.add(vRot.y()*scale+y);
				vertices.add(vRot.z()*scale+z);
				mats.add(1);
			}
		}
		return ModelLoader.buildModel("Gen_Floor-"+gridX+"_"+gridZ, GL11.GL_TRIANGLES, "vertices", vertices.toFloatArray(), /*"uvs", uvs.toFloatArray(), */"indices", indices.toIntArray(), "materialIds", mats.toFloatArray(), "genNormals", true).culface(false);
	}
	
	@Override
	public IModel getModel(){
		return model;
	}
	
	@Override
	public Matrix4f getTransform(){
		if(model.getMaterialCount()==1){
			IMaterial mat=model.createMaterial("grass");
			mat.setDiffuse(0.2F, 1, 0.25F, 1).setLightTroughput(0.5F).setJelly(0.3F);
			
			model.getMaterial(0).setShineDamper(80).setReflectivity(10F);
		}
		
		TRANSFORM.setIdentity();
		POS.x=x;
		POS.y=0;
		POS.z=z;
		TRANSFORM.translate(POS);
		return TRANSFORM;
	}
}
