package com.lapissea.opengl.program.rendering.gl.shader.shaders;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import com.lapissea.opengl.program.rendering.gl.Fbo;
import com.lapissea.opengl.program.rendering.gl.guis.GuiElement;
import com.lapissea.opengl.program.rendering.gl.shader.ShaderRenderer;
import com.lapissea.opengl.program.rendering.gl.shader.modules.ShaderModule;
import com.lapissea.opengl.program.rendering.gl.shader.uniforms.UniformMat4;
import com.lapissea.opengl.program.rendering.gl.shader.uniforms.floats.UniformFloat1;
import com.lapissea.opengl.program.rendering.gl.shader.uniforms.floats.UniformFloat2;
import com.lapissea.opengl.program.rendering.gl.shader.uniforms.floats.UniformFloat4;
import com.lapissea.opengl.window.api.util.color.ColorM;

public class GuiShader extends ShaderRenderer.Basic3D<GuiElement>{
	
	public Fbo mainBlur;
	
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
		
	}
	
	GuiElement foo=new GuiElement();
	
	UniformFloat1	borderWidth;
	UniformFloat2	size;
	RenderType		background,border;
	
	public GuiShader(){
		super("gui");
	}
	
	@Override
	public void render(){
		if(mainBlur==null){
			mainBlur=new Fbo(Display.getWidth(), Display.getHeight());
			mainBlur.hasDepth=false;
			
			mainBlur.create();
		}
		mainBlur.bind();
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
		getRenderer().fobMain.drawImg();
		getRenderer().fobMain.bind();
		
		if(foo.model.getTextures().isEmpty()){
			foo.model.getTextures().add(getRenderer().fobMain.tex);
		}
		foo.model.getTextures().set(0, getRenderer().fobMain.tex);
		renderSingle(foo);
		super.render();
	}
	
	@Override
	protected void setUpUniforms(){
		transformMat=getUniform(UniformMat4.class, "transformMat");
		size=getUniform(UniformFloat2.class, "size");
		
		background=new RenderType("background");
		border=new RenderType("border");
		borderWidth=getUniform(UniformFloat1.class, "borderWidth");
	}
	
	@Override
	protected void prepareGlobal(){
		bind();
		modulesGlobal.forEach(ShaderModule.Global::uploadUniformsGlobal);
	}
	
	@Override
	protected void prepareInstance(GuiElement renderable){
		super.prepareInstance(renderable);
		background.upload(3, new ColorM(0, 0.5, 0.7, 0.5), -1);
		border.upload(5, new ColorM(0.2, 0.1, 1, 0.5), 200);
		size.upload(renderable.getSize());
		borderWidth.upload(2);
	}
}
