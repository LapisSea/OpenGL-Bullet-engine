package com.lapissea.opengl.program.rendering;

import java.util.function.Consumer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.OpenGLException;
import org.lwjgl.util.vector.Matrix4f;

import com.lapissea.opengl.program.rendering.gl.model.Model;
import com.lapissea.opengl.program.util.LogUtil;
import com.lapissea.opengl.program.util.Quat4M;
import com.lapissea.opengl.program.util.color.IColorM;
import com.lapissea.opengl.program.util.math.vec.Vec3f;

public class GLUtil{
	
	public static boolean RUN_GL_ERROR_CHECK=false;
	
	public static interface IGlStateBool{
		
		void set(boolean enabled);
	}
	
	public static interface IGlStateObj<T>{
		
		void set(T value);
	}
	
	public static interface IGlStateMix<T>extends IGlStateBool,IGlStateObj<T>{}
	
	private static class GlBool implements IGlStateBool{
		
		protected boolean	prevEnabled;
		protected final int	enumId;
		
		public GlBool(int enumId){
			this.enumId=enumId;
		}
		
		@Override
		public void set(boolean enabled){
			if(enabled!=prevEnabled){
				if(prevEnabled=enabled) GL11.glEnable(enumId);
				else GL11.glDisable(enumId);
			}
		}
		
	}
	
	private static class GlObj<T> implements IGlStateObj<T>{
		
		protected T					prevValue;
		private final Consumer<T>	set;
		
		public GlObj(Consumer<T> set){
			this.set=set;
		}
		
		@Override
		public void set(T value){
			if(!value.equals(prevValue)){
				prevValue=value;
				set.accept(value);
			}
		}
		
	}
	
	private static class GlMix<T> implements IGlStateMix<T>{
		
		protected T					prevValue;
		private final Consumer<T>	set;
		
		protected boolean	prevEnabled;
		protected final int	enumId;
		
		public GlMix(int enumId, Consumer<T> set){
			this.set=set;
			this.enumId=enumId;
		}
		
		@Override
		public void set(T value){
			if(!value.equals(prevValue)){
				prevValue=value;
				set.accept(value);
			}
		}
		
		@Override
		public void set(boolean enabled){
			if(enabled!=prevEnabled){
				if(prevEnabled=enabled) GL11.glEnable(enumId);
				else GL11.glDisable(enumId);
			}
		}
		
	}
	
	public static enum CullFace{
		FRONT(GL11.GL_FRONT),BACK(GL11.GL_BACK),FRONT_BACK(GL11.GL_FRONT_AND_BACK);
		
		public final int key;
		
		private CullFace(int key){
			this.key=key;
		}
	}
	
	public static enum BlendFunc{
		
		NORMAL(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA),
		ADD(GL11.GL_SRC_ALPHA, GL11.GL_ONE),
		ADD2(GL11.GL_ONE, GL11.GL_ONE),
		INVERT(GL11.GL_ONE_MINUS_DST_COLOR, GL11.GL_ZERO);
		
		public final int sfactor,dfactor;
		
		private BlendFunc(int sfactor, int dfactor){
			this.sfactor=sfactor;
			this.dfactor=dfactor;
		}
	}
	
	public static final IGlStateBool			DEPTH_TEST	=new GlBool(GL11.GL_DEPTH_TEST);
	public static final IGlStateBool			BLEND		=new GlBool(GL11.GL_BLEND);
	public static final IGlStateMix<CullFace>	CULL_FACE	=new GlMix<>(GL11.GL_CULL_FACE, v->GL11.glCullFace(v.key));
	public static final IGlStateObj<BlendFunc>	BLEND_FUNC	=new GlObj<>(v->GL11.glBlendFunc(v.sfactor, v.dfactor));
	public static final IGlStateObj<IColorM>	CLEAR_COLOR	=new GlObj<>(v->GL11.glClearColor(v.r(), v.g(), v.b(), v.a()));
	
	public static void printAllUniforms(int program){
		GL20.glUseProgram(program);
		
		int id=0;
		String name,last="";
		while(!(name=GL20.glGetActiveUniform(program, id, 512)).isEmpty()){
			if(last.equals(name)) break;
			LogUtil.println(id, last=name);
			id++;
		}
		float[] a=new float[0];
		LogUtil.println("uniform count", id);
	}
	
	public static void checkError(){
		checkError(true);
	}
	
	public static void checkError(boolean willThrow){
		if(!RUN_GL_ERROR_CHECK)return;
		
		int err=GL11.glGetError();
		if(err!=GL11.GL_NO_ERROR){
			OpenGLException e=new OpenGLException(err);
			if(willThrow) throw e;
			e.printStackTrace();
		}
	}
	
	private static final class ModelInWorldSingleUse implements ModelInWorld{
		
		Matrix4f	mat;
		Model		model;
		
		@Override
		public Model getModel(){
			return model;
		}
		
		@Override
		public Matrix4f getTransform(){
			return mat;
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
	
	private static final ModelInWorldSingleUse MIW=new ModelInWorldSingleUse();
	
	protected static ModelInWorld singleUse0(Matrix4f worldTransform, Model model){
		MIW.mat=worldTransform;
		MIW.model=model;
		return MIW;
	}
	
}
