package com.lapissea.opengl.game.events;

import com.lapissea.opengl.core.Game;
import com.lapissea.opengl.rendering.Renderer;

public interface Renderable{
	
	default void preRender(){}
	
	void render();
	
	default Renderer getRenderer(){
		return Game.get().renderer;
	}
	
}
