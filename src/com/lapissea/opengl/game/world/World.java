package com.lapissea.opengl.game.world;

import java.util.ArrayList;
import java.util.List;

import com.lapissea.opengl.core.Game;
import com.lapissea.opengl.game.entity.Entity;
import com.lapissea.opengl.game.entity.EntityUpd;
import com.lapissea.opengl.game.entity.entitys.EntityCrazyCube;
import com.lapissea.opengl.game.entity.entitys.EntityPlayer;
import com.lapissea.opengl.game.events.Updateable;
import com.lapissea.opengl.game.physics.jbullet.PhysicsObjJBullet;
import com.lapissea.opengl.game.physics.jbullet.PhysicsWorldJbullet;
import com.lapissea.opengl.game.terrain.Chunk;
import com.lapissea.opengl.game.terrain.IHeightMapProvider;
import com.lapissea.opengl.rendering.Fog;
import com.lapissea.opengl.rendering.shader.modules.ShaderModuleLight;
import com.lapissea.opengl.util.BlackBody;
import com.lapissea.opengl.util.RandUtil;
import com.lapissea.opengl.util.data.OffsetArrayList;
import com.lapissea.opengl.util.math.SimplexNoise;
import com.lapissea.opengl.util.math.vec.Vec2i;
import com.lapissea.opengl.util.math.vec.Vec3f;
import com.lapissea.util.LogUtil;

public class World extends PhysicsWorldJbullet{
	
	public static int		PHYSICS_CUBE_AMMOUNT=50;
	private List<Entity>	entitys				=new ArrayList<>();
	public List<EntityUpd>	entitysUpd			=new ArrayList<>();
	private boolean			checkDead;
	private long			ticksPassed;
	private double			dayDuration			=1000;
	public Fog				fog					=new Fog();
	
	public final OffsetArrayList<OffsetArrayList<Chunk>>	chunks				=new OffsetArrayList<>();
	private ChunkLoadingSorter								chunkLoadingSorter	=new ChunkLoadingSorter();
	private IHeightMapProvider								hMap;
	
	public World(){
		setUpWorld();
	}
	
	private void setUpWorld(){
//		BufferedImage img;
//		try{
//			img=ImageIO.read(getResource("textures/h-maps/hm.png"));
//		}catch(IOException e){
//			throw uncheckedThrow(e);
//		}
//		hMap=(x, y)->{
//			return new Color(img.getRGB((int)Math.abs(x)%img.getWidth(), (int)Math.abs(y)%img.getHeight())).getRed()/4F;
//		};
		hMap=(x, z)->{
			double mainH=Math.min(1, Math.pow((1+SimplexNoise.noise(x/100, z/100))/2, 10))*300;
			mainH+=SimplexNoise.noise(x/5, z/5);
			return mainH;
		};
		for(int i=0;i<PHYSICS_CUBE_AMMOUNT;i++){
			EntityCrazyCube c;
			spawn(c=new EntityCrazyCube(this, new Vec3f(RandUtil.CRF(300), RandUtil.RF(20)+100, RandUtil.CRF(300))));
			if(i<ShaderModuleLight.MAX_POINT_LIGHT){
				c.lightColor=BlackBody.fromKelvin(null, RandUtil.RI(40000));
			}
		}
		
		spawn(new EntityPlayer(this, new Vec3f(0, 50, 0)));
		
		LogUtil.println("Done!");
		
		//		for(int i=0, j=10;i<j;i++){
		//			spawn(new EntityLight(this, new Vec3f(RandUtil.CRF(worldSize*1.5), 2, RandUtil.CRF(worldSize*1.5)), IColorM.randomRGB()));
		//		}
		//spawn(new EntityPlayer(this, new Vec3f(0, 0, 0)));
	}
	
	private void addChunk(Chunk chunk){
		synchronized(chunkLoadingSorter){
			OffsetArrayList<Chunk> zLine=chunks.get(chunk.pos.x());
			if(zLine==null) chunks.set(chunk.pos.x(), zLine=new OffsetArrayList<>());
//			LogUtil.println(zLine.get(chunk.pos.y()));
			zLine.set(chunk.pos.y(), chunk);
		}
	}
	
