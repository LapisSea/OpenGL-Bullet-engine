package com.lapissea.opengl.program.interfaces;

public interface WindowEvents{

	default void onResize(int width, int height){}
	default void onFocus(boolean focus){}
	
}
