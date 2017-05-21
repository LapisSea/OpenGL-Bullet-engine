package com.lapissea.opengl.program.rendering.gl;

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Vector3f;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;

import com.lapissea.opengl.program.core.Game;
import com.lapissea.opengl.program.game.Camera;
import com.lapissea.opengl.program.game.entity.Entity;
import com.lapissea.opengl.program.game.entity.entitys.EntityStatic;
import com.lapissea.opengl.program.game.events.Updateable;
import com.lapissea.opengl.program.game.particle.ParticleHandler;
import com.lapissea.opengl.program.game.particle.particles.ParticleFoo;
import com.lapissea.opengl.program.game.world.World;
import com.lapissea.opengl.program.rendering.FpsCounter;
import com.lapissea.opengl.program.rendering.GLUtil;
import com.lapissea.opengl.program.rendering.GLUtil.BlendFunc;
import com.lapissea.opengl.program.rendering.GLUtil.CullFace;
import com.lapissea.opengl.program.rendering.font.FontFamily;
import com.lapissea.opengl.program.rendering.gl.model.DynamicModel;
import com.lapissea.opengl.program.rendering.gl.model.ModelLoader;
import com.lapissea.opengl.program.rendering.gl.shader.ShaderRenderer;
import com.lapissea.opengl.program.rendering.gl.shader.Shaders;
import com.lapissea.opengl.program.rendering.gl.shader.light.DirectionalLight;
import com.lapissea.opengl.program.rendering.gl.shader.light.PointLight;
import com.lapissea.opengl.program.rendering.gl.shader.modules.ShaderModule;
import com.lapissea.opengl.program.rendering.gl.shader.shaders.SkyboxShader;
import com.lapissea.opengl.program.util.NanoTimer;
import com.lapissea.opengl.program.util.PairM;
import com.lapissea.opengl.program.util.UtilM;
import com.lapissea.opengl.program.util.math.vec.Vec3f;
import com.lapissea.opengl.window.api.events.MouseKeyEvent;
import com.lapissea.opengl.window.api.events.MouseKeyEvent.Action;
import com.lapissea.opengl.window.api.events.ResizeEvent;
import com.lapissea.opengl.window.api.events.util.InputEvents;
import com.lapissea.opengl.window.api.events.util.WindowEvents;
import com.lapissea.opengl.window.api.frustrum.Frustum;
import com.lapissea.opengl.window.api.frustrum.IFrustrumShape;
import com.lapissea.opengl.window.api.util.IRotation;
import com.lapissea.opengl.window.api.util.IVec3f;
import com.lapissea.opengl.window.api.util.color.ColorM;
import com.lapissea.opengl.window.api.util.color.IColorM;
import com.lapissea.opengl.window.assets.IModel;
import com.lapissea.opengl.window.assets.ModelAttribute;

import it.unimi.dsi.fastutil.floats.FloatList;

public class Renderer implements InputEvents,Updateable,WindowEvents{
	
	static{
		ShaderModule.register();
	}
	
	public static boolean RENDER_FRUSTRUM=true;
	
	private final Matrix4f	projection	=new Matrix4f(),view=new Matrix4f();
	private Camera			camera		=new Camera();
	public ColorM			skyColor	=new ColorM(0.05, 0.05, 0.1);
	public Fog				worldFog	=new Fog();
	public final Frustum	frustrum	=new Frustum();
	
	private final List<ShaderRenderer<?>>	toRender	=new ArrayList<>();
	public final List<PointLight>			pointLights	=new ArrayList<>();
	public final List<DirectionalLight>		dirLights	=new ArrayList<>();
	
	public final FontFamily fontComfortaa=new FontFamily("Comfortaa");
	
	public FpsCounter fpsCounter=new FpsCounter(true);
	
	private int potentialRenders,actualRenders;
	
	public ParticleHandler<ParticleFoo> particleHandler;
	
	private NanoTimer renderBuildBechmark=new NanoTimer(),renderBechmark=new NanoTimer();
	
	public static class Lined extends DynamicModel{
		
		public Lined(String name){
			super(name);
		}
		
