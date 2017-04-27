package com.lapissea.opengl.program.events;

public class MouseKeyEvent{
	
//	public final Action	action;
//	public final Button	botton;
	
	public MouseKeyEvent(int button, boolean state){
	}

	public static enum Action{
		PRESS,RELEASE;
	}
	public static enum Button{
		RIGHT,LEFT,MID;
	}
	
}
