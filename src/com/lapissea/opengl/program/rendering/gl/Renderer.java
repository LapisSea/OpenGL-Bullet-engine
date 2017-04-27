package com.lapissea.opengl.program.rendering.gl;

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Vector3f;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;

import com.lapissea.opengl.program.core.Game;
import com.lapissea.opengl.program.events.MouseKeyEvent;
import com.lapissea.opengl.program.game.Camera;
import com.lapissea.opengl.program.game.entity.Entity;
import com.lapissea.opengl.program.game.entity.entitys.EntityStatic;
import com.lapissea.opengl.program.game.world.World;
import com.lapissea.opengl.program.interfaces.InputEvents;
import com.lapissea.opengl.program.interfaces.Renderable;
import com.lapissea.opengl.program.interfaces.Updateable;
import com.lapissea.opengl.program.interfaces.WindowEvents;
import com.lapissea.opengl.program.rendering.FpsCounter;
import com.lapissea.opengl.program.rendering.GLUtil;
import com.lapissea.opengl.program.rendering.GLUtil.BlendFunc;
import com.lapissea.opengl.program.rendering.GLUtil.CullFace;
import com.lapissea.opengl.program.rendering.font.FontFamily;
import com.lapissea.opengl.program.rendering.gl.model.DynamicModel;
import com.lapissea.opengl.program.rendering.gl.model.ModelAttribute;
import com.lapissea.opengl.program.rendering.gl.model.ModelLoader;
import com.lapissea.opengl.program.rendering.gl.shader.ShaderRenderer;
import com.lapissea.opengl.program.rendering.gl.shader.Shaders;
import com.lapissea.opengl.program.rendering.gl.shader.light.DirectionalLight;
import com.lapissea.opengl.program.rendering.gl.shader.light.PointLight;
import com.lapissea.opengl.program.rendering.gl.shader.modules.ShaderModule;
import com.lapissea.opengl.program.util.PairM;
import com.lapissea.opengl.program.util.UtilM;
import com.lapissea.opengl.program.util.color.ColorM;
import com.lapissea.opengl.program.util.math.vec.Vec3f;

import it.unimi.dsi.fastutil.floats.FloatList;

public class Renderer implements InputEvents,Updateable,WindowEvents{
	
	static{
		ShaderModule.register();
	}
	
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
	
	public static class Lined extends DynamicModel{
		
		public Lined(String name){
			super(name);
		}
		
		@Override
		public void load(int vao, int vertexCount, boolean usesIndicies, boolean usesQuads, int[] vbos, ModelAttribute[] attributeIds, float rad){
			super.load(vao, vertexCount, usesIndicies, usesQuads, vbos, attributeIds, rad);
			glDrawId=GL11.GL_LINES;
		}
		
		@Override
		public void drawCall(){
			//GLUtil.DEPTH_TEST.set(false);
			super.drawCall();
			//GLUtil.DEPTH_TEST.set(true);
		}
	}
	
	public DynamicModel lines=ModelLoader.buildModel(Lined.class, "lines", false, "vertices", new float[9], "primitiveColor", new float[12], "genNormals", false);
	
	public DynamicModel fontDynamicModel=ModelLoader.buildModel(DynamicModel.class, "lines", false, "vertices", new float[9], "uvs", new float[6], "primitiveColor", new float[12], "genNormals", false);
	
	public Renderer(){
		fpsCounter.activate();
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
		camera.toMat(view);
		frustrum.extractFrustum(getProjection(), getView());
	}
	
	@Override
	public void onClick(MouseKeyEvent event){
		if(Window.isFocused()){
			Window.centerMouse();
			Mouse.setGrabbed(true);
		}
		//Shaders.load();
	}
	
	@Override
	public void update(){
		camera.update();
	}
	
	@Override
	public void onResize(int width, int height){
		getCamera().calcProjection();
	}
	
