package com.lapissea.opengl.abstr.opengl.events;

import com.lapissea.opengl.abstr.opengl.IGLWindow;

public class KeyEvent extends WindowEvent{
	
	public static interface IKeyEventHook{
		
		void onKeyEvent(KeyEvent e);
	}
	
	public static enum KeyAction{
		PRESS,HOLD,RELEASE;
	}
	
	public final KeyAction	action;
	public final int		code;
	public final char		ch;
	
	public KeyEvent(IGLWindow source, KeyAction action, int code){
		super(source);
		this.action=action;
		this.code=code;
		this.ch=(char)code;
	}
	
}
