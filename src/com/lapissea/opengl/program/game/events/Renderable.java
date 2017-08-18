package com.lapissea.opengl.program.game.events;

import com.lapissea.opengl.program.core.Game;
import com.lapissea.opengl.program.rendering.Renderer;

public interface Renderable{
	
	default void preRender(){}
	
	void render();
	
	default Renderer getRenderer(){
		return Game.get().renderer;
	}
	
}
