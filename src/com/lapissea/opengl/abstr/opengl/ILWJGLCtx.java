package com.lapissea.opengl.abstr.opengl;

public interface ILWJGLCtx{
	
	IGLWindow getCtxWindow();
	
	IGLLoader getGLLoader();
	
	boolean isGlThread(Thread thread);
	
	default boolean isGlThread(){
		return isGlThread(Thread.currentThread());
	}
	
	void init();
	
	void destroy();
}
