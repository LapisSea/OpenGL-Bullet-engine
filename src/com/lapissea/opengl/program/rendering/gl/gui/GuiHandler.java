package com.lapissea.opengl.program.rendering.gl.gui;

import static org.lwjgl.opengl.GL11.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import org.lwjgl.input.Mouse;

import com.lapissea.opengl.program.core.Game;
import com.lapissea.opengl.program.rendering.GLUtil;
import com.lapissea.opengl.program.rendering.GLUtil.BlendFunc;
import com.lapissea.opengl.program.rendering.gl.Fbo;
import com.lapissea.opengl.program.rendering.gl.FboRboTextured;
import com.lapissea.opengl.program.rendering.gl.gui.guis.DebugDisplay;
import com.lapissea.opengl.program.rendering.gl.shader.Shaders;
import com.lapissea.opengl.program.rendering.gl.shader.shaders.GuiRectShader;
import com.lapissea.opengl.window.assets.ITexture;
import com.lapissea.util.UtilL;

public class GuiHandler{
	
	public static int BLUR_DIV=2;
	
	private Stack<Gui>					guiStack	=new Stack<>();
	private LinkedList<IngameDisplay>	displays	=new LinkedList<>();
	private FboRboTextured				drawFbo		=new FboRboTextured(Fbo.TEXTURE);
	private Fbo							lastZLayer	=new Fbo(Fbo.TEXTURE);
	private boolean						first		=true;
	
	public GuiHandler(){
		Game.glCtx(()->displays.add(new DebugDisplay()));
	}
	
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
		if(g!=null) addRender(g);
		displays.forEach(this::addRender);
		
		if(renderList.size()>0&&renderList.get(0).size()>0){
			render0();
		}
		
	}
	
	public void update(){
		Gui g=getOpenGui();
		if(g!=null) g.update();
		displays.forEach(GuiElement::update);
	}
	
	private List<List<GuiElement>> renderList=new ArrayList<>();
	
	private void render0(){
		//		drawFbo.setRenderBufferType(false).setSample(8);
		
		drawFbo.setSize(Game.win().getSize());
		lastZLayer.setSize(Game.win().getSize());
		
		drawFbo.bind();
		glClearColor(0, 0, 0, 0);
		glClear(GL_COLOR_BUFFER_BIT|GL_DEPTH_BUFFER_BIT);
		GLUtil.BLEND.set(true);
		GLUtil.DEPTH_TEST.set(true);
		GLUtil.BLEND_FUNC.set(BlendFunc.NORMAL);
		
		GuiRectShader rect=Shaders.GUI_RECT;
		
		rect.prepareGlobal();
		first=true;
		renderList.forEach(l->{
			(first?Game.get().renderer.worldFbo:drawFbo).copyColorTo(lastZLayer);
			if(first) first=false;
			drawFbo.bind();
			
			UtilL.doAndClear(l, e->{
				List<ITexture> tx=e.getModel().getTextures();
				if(tx.isEmpty()) tx.add(lastZLayer.getTexture());
				else tx.set(0, lastZLayer.getTexture());
				rect.renderSingleBare(e);
			});
		});
		rect.unbind();
		
		drawFbo.bind();
		drawFbo.process();
		Fbo.bindDefault();
		drawFbo.drawImg();
		
		GLUtil.DEPTH_TEST.set(true);
		GLUtil.BLEND_FUNC.set(BlendFunc.NORMAL);
	}
	
	private void addRender(GuiElement e){
		int z=e.getZ();
		while(renderList.size()<=z){
			renderList.add(new ArrayList<>());
		}
		renderList.get(z).add(e);
		e.children.forEach(this::addRender);
	}
	
}
