package com.lapissea.opengl.program.game.terrain;

import static org.lwjgl.opengl.GL11.*;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import org.lwjgl.util.vector.Matrix4f;

import com.bulletphysics.collision.shapes.BvhTriangleMeshShape;
import com.bulletphysics.linearmath.Transform;
import com.lapissea.opengl.program.game.physics.jbullet.PhysicsObjJBullet;
import com.lapissea.opengl.program.rendering.ModelTransformed;
import com.lapissea.opengl.program.resources.model.ModelData;
import com.lapissea.opengl.program.resources.model.ModelLoader;
import com.lapissea.opengl.program.resources.model.ModelUtil;
import com.lapissea.opengl.program.util.Predicates;
import com.lapissea.opengl.program.util.RandUtil;
import com.lapissea.opengl.program.util.UtilM;
import com.lapissea.opengl.program.util.math.vec.Quat4;
import com.lapissea.opengl.program.util.math.vec.Vec2i;
import com.lapissea.opengl.program.util.math.vec.Vec3f;
import com.lapissea.opengl.window.assets.IModel;
import com.lapissea.opengl.window.impl.assets.Material;

import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.floats.FloatList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;

public class Chunk extends PhysicsObjJBullet implements ModelTransformed{
	
	protected static final Matrix4f	TRANSFORM	=new Matrix4f();
	protected static final Vec3f	POS			=new Vec3f();
	
	public static ModelData[] grass=ModelLoader.loadFolder("Grass", Predicates.FIRST_NUMERIC);
	
	public static final float	SIZE		=178;
	public static int			RESOLUTION	=32,GRASS_MIN=20,GRASS_RAND=30;
	
	public final int	x,z;
	public IModel		model	=ModelLoader.EMPTY_MODEL;
	
	protected final Matrix4f mat=new Matrix4f();
	
	public final double	seed=Math.random();
	private float[]		hMap;
	
	public Chunk(Vec2i pos, IHeightMapProvider hMap){
		this(pos.x(),pos.y(),hMap);
	}
	public Chunk(int gridX, int gridZ, IHeightMapProvider hMap){
		
		x=gridX;
		z=gridZ;
		mat.translate(new Vec3f(x*SIZE, -2, z*SIZE));
		generateModel(gridX, gridZ, hMap);
	}
	
	private void generateModel(int gridX, int gridZ, IHeightMapProvider hMap){
		int r1=RESOLUTION+1;
		
		FloatList vertices=new FloatArrayList();
		IntList mats=new IntArrayList(),indices=new IntArrayList();
		
		float mul=SIZE/RESOLUTION;
		for(int z=0;z<r1;z++){
			for(int x=0;x<r1;x++){
				vertices.add(x*mul);
				vertices.add(hMap.getHeightAt(x/(double)RESOLUTION+gridX, z/(double)RESOLUTION+gridZ));
				vertices.add(z*mul);
				mats.add(0);
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
		this.hMap=vertices.toFloatArray();
		
		init(0, new Transform(new javax.vecmath.Matrix4f(new Quat4f(0, 0, 0, 1), new Vector3f(x*SIZE, 0, z*SIZE), 1)), new BvhTriangleMeshShape(UtilM.verticesToPhysicsMesh(this.hMap, indices.toIntArray()), true), new Vec3f());
		
		Quat4 q=new Quat4();
		Vec3f rot=new Vec3f(),vRot=new Vec3f(),iterator=new Vec3f();
		for(int i=0, j=GRASS_MIN+RandUtil.RI(GRASS_RAND);i<j;i++){
			float xOnChunk=RandUtil.RF(SIZE);
			float zOnChunk=RandUtil.RF(SIZE);
			float y=getHeightAt(xOnChunk, zOnChunk);
			
			q.set(rot.setThis(0, RandUtil.RF(Math.PI*2), RandUtil.CRF(0.2)));
			
			float scale=0.7F+RandUtil.RF(0.3);
			ModelUtil.iterate(grass[RandUtil.RI(grass.length)].vertecies, iterator, v->{
				q.rotate(vRot.set(v));
				indices.add(vertices.size()/3);
				vertices.add(vRot.x()*scale+xOnChunk);
				vertices.add(vRot.y()*scale+y);
				vertices.add(vRot.z()*scale+zOnChunk);
				mats.add(1);
			});
		}
		model=ModelLoader.buildModel("Gen_Chunk-"+gridX+"_"+gridZ, GL_TRIANGLES, "vertices", vertices.toFloatArray(), /*"uvs", uvs.toFloatArray(), */"indices", indices.toIntArray(), "materialIds", mats.toIntArray(), "genNormals", true, "materials", new Material(0, "ground")).culface(false);
		
		model.createMaterial("grass")
		.setDiffuse(0.2F, 1, 0.25F, 1)
		.setLightTroughput(0.5F)
		.setJelly(0.3F);
		
		model.getMaterial("ground")
		.setShineDamper(50)
		.setDiffuse(0.2F, 1, 0.25F, 1)
		.setSpecular(1, 1, 1, 1);
	}
	
	@Override
	public IModel getModel(){
		return model;
	}
	
	@Override
	public Matrix4f getTransform(){
		
		TRANSFORM.setIdentity();
		POS.x=x*SIZE;
		POS.y=0;
		POS.z=z*SIZE;
		TRANSFORM.translate(POS);
		return TRANSFORM;
	}
	
	public float getHeightAt(float xOnChunk, float zOnChunk){
		
		float mul=SIZE/RESOLUTION;
		int r1=RESOLUTION+1;
		
		int xi=(int)Math.floor(xOnChunk/mul);
		int zi=(int)Math.floor(zOnChunk/mul);
		
		float xPerc=xOnChunk%mul/mul;
		float zPerc=zOnChunk%mul/mul;
		
		float y_00=hMap[(xi+zi*r1)*3+1];
		float y_11=hMap[(xi+1+(zi+1)*r1)*3+1];
		
		if(1-xPerc+zPerc>1){
			float y_01=hMap[(xi+(zi+1)*r1)*3+1];
			
			float y_0=y_00+(y_01-y_00)*zPerc;
			return y_0+(y_11-y_0)*xPerc;
		}
		
		float y_10=hMap[(xi+1+zi*r1)*3+1];
		
		float y_1=y_10+(y_11-y_10)*zPerc;
		return y_00+(y_1-y_00)*xPerc;
		
	}
}
