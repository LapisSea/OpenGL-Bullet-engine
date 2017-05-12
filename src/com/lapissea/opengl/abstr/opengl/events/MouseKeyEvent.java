package com.lapissea.opengl.abstr.opengl.events;

import com.lapissea.opengl.abstr.opengl.IGLWindow;

public class MouseKeyEvent extends WindowEvent{
	
	public static interface IMouseKeyEventHook{
		
		void onMouseKeyEvent(MouseKeyEvent e);
	}
	
	public enum Action{
		DOWN,UP;
	}
	
	public enum Button{
		RIGHT,LEFT,MID,BACK,FORVARD;
	}
	
	public final Action	action;
	public final Button	button;
	
	public MouseKeyEvent(IGLWindow source, Action action, Button button){
		super(source);
		this.action=action;
		this.button=button;
	}
	
	public static Button buttonFromInt(int button){
		switch(button){
		case 0:return Button.RIGHT;
		}
		return null;
	}
	
}
