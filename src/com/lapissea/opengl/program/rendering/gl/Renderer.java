package com.lapissea.opengl.program.rendering.gl;

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Vector3f;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;

import com.lapissea.opengl.program.core.Game;
import com.lapissea.opengl.program.game.Camera;
import com.lapissea.opengl.program.game.entity.Entity;
import com.lapissea.opengl.program.game.events.Updateable;
import com.lapissea.opengl.program.game.particle.ParticleHandler;
import com.lapissea.opengl.program.game.particle.particles.ParticleFoo;
import com.lapissea.opengl.program.game.world.World;
import com.lapissea.opengl.program.rendering.FpsCounter;
import com.lapissea.opengl.program.rendering.GLUtil;
import com.lapissea.opengl.program.rendering.GLUtil.BlendFunc;
import com.lapissea.opengl.program.rendering.GLUtil.CullFace;
import com.lapissea.opengl.program.rendering.font.FontFamily;
import com.lapissea.opengl.program.rendering.gl.gui.Gui;
import com.lapissea.opengl.program.rendering.gl.gui.GuiHandler;
import com.lapissea.opengl.program.rendering.gl.gui.guis.GuiPause;
import com.lapissea.opengl.program.rendering.gl.model.DynamicModel;
import com.lapissea.opengl.program.rendering.gl.model.ModelLoader;
import com.lapissea.opengl.program.rendering.gl.model.ObjModelLoader;
import com.lapissea.opengl.program.rendering.gl.shader.ShaderRenderer;
import com.lapissea.opengl.program.rendering.gl.shader.Shaders;
import com.lapissea.opengl.program.rendering.gl.shader.light.DirLight;
import com.lapissea.opengl.program.rendering.gl.shader.light.LightSource;
import com.lapissea.opengl.program.rendering.gl.shader.light.LineLight;
import com.lapissea.opengl.program.rendering.gl.shader.light.PointLight;
import com.lapissea.opengl.program.rendering.gl.shader.modules.ShaderModule;
import com.lapissea.opengl.program.util.NanoTimer;
import com.lapissea.opengl.program.util.math.PartialTick;
import com.lapissea.opengl.program.util.math.vec.Vec3f;
import com.lapissea.opengl.window.api.events.FocusEvent;
import com.lapissea.opengl.window.api.events.KeyEvent;
import com.lapissea.opengl.window.api.events.KeyEvent.KeyAction;
import com.lapissea.opengl.window.api.events.MouseButtonEvent;
import com.lapissea.opengl.window.api.events.MouseButtonEvent.Action;
import com.lapissea.opengl.window.api.events.MouseMoveEvent;
import com.lapissea.opengl.window.api.events.MouseScrollEvent;
import com.lapissea.opengl.window.api.events.ResizeEvent;
import com.lapissea.opengl.window.api.events.util.InputEvents;
import com.lapissea.opengl.window.api.events.util.WindowEvents;
import com.lapissea.opengl.window.api.frustrum.Frustum;
import com.lapissea.opengl.window.api.util.color.ColorM;
import com.lapissea.opengl.window.api.util.color.IColorM;
import com.lapissea.opengl.window.assets.IModel;
import com.lapissea.opengl.window.assets.ModelAttribute;
import com.lapissea.util.UtilL;

public class Renderer implements InputEvents,Updateable,WindowEvents{
	
	static{
		ShaderModule.register();
	}
	
	public static boolean	RENDER_FRUSTRUM			=false;
	public static int		SKY_RESOLUTION_DEVIDER	=1;
	
	private final Matrix4f	projection	=new Matrix4f(),view=new Matrix4f(),identity=new Matrix4f();
	private Camera			camera		=new Camera();
	public final Frustum	frustrum	=new Frustum();
	
	private final List<ShaderRenderer<?>>	toRender	=new ArrayList<>();
	public final List<PointLight>			pointLights	=new ArrayList<>();
	public final List<LineLight>			lineLights	=new ArrayList<>();
	public final List<DirLight>				dirLights	=new ArrayList<>();
	
	public final FontFamily fontComfortaa=new FontFamily("Comfortaa");
	
	public FpsCounter fpsCounter=new FpsCounter(true);
	
	private int potentialRenders,actualRenders;
	
	public ParticleHandler<ParticleFoo> particleHandler;
	
	private NanoTimer renderBuildBechmark=new NanoTimer(),renderBechmark=new NanoTimer();
	
