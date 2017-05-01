package com.lapissea.opengl.abstr.opengl.events;

import com.lapissea.opengl.abstr.opengl.IGLWindow;

public class MouseScrollEvent extends WindowEvent{
	
	public static interface IMouseScrollEventHook{
		
		void onMouseScrollEvent(MouseScrollEvent e);
	}
	
	public enum Direction{
		UP,DOWN;
	}
	
	public final Direction	direction;
	public final int		absolute,ammount;
	
	public MouseScrollEvent(IGLWindow source, int absolute){
		super(source);
		this.absolute=absolute;
		
		if(absolute<0){
			this.direction=Direction.DOWN;
			this.ammount=-absolute;
		}
		else{
			this.direction=Direction.UP;
			this.ammount=absolute;
		}
	}
	
}
