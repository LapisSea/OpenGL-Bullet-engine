package com.lapissea.opengl.program.rendering;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL20.*;

import java.util.Map;
import java.util.function.Consumer;

import org.lwjgl.opengl.OpenGLException;

import com.lapissea.opengl.window.api.util.color.IColorM;
import com.lapissea.opengl.window.api.util.vec.IVec2iR;
import com.lapissea.util.LogUtil;

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
				if(prevEnabled=enabled) glEnable(enumId);
				else glDisable(enumId);
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
				if(prevEnabled=enabled) glEnable(enumId);
				else glDisable(enumId);
			}
		}
		
	}
	
	public static enum CullFace{
		FRONT(GL_FRONT),BACK(GL_BACK),FRONT_BACK(GL_FRONT_AND_BACK);
		
		public final int key;
		
		private CullFace(int key){
			this.key=key;
		}
	}
	
	public static enum BlendFunc{
		
		NORMAL(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA),ADD(GL_SRC_ALPHA, GL_ONE),ADD2(GL_ONE, GL_ONE),INVERT(GL_ONE_MINUS_DST_COLOR, GL_ZERO);
		
		public final int sfactor,dfactor;
		
		private BlendFunc(int sfactor, int dfactor){
			this.sfactor=sfactor;
			this.dfactor=dfactor;
		}
	}
	
	public static final IGlStateBool			DEPTH_TEST	=new GlBool(GL_DEPTH_TEST);
	public static final IGlStateBool			BLEND		=new GlBool(GL_BLEND);
	public static final IGlStateBool			MULTISAMPLE	=new GlBool(GL_MULTISAMPLE);
	public static final IGlStateMix<CullFace>	CULL_FACE	=new GlMix<>(GL_CULL_FACE, v->glCullFace(v.key));
	public static final IGlStateObj<BlendFunc>	BLEND_FUNC	=new GlObj<>(v->glBlendFunc(v.sfactor, v.dfactor));
	public static final IGlStateObj<IColorM>	CLEAR_COLOR	=new GlObj<>(v->glClearColor(v.r(), v.g(), v.b(), v.a()));
	
	public static void printAllUniforms(int program){
		glUseProgram(program);
		
		int id=0;
		String name,last="";
		while(!(name=glGetActiveUniform(program, id, 512)).isEmpty()){
			if(last.equals(name)) break;
			LogUtil.println(id, last=name);
			id++;
		}
		LogUtil.println("uniform count", id);
	}
	
	public static void getAllUniforms(int program, Map<Integer,String> dest){
		glUseProgram(program);
		
		int id=0;
		String name,last="";
		while(!(name=glGetActiveUniform(program, id, 512)).isEmpty()){
			if(last.equals(name)) break;
			dest.put(id, last=name);
			id++;
		}
	}
	
	public static void checkError(){
		checkError(true);
	}
	
	public static void checkError(boolean willThrow){
		if(!RUN_GL_ERROR_CHECK) return;
		
		int err=glGetError();
		if(err!=GL_NO_ERROR){
			OpenGLException e=new OpenGLException(err);
			if(willThrow) throw e;
			e.printStackTrace();
		}
	}
	
	private static int	viewportWidth;
	private static int	viewportHeight;
	
	public static void viewport(IVec2iR size){
		viewport(size.x(), size.y());
	}
	
	public static void viewport(int width, int height){
		glViewport(0, 0, viewportWidth=width, viewportHeight=height);
	}
	
	public static int getViewportWidth(){
		return viewportWidth;
	}
	
	public static int getViewportHeight(){
		return viewportHeight;
	}
	
	public static void deleteDetachShader(int program, int shader){
		detachShader(program, shader);
		deleteShader(shader);
	}
	
	public static void deleteShader(int shader){
		if(shader>0) glDeleteShader(shader);
	}
	
	public static void detachShader(int program, int shader){
		if(program>0&&shader>0) glDetachShader(program, shader);
	}
	
	public static void attachShader(int program, int shader){
		if(program>0&&shader>0) glAttachShader(program, shader);
	}
	
}