	public DynamicModel		lines		=ModelLoader.buildModel(DynamicModel.class, "lines", GL11.GL_LINES, "vertices", new float[9], "primitiveColor", new float[12], "genNormals", false);
	private IModel			moon		=ObjModelLoader.loadAndBuild("moon");
	public FboRboTextured	worldFbo	=new FboRboTextured();
	public Fbo				skyFbo		=new Fbo();
	public GuiHandler		guiHandler	=new GuiHandler();
	
	
	private boolean renderWorldFlag=true;
	
	public Renderer(){
		skyFbo.setDepth(false);
		
		worldFbo.initHook=()->renderWorldFlag=true;
		fpsCounter.activate();
		particleHandler=new ParticleHandler<>((parent, pos)->new ParticleFoo(parent, pos));
		particleHandler.models.add(ModelLoader.buildModel("ParticleQuad", GL11.GL_TRIANGLES, "genNormals", false, "vertices", new float[]{
				-0.5F,-0.5F,0,
				+0.5F,-0.5F,0,
				+0.5F,+0.5F,0,
				
				-0.5F,-0.5F,0,
				+0.5F,+0.5F,0,
				-0.5F,+0.5F,0,
		}, "uvs",
				new float[]{
						0,0,
						1,0,
						1,1,
						
						0,0,
						1,1,
						0,1,
		}, "textures", "particle/SoftBloom"));
		
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
	public void update(){
		guiHandler.update();
		if(Game.isPaused()) return;
		camera.update();
		particleHandler.update();
//		particleHandler.spawn(new Vec3f(RandUtil.CRF(150), 0, RandUtil.CRF(150)));
	}
	
	@Override
	public void onMouseButton(MouseButtonEvent e){
		Gui openGui=guiHandler.getOpenGui();
		if(openGui!=null) openGui.onMouseButton(e);
		else e.source.centerMouse();
		
		if(e.action==Action.DOWN){
			if(Shaders.ENTITY!=null){
				Shaders.ENTITY.load();
				Shaders.TERRAIN.load();
				Shaders.SKYBOX.load();
				Shaders.GUI_RECT.load();
				//Shaders.POST_COPY.load();
			}
		}
		
		Mouse.setGrabbed(!Game.isPaused());
	}
	
	@Override
	public void onKey(KeyEvent e){
		Gui openGui=guiHandler.getOpenGui();
		if(openGui==null){
			if(e.code==Keyboard.KEY_ESCAPE){
				if(e.action==KeyAction.RELEASE) guiHandler.openGui(new GuiPause());
			}
			return;
		}
		openGui.onKey(e);
	}
	
	@Override
	public void onMouseMove(MouseMoveEvent e){
		if(!Game.isPaused()) getCamera().onMouseMove(e);
		Gui openGui=guiHandler.getOpenGui();
		if(openGui==null) return;
		openGui.onMouseMove(e);
		
	}
	
	@Override
	public void onMouseScroll(MouseScrollEvent e){
		Gui openGui=guiHandler.getOpenGui();
		if(openGui==null) return;
		openGui.onMouseScroll(e);
		
	}
	
	@Override
	public void onFocus(FocusEvent e){
		if(!e.focused) Mouse.setGrabbed(false);
	}
	
	@Override
	public void onResize(ResizeEvent e){
		getCamera().calcProjection();
	}
	
	public void render(){
		
		//PREPARE
		RENDER_FRUSTRUM=false;
		fpsCounter.newFrame();
		
		if(renderWorldFlag) renderWorld();
		renderWorldFlag=!Game.isPaused();
		
		Fbo.bindDefault();
		worldFbo.copyColorToScreen();
		
		guiHandler.render();
		
		pointLights.clear();
		dirLights.clear();
		lineLights.clear();
		
	}
	
	public void renderWorld(){
		
		World world=Game.get().world;
		if(RENDER_FRUSTRUM) world.bulletWorld.debugDrawWorld();
		float pt=Game.getPartialTicks();
		double sunPos=world.getSunPos(pt)*Math.PI*2;
		float bright=(float)world.getSunBrightness(pt);
		
		ColorM moonCol=new ColorM(0, 0, 0);
		
		List<Entity> entitys=Game.get().world.getAll();
		
		moonCol.a(1.1F-bright*bright);
		Vec3f sunDir=new Vec3f((float)(sunPos+Math.PI), 0, 0).eulerToVector();
		
		float h=10;
		float worldSiz=Math.max(0, 6371e3F/Math.max(1, 1+h*h/100));
		ColorM sunCol=new ColorM(1, 0.3, 0.2);
		sunCol.a(bright+0.1F);
		
		Game.get().world.fog.color.set(moonCol.mix(sunCol, bright, 1-bright));
		
		dirLights.add(new DirLight(sunDir, sunCol));
		sunPos-=Math.PI;
		
		Vec3f moonRot=new Vec3f((float)(sunPos+Math.PI), 0, 0).eulerToVector();
		dirLights.add(new DirLight(moonRot, moonCol));
		
		getCamera().createProjection(projection);
		setView();
		
		potentialRenders=actualRenders=0;
		
		worldFbo.setRenderBufferType(false).setSample(4);
		
		worldFbo.setSize(Game.win().getSize());
		worldFbo.bind();
		
		GLUtil.BLEND_FUNC.set(BlendFunc.NORMAL);
		GLUtil.CULL_FACE.set(CullFace.BACK);
		GLUtil.CULL_FACE.set(true);
		GLUtil.BLEND.set(true);
		
		GLUtil.MULTISAMPLE.set(false);
		skyFbo.setSize(worldFbo.getWidth()/SKY_RESOLUTION_DEVIDER, worldFbo.getHeight()/SKY_RESOLUTION_DEVIDER);
		skyFbo.bind();
		Shaders.SKYBOX.render();
		skyFbo.copyTo(worldFbo, GL11.GL_COLOR_BUFFER_BIT, GL11.GL_LINEAR);
		GLUtil.MULTISAMPLE.set(true);
		worldFbo.bind();
		GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
		
		//BACKGROUND
		
		GL11.glDepthMask(false);
		GLUtil.DEPTH_TEST.set(false);
		
		//
		
		moon.getMaterial(0).getAmbient().set(0.4F, 0.4F, 0.5F, 1);
		Matrix4f mat=new Matrix4f();
		mat.translate(moonRot.mul(25).add(PartialTick.calc(new Vec3f(), camera.prevPos, camera.pos)));
		Shaders.ENTITY.renderSingle(mat, moon);
		
		GLUtil.DEPTH_TEST.set(true);
		GL11.glDepthMask(true);
		
		//BUILD
		addShader(Shaders.TERRAIN);
		
		renderBuildBechmark.start();
		entitys.forEach(e->e.preRender());
		entitys.forEach(e->e.render());
		renderBuildBechmark.end();
		
		//RENDER
		renderBechmark.start();
		renderBechmark.end();
		UtilL.doAndClear(toRender, ShaderRenderer::render);
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		GLUtil.DEPTH_TEST.set(false);
		Shaders.LINE.renderSingle(identity, lines);
		GLUtil.DEPTH_TEST.set(true);
		lines.clear();
		Shaders.LINE.render();
		worldFbo.process();
		
		particleHandler.render();
		
	}
	
	public void drawLine(Vector3f from, Vector3f to, Vector3f color){
		lines.add(ModelAttribute.VERTEX_ATTR_3D, from.x, from.y, from.z);
		lines.add(ModelAttribute.VERTEX_ATTR_3D, to.x, to.y, to.z);
		
		lines.add(ModelAttribute.COLOR_ATTR, color.x, color.y, color.z, 1);
		lines.add(ModelAttribute.COLOR_ATTR, color.x, color.y, color.z, 1);
	}
	
	public void drawLine(Vec3f from, Vec3f to, IColorM color){
		
		lines.add(ModelAttribute.VERTEX_ATTR_3D, from.x, from.y, from.z);
		lines.add(ModelAttribute.VERTEX_ATTR_3D, to.x, to.y, to.z);
		
		lines.add(ModelAttribute.COLOR_ATTR, color.r(), color.g(), color.b(), 1);
		lines.add(ModelAttribute.COLOR_ATTR, color.r(), color.g(), color.b(), 1);
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
	
	public String getDebugInfo(){
		return "FPS:\t\t\t\t"+fpsCounter.getFps()+
				"\nOn screen:\t"+actualRenders+"/"+potentialRenders+
				(Game.isPaused()?"":"\nWorld build:\t"+renderBuildBechmark.msAvrg100()+"ms")+
				"\nRender:\t\t"+renderBechmark.msAvrg100()+"ms"+
				"\nSee bounds:\t"+RENDER_FRUSTRUM;
	}
	
	public void addLight(LightSource light){
		if(light instanceof PointLight) pointLights.add((PointLight)light);
		else if(light instanceof LineLight) lineLights.add((LineLight)light);
		else if(light instanceof DirLight) dirLights.add((DirLight)light);
	}
	
}
