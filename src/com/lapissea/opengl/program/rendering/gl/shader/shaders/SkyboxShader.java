package com.lapissea.opengl.program.rendering.gl.shader.shaders;

import java.util.List;

import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix4f;

import com.lapissea.opengl.abstr.opengl.assets.ModelAttribute;
import com.lapissea.opengl.program.rendering.ModelInWorld;
import com.lapissea.opengl.program.rendering.gl.model.Model;
import com.lapissea.opengl.program.rendering.gl.model.ModelLoader;
import com.lapissea.opengl.program.rendering.gl.shader.Shader;
import com.lapissea.opengl.program.rendering.gl.shader.ShaderRenderer;
import com.lapissea.opengl.program.rendering.gl.shader.uniforms.UniformMat4;
import com.lapissea.opengl.program.rendering.gl.texture.BasicTextureCube;
import com.lapissea.opengl.program.rendering.gl.texture.TextureLoader;
import com.lapissea.opengl.program.util.math.Maths;

public class SkyboxShader extends ShaderRenderer<ModelInWorld>{
	
	//	@Override
	//	protected String getFsSrc(){
	//		return UtilM.getTxtResource("shaders/entity.fs");
	//	}
	//	@Override
	//	protected String getVsSrc(){
	//		return UtilM.getTxtResource("shaders/entity.vs");
	//	}
	
	private static class UniformMat40 extends UniformMat4{
		
		public UniformMat40(Shader shader, int id, String name){
			super(shader, id, name);
		}
		
		@Override
		public void upload(Matrix4f mat){
			shader.bindingProttect();
			
			if(Maths.equals(mat, prev)) return;
			prev.load(mat);
			
			BUFF.put(prev.m00);
			BUFF.put(prev.m01);
			BUFF.put(prev.m02);
			BUFF.put(prev.m03);
			BUFF.put(prev.m10);
			BUFF.put(prev.m11);
			BUFF.put(prev.m12);
			BUFF.put(prev.m13);
			BUFF.put(prev.m20);
			BUFF.put(prev.m21);
			BUFF.put(prev.m22);
			BUFF.put(prev.m23);
			BUFF.put(0);//prev.m30);
			BUFF.put(0);//prev.m31);
			BUFF.put(0);//prev.m32);
			BUFF.put(prev.m33);
			BUFF.flip();
			GL20.glUniformMatrix4(id(), false, BUFF);
			checkError(this::retryUpload);
		}
		
	}
	
	private Model cube=ModelLoader.buildModel("Skybox", false, "vertices", new float[]{
			-1,1,-1,
			-1,-1,-1,
			1,-1,-1,
			1,-1,-1,
			1,1,-1,
			-1,1,-1,
			
			-1,-1,1,
			-1,-1,-1,
			-1,1,-1,
			-1,1,-1,
			-1,1,1,
			-1,-1,1,
			
			1,-1,-1,
			1,-1,1,
			1,1,1,
			1,1,1,
			1,1,-1,
			1,-1,-1,
			
			-1,-1,1,
			-1,1,1,
			1,1,1,
			1,1,1,
			1,-1,1,
			-1,-1,1,
			
			-1,1,-1,
			1,1,-1,
			1,1,1,
			1,1,1,
			-1,1,1,
			-1,1,-1,
			
			-1,-1,-1,
			-1,-1,1,
			1,-1,-1,
			1,-1,-1,
			-1,-1,1,
			1,-1,1
	}, "genNormals", false, "textures", TextureLoader.loadTexture("skybox/test", BasicTextureCube.class));
	
	@Override
	@Deprecated
	public void renderBatched(ModelInWorld entity){}
	
	@Override
	@Deprecated
	public void renderSingle(ModelInWorld renderable){}
	
	@Override
	@Deprecated
	public void renderBatch(List<? extends ModelInWorld> entitysWithSameModel){}
	
	@Override
	public void render(){
		onRendered();
		prepareGlobal();
		prepareModel(cube);
		cube.drawCall();
		unbindModel(cube);
		unbind();
	}
	
	@Override
	protected void prepareGlobal(){
		super.prepareGlobal();
	}
	
	@Override
	protected synchronized void bindAttributes(){
		bindAttribute(ModelAttribute.VERTEX_ATTR);
	}
	
	@Override
	protected void setUpUniforms(){
		projectionMat=getUniform(UniformMat4.class, "projectionMat");
		viewMat=getUniform(UniformMat40.class, "viewMat");
	}
	
}
