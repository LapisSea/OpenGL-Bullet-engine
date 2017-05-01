package com.lapissea.opengl.abstr.opengl.events;

import com.lapissea.opengl.abstr.opengl.events.KeyEvent.IKeyEventHook;
import com.lapissea.opengl.abstr.opengl.events.MouseKeyEvent.IMouseKeyEventHook;
import com.lapissea.opengl.abstr.opengl.events.MouseMoveEvent.IMouseMoveEventHook;
import com.lapissea.opengl.abstr.opengl.events.MouseScrollEvent.IMouseScrollEventHook;

public interface InputEvents extends IKeyEventHook,IMouseKeyEventHook,IMouseMoveEventHook,IMouseScrollEventHook{
	
	@Override
	default void onKeyEvent(KeyEvent e){}
	
	@Override
	default void onMouseKeyEvent(MouseKeyEvent e){}
	
	@Override
	default void onMouseMoveEvent(MouseMoveEvent e){}
	
	@Override
	default void onMouseScrollEvent(MouseScrollEvent e){}
}
