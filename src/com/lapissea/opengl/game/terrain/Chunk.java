package com.lapissea.opengl.game.terrain;

import static org.lwjgl.opengl.GL11.*;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import org.lwjgl.util.vector.Matrix4f;

import com.bulletphysics.collision.shapes.BvhTriangleMeshShape;
import com.bulletphysics.linearmath.Transform;
import com.lapissea.opengl.core.Game;
import com.lapissea.opengl.game.physics.jbullet.PhysicsObjJBullet;
import com.lapissea.opengl.game.world.World;
import com.lapissea.opengl.rendering.ModelTransformed;
import com.lapissea.opengl.resources.model.ModelBuilder;
import com.lapissea.opengl.resources.model.ModelDataBuilder;
import com.lapissea.opengl.resources.model.ModelLoader;
import com.lapissea.opengl.resources.model.ModelUtil;
import com.lapissea.opengl.util.Rand;
import com.lapissea.opengl.util.UtilM;
import com.lapissea.opengl.util.function.Predicates;
import com.lapissea.opengl.util.math.vec.Quat4;
import com.lapissea.opengl.util.math.vec.Vec2iFinal;
import com.lapissea.opengl.util.math.vec.Vec3f;
import com.lapissea.opengl.util.math.vec.Vec3fFinal;
import com.lapissea.opengl.window.api.util.vec.IVec2iR;
import com.lapissea.opengl.window.api.util.vec.IVec3fR;
import com.lapissea.opengl.window.assets.IModel;
import com.lapissea.opengl.window.impl.assets.Material;
import com.lapissea.util.LogUtil;

public class Chunk extends PhysicsObjJBullet implements ModelTransformed{
	
	protected static final Matrix4f	TRANSFORM	=new Matrix4f();
	protected static final Vec3f	POS			=new Vec3f();
	
	public static ModelBuilder[] GRASS_MODELS;
	static{
		Game.load(()->GRASS_MODELS=ModelLoader.loadFolder("Grass", Predicates.FIRST_NUMERIC));
	}
	
	public static final float	SIZE		=100;
	public static int			RESOLUTION	=18,GRASS_MIN=120,GRASS_RAND=30;
	
	public final IVec2iR	pos;
	public final IVec3fR	spacePos;
	private IModel			model	=ModelLoader.EMPTY_MODEL;
	
	protected final Matrix4f mat=new Matrix4f();
	
	private float[]	hMap;
	private boolean	loaded;
	private boolean	loading;
	
	public final World world;
	
	public Chunk(World world, IVec2iR pos){
		this(world, pos.x(), pos.y());
	}
	
	public Chunk(World world, int x, int z){
		pos=new Vec2iFinal(x, z);
		spacePos=new Vec3fFinal(x*SIZE, 0, z*SIZE);
		this.world=world;
	}
	
	public void unload(){
		if(!isLoaded()) return;
		loaded=false;
		world.removeRigidBody(this);
		if(isLoading()){
			LogUtil.printlnEr("nope");
			Game.glCtxLater(this::unload);
		}else getModel().delete();
	}
	
	public void load(IHeightMapProvider hMap){
		if(!loading) return;
		
		loaded=true;
		loading=false;
		mat.translate(new Vec3f(spacePos));
		generateModel(hMap);
	}
	
