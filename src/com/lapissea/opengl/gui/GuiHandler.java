package com.lapissea.opengl.gui;

import static org.lwjgl.opengl.GL11.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import org.lwjgl.input.Mouse;

import com.lapissea.opengl.core.Game;
import com.lapissea.opengl.gui.guis.DebugDisplay;
import com.lapissea.opengl.rendering.Fbo;
import com.lapissea.opengl.rendering.FboRboTextured;
import com.lapissea.opengl.rendering.GLUtil;
import com.lapissea.opengl.rendering.GLUtil.BlendFunc;
import com.lapissea.opengl.rendering.shader.Shaders;
import com.lapissea.opengl.rendering.shader.shaders.GuiRectShader;
import com.lapissea.opengl.window.assets.ITexture;
import com.lapissea.util.UtilL;

public class GuiHandler{
	
	public static int BLUR_DIV=2;
	
	private Stack<Gui>					guiStack	=new Stack<>();
	private LinkedList<IngameDisplay>	displays	=new LinkedList<>();
	private FboRboTextured				drawFbo		=new FboRboTextured(Fbo.TEXTURE);
	private Fbo							lastZ		=new Fbo(Fbo.TEXTURE);
	private List<List<GuiElement>>		renderList	=new ArrayList<>();
	private boolean first;
	
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
	
	private void render0(){
//		drawFbo.setRenderBufferType(true).setSample(4);
		lastZ.setSize(drawFbo);
		drawFbo.setSize(Game.win().getSize());
		
		lastZ.bind();
		glClearColor(0, 0, 0, 0);
		glClear(GL_COLOR_BUFFER_BIT);
		
		drawFbo.bind();
		glClearColor(0, 0, 0, 0);
		glClear(GL_COLOR_BUFFER_BIT);
		
		GLUtil.BLEND.set(true);
		GLUtil.DEPTH_TEST.set(false);
		GLUtil.BLEND_FUNC.set(BlendFunc.NORMAL);
		
		GuiRectShader rect=Shaders.GUI_RECT;
		
		FboRboTextured wfbo=Game.get().renderer.worldFbo;
		
		rect.prepareGlobal();
		
		first=true;
		for(List<GuiElement> l:renderList){
			drawFbo.bind();
			Fbo background=first?Game.get().renderer.worldFbo:lastZ;
			UtilL.doAndClear(l, e->{
				List<ITexture> tx=e.getModel().getTextures();
				if(tx.isEmpty()) tx.add(background.getTexture());
				else tx.set(0, background.getTexture());
				rect.renderSingleBare(e);
			});
			drawFbo.copyColorTo(lastZ);
			first=false;
		}
		rect.unbind();
		drawFbo.process();
		
		GLUtil.DEPTH_TEST.set(true);
		GLUtil.BLEND_FUNC.set(BlendFunc.NORMAL);
		
		Fbo.bindDefault();
		drawFbo.drawImg();
		
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