		@Override
		public IModel load(int vao, int vertexCount, boolean usesIndicies, boolean usesQuads, int[] vbos, ModelAttribute[] attributeIds, IFrustrumShape<? extends IVec3f,? extends IRotation> shape){
			super.load(vao, vertexCount, usesIndicies, usesQuads, vbos, attributeIds, shape);
			glDrawId=GL11.GL_LINES;
			return this;
			
		}
		
		@Override
		public IModel drawCall(){
			//GLUtil.DEPTH_TEST.set(false);
			super.drawCall();
			//GLUtil.DEPTH_TEST.set(true);
			return this;
		}
	}
	
	
	public DynamicModel	lines			=ModelLoader.buildModel(Lined.class, "lines", false, "vertices", new float[9], "primitiveColor", new float[12], "genNormals", false);
	public DynamicModel	fontDynamicModel=ModelLoader.buildModel(DynamicModel.class, "lines", false, "vertices", new float[9], "uvs", new float[6], "primitiveColor", new float[12], "genNormals", false).culface(false);
	//	private Model		moon			=ObjModelLoader.loadAndBuild("moon");
	
	public Renderer(){
		fpsCounter.activate();
		particleHandler=new ParticleHandler<>((parent, pos)->new ParticleFoo(parent, pos));
		particleHandler.models.add(ModelLoader.buildModel("ParticleQuad", false, "genNormals", false, "vertices", new float[]{
				-0.5F,-0.5F,0,
				+0.5F,-0.5F,0,
				+0.5F,+0.5F,0,
				
				-0.5F,-0.5F,0,
				+0.5F,+0.5F,0,
				-0.5F,+0.5F,0,
		}));
	}
	
	
	public Camera getCamera(){
		return camera;
	}
	
	public Matrix4f getProjection(){
		return projection;
	}
	
	public Matrix4f getView(){
		return view;
	}
	
	public void setCamera(Camera camera){
		this.camera=camera;
		setView();
	}
	
	private void setView(){
		camera.createView(view);
		frustrum.extractFrustum(getProjection(), getView());
	}
	
	@Override
	public void onMouseKeyEvent(MouseKeyEvent event){
		if(event.action==Action.DOWN){
			if(Shaders.ENTITY!=null){
				//Shaders.ENTITY.load();
				//Shaders.TERRAIN.load();
				//Shaders.SKYBOX.load();
				Shaders.GUI.load();
			}
		}
		
		event.source.centerMouse();
		Mouse.setGrabbed(true);
	}
	
	@Override
	public void update(){
		camera.update();
		particleHandler.update();
		particleHandler.spawn(new Vec3f(10, 10, 10));
	}
	
	@Override
	public void onResizeEvent(ResizeEvent e){
		getCamera().calcProjection();
	}
	
