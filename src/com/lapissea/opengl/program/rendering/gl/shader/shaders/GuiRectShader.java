package com.lapissea.opengl.program.rendering.gl.shader.shaders;

import com.lapissea.opengl.program.rendering.gl.gui.GuiElement;
import com.lapissea.opengl.program.rendering.gl.gui.GuiElementMaterial;
import com.lapissea.opengl.program.rendering.gl.gui.GuiHandler;
import com.lapissea.opengl.program.rendering.gl.shader.ShaderRenderer;
import com.lapissea.opengl.program.rendering.gl.shader.modules.ShaderModule;
import com.lapissea.opengl.program.rendering.gl.shader.uniforms.UniformMat4;
import com.lapissea.opengl.program.rendering.gl.shader.uniforms.floats.UniformFloat1;
import com.lapissea.opengl.program.rendering.gl.shader.uniforms.floats.UniformFloat2;
import com.lapissea.opengl.program.rendering.gl.shader.uniforms.floats.UniformFloat4;
import com.lapissea.opengl.window.api.util.color.ColorM;

public class GuiRectShader extends ShaderRenderer.Basic3D<GuiElement>{
	
	class RenderType{
		
		UniformFloat4	color;
		UniformFloat1	mouseRad,blurRad;
		
		
		public RenderType(String name){
			blurRad=getUniform(UniformFloat1.class, name+".blurRad");
			color=getUniform(UniformFloat4.class, name+".color");
			mouseRad=getUniform(UniformFloat1.class, name+".mouseRad");
		}
		
		
		void upload(float blur, ColorM col, float mouse){
			blurRad.upload(blur);
			color.upload(col);
			mouseRad.upload(mouse);
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
		transformMat=getUniform(UniformMat4.class, "transformMat");
		size=getUniform(UniformFloat2.class, "size");
		
		background=new RenderType("background");
		border=new RenderType("border");
		borderWidth=getUniform(UniformFloat1.class, "borderWidth");
		blurDiv=getUniform(UniformFloat1.class, "blurDiv");
	}
	
	@Override
	public void prepareGlobal(){
		bind();
		modulesGlobal.forEach(ShaderModule.Global::uploadUniformsGlobal);
		if(blurDiv!=null)blurDiv.upload(GuiHandler.BLUR_DIV);
	}
	
	@Override
	protected void prepareInstance(GuiElement renderable){
		super.prepareInstance(renderable);
		background.upload(renderable.getRenderBackground());
		border.upload(renderable.getRenderBorder());
		size.upload(renderable.getSize());
		borderWidth.upload(renderable.borderWidth);
		
	}
}
