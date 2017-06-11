package com.lapissea.opengl.program.rendering.gl.gui;

import com.lapissea.opengl.window.api.events.KeyEvent;
import com.lapissea.opengl.window.api.events.MouseButtonEvent;
import com.lapissea.opengl.window.api.events.MouseMoveEvent;
import com.lapissea.opengl.window.api.events.MouseScrollEvent;

public class IngameDisplay extends GuiElement{
	
	@Override
	public void update(){
		super.update();
	}
	
	@Override
	public void onKey(KeyEvent e){
		deepChildForEach(ch->ch.onKey(e));
	}
	
	@Override
	public void onMouseButton(MouseButtonEvent e){
		deepChildForEach(ch->ch.onMouseButton(e));
	}
	
	@Override
	public void onMouseMove(MouseMoveEvent e){
		deepChildForEach(ch->ch.onMouseMove(e));
	}
	
	@Override
	public void onMouseScroll(MouseScrollEvent e){
		deepChildForEach(ch->ch.onMouseScroll(e));
	}
	
}
