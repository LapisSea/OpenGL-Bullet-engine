package com.lapissea.opengl.program.rendering.gl.gui;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

import com.lapissea.opengl.program.core.Game;
import com.lapissea.opengl.program.rendering.GLUtil;
import com.lapissea.opengl.program.rendering.GLUtil.BlendFunc;
import com.lapissea.opengl.program.rendering.gl.Fbo;
import com.lapissea.opengl.program.rendering.gl.shader.Shaders;
import com.lapissea.opengl.program.rendering.gl.shader.shaders.GuiRectShader;
import com.lapissea.opengl.window.assets.ITexture;

public class GuiHandler{
	
	private Stack<Gui>					guiStack=new Stack<>();
	private LinkedList<IngameDisplay>	displays=new LinkedList<>();
	
	
	public static int	BLUR_DIV=2;
	public Fbo			mainBlur=new Fbo(0, 0);
	
	
	public void openGui(Gui gui){
		if(guiStack.contains(gui)) throw new IllegalStateException();
		guiStack.add(gui);
		Mouse.setGrabbed(false);
	}
	
	public Gui getOpenGui(){
		if(guiStack.isEmpty()) return null;
		return guiStack.peek();
	}
	
	public void closeOpenGui(){
		guiStack.pop();
		if(guiStack.isEmpty()){
			Game.win().centerMouse();
			Mouse.setGrabbed(true);
		}
	}
	
	public void render(){
		Gui g=getOpenGui();
		if(g!=null) render(g);
		displays.forEach(this::render);
	}
	
	
	public void update(){
		Gui g=getOpenGui();
		if(g!=null) g.update();
		displays.forEach(GuiElement::update);
	}
	
	
	private List<GuiElement> renderList=new ArrayList<>();
	
	private void render(IngameDisplay disp){
		
		copyMain();
		
		GLUtil.BLEND.set(true);
		GL14.glBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
		GLUtil.DEPTH_TEST.set(false);
		
		addRender(disp);
		renderList.sort((x, y)->-Integer.compare(y.getZ(), x.getZ()));
		
		GuiRectShader rect=Shaders.GUI_RECT;
		
		rect.prepareGlobal();
		renderList.forEach(e->{
			List<ITexture> tx=e.getModel().getTextures();
			if(tx.isEmpty()) tx.add(mainBlur.getTex());
			else tx.set(0, mainBlur.getTex());
			rect.renderSingleBare(e);
		});
		rect.unbind();
		renderList.clear();
		
		GLUtil.DEPTH_TEST.set(true);
		GLUtil.BLEND_FUNC.set(BlendFunc.NORMAL);
	}
	
	private void addRender(GuiElement e){
		renderList.add(e);
		e.children.forEach(this::addRender);
	}
	
	private void copyMain(){
		
		mainBlur.setSize(Display.getWidth()/BLUR_DIV, Display.getHeight()/BLUR_DIV);
		mainBlur.bind();
		Game.get().renderer.worldFob.drawImg();
		Fbo.bindDefault();
	}
	
}