	public void render(){
		//PREPARE 
		RENDER_FRUSTRUM=false;
		fpsCounter.newFrame();
		World world=Game.get().world;
		float pt=Game.getPartialTicks();
		double sunPos=world.getSunPos(pt)*Math.PI*2;
		float bright=(float)world.getSunBrightness(pt);
		
		ColorM moonCol=new ColorM(0.15, 0.15, 0.3);
		
		PairM<FloatList,FloatList> data=fontComfortaa.build(10,
				"FPS:\t\t\t\t"+fpsCounter+
						"\nOn screen:\t"+actualRenders+"/"+potentialRenders+
						"\nBuild:\t\t\t"+renderBuildBechmark.msAvrg100()+"ms"+
						"\nRender:\t\t"+renderBechmark.msAvrg100()+"ms");
		if(fontDynamicModel.getTextures().isEmpty()) fontDynamicModel.addTexture(fontComfortaa.letters);
		
		for(int i=0;i<data.obj1.size()/2;i++){
			fontDynamicModel.add(ModelAttribute.VERTEX_ATTR, data.obj1.getFloat(i*2)/100, data.obj1.getFloat(i*2+1)/100, 0);
			fontDynamicModel.add(ModelAttribute.UV_ATTR, data.obj2.getFloat(i*2), data.obj2.getFloat(i*2+1));
			fontDynamicModel.add(ModelAttribute.PRIMITIVE_COLOR_ATTR, 1, 1, 1, 1);
		}
		
		List<Entity> entitys=Game.get().world.getAll();
		
		worldFog.density=0.006F;
		worldFog.gradient=3;
		
		moonCol.a(1.1F-bright*bright);
		float cos=(float)Math.cos(sunPos);
		Vec3f sunDir=new Vec3f(cos/3, (float)Math.sin(sunPos), cos);
		float h=10;
		float worldSiz=Math.max(0, 6371e3F/Math.max(1, 1+h*h/100));
		Vec3f v=SkyboxShader.atmosphere(
				sunDir.clone().y(Math.max(sunDir.y(), 0.04F)),
				new Vec3f(0, worldSiz, 0),
				sunDir.clone().y(Math.max(sunDir.y(), 0.04F)),
				30,
				worldSiz,
				worldSiz+100e3F,
				new Vec3f(5.5e-6F, 13.0e-6F, 22.4e-6F),
				21e-6F,
				7e3F,
				1.2e3F,
				0.998F);
		if(Float.isNaN(v.x)) v.set(0, 0, 0);
		if(v.lengthSquared()>0) v.normalise();
		ColorM sunCol=new ColorM(v);
		sunCol.a(bright+0.1F);
		
		
		skyColor=moonCol.mix(sunCol, bright, 1-bright);
		
		
		dirLights.add(new DirectionalLight(sunDir, sunCol));
		sunPos-=Math.PI;
		
		cos=(float)Math.cos(sunPos);
		dirLights.add(new DirectionalLight(new Vec3f(cos/3, (float)Math.sin(sunPos), cos), moonCol));
		
		getCamera().createProjection(projection);
		setView();
		
		potentialRenders=actualRenders=0;
		
		
		GLUtil.BLEND_FUNC.set(BlendFunc.NORMAL);
		GLUtil.CULL_FACE.set(CullFace.BACK);
		GLUtil.DEPTH_TEST.set(true);
		GLUtil.CULL_FACE.set(true);
		GLUtil.BLEND.set(true);
		
		GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
		
		//BACKGROUND 
		
		GL11.glDepthMask(false);
		Shaders.SKYBOX.render();
		GL11.glDepthMask(true);
		
		
		//BUILD
		addShader(Shaders.TERRAIN);
		
		renderBuildBechmark.start();
		entitys.forEach(e->e.preRender());
		entitys.forEach(e->e.render());
		renderBuildBechmark.end();
		
		renderBechmark.start();
		
		//RENDER
		UtilM.doAndClear(toRender, ShaderRenderer::render);
		Shaders.ENTITY.renderSingle(new EntityStatic(world, fontDynamicModel, new Vec3f(0, 2, 0)));
		particleHandler.render();
		GL11.glLineWidth(GL11.GL_LINE_WIDTH_RANGE);
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		GL11.glEnable(GL11.GL_LINE_WIDTH);
		
		GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
		Shaders.LINE.renderSingle(new EntityStatic(world, lines, new Vec3f()));
		Shaders.LINE.render();
		Shaders.GUI.render();
		
		renderBechmark.end();
		
		pointLights.clear();
		dirLights.clear();
		
		//GUI
		
	}
	
	public void drawLine(Vector3f from, Vector3f to, Vector3f color){
		lines.add(ModelAttribute.VERTEX_ATTR, from.x, from.y, from.z);
		lines.add(ModelAttribute.VERTEX_ATTR, to.x, to.y, to.z);
		
		lines.add(ModelAttribute.PRIMITIVE_COLOR_ATTR, color.x, color.y, color.z, 1);
		lines.add(ModelAttribute.PRIMITIVE_COLOR_ATTR, color.x, color.y, color.z, 1);
	}
	
	public void drawLine(Vec3f from, Vec3f to, IColorM color){
		lines.add(ModelAttribute.VERTEX_ATTR, from.x, from.y, from.z);
		lines.add(ModelAttribute.VERTEX_ATTR, to.x, to.y, to.z);
		
		lines.add(ModelAttribute.PRIMITIVE_COLOR_ATTR, color.r(), color.g(), color.b(), 1);
		lines.add(ModelAttribute.PRIMITIVE_COLOR_ATTR, color.r(), color.g(), color.b(), 1);
	}
	
	public void addShader(ShaderRenderer<?> shader){
		toRender.add(shader);
	}
	
	public void notifyEntityRender(){
		potentialRenders++;
	}
	
	public void notifyEntityActualRender(){
		actualRenders++;
	}
	
	
}
