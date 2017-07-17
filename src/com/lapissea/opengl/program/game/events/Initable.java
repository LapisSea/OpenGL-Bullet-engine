package com.lapissea.opengl.program.game.events;

public interface Initable{
	
	default void preInit(){}
	
	default void init(){}
	
	default void postInit(){}
	
}
