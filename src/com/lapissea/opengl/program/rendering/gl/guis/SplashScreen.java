package com.lapissea.opengl.program.rendering.gl.guis;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;

import com.lapissea.opengl.abstr.opengl.assets.ModelAttribute;
import com.lapissea.opengl.program.core.Game;
import com.lapissea.opengl.program.rendering.GLUtil;
import com.lapissea.opengl.program.rendering.ModelInWorld;
import com.lapissea.opengl.program.rendering.gl.Renderer;
import com.lapissea.opengl.program.rendering.gl.model.Model;
import com.lapissea.opengl.program.rendering.gl.model.ObjModelLoader;
import com.lapissea.opengl.program.rendering.gl.shader.ShaderRenderer;
import com.lapissea.opengl.program.rendering.gl.shader.uniforms.floats.UniformFloat1;
import com.lapissea.opengl.program.util.Quat4M;
import com.lapissea.opengl.program.util.UtilM;
import com.lapissea.opengl.program.util.math.Maths;
import com.lapissea.opengl.program.util.math.vec.Vec3f;

public class SplashScreen{
	
	private class SplashScreenShader extends ShaderRenderer<ModelInWorld>{
		
		@Override
		protected String getFsSrc(){
			String src=
			"#version 400 core\n"+
			"out vec4 pixelColor;\n"+
					
			"void main(void){\n"+
			"	\n"+
			"	pixelColor=vec4(0);\n"+
			"	\n"+
			"}";
			return src;
		}
		@Override
		protected String getVsSrc(){
			String src=
			"#version 400 core\n"+
			"\n"+
			"\n"+
			"in vec3 pos;\n"+
			"in vec3 normalIn;\n"+
			"\n"+
			"\n"+
			"uniform mat4 transformMat;\n"+
			"uniform mat4 projectionMat;\n"+
			"uniform mat4 viewMat;\n"+
			"uniform float tim;\n"+
			"\n"+
			"void main(void){\n"+
			"	vec3 pos0=pos;\n"+
			"	float tm=(pos.x*40+pos.y*5+pos.z*10)*15+tim;\n"+
			"	pos0.x+=sin(tm)/500;\n"+
			"	pos0.y+=cos(tm)/500;\n"+
			"	vec4 worldPos=transformMat*vec4(pos0,1);\n"+
			"	vec4 posRelativeToCam=viewMat*worldPos;\n"+
			"	\n"+
			"	gl_Position=projectionMat*posRelativeToCam;\n"+
			"}";
			
			return src;
		}

		@Override
		protected void bindAttributes(){
			bindAttribute(ModelAttribute.VERTEX_ATTR);
			bindAttribute(ModelAttribute.NORMAL_ATTR);
		}
		@Override
		protected void setUpUniforms(){
			super.setUpUniforms();
			tim=getUniform(UniformFloat1.class, "tim");
		}
		public void renderSingle(Matrix4f transform, Model model){
			bind();
			tim.upload((float)((System.currentTimeMillis()-START)/100D));
			renderSingle(ModelInWorld.singleUse(transform, model));
		}
		@Override
		public Renderer getRenderer(){
			return null;
		}
		
	}
	
	private static final long START=System.currentTimeMillis();
	
	private UniformFloat1 tim;
	private Model model=ObjModelLoader.loadAndBuild("loading");
	private SplashScreenShader shader=new SplashScreenShader();
	
	private long endBegin=-1;
	private boolean zeroRender;
	
	public SplashScreen(){
		
	}
	
	public void update(){
		Game.win().updateInput();
	}
	
	public void render(){
		if(Game.win().isClosed()){
			System.exit(0);
			return;
		}
		GLUtil.checkError();
		GL11.glClearColor(1, 1, 1, 1);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT|GL11.GL_DEPTH_BUFFER_BIT);
		
		float scale=1;
		if(endBegin>0){
			scale=(float)(1-((System.currentTimeMillis()-endBegin)/100D));
			if(scale>0)scale=(float)Math.sqrt(scale);
			else{
				zeroRender=true;
				return;
			}
		}

		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
		double tim=System.currentTimeMillis();
		shader.renderSingle(Maths.createTransformMat(new Vec3f(0,0,-1F), new Quat4M(
				(float)Math.sin((tim/500)%(Math.PI*2))*0.1F,
				(float)Math.sin((tim/1500)%(Math.PI*2))*0.1F,
				(float)Math.sin((tim/2000)%(Math.PI*2))*0.1F,1
			), new Vec3f(scale,scale,scale)),model);
		Game.win().swapBuffers(60);
		//Game.get().loadGLData();
		GLUtil.checkError();
	}
	
	public void end(){
		endBegin=System.currentTimeMillis();
		while(!zeroRender)UtilM.sleep(2);
		Game.glCtx(()->{
			model.delete();
			shader.delete();
		});
	}

}