	public void render(){
		fpsCounter.frame();
		potentialRenders=actualRenders=0;
		World world=Game.get().world;
		float pt=Game.getPartialTicks();
		double sunPos=world.getSunPos(pt)*Math.PI*2;
		float bright=(float)world.getSunBrightness(pt);
		
		ColorM moonCol=new ColorM(0.05, 0.05, 0.15);
		ColorM sunCol=new ColorM(0.5, 0.6, 1);
		
		skyColor=moonCol.mix(sunCol, bright, 1-bright);
		

		GLUtil.BLEND_FUNC.set(BlendFunc.NORMAL);
		GLUtil.CULL_FACE.set(CullFace.BACK);
		GLUtil.DEPTH_TEST.set(true);
		GLUtil.CULL_FACE.set(true);
		GLUtil.BLEND.set(true);
		
		GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
		
		//TODO: remove when adding sky box
		GLUtil.CLEAR_COLOR.set(skyColor);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
		
		getCamera().createProjection(projection);
		setView();
		
		PairM<FloatList,FloatList> data=fontComfortaa.build(10, "0, 0, 0, 1\nDamn that's one \"thin matrix\"!\n                     ;)");
		if(fontDynamicModel.getTextures().isEmpty()) fontDynamicModel.addTexture(fontComfortaa.letters);
		
		for(int i=0;i<data.obj1.size()/2;i++){
			fontDynamicModel.add(ModelAttribute.VERTEX_ATTR, data.obj1.getFloat(i*2)/100, data.obj1.getFloat(i*2+1)/100, 0);
			fontDynamicModel.add(ModelAttribute.UV_ATTR, data.obj2.getFloat(i*2), data.obj2.getFloat(i*2+1));
			fontDynamicModel.add(ModelAttribute.PRIMITIVE_COLOR_ATTR, 0, 1, 1, 1);
		}
		
		Shaders.ENTITY.renderBatched(new EntityStatic(world, fontDynamicModel, new Vec3f(0, 2, 0)));
		
		List<Entity> entitys=Game.get().world.getAll();
		
		worldFog.density=0.006F;
		worldFog.gradient=3;
		sunCol.a(bright+0.1F);
		moonCol.a(1.1F-bright*bright);
		float cos=(float)Math.cos(sunPos);
		dirLights.add(new DirectionalLight(new Vec3f(cos/3, (float)Math.sin(sunPos), cos), sunCol));
		sunPos-=Math.PI;
		
		cos=(float)Math.cos(sunPos);
		dirLights.add(new DirectionalLight(new Vec3f(cos/3, (float)Math.sin(sunPos), cos), moonCol));
		
		GLUtil.checkError();
		entitys.stream().forEach(Renderable::preRender);
		entitys.stream().forEach(Renderable::render);
		lines.defaultMaterial.ambient.r(1);
		
		lines.defaultMaterial.ambient=new ColorM();
		GLUtil.checkError();
//		GL11.glDisable(GL11.GL_ALPHA_TEST);
//		GL11.glEnable(GL11.GL_BLEND);
//		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		addShader(Shaders.TERRAIN);
		UtilM.doAndClear(toRender, ShaderRenderer::render);
		
		GL11.glLineWidth(GL11.GL_LINE_WIDTH_RANGE);
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		GL11.glEnable(GL11.GL_LINE_WIDTH);
		
		GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
		Shaders.LINE.renderBatched(new EntityStatic(world, lines, new Vec3f()));
		Shaders.LINE.render();
		
		pointLights.clear();
		dirLights.clear();
		//		
		//		long gcTim=0;
		//		
		//		for(GarbageCollectorMXBean i:ManagementFactory.getGarbageCollectorMXBeans()){
		//			gcTim+=i.getCollectionTime();
		//		}
		//		MemoryUsage m=ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
		//		LogUtil.println("gc",gcTim,m.getUsed()/1024,"MB /",m.getCommitted()/1024,"MB");
		//LogUtil.println(potentialRenders,"/",actualRenders);
		//		LogUtil.println(fpsCounter.getFps());
	}
	
	public void drawLine(Vector3f from, Vector3f to, Vector3f color){
		lines.add(ModelAttribute.VERTEX_ATTR, from.x, from.y, from.z);
		lines.add(ModelAttribute.VERTEX_ATTR, to.x, to.y, to.z);
		
		lines.add(ModelAttribute.PRIMITIVE_COLOR_ATTR, color.x, color.y, color.z, 0.5F);
		lines.add(ModelAttribute.PRIMITIVE_COLOR_ATTR, color.x, color.y, color.z, 0.5F);
		
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
