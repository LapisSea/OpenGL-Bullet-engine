package com.lapissea.opengl.game.events;

public interface Initable{
	
	default void preInit(){}
	
	default void init(){}
	
	default void postInit(){}
	
}
