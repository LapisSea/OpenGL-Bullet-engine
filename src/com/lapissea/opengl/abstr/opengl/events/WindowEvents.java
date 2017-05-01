package com.lapissea.opengl.abstr.opengl.events;

import com.lapissea.opengl.abstr.opengl.events.FocusEvent.IFocusHook;
import com.lapissea.opengl.abstr.opengl.events.ResizeEvent.IResizeHook;

public interface WindowEvents extends IFocusHook,IResizeHook{

	@Override
	default void onFocusEvent(FocusEvent e){}
	
	@Override
	default void onResizeEvent(ResizeEvent e){}
}