	private void removeChunk(Chunk chunk){
		synchronized(chunkLoadingSorter){
			OffsetArrayList<Chunk> zLine=chunks.get(chunk.pos.x());
			if(zLine==null) return;
			zLine.remove(chunk.pos.y());
			Game.glCtxLater(chunk::unload);
		}
	}
	
	@Override
	public void addRigidBody(PhysicsObjJBullet obj){
		synchronized(chunkLoadingSorter){
			super.addRigidBody(obj);
		}
	}
	
	@Override
	public void removeRigidBody(PhysicsObjJBullet obj){
		synchronized(chunkLoadingSorter){
			super.removeRigidBody(obj);
		}
	}
	
	public void spawn(Entity e){
		if(e instanceof EntityUpd){
			EntityUpd eu=(EntityUpd)e;
			entitysUpd.add(eu);
			addRigidBody(eu.getPhysicsObj());
		}
		entitys.add(e);
	}
	
	private int		camXLast	=Integer.MIN_VALUE,camZLast=Integer.MIN_VALUE,lastSiz=0;
	private boolean	lastChange	=false;
	
	private void handleChunks(){
		Vec3f camPos=Game.get().renderer.getCamera().pos;
		
		if(lastChange||time()%10==0){
			synchronized(chunkLoadingSorter){
				int camX=(int)(camPos.x()/Chunk.SIZE);
				int camZ=(int)(camPos.z()/Chunk.SIZE);
				int siz=(int)Math.ceil(fog.getMaxDistance()/Chunk.SIZE);
				if(lastChange||camXLast!=camX||camZLast!=camZ||lastSiz!=siz||true){
					lastChange=false;
					camXLast=camX;
					camZLast=camZ;
					lastSiz=siz;
//					LogUtil.println(camX, camZ, getChunk(camX, camZ));
					
					Vec2i veci=new Vec2i();
					Vec3f vec3=new Vec3f();
					
					for(int x=-siz;x<siz;x++){
						for(int z=-siz;z<siz;z++){
							veci.set(x+camX, z+camZ);
							
							vec3.set(veci.x()*Chunk.SIZE, 0, veci.y()*Chunk.SIZE);
							if(vec3.distanceTo(camPos)>lastSiz*Chunk.SIZE) continue;
							
							Chunk c=getChunk(veci);
							
							if(c!=null){
								if(c.isLoading()&&!chunkLoadingSorter.hasNext()){
									chunkLoadingSorter.add(c);
									lastChange=true;
								}
								continue;
							}
							
							c=new Chunk(this, veci);
							addChunk(c);
							
							c.notifyStartLoading();
							chunkLoadingSorter.add(c);
							lastChange=true;
						}
					}
					for(OffsetArrayList<Chunk> line:chunks){
						line.removeIf(c->{
							if(c.spacePos.distanceTo(camPos)*0.8>lastSiz*Chunk.SIZE||!c.isLoaded()&&!c.isLoading()){
								Game.glCtxLater(c::unload);
								lastChange=true;
								return true;
							}
							return false;
						});
					}
					chunks.removeIf(OffsetArrayList::isEmpty);
				}
			}
			if(chunkLoadingSorter.hasNext()) Game.load(this::loadChunks);
		}
		if(!chunkLoadingSorter.hasNext()) return;
		chunkLoadingSorter.update();
		
	}
	
	private void loadChunks(){
		if(!chunkLoadingSorter.hasNext()) return;
		
		Vec3f camPos=Game.get().renderer.getCamera().pos;
		
		chunkLoadingSorter.iterate(chunk->{
			if(chunk.spacePos.distanceTo(camPos)*0.9<lastSiz*Chunk.SIZE){
				if(!chunk.isLoaded()) chunk.load(hMap);
			}
		});
	}
	
	public void update(){
		handleChunks();
		
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
		synchronized(chunkLoadingSorter){
			updatePhysics(1F/Game.get().timer.getUps());
		}
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
	
	public Chunk getChunk(Vec2i pos){
		return getChunk(pos.x(), pos.y());
	}
	
	public Chunk getChunk(int x, int z){
		OffsetArrayList<Chunk> zLine=chunks.get(x);
		return zLine==null?null:zLine.get(z);
	}
	
}
