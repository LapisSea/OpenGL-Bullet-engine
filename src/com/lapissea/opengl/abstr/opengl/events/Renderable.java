package com.lapissea.opengl.abstr.opengl.events;

import com.lapissea.opengl.program.core.Game;
import com.lapissea.opengl.program.rendering.gl.Renderer;

public interface Renderable{
	
	default void preRender(){}
	
	void render();
	
	default Renderer getRenderer(){
		return Game.get().renderer;
	}
	
}
