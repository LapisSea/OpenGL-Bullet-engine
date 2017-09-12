package com.lapissea.opengl.rendering.shader.shaders;

import com.lapissea.opengl.gui.GuiElement;
import com.lapissea.opengl.gui.GuiElementMaterial;
import com.lapissea.opengl.gui.GuiHandler;
import com.lapissea.opengl.rendering.shader.ShaderRenderer;
import com.lapissea.opengl.rendering.shader.modules.ShaderModule;
import com.lapissea.opengl.rendering.shader.uniforms.floats.UniformFloat1;
import com.lapissea.opengl.rendering.shader.uniforms.floats.UniformFloat2;
import com.lapissea.opengl.rendering.shader.uniforms.floats.UniformFloat4;
import com.lapissea.opengl.window.api.util.color.ColorM;

public class GuiRectShader extends ShaderRenderer.Basic3D<GuiElement>{
	
	class RenderType{
		
		UniformFloat4	color;
		UniformFloat1	mouseRad,blurRad;
		
		public RenderType(String name){
			blurRad=getUniform(name+".blurRad");
			color=getUniform(name+".color");
			mouseRad=getUniform(name+".mouseRad");
		}
		
		void upload(float blur, ColorM col, float mouse){
			if(blurRad!=null)blurRad.upload(blur);
			if(color!=null)color.upload(col);
			if(mouseRad!=null)mouseRad.upload(mouse);
		}
		
		public void upload(GuiElementMaterial background){
			upload(background.blurRad, background.color, background.mouseRad);
		}
		
	}
	
	UniformFloat1	borderWidth;
	UniformFloat1	blurDiv;
	UniformFloat2	size;
	RenderType		background,border;
	
	public GuiRectShader(){
		super("gui/rect");
	}
	
	@Override
	protected void setUpUniforms(){
		super.setUpUniforms();
		size=getUniform("size");
		
		background=new RenderType("background");
		border=new RenderType("border");
		borderWidth=getUniform("borderWidth");
		blurDiv=getUniform("blurDiv");
	}
	
	@Override
	public void prepareGlobal(){
//		super.prepareGlobal();
		bind();
//		uploadProjectionAndViewMat(getProjection(), getView());
		modulesGlobal.forEach(ShaderModule.Global::uploadUniformsGlobal);
		if(blurDiv!=null) blurDiv.upload(GuiHandler.BLUR_DIV);
	}
	
	@Override
	protected void prepareInstance(GuiElement renderable){
//		LogUtil.println(renderable.getModel().getTextures());
		super.prepareInstance(renderable);
		background.upload(renderable.getRenderBackground());
		border.upload(renderable.getRenderBorder());
		if(size!=null)size.upload(renderable.getSize());
		if(borderWidth!=null)borderWidth.upload(renderable.borderWidth);
		
	}
}
