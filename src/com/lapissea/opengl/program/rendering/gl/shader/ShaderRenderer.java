package com.lapissea.opengl.program.rendering.gl.shader;

import java.util.List;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

import com.lapissea.opengl.abstr.opengl.assets.IModel;
import com.lapissea.opengl.abstr.opengl.assets.ModelAttribute;
import com.lapissea.opengl.abstr.opengl.events.Renderable;
import com.lapissea.opengl.program.rendering.GLUtil;
import com.lapissea.opengl.program.rendering.ModelInWorld;
import com.lapissea.opengl.program.rendering.gl.Renderer;
import com.lapissea.opengl.program.rendering.gl.shader.modules.ShaderModule;
import com.lapissea.opengl.program.util.MapOfLists;
import com.lapissea.opengl.program.util.UtilM;

public abstract class ShaderRenderer<RenderType extends ModelInWorld>extends Shader implements Renderable{
	
	private static final Matrix4f NULL_VIEW=new Matrix4f(),NULL_PROJECTION=new Matrix4f();
	
	private final MapOfLists<IModel,RenderType>	toRender=new MapOfLists<>();
	private boolean								added	=false;
	
	public ShaderRenderer(){
		super();
	}
	
	public ShaderRenderer(String name){
		super(name);
	}
	
	protected void onRendered(){
		added=false;
	}
	
	//----BATCHED----//
	
	@Override
	public void preRender(){}
	
	@Override
	public void render(){
		if(!isLoaded()){
			toRender.clear();
			return;
		}
		GLUtil.checkError();
		
		onRendered();
		GLUtil.checkError();
		if(toRender.isEmpty()) return;
		prepareGlobal();
		GLUtil.checkError();
		
		UtilM.doAndClear(toRender, (model, entitysWithSameModel)->{
			if(entitysWithSameModel.isEmpty()) return;
			if(!model.isLoaded()) return;
			prepareModel(model);
			entitysWithSameModel.forEach(renderable->{
				prepareInstance(renderable);
				model.drawCall();
			});
			unbindModel(model);
		});
		unbind();
	}
	
	public void renderBatch(List<? extends RenderType> entitysWithSameModel){
		if(!isLoaded()) return;
		if(entitysWithSameModel.isEmpty()) return;
		
		prepareGlobal();
		IModel model=entitysWithSameModel.get(0).getModel();
		if(!model.isLoaded()) return;
		prepareModel(model);
		
		entitysWithSameModel.forEach(renderable->{
			prepareInstance(renderable);
			model.drawCall();
		});
		unbindModel(model);
		unbind();
		
	}
	
	public void renderBatched(RenderType renderable){
		if(!isLoaded()) return;
		if(!added){
			getRenderer().addShader(this);
			added=true;
		}
		toRender.add(renderable.getModel(), renderable);
	}
	
	protected void prepareGlobal(){
		bind();
		uploadProjectionAndViewMat(getProjection(), getView());
		modulesGlobal.forEach(ShaderModule.Global::uploadUniformsGlobal);
	}
	
	protected void prepareModel(IModel model){
		GLUtil.checkError();
		model.bindVao();
		model.enableAttributes();
		modulesModelUniforms.forEach(module->module.uploadUniformsModel(model));
	}
	
	protected void prepareInstance(RenderType renderable){
		uploadTransformMat(renderable.getTransform());
		modulesInstance.forEach(md->md.uploadUniformsInstance(renderable));
	}
	
	protected void unbindModel(IModel model){
		model.disableAttributes();
		GL30.glBindVertexArray(0);
	}
	
	//----SINGLE----//
	
	public void renderSingle(RenderType renderable){
		if(!isLoaded()) return;
		if(!renderable.getModel().isLoaded()) return;
		prepareGlobal();
		
		renderSingleBare(renderable);
		unbind();
	}
	
	public void renderSingleBare(RenderType renderable){
		IModel model=renderable.getModel();
		
		prepareModel(model);
		prepareInstance(renderable);
		model.drawCall();
		unbindModel(model);
		
	}
	
	protected Matrix4f getProjection(){
		Renderer r=getRenderer();
		if(r!=null) return r.getProjection();
		
		float farPlane=1000,nearPlane=0.1F;
		float aspectRatio=(float)Display.getWidth()/(float)Display.getHeight();
		float y_scale=(float)((1f/Math.tan(Math.toRadians(60/2f)))*aspectRatio);
		float x_scale=y_scale/aspectRatio;
		float frustum_length=farPlane-nearPlane;
		
		NULL_PROJECTION.setIdentity();
		NULL_PROJECTION.m00=x_scale;
		NULL_PROJECTION.m11=y_scale;
		NULL_PROJECTION.m22=-((farPlane+nearPlane)/frustum_length);
		NULL_PROJECTION.m23=-1;
		NULL_PROJECTION.m32=-((2*nearPlane*farPlane)/frustum_length);
		NULL_PROJECTION.m33=0;
		
		return NULL_PROJECTION;
	}
	
	protected Matrix4f getView(){
		Renderer r=getRenderer();
		if(r!=null) return r.getView();
		NULL_VIEW.setIdentity();
		
		return NULL_VIEW;
	}
	
	public static class Basic3D<RenderType extends ModelInWorld>extends ShaderRenderer<RenderType>{
		
		public Basic3D(){
			super();
		}
		
		public Basic3D(String name){
			super(name);
		}
		
		@Override
		protected synchronized void bindAttributes(){
			bindAttribute(ModelAttribute.VERTEX_ATTR);
			bindAttribute(ModelAttribute.UV_ATTR);
			bindAttribute(ModelAttribute.NORMAL_ATTR);
			bindAttribute(ModelAttribute.PRIMITIVE_COLOR_ATTR);
		}
		
	}
}
