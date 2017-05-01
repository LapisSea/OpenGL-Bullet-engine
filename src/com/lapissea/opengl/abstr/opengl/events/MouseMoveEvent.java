package com.lapissea.opengl.abstr.opengl.events;

import com.lapissea.opengl.abstr.opengl.IGLWindow;

public class MouseMoveEvent extends WindowEvent{

	public static interface IMouseMoveEventHook{
		
		void onMouseMoveEvent(MouseMoveEvent e);
	}
	
	public final int xOrigin,yOrigin,xDelta,yDelta;

	public MouseMoveEvent(IGLWindow source, int xOrigin, int yOrigin, int xDelta, int yDelta){
		super(source);
		this.xOrigin=xOrigin;
		this.yOrigin=yOrigin;
		this.xDelta=xDelta;
		this.yDelta=yDelta;
	}
	
}
