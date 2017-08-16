package com.lapissea.opengl.program.game.world;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import com.lapissea.opengl.program.core.Game;
import com.lapissea.opengl.program.game.entity.Entity;
import com.lapissea.opengl.program.game.entity.EntityUpd;
import com.lapissea.opengl.program.game.entity.entitys.EntityCrazyCube;
import com.lapissea.opengl.program.game.entity.entitys.EntityPlayer;
import com.lapissea.opengl.program.game.events.Updateable;
import com.lapissea.opengl.program.game.physics.jbullet.PhysicsWorldJbullet;
import com.lapissea.opengl.program.game.terrain.Chunk;
import com.lapissea.opengl.program.game.terrain.IHeightMapProvider;
import com.lapissea.opengl.program.rendering.gl.Fog;
import com.lapissea.opengl.program.rendering.gl.shader.modules.ShaderModuleLight;
import com.lapissea.opengl.program.util.BlackBody;
import com.lapissea.opengl.program.util.RandUtil;
import com.lapissea.opengl.program.util.UtilM;
import com.lapissea.opengl.program.util.data.OffsetArray;
import com.lapissea.opengl.program.util.math.vec.Vec3f;
import com.lapissea.util.LogUtil;

public class World extends PhysicsWorldJbullet{
	
	public static int		PHYSICS_CUBE_AMMOUNT=50,CHUNK_GRID_SIZE=50;
	private List<Entity>	entitys				=new ArrayList<>();
	public List<EntityUpd>	entitysUpd			=new ArrayList<>();
	private boolean			checkDead;
	private long			ticksPassed;
	private double			dayDuration			=1000;
	public Fog				fog					=new Fog();
	
	public final OffsetArray<OffsetArray<Chunk>> chunks=new OffsetArray<>();
	
	public World(){
		setUpWorld();
	}
	
	
	private void setUpWorld(){
		BufferedImage img;
		try{
			img=ImageIO.read(UtilM.getResource("textures/h-maps/hm.png"));
		}catch(IOException e){
			throw new RuntimeException(e);
		}
		IHeightMapProvider hMap=(x, y)->{
			x/=4;
			y/=4;
			x++;
			y++;
			x/=2;
			y/=2;
			x*=img.getWidth();
			y*=img.getHeight();
			return new Color(img.getRGB((int)Math.abs(x)%img.getWidth(), (int)Math.abs(y)%img.getHeight())).getRed()/4F;
		};
		for(int x=0;x<CHUNK_GRID_SIZE;x++){
			for(int z=0;z<CHUNK_GRID_SIZE;z++){
				addChunk(new Chunk(x-CHUNK_GRID_SIZE/2, z-CHUNK_GRID_SIZE/2, hMap));
			}
		}
		
		LogUtil.println("Done!");
		
		for(int i=0;i<PHYSICS_CUBE_AMMOUNT;i++){
			EntityCrazyCube c;
			spawn(c=new EntityCrazyCube(this, new Vec3f(RandUtil.CRF(300), RandUtil.RF(20)+100, RandUtil.CRF(300))));
			if(i<ShaderModuleLight.MAX_POINT_LIGHT){
				c.lightColor=BlackBody.fromKelvin(null, RandUtil.RI(40000));
			}
		}
		
		spawn(new EntityPlayer(this, new Vec3f(0, 50, 0)));
		
		//		for(int i=0, j=10;i<j;i++){
		//			spawn(new EntityLight(this, new Vec3f(RandUtil.CRF(worldSize*1.5), 2, RandUtil.CRF(worldSize*1.5)), IColorM.randomRGB()));
		//		}
		//spawn(new EntityPlayer(this, new Vec3f(0, 0, 0)));
	}
	
	private void addChunk(Chunk chunk){
		
		OffsetArray<Chunk> zLine=chunks.get(chunk.x);
		if(zLine==null) chunks.set(chunk.x, zLine=new OffsetArray<>());
		zLine.set(chunk.z, chunk);
		addRigidBody(chunk);
	}
	
	public void removeChunk(Chunk chunk){
		OffsetArray<Chunk> zLine=chunks.get(chunk.x);
		if(zLine==null) return;
		Chunk chunk0=zLine.remove(chunk.z);
		if(chunk0!=null)removeRigidBody(chunk0);
	}
	
	public void spawn(Entity e){
		if(e instanceof EntityUpd){
			EntityUpd eu=(EntityUpd)e;
			entitysUpd.add(eu);
			addRigidBody(eu.getPhysicsObj());
		}
		entitys.add(e);
	}
	
	public void update(){
		
		dayDuration=2000;
		fog.setDensity(0.0015F);
		fog.setGradient(2);
		
		if(checkDead){
			entitysUpd.removeIf(Entity::isDead);
			checkDead=false;
		}
		ticksPassed++;
		
		Game.get().renderer.lines.clearIfEmpty();
		
		entitysUpd.forEach(Updateable::update);
		
		updatePhysics(1F/Game.get().timer.getUps());
	}
	
	public List<Entity> getAll(){
		return entitys;
	}
	
	public long time(){
		return ticksPassed;
	}
	
	public void markDeadDirty(){
		checkDead=true;
	}
	
	public double getSunPos(){
		return getSunPos(0);
	}
	
	public double getSunPos(double pt){
		return (time()+pt)/dayDuration%1;
		//return 0.75;
		//return 0.25;
		//return 0.5;
	}
	
	public double getSunBrightness(){
		return getSunBrightness(0);
	}
	
	public double getSunBrightnessPos(double pos){
		double gradientSize=0.1,result;
		
		if(pos<0.5){
			if(pos<gradientSize) result=0.5+pos/gradientSize/2;
			else{
				pos=0.5-pos;
				if(pos<gradientSize) result=0.5+pos/gradientSize/2;
				else result=1;
			}
		}else{
			pos-=0.5;
			
			if(pos<gradientSize) result=0.5-pos/gradientSize/2;
			else{
				pos=0.5-pos;
				if(pos<gradientSize) result=0.5-pos/gradientSize/2;
				else result=0;
			}
		}
		return Math.sqrt(result);
	}
	
	public double getSunBrightness(float pt){
		return getSunBrightnessPos(getSunPos(pt));
	}
	
	public Vec3f putHeightAt(Vec3f vec){
		vec.setY(getHeightAt(vec.x, vec.z));
		return vec;
	}
	
	public float getHeightAt(float x, float z){
		
		Chunk chunk=getChunk((int)Math.floor(x/Chunk.SIZE), (int)Math.floor(z/Chunk.SIZE));
		if(chunk==null) return 0;
		
		float xOnChunk=Math.abs(x)%Chunk.SIZE;
		float zOnChunk=Math.abs(z)%Chunk.SIZE;
		if(x<0) xOnChunk=Chunk.SIZE-xOnChunk;
		if(z<0) zOnChunk=Chunk.SIZE-zOnChunk;
		return chunk.getHeightAt(xOnChunk, zOnChunk);
	}
	
	public Chunk getChunk(int x, int z){
		OffsetArray<Chunk> zLine=chunks.get(x);
		return zLine==null?null:zLine.get(z);
	}
	
}
