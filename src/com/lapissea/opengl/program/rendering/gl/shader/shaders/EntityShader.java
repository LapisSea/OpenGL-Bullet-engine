package com.lapissea.opengl.program.rendering.gl.shader.shaders;

import org.lwjgl.opengl.GL13;

import com.lapissea.opengl.program.rendering.ModelTransformed;
import com.lapissea.opengl.program.rendering.gl.shader.ShaderRenderer;
import com.lapissea.opengl.program.rendering.gl.shader.uniforms.UniformSampler2D;

public class EntityShader extends ShaderRenderer.Basic3D<ModelTransformed>{
	
	UniformSampler2D skyBuffer;
	
	public EntityShader(){
		super("entity");
	}
	
	@Override
	protected void setUpUniforms(){
		super.setUpUniforms();
		skyBuffer=getUniform("skyBuffer");
	}
	
	@Override
	public void prepareGlobal(){
		super.prepareGlobal();
		if(skyBuffer!=null){
			GL13.glActiveTexture(GL13.GL_TEXTURE0+1);
			getRenderer().skyFbo.getTexture().bind();
			skyBuffer.upload(1);
		}
	}//skyBuffer((gl_FragCoord.xy+1)).rgb
	
}
