package com.lapissea.opengl.program.events;

public class MouseMoveEvent{
	
//	public final Action	action;
//	public final Button	botton;
	
	public MouseMoveEvent(int button, int action, int mods){
	}

	public static enum Action{
		PRESS,RELEASE;
	}
	public static enum Button{
		RIGHT,LEFT,MID;
	}
	
}