	private void generateModel(IHeightMapProvider hMap){
		int r1=RESOLUTION+1;
		int gridX=pos.x(),gridZ=pos.y();
		ModelDataBuilder modelBuild=new ModelDataBuilder("Gen_Chunk-"+gridX+"_"+gridZ);
		
		float mul=SIZE/RESOLUTION;
		for(int z=0;z<r1;z++){
			for(int x=0;x<r1;x++){
				modelBuild.vertices.add(x*mul);
				modelBuild.vertices.add((float)hMap.getHeightAt(x+gridX*RESOLUTION, z+gridZ*RESOLUTION));
				modelBuild.vertices.add(z*mul);
				modelBuild.materials.add(0);
			}
		}
		this.hMap=modelBuild.vertices.toFloatArray();
		
		for(int z=0;z<RESOLUTION;z++){
			for(int x=0;x<RESOLUTION;x++){
				
				int x0=x,z0=z,x1=x+1,z1=z+1;
				
				int p0=x0+z0*r1;
				int p1=x1+z0*r1;
				int p2=x0+z1*r1;
				int p3=x1+z1*r1;
				
				modelBuild.indices.add(p3);
				modelBuild.indices.add(p1);
				modelBuild.indices.add(p0);
				
				modelBuild.indices.add(p0);
				modelBuild.indices.add(p2);
				modelBuild.indices.add(p3);
			}
		}
		
		init(0, new Transform(new javax.vecmath.Matrix4f(new Quat4f(0, 0, 0, 1), new Vector3f(spacePos.x(), spacePos.y(), spacePos.z()), 1)), new BvhTriangleMeshShape(UtilM.verticesToPhysicsMesh(this.hMap, modelBuild.indices.toIntArray()), true), new Vec3f());
		
		world.addRigidBody(this);
		
		Quat4 q=new Quat4();
		Vec3f rot=new Vec3f(),vRot=new Vec3f(),iterator=new Vec3f();
		for(int i=0, j=GRASS_MIN+Rand.i(GRASS_RAND);i<j;i++){
			float xOnChunk=Rand.f(SIZE);
			float zOnChunk=Rand.f(SIZE);
			float y=getHeightAt(xOnChunk, zOnChunk);
			
			q.set(rot.setThis(0, Rand.f(Math.PI*2), Rand.cf(0.2F)));
			
			float scale=0.7F+Rand.f(0.3);
			int start=modelBuild.vertices.size()/3;
			ModelBuilder mdl=Rand.pick(GRASS_MODELS);
			
			ModelUtil.iterate(mdl.vertices, iterator, v->{
				q.rotate(vRot.set(v));
				modelBuild.vertices.add(vRot.x()*scale+xOnChunk);
				modelBuild.vertices.add(vRot.y()*scale+y);
				modelBuild.vertices.add(vRot.z()*scale+zOnChunk);
				modelBuild.materials.add(1);
			});
			for(int id:mdl.indices){
				modelBuild.indices.add(start+id);
			}
		}
		
		ModelUtil.fixIndexedFlatShading(modelBuild, 3,
				ModelUtil.uncompress(modelBuild.vertices, modelBuild.indices, 3),
				ModelUtil.uncompress(modelBuild.materials, modelBuild.indices, 1),
				new float[0], new float[0]);
		
		model=ModelLoader.buildModel(modelBuild.compile()
				.withFormat(GL_TRIANGLES)
				.generateNormals(true)
				.withCulface(true)
				.withMaterialDefs(
						new Material(0, "ground")
						.setShineDamper(50)
						.setDiffuse(0.2F, 1, 0.25F, 1)
						.setSpecular(1, 1, 1, 1),
						
						new Material(1, "grass")
						.setDiffuse(0.2F, 1, 0.25F, 1)
						.setLightTroughput(0.5F)
						.setSpecular(0, 0, 0, 0)
						.setJelly(0.3F)));
		
	}
	
	@Override
	public IModel getModel(){
		return model;
	}
	
	@Override
	public Matrix4f getTransform(){
		
		TRANSFORM.setIdentity();
		POS.set(spacePos);
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
	
	public boolean isLoaded(){
		return loaded;
	}
	
	@Override
	protected void finalize(){
		if(isLoaded()){
			LogUtil.printlnEr("Chunk deleted but not unloaded! pos:", pos);
			System.exit(0);
		}
	}
	
	public boolean isLoading(){
		return loading;
	}
	
	public void notifyStartLoading(){
		loading=true;
	}
	
	public void cancelLoading(){
		loading=false;
	}
	
	@Override
	public String toString(){
		return "Chunk{at="+pos+(isLoading()?", loading":isLoaded()?", loaded":"")+"}";
	}
}
