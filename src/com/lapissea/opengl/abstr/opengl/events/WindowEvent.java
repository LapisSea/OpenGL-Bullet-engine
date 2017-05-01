package com.lapissea.opengl.abstr.opengl.events;

import com.lapissea.opengl.abstr.opengl.IGLWindow;

public abstract class WindowEvent{
	
	public final IGLWindow source;
	
	public WindowEvent(IGLWindow source){
		this.source=source;
	}
	
}
